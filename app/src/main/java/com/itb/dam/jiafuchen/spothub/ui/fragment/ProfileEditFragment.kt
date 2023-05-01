package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentProfileEditBinding
import com.itb.dam.jiafuchen.spothub.domain.model.User
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.ProfileEditViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    lateinit var binding : FragmentProfileEditBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel : ProfileEditViewModel by activityViewModels()
    private val args : ProfileEditFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            viewModel.goBack()
            findNavController().popBackStack()
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let {
                    binding.ProfileEditAvatar.setImageURI(it)
                    viewModel.avatar = it
                }
            }
        }

        if(args.profileEditArgs?.image != null){
            viewModel.avatar = args.profileEditArgs?.image
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        viewModel.onUpdated.observe(viewLifecycleOwner){
            if(it != null){
                Utils.makeToast(requireContext(), "Profile updated")
                findNavController().popBackStack()
                viewModel.onUpdated.postValue(null)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            if(it != null){
                Utils.makeSimpleAlert(requireContext(), "Error updating profile: \n\n $it")
                viewModel.errorMessage.postValue(null)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        render()

        binding.toolbar.setNavigationOnClickListener {
            viewModel.goBack()
            navController.popBackStack()
        }

        binding.profileEditConfirmBtn.setOnClickListener {

            val newUser = User().apply {
                username = binding.profileEditUsername.text.toString()
                description = binding.profileEditDesc.text.toString()
            }

            if(newUser.username.isEmpty()){
                Utils.makeSimpleAlert(requireContext(), "Username cannot be empty")
                return@setOnClickListener
            }

            if(viewModel.avatar != null){
                val byteArray = Utils.uriToByteArray(requireContext(), viewModel.avatar!!)
                newUser.avatar = byteArray
            }

            viewModel.updateUser(newUser)

        }

        binding.ProfileEditOpenGallery.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ProfileEditTakePhotoBtn.setOnClickListener {
            saveCurrentData()
            val directions = ProfileEditFragmentDirections.actionProfileEditFragmentToCameraFragment()
            navController.navigate(directions)
        }

    }

    override fun onStop() {
        Utils.hideSoftKeyboard(requireActivity())
        super.onStop()
    }

    private fun saveCurrentData(){
        viewModel.username = binding.profileEditUsername.text.toString()
        viewModel.description = binding.profileEditDesc.text.toString()
    }

    private fun render(){
        if(viewModel.username != null){
            binding.profileEditUsername.setText(viewModel.username)
        }else{
            binding.profileEditUsername.setText(viewModel.currentUser?.username)
        }

        if(viewModel.description != null){
            binding.profileEditDesc.setText(viewModel.description)
        }else{
            binding.profileEditDesc.setText(viewModel.currentUser?.description)
        }

        if(viewModel.avatar != null){
            binding.ProfileEditAvatar.setImageURI(viewModel.avatar)
        }else{
            if(viewModel.currentUser?.avatar != null){
                binding.ProfileEditAvatar.setImageBitmap(Utils.byteArrayToImage(viewModel.currentUser!!.avatar))
            }else{
                binding.ProfileEditAvatar.setImageResource(R.drawable.user_default_avatar)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }


}