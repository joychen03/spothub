package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.ViewPagerAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.ProfileViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale.Category


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



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initTabLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.searchTabs.selectTab(binding.searchTabs.getTabAt(position))

            }
        })

        binding.navMenuBtn.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        binding.ProfileEditBtn.setOnClickListener {
            val directions = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment()
            findNavController().navigate(directions)
        }

        sharedViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.ProfileUserName.text = user.username
                binding.ProfileEmail.text = user.email
                binding.ProfileAvatar.setImageBitmap(Utils.byteArrayToImage(user.avatar))
                binding.ProfileDescription.text = user.description
                binding.ProfileFollowers.text = user.followers.count().toString()
                binding.ProfileFollowings.text = user.followings.count().toString()
            }
        }

    }

    private fun initTabLayout() {

        val fragmentList = arrayListOf(
            ProfilePostsFragment(),
            ProfileFavouritesFragment()
        )

        binding.searchTabs.addTab(binding.searchTabs.newTab().setText("Posts"))
        binding.searchTabs.addTab(binding.searchTabs.newTab().setText("Favourites"))

        binding.searchPager.adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)

        binding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.searchPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

    }
}