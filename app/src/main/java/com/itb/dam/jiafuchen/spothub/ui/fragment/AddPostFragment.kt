package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavBackStackEntry
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viemodel.AddPostViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class AddPostFragment : Fragment(R.layout.fragment_add_post) {

    lateinit var binding : FragmentAddPostBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel by activityViewModels<AddPostViewModel>()
    private val args : AddPostFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        if(!args.image.isNullOrEmpty()){
            viewModel.image = args.image!!.toUri()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.root.setOnClickListener {
            Utils.hideSoftKeyboard(requireActivity())
        }

        val editText = binding.desc
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editText.clearFocus()
                val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                true
            } else {
                false
            }
        }
        val popupMenu = PopupMenu(requireContext(), binding.AddPostImage)
        popupMenu.menuInflater.inflate(R.menu.add_post_menu, popupMenu.menu)

        binding.AddPostImage.setOnClickListener {
            popupMenu.show()
        }


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add_post_nav_takePhoto -> {
                    val direction = AddPostFragmentDirections.actionAddPostFragmentToCameraFragment()
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


        binding.mapButton.setOnClickListener {
            val direction = AddPostFragmentDirections.actionAddPostFragmentToMapLocatingFragment()
            findNavController().navigate(direction)
        }

        return binding.root
    }

    override fun onDestroy() {
        viewModel.title = binding.title.text.toString()
        viewModel.description = binding.desc.text.toString()
        viewModel.location = LatLng(binding.latitude.text.toString().toDouble(), binding.longitude.text.toString().toDouble())
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                imageUri?.let {
                    binding.AddPostImage.setImageURI(it)
                    viewModel.image = it
                }
            }
        }

        setup()

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }


    private fun setup(){
        binding.title.setText(viewModel.title)
        binding.desc.setText(viewModel.description)
        binding.latitude.setText(viewModel.location.latitude.toString())
        binding.longitude.setText(viewModel.location.longitude.toString())
        if (viewModel.image != null && viewModel.image != Uri.EMPTY){
            binding.AddPostImage.setImageURI(viewModel.image)
        }
    }



}

