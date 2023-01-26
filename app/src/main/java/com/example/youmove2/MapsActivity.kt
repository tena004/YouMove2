package com.example.youmove2

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.TextView
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
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import java.util.*



@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback, SensorEventListener, TextToSpeech.OnInitListener{

    //BINDING
    private lateinit var binding: ActivityMapsBinding

    //TTS
    private var tts: TextToSpeech? = null

    //BAZA
    private var dbase : Baza = Baza(this)

    // GEOFENCING
    private val GEOFENCE_RADIUS = 1000f
    private val GEOFENCE_ID = 101
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    // MAPA
    private lateinit var mMap: GoogleMap
    private var initial_zoom = 14f
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var goal : Marker? = null

    // STEP COUNTER SESNOR
    private lateinit var sensorManager: SensorManager
    //STEP COUNTER VERIJABLE
    private var totalSteps = 0f

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

        super.onCreate(savedInstanceState)

        popUpWindow()

        // step counter
        //loadData()
        if (ContextCompat.checkSelfPermission(this@MapsActivity, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            val REQUEST_PERMISSIONS_REQUEST_CODE = 2
            ActivityCompat.requestPermissions(this@MapsActivity,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            stepSensor?.let {
                sensorManager.registerListener(this@MapsActivity, it, SensorManager.SENSOR_DELAY_FASTEST)
            }
        }

        //geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this);

        //tts
        tts = TextToSpeech(this,this)

        // binding i view
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.stepCounter.setTextColor(Color.BLACK)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Dohvat podataka iz baze za korake i metre
        //dbase!!.getUserDetails(userData)
        //// previousTotalSteps =

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.startStopBtn.setOnClickListener{
                dbase.updateKoraciPrijedeno(totalSteps, totalSteps * 0.75f)
                totalSteps = 0f
                binding.stepCounter.text = "Broj koraka: " + totalSteps.toString()
                overridePendingTransition(0, 0)
                finish();
                overridePendingTransition(0, 0)
                startActivity(intent);
                overridePendingTransition(0, 0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            var pom = data!!.getBooleanExtra("generate", false)
            dbase.generate(pom)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation();
        var home: LatLng
        home = LatLng(0.0,0.0)
        Toast.makeText(applicationContext, generate.toString(), Toast.LENGTH_LONG).show()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? -> // Got last known location. In some rare situations this can be null.
                home = if (location != null) {
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(43.640278, 19.108334)
                }
                mMap.addMarker(
                    MarkerOptions()
                        .position(home)
                        .draggable(true)
                        .title("Krenite")
                )
                if (generate){
                    val nova = getRandomMarker(home, GEOFENCE_RADIUS)
                    goal = mMap.addMarker(
                        MarkerOptions()
                            .position(nova)
                            .icon(bitmapDescriptorFromVector(applicationContext, R.drawable.finish))
                    )
                }else{
                    mMap.setOnMapLongClickListener {
                        goal = mMap.addMarker(
                            MarkerOptions()
                                .position(it)
                                .icon(
                                    bitmapDescriptorFromVector(
                                        applicationContext,
                                        R.drawable.finish
                                    )
                                )
                        )
                    }
                }
                addGeofence(home, GEOFENCE_RADIUS);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, initial_zoom))
            }

        mMap.isMyLocationEnabled = true // Show current location
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    //Ako aplikacija sama stavlja marker
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
        val indexOfNearestPointToCentre = randomDistances.indexOf(Collections.max(randomDistances))
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

    // Step cunter

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        // Data 1: According to official documentation, the first value of the `SensorEvent` value is the step count
        event.values.firstOrNull()?.let {
            totalSteps ++
            binding.stepCounter.text = "Broj koraka: " + totalSteps.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    fun popUpWindow(){
        val iPopUpWindow = Intent(this, PopUpWindow::class.java)
        startActivityForResult(iPopUpWindow,1000)
    }

    companion object Generiraj{
        var generate: Boolean = false
    }

}

