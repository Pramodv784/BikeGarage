package com.example.bikegarage.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bikegarage.R
import com.example.bikegarage.activity.BikeDetailPage
import com.example.bikegarage.databinding.ListItemViewBinding
import com.example.bikegarage.model.AddBikeModel

class BikeListAdaptor(
    private val context:Context,
    private val bikeList:ArrayList<AddBikeModel>
):RecyclerView.Adapter<BikeListAdaptor.BikeListViewHolder>()
{



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BikeListAdaptor.BikeListViewHolder {
       val binding=ListItemViewBinding.inflate(LayoutInflater.from(context),parent,false)
        return BikeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BikeListAdaptor.BikeListViewHolder, position: Int) {
      holder.bind(bikeList[position])
       var obj: AddBikeModel=bikeList.get(position)
        holder.mBinding.bikeName.setText(""+obj.bikeName)
        holder.mBinding.bikePrice.setText("Rs. "+obj.bikeprice)
        holder.mBinding.userName.setText(obj.userName)
        if(obj.remainingprice!!>0)
        {
            holder.mBinding.remainig.visibility=View.VISIBLE
            holder.mBinding.remainig.setText("Remainig Price "+obj.remainingprice.toString())
        }
        else
        {
            holder.mBinding.remainig.visibility=View.GONE
        }

        Glide.with(context)
            .load(bikeList[position].imageurl)
            .into(holder.mBinding.ivImage)
            .onLoadFailed(context.getDrawable(R.drawable.ic_image))
        holder.mBinding.rlView.setOnClickListener {
            context.startActivity(Intent(context,BikeDetailPage::class.java)
                .putExtra("data",obj))
        }



    }

    override fun getItemCount(): Int {
        return  bikeList.size
    }
    inner class BikeListViewHolder(val mBinding:ListItemViewBinding) :RecyclerView.ViewHolder(mBinding.root){
        fun bind(dataNew:AddBikeModel){

            mBinding.executePendingBindings()
        }


    }
}