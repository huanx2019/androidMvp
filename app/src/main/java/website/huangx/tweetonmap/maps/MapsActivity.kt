package website.huangx.tweetonmap.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import website.huangx.tweetonmap.R
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

private const val PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 0

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsView, GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null

    private lateinit var mPresenter: MapsPresenter

    private var mLocationManager : LocationManager? = null

    private var mLocationListener:LocationListener? = null

    private val mAddedMarkers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mLocationManager = (getSystemService(Context.LOCATION_SERVICE) as LocationManager)

        mPresenter = MapsPresenterImpl(this)
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        }

        mPresenter.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMarkerClickListener(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap?.isMyLocationEnabled = true
        }

    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.request_usr_permission_message))
                    .setPositiveButton(getString(R.string.literal_OK)){ _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION)
                    }
                    .show()
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if (permissions.size == 1
                    && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mMap?.isMyLocationEnabled = true
                }
            }
        }
    }

    override fun getContext(): Context = this

    override fun getViwContext(): Context = this

    override fun addMarker(markerOptions: MarkerOptions): Marker? {
        val marker = mMap?.addMarker(markerOptions)
        if (marker != null) mAddedMarkers.add(marker)
        return marker
    }

    @SuppressWarnings("MissingPermission")
    override fun startLocationUpdate(onNewLocation: (Location?) -> Unit) {
        if (mLocationListener != null) return

        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                onNewLocation(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20f, mLocationListener)
    }

    override fun stopLocationUpdate() {
        mLocationManager?.removeUpdates(mLocationListener)
        mLocationListener = null
    }

    override fun clearMarkers() {
        for (marker in mAddedMarkers) {
            marker.remove()
        }
        mAddedMarkers.clear()
    }

    override fun moveCameraTo(latLng: LatLng) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
    }

    override fun onMarkerClick(p0: Marker?): Boolean {

        if (p0 != null) mPresenter.onMarkerClick(p0)

        return false
    }
}
