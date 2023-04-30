package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User

class MapPostListAdapter(
    private val currentUser : User,
    private val postList : MutableList<Post>,
    private val onClickListener : (Int) -> Unit,
    private val onClickLikeListener : (Int, Boolean) -> Unit
) : RecyclerView.Adapter<MapPostListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapPostListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MapPostListViewHolder(layoutInflater.inflate(R.layout.item_map_post, parent, false))
    }

    override fun onBindViewHolder(holder: MapPostListViewHolder, position: Int) {
        val item = postList[position]

        holder.render(
            position,
            currentUser,
            item,
            onClickListener,
            onClickLikeListener
        )
    }

    override fun getItemCount(): Int = postList.size


}