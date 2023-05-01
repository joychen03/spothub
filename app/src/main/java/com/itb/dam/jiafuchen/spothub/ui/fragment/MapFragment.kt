package com.itb.dam.jiafuchen.spothub.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.itb.dam.jiafuchen.spothub.R
import com.itb.dam.jiafuchen.spothub.databinding.FragmentMapBinding
import com.itb.dam.jiafuchen.spothub.domain.model.AddEditPostArgs
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.ui.adapter.MapPostListAdapter
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.MapViewModel
import com.itb.dam.jiafuchen.spothub.ui.viewmodel.SharedViewModel
import com.itb.dam.jiafuchen.spothub.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class MapFragment :
    Fragment(R.layout.fragment_map),
    GoogleMap.OnMapClickListener,
    OnMapReadyCallback
{

    private lateinit var binding : FragmentMapBinding
    private lateinit var map: GoogleMap
    private lateinit var viewPager : ViewPager2
    private lateinit var adapter : MapPostListAdapter

    private val markers = mutableListOf<Marker>()
    private val viewModel : MapViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }

        viewModel.setup()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        viewPager = binding.postListViewPager
        getPosts()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchBar()
    }

    override fun onMapClick(p0: LatLng) {
        askAddPost(p0)
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

    override fun onPause() {
        childFragmentManager.findFragmentById(R.id.map)?.let {
            childFragmentManager.beginTransaction().remove(it).commit()
        }
        super.onPause()
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.setOnMapClickListener(this)

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

        addMarkers()

        map.setOnMarkerClickListener {
            val post = it.tag as Post?
            if(post != null){
                val index = viewModel.postList.indexOf(post)
                viewPager.setCurrentItem(index, true)
                return@setOnMarkerClickListener true
            }else{
                it.remove()
                askAddPost(it.position)
                return@setOnMarkerClickListener false
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(markers[position].position, 15f))
                markers[position].showInfoWindow()
            }

        })

        if(viewModel.postList.isNotEmpty()){
            moveToCurrentSelectedPost()
        }else{
            val location = getLastKnownLocation()
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

        viewModel.postAdded.observe(viewLifecycleOwner){
            if(it){
                adapter.notifyItemInserted(0)
                addMarkers()
                viewModel.postAdded.value = false
            }
        }

        viewModel.postDeleted.observe(viewLifecycleOwner){
            if(it){
                getPosts()
                addMarkers()
                viewModel.postDeleted.value = false
            }
        }

        viewModel.postUpdated.observe(viewLifecycleOwner){
            addMarkers()
            adapter.notifyItemChanged(it)
        }

    }

    private fun addMarkers() {
        map.clear()
        markers.clear()
        viewModel.postList.forEach {
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(Utils.formatTime(it.createDateTime.epochSeconds * 1000))
            )
            marker?.tag = it

            if(marker != null){
                markers.add(marker)
            }
        }

    }

    private fun getPosts(){
        adapter = MapPostListAdapter(
            viewModel.currentUser!!,
            viewModel.postList,
            this::onPostClick,
            this::onPostLikeClick
        )


        viewPager.adapter = adapter
        viewPager.clipChildren = false
        viewPager.clipToPadding = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer  = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        viewPager.setPageTransformer(compositePageTransformer)

        if(viewModel.postList.isEmpty()){
            binding.postListViewPager.visibility= View.GONE
        }else{
            binding.postListViewPager.visibility= View.VISIBLE
        }

    }

    private fun moveToCurrentSelectedPost(){
        val marker = markers[viewPager.currentItem]
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
        marker.showInfoWindow()
    }

    private fun onPostClick(position : Int){
        val directions = MapFragmentDirections.actionMapFragmentToEditPostFragment(AddEditPostArgs(post = viewModel.postList[position]))
        findNavController().navigate(directions)
    }

    private fun onPostLikeClick(position : Int, checked : Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            val post = if(checked){
                sharedViewModel.likePost(viewModel.postList[position]._id)
            }else{
                sharedViewModel.unlikePost(viewModel.postList[position]._id)
            }

            if(post == null){
                Utils.makeSimpleAlert(requireContext(), "Error liking post")
            }
        }
    }

    private fun initSearchBar() {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.searcher) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                map.addMarker(MarkerOptions().position(latLng!!).title(place.name))

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
            val locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
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

    private fun askAddPost(location: LatLng){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to add a post here? \n\n lat: ${location.latitude} \n long: ${location.longitude}")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val directions = MapFragmentDirections.actionMapFragmentToAddPostFragment(
                    AddEditPostArgs(
                        location = location
                    )
                )
                findNavController().navigate(directions)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }




}