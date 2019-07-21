package website.huangx.tweetonmap.models.data

import android.graphics.drawable.LayerDrawable
import com.google.android.gms.maps.model.LatLng

class Tweet (val idString: String, val coordinates:LatLng?, val user:User)

class User(val id: String, profileImgUrl: String?)