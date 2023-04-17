package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationBarItemView
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.databinding.FragmentHomeBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viemodel.HomeViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.ext.profileAsBsonDocument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding : FragmentHomeBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : HomeViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(app.currentUser!=null){
            (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
            viewModel.getPosts()
        }else{
            val directions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            findNavController().navigate(directions)
        }

        binding.navMenuBtn.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }


//        GlobalScope.launch {
//            val x  = RealmRepository.getMyUser()
//            println(x?.username)
//            println()
//        }


    }


}