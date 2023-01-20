package com.clevertap.ct_demo_androidtv

import android.os.Bundle
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import androidx.leanback.widget.*
import com.clevertap.ct_demo_androidtv.Section.Movie
import com.clevertap.ct_demo_androidtv.utils.CardPresenter
import com.clevertap.ct_demo_androidtv.utils.toast

class MainFragment : BrowseSupportFragment() {
    @Deprecated("")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)
        val activity  = activity?:return

        context.getCT().pushEvent("HELLO_TV_APP")

        //setupUIElements-----------------------------
        title = ""
        headersState = 1
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity, R.color.black)
        searchAffordanceColor = ContextCompat.getColor(activity, R.color.search_opaque)
        BackgroundManager.getInstance(activity).run {
            attach(activity.window)
            drawable = ContextCompat.getDrawable(activity, R.drawable.ct_background)
        }
        activity.windowManager.defaultDisplay.getMetrics(DisplayMetrics())

        setOnSearchClickedListener {toast("Welcome to CleverTap. Click on various cards twice to run a feature") }
        setOnItemClickedAfterSelectingListener()
        setOnItemViewSelectedListener()

        //loadRows-------------------
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        Section.data().forEachIndexed { i, it ->
            val cardsAdapter = ArrayObjectAdapter(CardPresenter())
            cardsAdapter.addAll(0,it.entries)

            val header = HeaderItem(i.toLong(),it.header)
            val listRow = ListRow(header,cardsAdapter)
            rowsAdapter.add(listRow)
        }
        adapter = rowsAdapter



    }

    private fun setOnItemViewSelectedListener() {
        //onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, _ ->
            //activity.runOnUiThread {
            //    if(item is Movie){
            //        val width = mMetrics.widthPixels
            //        val height = mMetrics.heightPixels
            //        val targetListener = object : SimpleTarget<Drawable>(width, height) {
            //            override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            //                mBackgroundManager.drawable = drawable
            //            }
            //        }
            //        val url = item.backgroundImageUrl
            //        Glide.with(activity).load(url).centerCrop().into<SimpleTarget<Drawable>>(targetListener)
            //    }
            //
            //}
       //}
    }

    private fun setOnItemClickedAfterSelectingListener(){

        this.onItemViewClickedListener =
            OnItemViewClickedListener { _: Presenter.ViewHolder?, item: Any, _: RowPresenter.ViewHolder, _: Row? ->
                (item as? Movie)?.let {
                    toast(" clicked  item : ${item.title}")
                    Section.onDataClick(context.getCT(),it,activity)
                }

            }
    }


    companion object {
        const  val TAG = "MainFragment"

    }
}