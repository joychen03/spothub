package com.itb.dam.jiafuchen.spothub.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    val fragmengList : ArrayList<Fragment>,
    fm : FragmentManager,
    lifecycle : Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return fragmengList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmengList[position]
    }

}
