package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfilePostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchPostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.ui.adapter.OwnPostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.ProfileViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfilePostsFragment : Fragment(R.layout.fragment_profile_posts) {

    lateinit var binding : FragmentProfilePostsBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : ProfileViewModel by activityViewModels()

    private lateinit var rv : RecyclerView
    private lateinit var rvAdapter : OwnPostListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = binding.ProfileOwnPostsRecyclerView

        render()

        viewModel.myPostsChanged.observe(viewLifecycleOwner){ positions ->
            positions.forEach {
                rvAdapter.notifyItemChanged(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun render(){
        rvAdapter = OwnPostListAdapter(
            viewModel.currentUser!!,
            viewModel.myPosts,
            ::onPostClickListener,
            ::onPostLikeClickListener,
            ::onPostSearchLocationClickListener
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.itemAnimator = null
        rv.layoutAnimation = null
        rv.adapter = rvAdapter
    }

    private fun onPostClickListener(post : Post){
        viewModel.goToPost(post)
    }

    private fun onPostLikeClickListener(position : Int, post : Post, checked : Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            val updatedPost = if(checked){
                sharedViewModel.likePost(post._id)
            }else{
                sharedViewModel.unlikePost(post._id)
            }

            if(updatedPost == null){
                Utils.makeSimpleAlert(requireContext(), "Error al actualizar el post")
                return@launch
            }
        }
    }

    private fun onPostSearchLocationClickListener(post : Post){
        val label = "${post.latitude}, ${post.longitude}"
        val gmmIntentUri = Uri.parse("geo:${post.latitude},${post.longitude}?q=${post.latitude},${post.longitude}($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }




}