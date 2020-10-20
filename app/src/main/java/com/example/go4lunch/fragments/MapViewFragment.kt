package com.example.go4lunch.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.go4lunch.R
import com.example.go4lunch.models.PlaceDetails
import com.example.go4lunch.models.Places
import com.example.go4lunch.utils.APICalls
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.PreferenceKeys
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapViewFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, APICalls.ICallBacks {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mContext: Context
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLastLocation: Location
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private var mLocationUpdateState = false
    private lateinit var mPlaces: Places

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)
        mContext = view.context

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                mLastLocation = p0.lastLocation
            }
        }

        createLocationRequest()

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style))

        tryAccessLocation()
        zoomToCurrentLocation()
    }

    override fun onMarkerClick(p0: Marker?) : Boolean {
        if (p0 != null) {
            if (p0.tag != null) {
                val position = p0.tag.toString().toInt()

                val bottomSheet = RestaurantBottomSheetFragment(mPlaces.results[position])

                if (activity != null) {
                    bottomSheet.show(activity!!.supportFragmentManager, "restaurantBottomSheet")
                }
            }
        }

        return false
    }

    private fun tryAccessLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun zoomToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        mMap.isMyLocationEnabled = true

        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mLastLocation = location

                val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
                if (preferences != null) {
                    with (preferences.edit()) {
                        putFloat(PreferenceKeys.PREF_KEY_LAT, location.latitude.toFloat())
                        putFloat(PreferenceKeys.PREF_KEY_LNG, location.longitude.toFloat())
                        commit()
                    }
                }

                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                getRestaurants()
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as AppCompatActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)

        val client = LocationServices.getSettingsClient(activity as AppCompatActivity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            mLocationUpdateState = true
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied
                // Show the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    e.startResolutionForResult(activity as AppCompatActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun getRestaurants() {
        APICalls.fetchPlaces(this, mLastLocation, 1500, "restaurant", "restaurant", APIConstants.API_KEY)
    }

    private fun placeMarker(location: LatLng, position: Int) {
        val marker = mMap.addMarker(MarkerOptions()
            .position(location)
            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.map_restaurant_pin))))

        marker.tag = position
    }

    override fun onResponse(places: Places?) {
        if (places != null) {

            mPlaces = places

            for ((index, place) in places.results.withIndex()) {
                placeMarker(LatLng(place.geometry.location.lat, place.geometry.location.lng), index)
            }
        }
    }

    override fun onResponse(place: PlaceDetails?) {

    }

    override fun onFailure() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                mLocationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!mLocationUpdateState) {
            startLocationUpdates()
        }
    }
}