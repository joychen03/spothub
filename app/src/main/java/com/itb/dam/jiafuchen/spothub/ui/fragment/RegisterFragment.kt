package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentRegisterBinding
import com.itb.dam.jiafuchen.spothub.utils.Utils
import com.itb.dam.jiafuchen.spothub.ui.viemodel.RegisterViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel


class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var binding : FragmentRegisterBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {
            try {
                viewModel.register(binding.email.text.toString(), binding.passwd.text.toString())
            }catch (e: Exception){
                Utils.makeSimpleAlert(this.requireContext(), e.message.toString())
            }
        }

        binding.clickHere.setOnClickListener{
            val direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(direction)
        }

        viewModel.loggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            if(loggedIn){
                val direction = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(direction)
            }
        })

        viewModel.errorMsg.observe(viewLifecycleOwner, Observer { msg ->
            Utils.makeSimpleAlert(this.requireContext(), msg)
        })
    }


}