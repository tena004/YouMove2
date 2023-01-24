package com.example.youmove2

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback, SensorEventListener, /*EasyPermissions.PermissionCallbacks,*/ TextToSpeech.OnInitListener{
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var initial_zoom = 14f
    private var tts: TextToSpeech? = null
    private val GEOFENCE_RADIUS = 1000f
    private val GEOFENCE_ID = 101
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var home : LatLng
    private lateinit var goal : LatLng

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_info -> {
                val iInformation = Intent(this, UserProfileActivity::class.java)
                startActivity(iInformation)
                true
            }
            R.id.action_settings -> {
                val iSettings = Intent(this, SettingsActivity::class.java)
                startActivity(iSettings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        val backgorund : Drawable?

        backgorund = ContextCompat.getDrawable(this, R.drawable.home_bg)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        setUpSensor()

        super.onCreate(savedInstanceState)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this);

        tts = TextToSpeech(this,this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
           .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation();
        home = LatLng(0.0, 0.0)
        //var bm : Bitmap = decodeResource(this.getResources(), R.drawable.finish)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? -> // Got last known location. In some rare situations this can be null.
                home = if (location != null) {
                    // Pan the camera to location
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(43.640278, 19.108334)
                }
                mMap.addMarker(
                    MarkerOptions()
                        .position(home)
                        .draggable(true)

                )

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, initial_zoom))
                goal = getRandomMarker(home, GEOFENCE_RADIUS)
                mMap.addMarker(
                    MarkerOptions()
                        .position(goal)
                       // .icon(BitmapDescriptorFactory.fromBitmap(bm))
                )
                }

        addGeofence(home, GEOFENCE_RADIUS);

        mMap.isMyLocationEnabled = true // Show current location

        //val url: String = getMapsApiDirectionsUrl(home, goal)

    }

    fun getRandomMarker(point: LatLng, radius: Float): LatLng {
        val randomPoints: MutableList<LatLng> = ArrayList()
        val randomDistances: MutableList<Float> = ArrayList()
        val myLocation = Location("")
        myLocation.latitude = point.latitude
        myLocation.longitude = point.longitude

        //This is to generate 10 random points
        for (i in 0..9) {
            val x0 = point.latitude
            val y0 = point.longitude
            val random = Random()

            // Convert radius from meters to degrees
            val radiusInDegrees = (radius / 111000f).toDouble()
            val u = random.nextDouble()
            val v = random.nextDouble()
            val w = radiusInDegrees * Math.sqrt(u)
            val t = 2 * Math.PI * v
            val x = w * Math.cos(t)
            val y = w * Math.sin(t)

            // Adjust the x-coordinate for the shrinking of the east-west distances
            val new_x = x / Math.cos(y0)
            val foundLatitude = new_x + x0
            val foundLongitude = y + y0
            val randomLatLng = LatLng(foundLatitude, foundLongitude)
            randomPoints.add(randomLatLng)
            val l1 = Location("")
            l1.latitude = randomLatLng.latitude
            l1.longitude = randomLatLng.longitude
            randomDistances.add(l1.distanceTo(myLocation))
        }
        //Get nearest point to the centre
        val indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances))
        return randomPoints[indexOfNearestPointToCentre]
    }

    private fun enableMyLocation() {
        requestPermissionsIfNecessary(
            arrayOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            val REQUEST_PERMISSIONS_REQUEST_CODE = 1
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(latLng : LatLng, radius: Float) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            attr.radius.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getThatPendingIntent()

        geofencingClient.addGeofences(geofencingRequest!!, pendingIntent!!)
            .addOnSuccessListener { Log.d(TAG, "onSuccess: Geofence Added...") }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }
    }

    private fun getMapsApiDirectionsUrl(origin: LatLng, dest: LatLng): String {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"

        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor"

        // Output format
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

   /*

    private fun decodePoly(encoded: String): List<LatLng>? {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }*/

    // Akcelerometar

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL)
        }


    }
    override fun onSensorChanged(event: SensorEvent?) {
        /*if (event != null) {
            Log.d(
                "acc", "x = ${event.values[0]}\n\n" +
                        "y = ${event.values[1]}\n\n" +
                        "z = ${event.values[2]}\n\n"
            )
        }*/
    }

    // Akcelerometar
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}