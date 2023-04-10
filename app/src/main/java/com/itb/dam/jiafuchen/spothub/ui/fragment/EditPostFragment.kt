package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentEditPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentMapLocatingBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentPostDetailBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileEditBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileFavouritesBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfilePostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchPostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding


class EditPostFragment : Fragment(R.layout.fragment_edit_post) {

    lateinit var binding : FragmentEditPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}