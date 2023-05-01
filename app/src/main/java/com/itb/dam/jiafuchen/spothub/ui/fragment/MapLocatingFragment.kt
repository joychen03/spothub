package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentMapLocatingBinding
import com.itb.dam.jiafuchen.spothub.domain.model.AddEditPostArgs
import com.itb.dam.jiafuchen.spothub.ui.activity.MainActivity
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.MapLocatingViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils


class MapLocatingFragment :
    Fragment(R.layout.fragment_map_locating),
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback
{

    lateinit var binding : FragmentMapLocatingBinding
    private lateinit var map: GoogleMap
    private val viewModel : MapLocatingViewModel by activityViewModels()
    private var marker : Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapLocatingBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        val navController = view.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        initSearchBar()

        binding.mapLocateConfirm.setOnClickListener {

            //check last backstack fragment
            val previousFragment =  findNavController().previousBackStackEntry?.destination?.label

            when(previousFragment){
                "fragment_add_post" -> {
                    val directions = MapLocatingFragmentDirections.actionMapLocatingFragmentToAddPostFragment(AddEditPostArgs(location = viewModel.lastMarker.value?.position))
                    findNavController().navigate(directions, NavOptions.Builder().setPopUpTo(R.id.addPostFragment, true).build())
                }
                "fragment_edit_post" -> {
                    val directions = MapLocatingFragmentDirections.actionMapLocatingFragmentToEditPostFragment(AddEditPostArgs(location = viewModel.lastMarker.value?.position))
                    findNavController().navigate(directions, NavOptions.Builder().setPopUpTo(R.id.editPostFragment, true).build())
                }
                else -> throw Exception("No previous fragment")
            }
        }


         viewModel.lastMarker.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.mapLocateConfirm.visibility = View.VISIBLE
            }else{
                binding.mapLocateConfirm.visibility = View.GONE
            }
        }


    }



    override fun onMapClick(p0: LatLng) {
        map.clear()
        val marker = MarkerOptions().position(p0)
        map.addMarker(marker)
        viewModel.setMarker(marker)
    }

    override fun onMapLongClick(p0: LatLng) {

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        map.clear()
        viewModel.setMarker(null)
        return true
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnMarkerClickListener(this)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style_dark
                )
            )
        } else {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                map.isMyLocationEnabled = true

                if(viewModel.lastMarker.value != null){
                    map.addMarker(viewModel.lastMarker.value!!)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.lastMarker.value!!.position, 15f))
                }else{
                    val location = getLastKnownLocation()
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }

            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Utils.makeSimpleAlert(requireContext(), "Location permission is needed to show your location on the map")
            }
            else -> {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if(viewModel.lastMarker.value != null){
            map.addMarker(viewModel.lastMarker.value!!)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.lastMarker.value!!.position, 15f))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style_dark
                )
            )
        } else {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
        }
    }

    private fun initSearchBar() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.locateMapSearcher) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                val options = MarkerOptions().position(latLng!!)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                map.addMarker(options)

                viewModel.setMarker(options)
            }

            override fun onError(status: Status) {
                Log.v("MapFragment", "An error occurred: $status")
            }
        })

    }

    @SuppressLint("MissingPermission")
    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            map.isMyLocationEnabled = true
            findNavController().navigate(R.id.toMap)
        } else {
            Utils.makeSimpleAlert(requireContext(), "Location permission is needed to show your location on the map")
        }
    }

    private fun getLastKnownLocation(): Location? {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            return bestLocation

        }
        return null
    }



}