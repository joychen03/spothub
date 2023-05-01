package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.databinding.ItemOwnPostBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils

class OwnPostListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val layout = ItemOwnPostBinding.bind(view)

    fun render(
        position : Int,
        currentUser: User,
        post: Post,
        onClickListener: (Post) -> Unit,
        onLikeClickListener: (Int, Post, Boolean) -> Unit,
        onSearchLocationClickListener: (Post) -> Unit,
    ){

        layout.OwnPostUpdateDatetime.text = Utils.formatTime(post.updateDataTime.epochSeconds * 1000)
        layout.OwnPostTitle.text = post.title
        layout.OwnPostImage.setImageBitmap(Utils.byteArrayToImage(post.image))
        layout.OwnPostLikesCount.text = post.likes.count().toString()
        layout.OwnPostLikeBtn.isChecked = post.likes.contains(currentUser._id)

        itemView.setOnClickListener {
            onClickListener(post)
        }

        layout.OwnPostLikeBtn.setOnClickListener {
            onLikeClickListener(position, post, layout.OwnPostLikeBtn.isChecked)
        }

        layout.OwnPostSearchLocation.setOnClickListener {
            onSearchLocationClickListener(post)
        }

    }
}