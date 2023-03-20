package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentLoginBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.util.Utils
import com.itb.dam.jiafuchen.spothub.ui.viemodel.HomeViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.LoginViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel


class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var binding : FragmentLoginBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setBottomNavigationVisibility(false)

        binding.signIn.setOnClickListener {
            try {
                viewModel.login(binding.email.text.toString(), binding.passwd.text.toString())
            }catch (e: Exception){
                Utils.makeSimpleAlert(this.requireContext(), e.message.toString())
            }
        }

        binding.clickHere.setOnClickListener{
            val direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(direction)
        }

        viewModel.loggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            if(loggedIn){
                val direction = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(direction)
            }
        })

        viewModel.errorMsg.observe(viewLifecycleOwner, Observer { msg ->
            Utils.makeSimpleAlert(this.requireContext(), msg)
        })

    }




}