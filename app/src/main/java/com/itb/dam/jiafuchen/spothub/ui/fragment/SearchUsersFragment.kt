package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.adapter.UserListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SearchViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchUsersFragment : Fragment(R.layout.fragment_search_users) {

    lateinit var binding : FragmentSearchUsersBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    private lateinit var rv : RecyclerView
    private lateinit var rvAdapter : UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = binding.searchUsersRecyclerView

        render()

        viewModel.userChanged.observe(viewLifecycleOwner) {
            rvAdapter.notifyItemChanged(it)
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

    private fun onUserClickListener(i : Int){
        val clickedUser = viewModel.users[i]

        if(clickedUser._id == viewModel.currentUser?._id){
            (requireActivity() as MainActivity).binding.bottomNav.selectedItemId = R.id.nav_profile
        }else{
            viewModel.goToUser(clickedUser)
        }
    }

    private fun onFollowClickListener(i : Int){
        CoroutineScope(Dispatchers.Main).launch {
            sharedViewModel.addFollower(viewModel.users[i]._id)
        }
    }

    private fun render(){
        rvAdapter = UserListAdapter(
            viewModel.currentUser!!,
            viewModel.users,
            ::onUserClickListener,
            ::onFollowClickListener
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.itemAnimator = null
        rv.layoutAnimation = null
        rv.adapter = rvAdapter
    }

}