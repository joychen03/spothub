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
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileFavouritesBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.PostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.ProfileViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFavouritesFragment : Fragment(R.layout.fragment_profile_favourites) {

    lateinit var binding : FragmentProfileFavouritesBinding
    private lateinit var rv : RecyclerView
    private lateinit var rvAdapter : PostListAdapter

    private val viewModel : ProfileViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFavouritesBinding.inflate(inflater, container, false)
        rv = binding.ProfileFavouritesRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        render()

        viewModel.favPostsChanged.observe(viewLifecycleOwner){ positions ->
            positions.forEach {
                rvAdapter.notifyItemChanged(it)
            }
        }

        viewModel.favPostsAdded.observe(viewLifecycleOwner){ position ->

            if(viewModel.favPosts.size < 3){
                rvAdapter.notifyDataSetChanged()
            }else{
                rvAdapter.notifyItemInserted(position)
                rvAdapter.notifyItemInserted(viewModel.favPosts.size - 1)
            }
        }

        viewModel.favPostsRemoved.observe(viewLifecycleOwner){ position ->

            if(viewModel.favPosts.size < 2){
                rvAdapter.notifyDataSetChanged()
            }else{
                rvAdapter.notifyItemRemoved(position)
                rvAdapter.notifyItemInserted(viewModel.favPosts.size - 1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun render(){
        rvAdapter = PostListAdapter(
            currentUser = viewModel.currentUser!!,
            postList = viewModel.favPosts,
            userList = viewModel.users,
            onClickListener = ::onPostClickListener,
            onLikeClickListener = ::onPostLikeClickListener,
            onFollowClickListener = ::onPostFollowClickListener,
            onOwnerClickListener = ::onPostOwnerClickListener,
            onSearchLocationClickListener = ::onPostSearchLocationClickListener
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.itemAnimator = null
        rv.layoutAnimation = null
        rv.adapter = rvAdapter

    }

    private fun onPostClickListener(post : Post){
        viewModel.goToPost(post)
    }

    private fun onPostLikeClickListener(position : Int, post : Post, checked: Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            val updatedPost = if(checked){
                sharedViewModel.likePost(post._id)
            }else{
                sharedViewModel.unlikePost(post._id)
            }

            if(updatedPost == null){
                Utils.makeSimpleAlert(requireContext(), "Error al actualizar el post")
            }
        }

    }


    private fun onPostFollowClickListener(user: User){
        CoroutineScope(Dispatchers.Main).launch {
            val userUpdated = sharedViewModel.addFollower(user._id)

            if (userUpdated == null) {
                Utils.makeSimpleAlert(requireContext(), "Error al actualizar el usuario")
            }
        }

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



}