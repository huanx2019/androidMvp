package website.huangx.tweetonmap.models

import org.json.JSONObject

interface MapsModel {
    class Area(val lat:Double, val lng:Double, val radius:Double, val lang:String = "en"){}

    fun loadTweetAroundArea(area: Area, onSuccess: (JSONObject) -> Unit)
}