package com.example.image_app.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.image_app.R
import com.example.image_app.databinding.ItemImageBinding
import com.example.image_app.ui.data.MImage
import com.example.image_app.ui.main.interfaces.Callback
import com.squareup.picasso.Picasso

class ImageAdapter(private val movieList: ArrayList<MImage>, private val callBack: Callback) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemImageBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_image, parent, false
        )
        return ViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val root = holder.binding
        val item = movieList[position]
        item.position=position+1
        root.tvCount.text =
            "${root.root.rootView.context.resources.getString(R.string.image)} ${position + 1}"
        Picasso.get().load(item.urls.thumb).error(R.drawable.ic_suggestion)
            .into(root.img)
        root.root.setOnClickListener { callBack.openSelectedImage(item,root) }
    }

    class ViewHolder(internal var binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

}