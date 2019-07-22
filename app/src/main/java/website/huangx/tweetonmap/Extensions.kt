package website.huangx.tweetonmap

import com.android.volley.Request
import com.android.volley.RequestQueue
import org.json.JSONArray
import org.json.JSONObject

inline fun <T>JSONArray.map(action: (JSONObject) -> T) : List<T> {
    val list = ArrayList<T>(this.length())

    for (i in 0 until this.length()) {
        list.add(action(this.getJSONObject(i)))
    }

    return list
}

inline fun <T>Request<T>.addTo(queue: RequestQueue) { queue.add(this) }