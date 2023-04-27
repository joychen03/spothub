package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.FragmentHomeBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.PostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.HomeViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding : FragmentHomeBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : HomeViewModel by activityViewModels()
    lateinit var rv : RecyclerView
    lateinit var rvAdapter : PostListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(app.currentUser == null){
            val directions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(directions)
            return
        }
        sharedViewModel.getCurrentUser()

        requireActivity().onBackPressedDispatcher.addCallback(this){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }

        viewModel.setup(sharedViewModel.homeShouldUpdate)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        if(sharedViewModel.homeShouldUpdate){
            (requireActivity() as MainActivity).homeNavRefreshAnimationStart()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.focusableLayout.requestFocus()
        (requireActivity() as MainActivity).setBottomNavigationVisibility(true)

        binding.navMenuBtn.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        rv = binding.HomePostsRV

        initPostRecyclerView()
        configSwipeLayout()

        binding.HomePostsRV.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val indexItemRV = (rv.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()!!
                val v = (rv.layoutManager as? LinearLayoutManager)?.getChildAt(0)
                val topViewRV = if(v == null) 0 else v.top - (rv.layoutManager as? LinearLayoutManager)?.paddingTop!!

                viewModel.postListScrollTo(indexItemRV, topViewRV)

            }
        })

        viewModel.postUpdated.observe(viewLifecycleOwner){ positions ->
            if(positions.isNotEmpty()){
                positions.forEach {
                    rvAdapter.notifyItemChanged(it)
                }
                viewModel.postUpdated.postValue(listOf())
            }
        }

        viewModel.postAdded.observe(viewLifecycleOwner){
            sharedViewModel.newPostAdded(it)
        }

        sharedViewModel.homeShouldUpdate = false
    }

    private fun onPostClickListener(post : Post){
        val directions = HomeFragmentDirections.actionHomeFragmentToPostDetailFragment(post)
        findNavController().navigate(directions)
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
            val userPosition = viewModel.userList.indexOfFirst { it._id == user._id }

            if (userUpdated == null || userPosition == -1 ) {
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

    private fun scrollToPosition(position : Int, offset : Int = 0){
        rv.stopScroll()
        (rv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position,offset)
    }

    private fun initPostRecyclerView(){

        rvAdapter = PostListAdapter(
            viewModel.currentUser!!,
            viewModel.postList,
            viewModel.userList,
            ::onPostClickListener,
            ::onPostFollowClickListener,
            ::onPostLikeClickListener,
            ::onPostSearchLocationClickListener,
            ::onPostOwnerClickListener
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = rvAdapter

        if(!sharedViewModel.homeShouldUpdate){
            scrollToPosition(viewModel.scrollPosition, viewModel.scrollOffset)
        }
    }

    private fun configSwipeLayout(){

        binding.HomeSwipeRefresh.setColorSchemeColors(requireActivity().getColor(R.color.primary_btn))

        binding.HomeSwipeRefresh.setOnRefreshListener {
            sharedViewModel.homeShouldUpdate = true
            initPostRecyclerView()
            binding.HomeSwipeRefresh.isRefreshing = false
            sharedViewModel.homeScreenUpdated()
        }
    }



}