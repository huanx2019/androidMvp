package website.huangx.tweetonmap.maps

import website.huangx.tweetonmap.models.MapsModel
import website.huangx.tweetonmap.models.MapsModelImpl

class MapsPresenterImpl(view:MapsView):MapsPresenter {
    private val model = MapsModelImpl(view.getContext())

    override fun onMapLoaded() {

    }

    override fun onResume() {
        model.loadTweetAroundArea(MapsModel.Area(51.047661, -114.207284, 5.0)){
            print(it)
        }
    }
}