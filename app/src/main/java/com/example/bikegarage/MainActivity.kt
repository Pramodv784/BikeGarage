package com.example.bikegarage

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bikegarage.activity.AddBikeActivity
import com.example.bikegarage.adaptor.BikeListAdaptor
import com.example.bikegarage.databinding.ActivityMainBinding
import com.example.bikegarage.model.AddBikeModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {
    var _binding: ActivityMainBinding? = null
    var databaseReference: FirebaseDatabase? = null
    var yourReference: DatabaseReference? = null
    var itemList: ArrayList<AddBikeModel> = ArrayList()
    var _adaptor:BikeListAdaptor?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        databaseReference = FirebaseDatabase.getInstance()
        yourReference = databaseReference?.getReference("Bike")
        initView()
    }

    private fun initView() {

          _binding?.topbar?.tvTitle?.setText("Yuvi Chandravanshi Auto Deal")
          _binding?.topbar?.ivBack?.visibility=View.GONE
          _binding?.progressBar?.visibility= View.VISIBLE
        _binding?.fabBt?.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddBikeActivity::class.java))

        }
        yourReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /* This method is called once with the initial value and again whenever data at this location is updated.*/
                val value = dataSnapshot.childrenCount
                 itemList.clear()
                _binding?.progressBar?.visibility= View.GONE

                // var itemData: ArrayList<AddBikeModel>? = dta.values
                for (data in dataSnapshot.children) {
                   Log.e("Datta Id","${data}")
                  val dataBike = data.getValue(AddBikeModel::class.java)!!
                    dataBike.id=data.key
                    itemList.add(dataBike)
                }
                 _binding?.tv?.setText("Total Bike Sale  "+itemList.size)
//                Log.e("item size :::::::" , " ${itemList[0].bikeName}")
                _adaptor= BikeListAdaptor(this@MainActivity,itemList)
                _binding?.rv?.apply {
                    adapter=_adaptor
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
}