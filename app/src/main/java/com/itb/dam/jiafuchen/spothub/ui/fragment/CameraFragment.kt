package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentAddPostBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentCameraBinding
import com.itb.dam.jiafuchen.spothub.databinding.FragmentLoginBinding
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viemodel.CameraViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    lateinit var binding : FragmentCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    val viewModel by viewModels<CameraViewModel>()

    private val navigation: NavController by lazy {
        findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputDirectory = Utils.getOutputDirectory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                Utils.makeSimpleAlert(requireContext(), "Camera permission is needed to take photos")
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.PostCameraTakePhotoBtn.setOnClickListener {
            takePhoto()

        }

        binding.PostCameraRefreshBtn.setOnClickListener {
            viewModel.reset()
        }

        viewModel.tookPhoto.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.PostCameraResultImage.setImageURI(it)
                binding.PostCameraRefreshBtn.visibility = View.VISIBLE
                binding.PostCameraTakePhotoBtn.visibility = View.GONE
                binding.PostCameraConfirmBtn.visibility = View.VISIBLE
            }else{
                binding.PostCameraResultImage.setImageURI(null)
                binding.PostCameraRefreshBtn.visibility = View.GONE
                binding.PostCameraTakePhotoBtn.visibility = View.VISIBLE
                binding.PostCameraConfirmBtn.visibility = View.GONE
            }
        }

        binding.PostCameraConfirmBtn.setOnClickListener {

            val action = CameraFragmentDirections.actionCameraFragmentToAddPostFragment(viewModel.tookPhoto.value.toString())
            findNavController().navigate(action, NavOptions.Builder().setPopUpTo(R.id.addPostFragment, true).build())

            //val bundle = Bundle()
            //bundle.putString("image", viewModel.tookPhoto.value.toString())
            //parentFragmentManager.setFragmentResult("image", bundle)
            //navController.popBackStack()

            //findNavController().previousBackStackEntry?.savedStateHandle?.set("image", "JIAFU")
            //navigation.popBackStack()
        }

    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Utils.makeSimpleAlert(requireContext(), "Camera permission is needed to take photos")
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            }catch (exc: Exception) {
                Utils.makeSimpleAlert(requireContext(), "Start camera failed")
            }

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(requireContext()))


    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Utils.makeSimpleAlert(requireContext(), "Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Utils.makeToast(requireContext(), msg)

                    viewModel.takePhoto(savedUri)
                }
            }
        )
    }



}