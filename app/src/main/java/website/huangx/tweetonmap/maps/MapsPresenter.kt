package website.huangx.tweetonmap.maps

import com.google.android.gms.maps.model.Marker

interface MapsPresenter {
    fun onMapLoaded()
    fun onResume()
    fun onPause()
    fun onMarkerClick(marker: Marker)
}