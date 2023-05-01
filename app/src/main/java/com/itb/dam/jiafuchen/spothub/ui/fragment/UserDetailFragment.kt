package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentUserDetailBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.OwnPostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.HomeViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.UserDetailViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserDetailFragment : Fragment(R.layout.fragment_user_detail) {

    lateinit var binding : FragmentUserDetailBinding
    lateinit var rvAdapter : OwnPostListAdapter
    lateinit var rv : RecyclerView

    private val viewModel : UserDetailViewModel by viewModels()
    private val homeViewModel : HomeViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val args : UserDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getUser(args.user._id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        rv = binding.UserDetailPosts
        configSwipeLayout()

        viewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.UserDetailUsername.text = it.username
                binding.UserDetailEmail.text = it.email
                binding.UserDetailDescription.text = it.description
                binding.UserDetailFollowers.text = it.followers.size.toString()
                binding.UserDetailFollowing.text = it.followings.size.toString()
                binding.UserDetailAvatar.setImageBitmap(Utils.byteArrayToImage(it.avatar))

                if(it.followers.contains(viewModel.currentUser?._id)) {
                    binding.UserDetailFollowBtn.visibility = View.GONE
                    binding.UserDetailUnfollowBtn.visibility = View.VISIBLE
                }else{
                    binding.UserDetailFollowBtn.visibility = View.VISIBLE
                    binding.UserDetailUnfollowBtn.visibility = View.GONE
                }

                viewModel.getPosts(it.owner_id)
            }else{
                Utils.makeToast(requireContext(), "User not found")
                findNavController().popBackStack()
            }
        }

        viewModel.userPosts.observe(viewLifecycleOwner) {
            initPostRecyclerView(it)
        }

        binding.UserDetailPosts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val indexItemRV = (rv.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()!!
                val v = (rv.layoutManager as? LinearLayoutManager)?.getChildAt(0)
                val topViewRV = if(v == null) 0 else v.top - (rv.layoutManager as? LinearLayoutManager)?.paddingTop!!

                viewModel.postListScrollTo(indexItemRV, topViewRV)
            }
        })

        binding.UserDetailUnfollowBtn.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                val user = sharedViewModel.removeFollower(args.user._id)

                homeViewModel.userList.indexOfFirst { it._id == user?._id }.let { index ->
                    if (index != -1) {
                        homeViewModel.userList[index] = user!!
                    }
                }

                viewModel.getUser(args.user._id)
            }

        }

        binding.UserDetailFollowBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val user = sharedViewModel.addFollower(args.user._id)

                homeViewModel.userList.indexOfFirst { it._id == user?._id }.let { index ->
                    if (index != -1) {
                        homeViewModel.userList[index] = user!!
                    }
                }

                viewModel.getUser(args.user._id)
            }
        }

    }


    private fun initPostRecyclerView(postList : List<Post>){

        rv.layoutManager = LinearLayoutManager(requireContext())
        rvAdapter = OwnPostListAdapter(
            viewModel.currentUser!!,
            postList,
            ::onPostClickListener,
            ::onPostLikeClickListener,
            ::onPostSearchLocationClickListener,
        )
        rv.adapter = rvAdapter

        //scrollToPosition(viewModel.scrollPosition, viewModel.scrollOffset)

    }

    private fun onPostClickListener(post : Post){
        val action = UserDetailFragmentDirections.actionUserDetailFragmentToPostDetailFragment(post)
        findNavController().navigate(action)
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

            viewModel.updatePost(position, updatedPost)
            rvAdapter.notifyItemChanged(position)

            homeViewModel.postList.indexOfFirst { it._id == post._id }.let {
                if (it != -1) {
                    homeViewModel.postList[it] = updatedPost
                }
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


    private fun configSwipeLayout(){

        binding.UserDetailRefresh.setColorSchemeColors(requireActivity().getColor(R.color.primary_btn))

        binding.UserDetailRefresh.setOnRefreshListener {
            viewModel.getPosts(args.user.owner_id)
            binding.UserDetailRefresh.isRefreshing = false
        }
    }


    private fun scrollToPosition(position : Int, offset : Int = 0){
        rv.stopScroll()
        (rv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position,offset)
    }

}