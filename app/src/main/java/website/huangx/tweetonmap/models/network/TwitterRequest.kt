package website.huangx.tweetonmap.models.network

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.android.gms.maps.model.LatLng
import website.huangx.tweetonmap.models.data.Tweet
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import website.huangx.tweetonmap.map
import website.huangx.tweetonmap.models.data.User

fun getOAuth2Token(twitterApiKey: String,
                   twitterApiSecret: String,
                   onSuccess: (tokenType: String, tokenValue:String) -> Unit,
                   onError: (VolleyError) -> Unit)
        : Request<JSONObject>

        = object : Request<JSONObject> (Method.POST,"https://api.TwitterRequest.com/oauth2/token", onError) {
    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String,String>().apply {
            put("authorization", "Basic ${Base64.getEncoder().encodeToString("$twitterApiKey:$twitterApiSecret".toByteArray())}")
        }
    }

    override fun getBodyContentType(): String = "application/x-www-form-urlencoded"

    override fun getParams(): MutableMap<String, String> = mutableMapOf(Pair("grant_type", "client_credentials"))

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> =
        try {
            response?.data?.run { JSONObject(String(this)) }?.run { Response.success(this, HttpHeaderParser.parseCacheHeaders(response)) }
                ?: Response.error(VolleyError("No Token Returned"))
        }
        catch (e: Exception){
            Response.error(ParseError(e))
        }

    override fun deliverResponse(response: JSONObject) =
        kotlin.run { onSuccess( response["token_type"] as String,
            response["access_token"] as String) }
}

fun getTweetsInArea(latLng: LatLng, radius: Double, tokenType: String, tokenValue: String,
                    onSuccess: (List<Tweet>) -> Unit,
                    onError: (VolleyError) -> Unit): Request<List<Tweet>> =
    object : Request<List<Tweet>>(Method.GET, "https://api.twitter.com/1.1/search/tweets.json?q=geocode:${latLng.latitude},${latLng.longitude},$radius&result_type=recent", onError) {

        override fun getHeaders(): MutableMap<String, String> = mutableMapOf(Pair("authorization", "$tokenType $tokenValue"))

        override fun parseNetworkResponse(response: NetworkResponse?): Response<List<Tweet>> =
            try {
                response?.data?.run {
                    JSONObject(String(this)).getJSONArray("statuses")
                        .map {
                            Tweet(idString = it.get("id_str") as String,
                                coordinates = (it.get("coordinates") as? JSONObject)
                                      ?.getJSONArray("coordinates")?.run {
                                        LatLng(this.getDouble(0), this.getDouble(1))
                                    },
                                user = it.getJSONObject("user").run {
                                    User(id = it.getString("id"), profileImgUrl =  when{
                                        (it.get("profile_image_url") as? String) != null -> it.getString("profile_image_url")
                                        (it.get("profile_image_url_https") as? String) != null -> it.getString("profile_image_url_https")
                                        else -> null
                                    })
                                }
                            )
                        }
                        .filter { it.coordinates != null }
                        .run{Response.success(this, HttpHeaderParser.parseCacheHeaders(response))}
                }?:Response.error(VolleyError())
            }
            catch (e: Exception){
                Response.error(VolleyError())
            }

        override fun deliverResponse(response: List<Tweet>) {
            onSuccess(response)
        }
    }
