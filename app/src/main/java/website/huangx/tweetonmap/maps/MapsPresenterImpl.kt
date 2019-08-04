package website.huangx.tweetonmap.maps

import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.tweet_marker_view.view.*
import website.huangx.tweetonmap.R
import website.huangx.tweetonmap.models.data.Tweet
import java.util.*

class MapsPresenterImpl(private val view:MapsView):MapsPresenter {
    private val model = MapsModelImpl(view.getContext())

    private val tweets:MutableMap<String, Tweet> = mutableMapOf()

    private var previousMarker: Marker? = null

    override fun onMapLoaded() {
        view.startLocationUpdate {
            if (it != null) onNewLocation(it)
        }
    }

    override fun onResume() {
        view.startLocationUpdate {
            if (it != null) onNewLocation(it)
        }
    }

    override fun onPause() {
        view.stopLocationUpdate()
    }

    private fun onNewLocation(location: Location){
        view.moveCameraTo(LatLng(location.latitude, location.longitude))

        model.getTweetsInArea(
            LatLng(location.latitude, location.longitude), 5.0, {
                val layoutInflater = LayoutInflater.from(view.getViwContext())
                view.clearMarkers()
                tweets.clear()

                it.forEach { tweet ->
                    val contentView = layoutInflater
                        .inflate(R.layout.tweet_marker_view, null, false)
                        .apply {
                            tweet_marker_user_name.text = tweet.user.name
                            tweet_marker_user_screen_name.text = "@${tweet.user.screenName}"
                            tweet_marker_text.text = tweet.text
                        }

                    val iconGenerator = IconGenerator(view.getContext())
                        .apply {
                            setContentView(contentView)
                            setContentPadding(100, 100, 100, 100)
                        }

                    val markerOptions = MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                        .position(tweet.coordinates!!)
                        .anchor(iconGenerator.anchorU, iconGenerator.anchorV)
                        .zIndex(0f)

                    view.addMarker(markerOptions)?.run {
                        if (tweet.user.profileImgUrl != null) {
                            model.getBitmapFromUrl(tweet.user.profileImgUrl,
                                {bitmap ->
                                    contentView.findViewById<ImageView>(R.id.tweet_marker_profile_image)
                                        .setImageBitmap(bitmap)
                                    this.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                                },
                                {error ->
                                    Log.d("test", error.message)
                                })
                        }

                        this.tag = tweet.idString
                        tweets[tweet.idString] = tweet
                    }

                }
            },
            { error ->
                Log.d("test", error.message)
            })
    }

    override fun onMarkerClick(marker: Marker) {
        marker.zIndex = 10f

        previousMarker?.zIndex = 0f
        previousMarker = marker

        if (!(marker?.tag as? String).isNullOrEmpty()
            && tweets.containsKey(marker.tag!! as String )) {

            val tweet = tweets[marker.tag!! as String]!!

            Log.d("test", "Marker for ${tweet.idString}")
        }
    }
}