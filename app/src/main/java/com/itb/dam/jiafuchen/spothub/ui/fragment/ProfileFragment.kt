package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileBinding
import com.itb.dam.jiafuchen.spothub.domain.model.AddEditPostArgs
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.ViewPagerAdapter
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.ProfileViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    lateinit var binding : FragmentProfileBinding
    private val viewModel : ProfileViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }

        viewModel.setup()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initTabLayout()
        configSwipeLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ProfileSearchPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ProfileSearchTabs.selectTab(binding.ProfileSearchTabs.getTabAt(position))

            }
        })

        binding.navMenuBtn.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        binding.ProfileEditBtn.setOnClickListener {
            val directions = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(null)
            findNavController().navigate(directions)
        }

        sharedViewModel.getCurrentUserAsFlow().onEach { user ->
            if(user != null) {
                withContext(Dispatchers.Main){
                    binding.ProfileUserName.text = user.username
                    binding.ProfileEmail.text = user.email
                    binding.ProfileAvatar.setImageBitmap(Utils.byteArrayToImage(user.avatar))
                    binding.ProfileDescription.text = user.description
                    binding.ProfileFollowers.text = user.followers.count().toString()
                    binding.ProfileFollowings.text = user.followings.count().toString()
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))

        viewModel.postClick.observe(viewLifecycleOwner) {
            if (it != null) {
                if(it.owner_id == app.currentUser!!.id){
                    val args = AddEditPostArgs(post = it)
                    val directions = ProfileFragmentDirections.actionProfileFragmentToEditPostFragment(args)
                    findNavController().navigate(directions)
                    viewModel.postClick.value = null
                }else{
                    val directions = ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(it)
                    findNavController().navigate(directions)
                    viewModel.postClick.value = null
                }
            }
        }

        viewModel.userClick.observe(viewLifecycleOwner) {
            if (it != null) {
                val directions = ProfileFragmentDirections.actionProfileFragmentToUserDetailFragment(it)
                findNavController().navigate(directions)
                viewModel.userClick.value = null
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.hideSoftKeyboard(requireActivity())
        binding.ProfileSearchPager.adapter = null
    }

    private fun initTabLayout() {

        val fragmentList = arrayListOf(
            ProfilePostsFragment(),
            ProfileFavouritesFragment()
        )

        binding.ProfileSearchTabs.addTab(binding.ProfileSearchTabs.newTab().setText("Posts"))
        binding.ProfileSearchTabs.addTab(binding.ProfileSearchTabs.newTab().setText("Favourites"))

        binding.ProfileSearchPager.adapter = ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        binding.ProfileSearchTabs.isSaveEnabled = false

        binding.ProfileSearchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.ProfileSearchPager.currentItem = tab.position
                viewModel.selectedTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        binding.ProfileSearchTabs.selectTab(binding.ProfileSearchTabs.getTabAt(viewModel.selectedTab))
    }

    private fun configSwipeLayout(){

        binding.ProfileSwipeRefreshLayout.setColorSchemeColors(requireActivity().getColor(R.color.primary_btn))

        binding.ProfileSwipeRefreshLayout.setOnRefreshListener {
            binding.ProfileSwipeRefreshLayout.isRefreshing = false
            (requireActivity() as MainActivity).navController.navigate(R.id.profileFragment)
        }
    }



}