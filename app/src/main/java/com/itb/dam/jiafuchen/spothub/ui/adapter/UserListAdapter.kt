package com.itb.dam.jiafuchen.spothub.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.domain.model.User

class UserListAdapter(
    private val currentUser: User,
    private val userList : MutableList<User>,
    private val onClickListener : (Int) -> Unit,
    private val onFollowClickListener : (Int) -> Unit
) : RecyclerView.Adapter<UserListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserListViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val item = userList[position]

        holder.render(
            position,
            item,
            currentUser,
            onClickListener,
            onFollowClickListener,
        )

    }

    override fun getItemCount(): Int = userList.size


}