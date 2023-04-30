package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User

class OwnPostListAdapter(
    private val currentUser: User,
    private val postList : List<Post>,
    private val onClickListener : (Post) -> Unit,
    private val onLikeClickListener : (Int, Post, Boolean) -> Unit,
    private val onSearchLocationClickListener : (Post) -> Unit,
) : RecyclerView.Adapter<OwnPostListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnPostListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return OwnPostListViewHolder(layoutInflater.inflate(R.layout.item_own_post, parent, false))
    }

    override fun onBindViewHolder(holder: OwnPostListViewHolder, position: Int) {
        val item = postList[position]

        holder.render(
            position,
            currentUser,
            item,
            onClickListener,
            onLikeClickListener,
            onSearchLocationClickListener,
        )

    }

    override fun getItemCount(): Int = postList.size



}