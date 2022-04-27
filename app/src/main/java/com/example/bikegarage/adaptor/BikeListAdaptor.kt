package com.example.bikegarage.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun getItemCount(): Int {
        return  bikeList.size
    }
    inner class BikeListViewHolder(val mBinding:ListItemViewBinding) :RecyclerView.ViewHolder(mBinding.root){
        fun bind(dataNew:AddBikeModel){
         mBinding.dataBike=dataNew
            mBinding.executePendingBindings()
        }


    }
}