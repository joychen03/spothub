package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.ViewPagerAdapter
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SearchViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var binding : FragmentSearchBinding
    private val viewModel : SearchViewModel by activityViewModels()
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
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initTabLayout()
        configSwipeLayout()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setup()

        binding.searchField.setQuery(viewModel.searchText, false)

        binding.searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //remove search view focus
                Utils.hideSoftKeyboard(requireActivity())
                binding.focusableLayout.requestFocus()

                viewModel.search(query.toString())

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()){
                    viewModel.search("")
                }
                return true
            }
        })

        //callback viewpager2
        binding.searchPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.searchTabs.selectTab(binding.searchTabs.getTabAt(position))
            }
        })

        viewModel.userClick.observe(viewLifecycleOwner) {
            if (it != null) {
                val action = SearchFragmentDirections.actionSearchFragmentToUserDetailFragment(it)
                findNavController().navigate(action)
                viewModel.userClick.value = null
            }
        }

        viewModel.postClick.observe(viewLifecycleOwner) {
            if (it != null) {
                val action = SearchFragmentDirections.actionSearchFragmentToPostDetailFragment(it)
                findNavController().navigate(action)
                viewModel.postClick.value = null
            }
        }
    }

    private fun initTabLayout() {

        val fragmentList = listOf(
            SearchPostsFragment(),
            SearchUsersFragment()
        )

        binding.searchTabs.addTab(binding.searchTabs.newTab().setText("Posts"))
        binding.searchTabs.addTab(binding.searchTabs.newTab().setText("Users"))

        binding.searchPager.adapter = ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        binding.searchPager.isSaveEnabled = false

        binding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.searchPager.currentItem = tab.position
                viewModel.selectedTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        binding.searchPager.currentItem = viewModel.selectedTab
        binding.searchTabs.selectTab(binding.searchTabs.getTabAt(viewModel.selectedTab))

    }


    override fun onDestroyView() {
        super.onDestroyView()
        Utils.hideSoftKeyboard(requireActivity())
        binding.searchPager.adapter = null
    }

    private fun configSwipeLayout(){

        binding.SearchSwipeRefresh.setColorSchemeColors(requireActivity().getColor(R.color.primary_btn))

        binding.SearchSwipeRefresh.setOnRefreshListener {
            binding.SearchSwipeRefresh.isRefreshing = false
            (requireActivity() as MainActivity).navController.navigate(R.id.searchFragment)
        }
    }

}