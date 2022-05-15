package com.example.customviewapp

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView

import android.widget.LinearLayout
import com.example.bikegarage.R


class CustomEditText(context:Context,attributeSet: AttributeSet):LinearLayout(context,attributeSet) {
var et:EditText?=null
var image:ImageView?=null
    init {
        inflate(context, R.layout.custom_edit_text,this)
        et=findViewById(R.id.et)
        image=findViewById(R.id.image)

        et?.setOnFocusChangeListener { view, b ->
            if(b)
            {
                image?.setImageResource(R.drawable.check)
            }
            else
            {
                image?.setImageResource(R.drawable.uncheck)
            }
        }


    }
}