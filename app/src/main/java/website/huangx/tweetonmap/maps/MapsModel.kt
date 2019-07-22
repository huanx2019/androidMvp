package website.huangx.tweetonmap.maps

import com.google.android.gms.maps.model.LatLng
import website.huangx.tweetonmap.models.data.Tweet
import java.lang.Exception

interface MapsModel {
    fun getTweetsInArea(latLng: LatLng, radius: Double, onSuccess: (List<Tweet>) -> Unit, onError: (Exception) -> Unit)
}