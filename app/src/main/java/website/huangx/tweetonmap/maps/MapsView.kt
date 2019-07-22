package website.huangx.tweetonmap.maps

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

interface MapsView {
    fun getContext():Context
    fun getViwContext():Context
    fun addMarker(markerOptions: MarkerOptions): Marker?
    fun startLocationUpdate(onNewLocation: (Location?) -> Unit)
    fun stopLocationUpdate()
    fun clearMarkers()
    fun moveCameraTo(latLng: LatLng)
}