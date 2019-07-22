package website.huangx.tweetonmap.maps

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.tweet_marker_view.view.*
import website.huangx.tweetonmap.R

class MapsPresenterImpl(private val view:MapsView):MapsPresenter {
    private val model = MapsModelImpl(view.getContext())

    override fun onMapLoaded() {

    }

    override fun onResume() {
        model.getTweetsInArea(LatLng(51.078306, -114.134961), 5.0, {
            it.forEach { tweet ->
                val layoutInflater = LayoutInflater.from(view.getViwContext())

                val iconGenerator = IconGenerator(view.getContext())
                    .apply {
                        setContentView(
                            layoutInflater
                                .inflate(R.layout.tweet_marker_view, null, false)
                                .apply {
                                    tweet_marker_user_name.text = tweet.user.name
                                    tweet_marker_user_screen_name.text = "@${tweet.user.screenName}"
                                    tweet_marker_text.text = tweet.text
                                }
                        )
                    }

                val markerOptions = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                    .position(tweet.coordinates!!)
                    .anchor(iconGenerator.anchorU, iconGenerator.anchorV)

                view.addMarker(markerOptions)
            }
        },
            { error ->
                Log.d("test", error.message)
            })
    }
}