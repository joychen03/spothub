package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.databinding.ItemPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.ItemUserBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils

class UserListViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val layout = ItemUserBinding.bind(view)

    fun render(
        position : Int,
        user : User,
        currentUser: User,
        onClickListener: (Int) -> Unit,
        onFollowClickListener: (Int) -> Unit,
    ){
        layout.UserAvatar.setImageBitmap(Utils.byteArrayToImage(user.avatar))
        layout.UserUsername.text = user.username
        layout.UserFollowers.text = "${user.followers.size} followers"

        if(user._id == currentUser._id){
            layout.UserFollowBtn.visibility = View.GONE
            layout.UserFollowingTag.visibility = View.GONE
        }else{
            if(user.followers.contains(currentUser._id)){
                layout.UserFollowBtn.visibility = View.GONE
                layout.UserFollowingTag.visibility = View.VISIBLE
            }else{
                layout.UserFollowBtn.visibility = View.VISIBLE
                layout.UserFollowingTag.visibility = View.GONE
            }
        }

        itemView.setOnClickListener {
            onClickListener(position)
        }

        layout.UserFollowBtn.setOnClickListener {
            onFollowClickListener(position)
        }

    }
}