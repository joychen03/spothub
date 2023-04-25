package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.databinding.ItemPostBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils

class PostListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val layout = ItemPostBinding.bind(view)

    fun render(
        position : Int,
        lastOne: Boolean,
        currentUser: User,
        post: Post,
        owner: User,
        onClickListener: (Post) -> Unit,
        onFollowClickListener: (User) -> Unit,
        onLikeClickListener: (Int, Post, Boolean) -> Unit,
        onSearchLocationClickListener: (Post) -> Unit,
        onOwnerClickListener: (User) -> Unit
    ){
        layout.PostUsername.text = owner.username
        layout.PostUserAvatar.setImageBitmap(Utils.byteArrayToImage(owner.avatar))
        layout.PostFollowers.text = "${ owner.followers.count()} followers"
        if(currentUser?.followings?.contains(owner._id) == true){
            layout.PostFollowBtn.visibility = View.GONE
            layout.PostFollowingTag.visibility = View.VISIBLE
        }else{
            layout.PostFollowBtn.visibility = View.VISIBLE
            layout.PostFollowingTag.visibility = View.GONE
        }
        layout.PostUpdateDatetime.text = Utils.formatTime(post.updateDataTime.epochSeconds * 1000)
        layout.PostTitle.text = post.title
        layout.PostImage.setImageBitmap(Utils.byteArrayToImage(post.image))
        layout.PostLikesCount.text = post.likes.count().toString()

        if(lastOne){
            layout.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30,30,30,300)
            }
        }else{
            layout.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30,30,30,0)
            }
        }

        layout.PostLikeBtn.isChecked = post.likes.contains(currentUser._id)

        if(owner._id == currentUser._id){
            layout.PostFollowBtn.visibility = View.GONE
            layout.PostFollowingTag.visibility = View.GONE
        }else{
            if(owner.followers.contains(currentUser._id)){
                layout.PostFollowBtn.visibility = View.GONE
                layout.PostFollowingTag.visibility = View.VISIBLE
            }else{
                layout.PostFollowBtn.visibility = View.VISIBLE
                layout.PostFollowingTag.visibility = View.GONE
            }
        }

        itemView.setOnClickListener {
            onClickListener(post)
        }

        layout.PostFollowBtn.setOnClickListener {
            onFollowClickListener(owner)
        }

        layout.PostLikeBtn.setOnClickListener {
            onLikeClickListener(position, post, layout.PostLikeBtn.isChecked)
        }

        layout.PostSearchLocation.setOnClickListener {
            onSearchLocationClickListener(post)
        }

        layout.PostUserAvatar.setOnClickListener{
            onOwnerClickListener(owner)
        }

    }
}