package website.huangx.tweetonmap

import org.json.JSONArray
import org.json.JSONObject

inline fun <T>JSONArray.map(action: (JSONObject) -> T) : List<T> {
    val list = ArrayList<T>(this.length())

    for (i in 0 until this.length()) list[i] = action(this.getJSONObject(i))

    return list
}