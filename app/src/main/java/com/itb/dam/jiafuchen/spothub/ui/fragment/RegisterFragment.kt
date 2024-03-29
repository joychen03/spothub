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
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.databinding.FragmentRegisterBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.RegisterViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.ext.profileAsBsonDocument


class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var binding : FragmentRegisterBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val viewModel : RegisterViewModel by viewModels()

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {

            binding.confirmPasswd.requestFocus()
            Utils.hideSoftKeyboard(requireActivity())

            try {
                if(binding.passwd.text.toString() != binding.confirmPasswd.text.toString()){
                    throw Exception("Passwords don't match")
                }

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
            if(loggedIn && app.currentUser != null){

                val defaultAvatar = Utils.getByteArrayFromDrawable(requireContext(), R.drawable.user_default_avatar)

                val user = User().apply {
                    this.owner_id = app.currentUser!!.id
                    this.email = app.currentUser?.profileAsBsonDocument()?.get("email")
                        ?.asString()?.value.toString()
                    this.username = "Anonymous"
                    this.description = "No description"
                    this.avatar = defaultAvatar
                    this.followers = realmListOf()
                    this.followings = realmListOf()
                }

                viewModel.createAppUser(user)

            }else{
                Utils.makeSimpleAlert(this.requireContext(), "Error al iniciar sesión")
            }
        })


        viewModel.appUserCreated.observe(viewLifecycleOwner, Observer { user ->
            if(user != null){
                sharedViewModel.getCurrentUser()
                (requireActivity() as MainActivity).binding.bottomNav.menu.getItem(0).isChecked = true
                val direction = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(direction)
                requireActivity().recreate()
            }else{
                Utils.makeSimpleAlert(this.requireContext(), "Error al crear usuario de aplicacion")
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