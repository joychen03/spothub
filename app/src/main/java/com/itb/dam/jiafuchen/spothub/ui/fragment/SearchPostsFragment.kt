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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchPostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.PostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.HomeViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SearchViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchPostsFragment : Fragment(R.layout.fragment_search_posts) {

    lateinit var binding : FragmentSearchPostsBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private val homeViewModel : HomeViewModel by activityViewModels()

    lateinit var rv : RecyclerView
    lateinit var rvAdapter : PostListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
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

        viewModel.posts.observe(viewLifecycleOwner){
            initRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    fun initRecyclerView(){

    }

    private fun onPostClickListener(post : Post){
        val directions = HomeFragmentDirections.actionHomeFragmentToPostDetailFragment()
        findNavController().navigate(directions)
    }


    private fun onPostOwnerClickListener(user : User){
        if(user._id == viewModel.currentUser?._id){
            (requireActivity() as MainActivity).binding.bottomNav.selectedItemId = R.id.nav_profile
        }else{
            val directions = HomeFragmentDirections.actionHomeFragmentToUserDetailFragment(user)
            findNavController().navigate(directions)
        }
    }

    private fun onPostSearchLocationClickListener(post : Post){
        //OPEN GOOGLE MAP
        val label = "${post.latitude}, ${post.longitude}"
        val gmmIntentUri = Uri.parse("geo:${post.latitude},${post.longitude}?q=${post.latitude},${post.longitude}($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

    fun getPosts(query: String){


    }



}