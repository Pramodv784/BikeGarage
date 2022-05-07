package com.example.bikegarage.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.bikegarage.R
import com.example.bikegarage.databinding.ActivityAddBikeBinding
import com.example.bikegarage.model.AddBikeModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*



class AddBikeActivity : AppCompatActivity() {
    var databaseReference:FirebaseDatabase?=null
    private var _activity: AddBikeActivity? = null
    lateinit var listener:ChildEventListener
    var yourReference:DatabaseReference?=null
    private var _binding: ActivityAddBikeBinding? = null
    private var bikeData: DatabaseReference? = null
    private var _dataList: ArrayList<AddBikeModel>? = null
    var getAsSquare: Boolean? = false
    var getFromCamera: Boolean? = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bike)

        _binding?.executePendingBindings()
        _binding?.topbar?.ivBack?.setOnClickListener { onBackPressed() }

       databaseReference= FirebaseDatabase.getInstance()

        _binding?.ivImage?.setOnClickListener {  launchImagePickerDialog(true) }
        yourReference=databaseReference?.getReference("Bike")
        _binding?.btAdd?.setOnClickListener {
            addData()
        }

    }



    private fun addData() {
       _binding?.progressBar?.visibility= VISIBLE
       // mDbRef = databaseReference.("Donor/Name");
        if (_binding?.etName?.text.toString().isEmpty()) {
            Toast.makeText(applicationContext, "Please enter name", Toast.LENGTH_SHORT).show()
        } else {
            /*bikeData?.setValue("aniket")?.addOnFailureListener {
                print(it)
            }*/
           var data=
                AddBikeModel(
                    _binding?.etName?.text.toString().trim(),
                    _binding?.etBrand?.text.toString().trim(),
                    _binding?.etModel?.text.toString().trim(),
                    "",
                    20000.0,
                    ""
                )

            var key: String? =yourReference?.push()?.key

            yourReference?.child(key.toString())?.setValue(data)?.addOnCompleteListener {
                if(it.isSuccessful)
                {
                    _binding?.progressBar?.visibility=GONE

                    onBackPressed()
                }
                else{
                    _binding?.progressBar?.visibility=GONE
                }
            }
           // addDataList(_dataList)



        }

    }

    fun launchImagePickerDialog(getAsSquare: Boolean) {
        this.getAsSquare = getAsSquare
        val bottomSheetDialog = BottomSheetDialog(_activity!!)
        bottomSheetDialog.setContentView(R.layout.bottomsheet_image_picker)
        val camera = bottomSheetDialog.findViewById<ImageView>(R.id.camera)
        val gallery = bottomSheetDialog.findViewById<ImageView>(R.id.gallery)
        assert(camera != null)
        camera!!.setOnClickListener { v: View? ->
            loadImage(true)
            bottomSheetDialog.hide()
        }
        assert(gallery != null)
        gallery!!.setOnClickListener { v: View? ->
            loadImage(false)
            bottomSheetDialog.hide()
        }
        bottomSheetDialog.show()
    }

    fun loadImage(getFromCamera: Boolean) {

        this.getFromCamera = getFromCamera
        TedPermission.with(_activity).setPermissionListener(permissionlistener1)
            .setDeniedMessage("Please provide use required permission to continue").setPermissions(
                "android.permission.CAMERA",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ).check()
    }
    var permissionlistener1: PermissionListener = object : PermissionListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPermissionGranted() {
            if (getFromCamera!!) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = randomFile()
                photoUri = photoFile?.let {
                    FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.bikegarage",
                        it
                    )
                }
                //Timber.i("photoUri before opening camera: ${photoUri.path}");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraResultLauncher.launch(cameraIntent)

                /*Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .getIntent(_activity);
                cameraResultLauncher.launch(intent);*/
            } else {
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.type = "*/*"
                galleryResultLauncher.launch(galleryIntent)
            }
        }

        override fun onPermissionDenied(arrayList: java.util.ArrayList<String>) {}
    }
    var galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                launchImageCropper(Uri.parse(result.data!!.data.toString()))

                /* Uri uri = FileProvider.getUriForFile(_activity, "com.more_yeahs.build_dream_tr", photoFile);
                   launchImageCropper(uri);*/
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun launchImageCropper(path: Uri) {
        if (getAsSquare!!) {
            cropResult.launch(CropImage.activity(path).getIntent(_activity!!))
        } else {
            cropResult.launch(CropImage.activity(path).getIntent(_activity!!))
        }
    }


}