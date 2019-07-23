package website.huangx.tweetonmap.models.network.requests

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import website.huangx.tweetonmap.map
import website.huangx.tweetonmap.models.data.Tweet
import website.huangx.tweetonmap.models.data.User
import java.lang.Exception

class TweetsInAreaRequest(latLng: LatLng,
                          radius: Double,
                          private val tokenType: String,
                          private val tokenValue: String,
                          private val onSuccess: (List<Tweet>) -> Unit,
                          onError: (Exception) -> Unit)
    : Request<List<Tweet>>(Method.GET, "https://api.twitter.com/1.1/search/tweets.json?q=geocode:${latLng.latitude},${latLng.longitude},${radius}km&result_type=recent", onError) {


    override fun getHeaders(): MutableMap<String, String> = mutableMapOf(Pair("authorization", "$tokenType $tokenValue"))

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<Tweet>> =
        try {
            response?.data?.run {
                JSONObject(String(this)).getJSONArray("statuses")
                    .map {
                        Tweet(idString = it.get("id_str") as String,
                            coordinates = (it.get("geo") as? JSONObject)
                                ?.getJSONArray("coordinates")?.run {
                                    LatLng(this.getDouble(0), this.getDouble(1))
                                },
                            user = it.getJSONObject("user").run {
                                User(id = this.getString("id"),
                                    profileImgUrl =  when{
                                        (this.get("profile_image_url_https") as? String) != null -> this.getString("profile_image_url_https")
                                        (this.get("profile_image_url") as? String) != null -> this.getString("profile_image_url")
                                        else -> null
                                    },
                                    name = this.getString("name"),
                                    screenName = this.getString("screen_name"))
                            },
                            text = if(it.has("text")) it.getString("text") else ""
                        )
                    }
                    .filter { it.coordinates != null }
                    .run{ Response.success(this, HttpHeaderParser.parseCacheHeaders(response))}
            }?: Response.error(VolleyError("Volley error"))
        }
        catch (e: Exception){
            Response.error(VolleyError("Volley error"))
        }

    override fun deliverResponse(response: List<Tweet>) {
        onSuccess(response)
    }
}