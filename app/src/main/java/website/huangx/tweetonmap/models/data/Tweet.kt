package website.huangx.tweetonmap.models.data

import android.graphics.drawable.LayerDrawable
import com.google.android.gms.maps.model.LatLng

class Tweet (val idString: String,
             val coordinates:LatLng?,
             val user:User,
             val text:String)

class User(val id: String, val profileImgUrl: String?, val name: String, val screenName: String)