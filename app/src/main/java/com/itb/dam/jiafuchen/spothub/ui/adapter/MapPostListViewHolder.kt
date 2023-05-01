package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.databinding.ItemMapPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.ItemPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.ItemUserBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils

class MapPostListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val layout = ItemMapPostBinding.bind(view)

    fun render(
        position : Int,
        currantUser : User,
        post : Post,
        onClickListener: (Int) -> Unit,
        onLikeListener : (Int, Boolean) -> Unit,
    ){

        layout.MapPostTitle.text = post.title
        layout.MapPostImage.setImageBitmap(Utils.byteArrayToImage(post.image))
        layout.MapPostLikesCount.text = post.likes.size.toString()

        layout.MapPostLikeBtn.isChecked = post.likes.contains(currantUser._id)

        itemView.setOnClickListener {
            onClickListener(position)
        }

        layout.MapPostLikeBtn.setOnClickListener {
            onLikeListener(position, layout.MapPostLikeBtn.isChecked)
        }

    }
}