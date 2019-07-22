package website.huangx.tweetonmap.maps

import android.content.Context
import com.google.android.gms.maps.model.MarkerOptions

interface MapsView {
    fun getContext():Context
    fun getViwContext():Context
    fun addMarker(markerOptions: MarkerOptions)
}