package tech.hyperjump.runningapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*

const val REQUEST_CODE_LOCATION_PERMISSION = 0
const val POLYLINE_WIDTH = 8f
const val MAP_ZOOM = 14f

const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var googleMap: GoogleMap
    private var locationPermissionGranted = false

    private var lastLocation: Location? = null
    private var isTracking = false

    // list of polyline points
    private val pathPoints = mutableListOf<LatLng>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestPermission()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        btnStartDrawing.setOnClickListener {
            if (locationPermissionGranted && !isTracking) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)
                isTracking = true
            }
        }

        btnStopDrawing.setOnClickListener {
            if (isTracking) {
                isTracking = false
                locationManager.removeUpdates(this)
            }
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        if (location != null) {
            val position = LatLng(location.latitude, location.longitude)
            if(pathPoints.isEmpty()) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM))
            }

            pathPoints.add(position)
            googleMap.addPolyline(PolylineOptions()
                .color(Color.RED)
                .width(POLYLINE_WIDTH)
                .addAll(pathPoints))
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onLocationChanged(newLoction: Location?) {
        if(isTracking) {
            addPathPoint(newLoction)
            Log.d(TAG, "Location changed: " +
                "${newLoction?.latitude},${newLoction?.longitude}")
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, "Status change: $status, $provider")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.d(TAG, "Provider enabled: $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d(TAG, "Provider disableed: $provider")
    }
}
