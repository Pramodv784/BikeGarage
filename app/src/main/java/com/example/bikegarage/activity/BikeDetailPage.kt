package com.example.bikegarage.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.bikegarage.R
import com.example.bikegarage.databinding.ActivityBikeDetailPageBinding
import com.example.bikegarage.model.AddBikeModel
import com.google.firebase.database.FirebaseDatabase


class BikeDetailPage : AppCompatActivity() {
     var _mbinding:ActivityBikeDetailPageBinding?=null
    var addBikeData:AddBikeModel?=null
    var remainigAmount=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mbinding=DataBindingUtil.setContentView(this,R.layout.activity_bike_detail_page)
        initViews()
    }

    private fun initViews() {
        addBikeData= intent.getSerializableExtra("data") as AddBikeModel?
        Log.e("Key Data>>>>>","${addBikeData?.id}")
        _mbinding?.btDelete?.setOnClickListener {
            deleteItemDialog()
        }

        _mbinding?.data=addBikeData
        Glide.with(this)
            .load(addBikeData?.imageurl)
            .into(_mbinding?.ivBike!!)
            .onLoadFailed(this.getDrawable(R.drawable.ic_image))
        _mbinding?.topbar?.tvTitle?.setText("Bike Detail")
        _mbinding?.topbar?.ivBack?.setOnClickListener { onBackPressed() }
        if(addBikeData?.remainingprice!!>0)
        {
            _mbinding?.etRemainig?.visibility=View.VISIBLE
            _mbinding?.bt?.visibility= View.VISIBLE
            _mbinding?.bt?.setText("Remainig Price "+addBikeData?.remainingprice.toString())
        }
        else
        {
            _mbinding?.etRemainig?.visibility=View.GONE
            _mbinding?.bt?.visibility= View.GONE
        }
        _mbinding?.bt?.setOnClickListener {
            if(_mbinding?.etRemainig?.text?.toString()?.isEmpty() == true)
            {
                Toast.makeText(applicationContext,"Enter remainig Amount",Toast.LENGTH_SHORT).show()
            }
            else
            {
                updateDetail()
            }

        }
        _mbinding?.etRemainig?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length>0)
                {
                    remainigAmount= (addBikeData?.remainingprice!!) -(s.toString().toDouble())
                    _mbinding?.bt?.setText("Remainig "+remainigAmount.toString())
                }
                else
                {
                    _mbinding?.bt?.setText("Remainig "+addBikeData?.remainingprice.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        _mbinding?.alterCall?.setOnClickListener {
         if(isPermissionGranted()){
             call_action(_mbinding?.alterCall?.text.toString())
         }
        }
        _mbinding?.mobileCall?.setOnClickListener {
            if(isPermissionGranted()){
                call_action(_mbinding?.mobileCall?.text.toString())
            }
        }



    }

    private fun deleteItemDialog() {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete")
        //set message for alert dialog
        builder.setMessage("Are you sure want to delete bike data")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            deleteItem()
        }
        //performing cancel action

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
           dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()



    }
    private  fun deleteItem(){
        _mbinding?.progressBar?.visibility= View.VISIBLE
        val reference =
            FirebaseDatabase.getInstance().getReference("Bike").child(addBikeData?.id.toString())
        reference.removeValue().addOnSuccessListener {
            Toast.makeText(applicationContext,"Bike Data Deleted",Toast.LENGTH_SHORT).show()
            _mbinding?.progressBar?.visibility= View.GONE
            onBackPressed()

        }
    }

    private fun updateDetail() {
        _mbinding?.progressBar?.visibility= View.VISIBLE
        val reference =
            FirebaseDatabase.getInstance().getReference("Bike").child(addBikeData?.id.toString())

         addBikeData?.remainingprice=remainigAmount

        reference.setValue(addBikeData).addOnCompleteListener {
            if(it.isSuccessful)
            {
                _mbinding?.progressBar?.visibility= View.GONE

                onBackPressed()
            }
            else{
                _mbinding?.progressBar?.visibility= View.GONE
            }
        }
    }
    fun call_action(number:String) {
        //val phnum: String = etPhoneno.getText().toString()
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    }
    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("TAG", "Permission is granted")
                true
            } else {
                Log.v("TAG", "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            true
        }
    }
}