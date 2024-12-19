package com.dicoding.learn.ui.activity

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.learn.R
import com.dicoding.learn.databinding.ActivityMapsBinding
import com.dicoding.learn.helpers.vectorToBitmap
import com.dicoding.learn.ui.viewmodels.MapsViewModel
import com.dicoding.learn.ui.viewmodels.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.learn.helpers.Result
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted : Boolean ->
        if(isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if(ContextCompat.checkSelfPermission(
            this.applicationContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupData()

        supportActionBar?.title = getString(R.string.story_location)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private fun setupData() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is Result.Error -> {
                    Log.d(TAG, result.error)
                }
                is Result.Success -> {
                    val data = result.data.listStory
                    val boundsBuilder = LatLngBounds.Builder()
                    data.forEach { res ->
                        val latLng = LatLng(res.lat!!, res.lon!!)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(res.name)
                                .snippet(res.description)
                                .icon(vectorToBitmap(R.drawable.ic_person_location, Color.parseColor("#6DC5FF"), this@MapsActivity))
                        )
                        boundsBuilder.include(latLng)
                    }
                    val latestLocation = data.first()
                    val setInitialMarker = LatLng(latestLocation.lat!!, latestLocation.lon!!)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(setInitialMarker, 12f))

                    Log.d(TAG, data.toString())
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.map_style))
            if(!success) {
                Log.e(TAG, "Style Parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error : $e")
        }
    }

    companion object {
        const val TAG = "MapsActivity"
    }
}