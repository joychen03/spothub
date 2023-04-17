package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.AuthRepository
import com.itb.dam.jiafuchen.spothub.data.mongodb.RealmRepository
import com.itb.dam.jiafuchen.spothub.databinding.FragmentRegisterBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.utils.Utils
import com.itb.dam.jiafuchen.spothub.ui.viemodel.RegisterViewModel
import com.itb.dam.jiafuchen.spothub.ui.viemodel.SharedViewModel
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.ext.profileAsBsonDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


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
            if(loggedIn && app.currentUser != null){

                CoroutineScope(Dispatchers.IO).launch {
                    val defaultAvatar = Utils.getByteArrayFromDrawable(requireContext(), R.drawable.c13)

                    val user = User().apply {
                        this.owner_id = app.currentUser!!.id
                        this.email = app.currentUser?.profileAsBsonDocument()?.get("email")
                            ?.asString()?.value.toString()
                        this.username = Utils.getRandomUsername()
                        this.description = "No description"
                        this.avatar = defaultAvatar
                        this.followers = realmListOf()
                        this.followings = realmListOf()
                    }

                    viewModel.createAppUser(user)
                }

            }else{
                Utils.makeSimpleAlert(this.requireContext(), "Error al iniciar sesión")
            }
        })

//        sharedViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
//            if(user != null){
//                println("IM TRYING TO GO TO HOME")
//                val direction = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
//                findNavController().navigate(direction)
//            }else{
//                Utils.makeSimpleAlert(this.requireContext(), "Error al iniciar sesión")
//                CoroutineScope(Dispatchers.Main).launch {
//                    runCatching {
//                        app.currentUser?.logOut()
//                    }.onSuccess {
//                        val direction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
//                        findNavController().navigate(direction)
//                    }.onFailure {
//                        it.message?.let { it1 -> Log.v("ERROR", it1) }
//                    }
//                }
//            }
//        })

        viewModel.appUserCreated.observe(viewLifecycleOwner, Observer { user ->
            if(user != null){
                sharedViewModel.getCurrentUser()
                val direction = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(direction)
            }else{
                Utils.makeSimpleAlert(this.requireContext(), "Error al crear usuario de aplicacion")
            }
        })

        viewModel.errorMsg.observe(viewLifecycleOwner, Observer { msg ->
            Utils.makeSimpleAlert(this.requireContext(), msg)
        })
    }




}