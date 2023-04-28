package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchPostsBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.PostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SearchViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchPostsFragment : Fragment(R.layout.fragment_search_posts) {

    lateinit var binding : FragmentSearchPostsBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    private lateinit var rv : RecyclerView
    private lateinit var rvAdapter : PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = binding.searchPostsRecyclerView

        render()

        viewModel.postsChanged.observe(viewLifecycleOwner) { positions ->
            positions.forEach {
                rvAdapter.notifyItemChanged(it)
            }
        }

        viewModel.onSearch.observe(viewLifecycleOwner) {
            if(it){
                render()
                viewModel.onSearch.postValue(false)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun render() {
        rvAdapter = PostListAdapter(
            viewModel.currentUser!!,
            viewModel.posts,
            viewModel.allAppUsers,
            ::onPostClickListener,
            ::onFollowClickListener,
            ::onPostLikeListener,
            ::onPostSearchLocationClickListener,
            ::onPostOwnerClickListener,
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.itemAnimator = null
        rv.layoutAnimation = null
        rv.adapter = rvAdapter

    }

    private fun onFollowClickListener(user : User){
        CoroutineScope(Dispatchers.Main).launch {
            sharedViewModel.addFollower(user._id)
        }
    }

    private fun onPostLikeListener(position : Int, post : Post, like : Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            if(like){
                sharedViewModel.likePost(post._id)
            }else{
                sharedViewModel.unlikePost(post._id)
            }
        }
    }

    private fun onPostClickListener(post : Post){
        viewModel.goToPost(post)
    }

    private fun onPostOwnerClickListener(user : User){
        if(user._id == viewModel.currentUser?._id){
            (requireActivity() as MainActivity).binding.bottomNav.selectedItemId = R.id.nav_profile
        }else{
            viewModel.goToUser(user)
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