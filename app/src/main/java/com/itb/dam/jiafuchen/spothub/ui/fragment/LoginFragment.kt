package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentLoginBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.LoginViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var binding : FragmentLoginBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : LoginViewModel by viewModels()

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setBottomNavigationVisibility(false)

        binding.signIn.setOnClickListener {

            binding.passwd.requestFocus()
            Utils.hideSoftKeyboard(requireActivity())

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
                sharedViewModel.getCurrentUser()
                (requireActivity() as MainActivity).binding.bottomNav.menu.getItem(0).isChecked = true
                val direction = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(direction)
                requireActivity().recreate()
            }
        })

        viewModel.errorMsg.observe(viewLifecycleOwner, Observer { msg ->
            Utils.makeSimpleAlert(this.requireContext(), msg)
        })

    }

    override fun onDestroy() {
        Utils.hideSoftKeyboard(requireActivity())
        super.onDestroy()
    }

}