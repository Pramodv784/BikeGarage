package com.example.bikegarage.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


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
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var file: File?=null
    var resultUri:Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bike)
        _activity = this
        _binding?.executePendingBindings()
        _binding?.topbar?.ivBack?.setOnClickListener { onBackPressed() }

       databaseReference= FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance();
        storageReference = storage?.getReference();

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
                        BuildConfig.APPLICATION_ID + ".provider",
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

    var cameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val uri = FileProvider.getUriForFile(
                    _activity!!, BuildConfig.APPLICATION_ID + ".provider",
                    photoFile!!
                )
                /* // Uri resultUri = Objects.requireNonNull(CropImage.getActivityResult(result.getData())).getUri();
        Bitmap bmp = MediaStore.Images.Media.getBitmap(_activity.getContentResolver(), uri);
        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
        String path = MediaStore.Images.Media.insertImage(_activity.getContentResolver(), bmp, "title", null);*/launchImageCropper(
                    uri
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun randomFile(): File {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val now = LocalDate.now()
        return File(
            if (_activity != null) _activity!!.filesDir else null,
            "pic-" + UUID.randomUUID() + '-' + now.format(formatter) + ".jpg"
        )
    }
    private fun launchImageCropper(path: Uri) {
        if (getAsSquare!!) {
            cropResult.launch(CropImage.activity(path).getIntent(_activity!!))
        } else {
            cropResult.launch(CropImage.activity(path).getIntent(_activity!!))
        }
    }
    var cropResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {

            try {
               resultUri =
                    Objects.requireNonNull(CropImage.getActivityResult(result.data))
                        .uri
                val bmp =
                    MediaStore.Images.Media.getBitmap(_activity!!.contentResolver, resultUri)
                _binding!!.ivImage.setImageBitmap(bmp)
                file = saveBitmap(_activity!!, bmp, "photoName")
                val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file", file!!.name, RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        (file!!)
                    )

                )

                uploadImage()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage() {
        if (resultUri != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
           var storageRefrence:StorageReference= storageReference?.child(
               "images/"
                       + UUID.randomUUID().toString())!!;




            // adding listeners on upload
            // or failure of image
            storageRefrence.putFile(resultUri!!)
                .addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot?> { // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@AddBikeActivity,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    })
                .addOnFailureListener(OnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@AddBikeActivity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                })
                .addOnProgressListener(
                    OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->

                        // Progress Listener for loading
                        // percentage on the dialog box
                        val progress = (100.0
                                * taskSnapshot.bytesTransferred
                                / taskSnapshot.totalByteCount)
                        progressDialog.setMessage(
                            "Uploaded "
                                    + progress.toInt() + "%"
                        )
                    })
        }
    }
    private fun saveBitmap(context: Context, bitmap: Bitmap, name: String): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "$name.jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageFile
    }


}