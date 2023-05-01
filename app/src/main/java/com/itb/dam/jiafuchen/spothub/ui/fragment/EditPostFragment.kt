package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.google.android.gms.maps.model.LatLng
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentEditPostBinding
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.EditPostViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class EditPostFragment : Fragment(R.layout.fragment_edit_post) {

    lateinit var binding : FragmentEditPostBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel : EditPostViewModel by activityViewModels()
    private val args : EditPostFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            viewModel.clearAll()
            findNavController().popBackStack()
        }

        if(args.editPostArgs?.post != null) {
            viewModel.currentPost = args.editPostArgs?.post!!
        }

        if(viewModel.currentPost == null){
            Utils.makeSimpleAlert(requireContext(), "Post does not exist")
            findNavController().popBackStack()
        }

        if(args.editPostArgs?.image != null){
            viewModel.image = args.editPostArgs?.image!!
        }

        if(args.editPostArgs?.location != null){
            viewModel.location = args.editPostArgs?.location!!
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let {
                    binding.editPostImage.setImageURI(it)
                    viewModel.image = it
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)

        viewModel.onUpdated.observe(viewLifecycleOwner){
            if(it != null){
                Utils.makeToast(requireContext(), "Post updated successfully!")
                findNavController().popBackStack()
                viewModel.onUpdated.postValue(null)
            }
        }

        viewModel.onDeleted.observe(viewLifecycleOwner){
            if(it){
                Utils.makeToast(requireContext(), "Post deleted successfully!")
                findNavController().popBackStack()
                viewModel.onDeleted.postValue(false)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            if(it != null){
                Utils.makeSimpleAlert(requireContext(), it)
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
            viewModel.clearAll()
            findNavController().popBackStack()
        }

        binding.editPostMapBtn.setOnClickListener {
            val action = EditPostFragmentDirections.actionEditPostFragmentToMapLocatingFragment()
            navController.navigate(action)
        }

        binding.editPostConfirmBtn.setOnClickListener {
            checkAndSavePost()
        }

        binding.editPostDeleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deletePost()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        val popupMenu = PopupMenu(requireContext(), binding.editPostImage)
        popupMenu.menuInflater.inflate(R.menu.add_post_menu, popupMenu.menu)

        binding.editPostImage.setOnClickListener {
            popupMenu.show()
        }

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add_post_nav_takePhoto -> {
                    saveCurrentData()
                    val direction = EditPostFragmentDirections.actionEditPostFragmentToCameraFragment()
                    findNavController().navigate(direction)
                    true
                }
                R.id.add_post_nav_openGallery -> {
                    pickImageFromGallery()
                    true
                }
                else -> false
            }
        }

    }

    override fun onStop() {
        Utils.hideSoftKeyboard(requireActivity())
        super.onStop()
    }

    private fun render(){

        if(viewModel.title != null){
            binding.editPostTitle.setText(viewModel.title)
        }else{
            binding.editPostTitle.setText(viewModel.currentPost!!.title)
        }

        if(viewModel.description != null){
            binding.editPostDesc.setText(viewModel.description)
        }else{
            binding.editPostDesc.setText(viewModel.currentPost!!.description)
        }

        if(viewModel.location != null){
            binding.editPostLatitude.setText(viewModel.location!!.latitude.toString())
            binding.editPostLongitude.setText(viewModel.location!!.longitude.toString())
        }else{
            binding.editPostLatitude.setText(viewModel.currentPost!!.latitude.toString())
            binding.editPostLongitude.setText(viewModel.currentPost!!.longitude.toString())
        }

        if(viewModel.image != null){
            binding.editPostImage.setImageURI(viewModel.image)
        }else{
            binding.editPostImage.setImageBitmap(Utils.byteArrayToImage(viewModel.currentPost!!.image))
        }

    }

    private fun checkAndSavePost(){

        if(binding.editPostTitle.text.toString().isEmpty()){
            Utils.makeSimpleAlert(requireContext(), "Please fill all the fields")
            return
        }

        if(binding.editPostImage.drawable == null){
            Utils.makeSimpleAlert(requireContext(), "Please add an image")
            return
        }

        try {
            val lat = binding.editPostLatitude.text.toString().toDouble()
            val long = binding.editPostLongitude.text.toString().toDouble()
            if(lat == 0.0 && long == 0.0){
                Utils.makeSimpleAlert(requireContext(), "Please add a location")
                return
            }
        }catch (e: Exception) {
            Utils.makeSimpleAlert(requireContext(), "Location is not valid")
            return
        }

        val updatedPost = Post().apply {
            title = binding.editPostTitle.text.toString()
            description = binding.editPostDesc.text.toString()
            if(viewModel.image != null){
                val byteArray = Utils.uriToByteArray(requireContext(), viewModel.image!!)
                image = byteArray
            }
            latitude = binding.editPostLatitude.text.toString().toDouble()
            longitude = binding.editPostLongitude.text.toString().toDouble()
        }

        viewModel.updatePost(viewModel.currentPost!!._id, updatedPost)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun saveCurrentData(){
        viewModel.title = binding.editPostTitle.text.toString()
        viewModel.description = binding.editPostDesc.text.toString()
        viewModel.location = LatLng(
            binding.editPostLatitude.text.toString().toDouble(),
            binding.editPostLongitude.text.toString().toDouble()
        )
    }

}