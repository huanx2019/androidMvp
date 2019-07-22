package website.huangx.tweetonmap.maps

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import website.huangx.tweetonmap.R
import website.huangx.tweetonmap.addTo
import website.huangx.tweetonmap.models.data.Tweet
import website.huangx.tweetonmap.models.network.getOAuth2Token
import java.lang.Exception

class MapsModelImpl(private val context: Context): MapsModel {

    private val q = Volley.newRequestQueue(context)

    override fun getTweetsInArea(
        latLng: LatLng,
        radius: Double,
        onSuccess: (List<Tweet>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        getOAuth2Token(context.getString(R.string.twitter_api_key), context.getString(R.string.twitter_api_secret_key),
            { tokenType, tokenValue ->
                website.huangx.tweetonmap.models.network.getTweetsInArea(latLng, radius, tokenType, tokenValue,{
                    onSuccess(it)
                },
                    {
                        onError(it)
                    })
                    .addTo(q)
            },
            {
                onError(it)
            })
            .addTo(q)
    }


    override fun getBitmapFromUrl(url: String, onSuccess: (Bitmap) -> Unit, onError: (Exception) -> Unit) {
        ImageRequest(url, onSuccess, 0, 0, ImageView.ScaleType.FIT_XY, null, onError)
            .addTo(q)
    }
}