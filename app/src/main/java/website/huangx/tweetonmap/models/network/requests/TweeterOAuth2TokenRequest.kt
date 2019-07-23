package website.huangx.tweetonmap.models.network.requests

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class TweeterOAuth2TokenRequest(
    private val twitterApiKey: String,
    private val twitterApiKeySecretKey: String,
    private val onSuccess: (tokenType: String, tokenValue: String)-> Unit,
                                onError: (Exception) -> Unit)
: Request<JSONObject> (Method.POST,"https://api.twitter.com/oauth2/token", onError) {
    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String,String>().apply {
            put("authorization", "Basic ${Base64.getEncoder().encodeToString("$twitterApiKey:$twitterApiKeySecretKey".toByteArray())}")
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
            Response.error(VolleyError(e.message))
        }

    override fun deliverResponse(response: JSONObject) =
        kotlin.run { onSuccess( response["token_type"] as String,
            response["access_token"] as String) }
}