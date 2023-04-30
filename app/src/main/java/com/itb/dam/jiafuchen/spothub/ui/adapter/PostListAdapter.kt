package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User

class PostListAdapter(
    private val currentUser: User,
    private val postList : List<Post>,
    private val userList : List<User>,
    private val onClickListener : (Post) -> Unit,
    private val onFollowClickListener : (User) -> Unit,
    private val onLikeClickListener : (Int, Post, Boolean) -> Unit,
    private val onSearchLocationClickListener : (Post) -> Unit,
    private val onOwnerClickListener : (User) -> Unit
) : RecyclerView.Adapter<PostListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostListViewHolder(layoutInflater.inflate(R.layout.item_post, parent, false))
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        val item = postList[position]

        val owner = userList.find { it.owner_id == item.owner_id } ?: return

        holder.render(
            position,
            currentUser,
            item,
            owner,
            onClickListener,
            onFollowClickListener,
            onLikeClickListener,
            onSearchLocationClickListener,
            onOwnerClickListener
        )


    }

    override fun getItemCount(): Int = postList.size


}