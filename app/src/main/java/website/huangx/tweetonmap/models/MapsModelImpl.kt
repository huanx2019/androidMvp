package website.huangx.tweetonmap.models

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MapsModelImpl(val context: Context):MapsModel {
    private val requestQueue = Volley.newRequestQueue(context)
    override fun loadTweetAroundArea(area: MapsModel.Area, onSuccess:(JSONObject) -> Unit) {
        getAuthorizationToken {
            val url = "https://api.TwitterRequest.com/1.1/search/tweets.json?geocode=${area.lat},${area.lng},${area.radius}mi&lang=${area.lang}&result_type=mixed"
            object:JsonObjectRequest(Method.GET, url, null, onSuccess,  { error ->
                print("error:$error")}) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf(Pair("authorization", "${it["token_type"]} ${it["access_token"]}"))
                }
            }
                .run { requestQueue.add(this) }
        }
    }

    private fun getAuthorizationToken(onSuccess: (JSONObject) -> Unit){
        val url = "https://api.TwitterRequest.com/oauth2/token"
        object : Request<JSONObject>(Method.POST, url, {}){

        }
            .run { requestQueue.add(this) }
    }
}