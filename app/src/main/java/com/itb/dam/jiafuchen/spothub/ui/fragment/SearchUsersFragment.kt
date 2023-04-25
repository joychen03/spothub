package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentSearchUsersBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SearchViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchUsersFragment : Fragment(R.layout.fragment_search_users) {

    lateinit var binding : FragmentSearchUsersBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

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


    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun onUserClickListener(user : User){
        viewModel.goToUser(user)
    }

    private fun onFollowClickListener(user : User){
        CoroutineScope(Dispatchers.Main).launch {
            sharedViewModel.addFollow(user._id)
        }
    }



    fun getUsersByQuery(query: String){

    }

    fun render() {
        println("caca")
    }


}