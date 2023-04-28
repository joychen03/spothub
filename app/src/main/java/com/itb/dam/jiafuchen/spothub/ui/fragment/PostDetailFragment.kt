package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentPostDetailBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileEditBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileFavouritesBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfilePostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchPostsBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viemodel.PostDetailViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    lateinit var binding : FragmentPostDetailBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        viewModel.postUpdated.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.PostDetailTitle.text = it.title
                binding.PostDetailDescription.text = it.description
                binding.PostDetailUpdateDatetime.text = Utils.formatTime(it.updateDataTime.epochSeconds * 1000)
                binding.PostDetailImage.setImageBitmap(Utils.byteArrayToImage(it.image))
                binding.PostDetailLikesCount.text = it.likes.size.toString()
                binding.PostDetailLikeBtn.isChecked = it.likes.contains(viewModel.currentUser?._id)
            }else{
                Utils.makeToast(requireContext(), "Post not found")
                findNavController().popBackStack()
            }
        }

        viewModel.userUpdated.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.PostDetailUsername.text = it.username
                binding.PostDetailFollowers.text = "${it.followers.size} followers"
                binding.PostDetailUserAvatar.setImageBitmap(Utils.byteArrayToImage(it.avatar))

                if(it.owner_id != app.currentUser!!.id){
                    if(it.followers.contains(viewModel.currentUser?._id)) {
                        binding.PostDetailFollowBtn.visibility = View.GONE
                        binding.PostDetailFollowingTag.visibility = View.VISIBLE
                    }else{
                        binding.PostDetailFollowBtn.visibility = View.VISIBLE
                        binding.PostDetailFollowingTag.visibility = View.GONE
                    }
                }

            }else{
                Utils.makeToast(requireContext(), "User not found")
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.setUp(args.post)

        binding.PostDetailLikeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val checked = binding.PostDetailLikeBtn.isChecked

                if(checked){
                    sharedViewModel.likePost(args.post._id)
                }else{
                    sharedViewModel.unlikePost(args.post._id)
                }
            }
        }

        binding.PostDetailFollowBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
               sharedViewModel.addFollower(viewModel.userUpdated.value!!._id)
            }
        }

        binding.PostDetailSearchLocation.setOnClickListener {
            val label = "${args.post.latitude}, ${args.post.longitude}"
            val gmmIntentUri = Uri.parse("geo:${args.post.latitude},${args.post.longitude}?q=${args.post.latitude},${args.post.longitude}($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.PostDetailUserAvatar.setOnClickListener {
            val action = PostDetailFragmentDirections.actionPostDetailFragmentToUserDetailFragment(viewModel.userUpdated.value!!)
            findNavController().navigate(action)
        }

    }


}