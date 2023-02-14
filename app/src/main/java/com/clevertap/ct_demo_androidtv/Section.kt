package com.clevertap.ct_demo_androidtv

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.ct_demo_androidtv.utils.log
import java.io.Serializable
import java.util.*
import kotlin.random.Random


data class Section(
    val header:String,
    val entries : List<Movie>
){

    data class Movie(
        val id: Int = 0,
        val title: String = "API In Development",
        val details: String = "Test Description",
        var cardImageUrl: Int = R.drawable.ct_bg_1,
        val videoUrl: String = "https://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
    ) : Serializable

    companion object{
        fun data() : List<Section>{
            val systemSettings = listOf(
                Movie(101,"Get Alert Permission")
            )

            val profileRelated = listOf(
                Movie(1,"pushProfile","update existing profile"),
                Movie(2,"onUserLogin","crete new User Profile"),
                Movie(3,"getCleverTapID","Asyncronously Fetch User id and log in terminal"),
            )
            profileRelated.forEach { it.cardImageUrl = R.drawable.ct_bg_1 }

            val events = listOf(
                Movie(4,"pushEvent","push an event"),
                Movie(5,"pushEvent (props)","push an event with parameters"),
                Movie(6,"pushChargedEvent","push a charged Event"),
                Movie(7,"addMultiValueForKey","Add a user property"),
                Movie(8,"removeValueForKey","Remove a user property"),
            )
            events.forEach { it.cardImageUrl = R.drawable.ct_bg_2 }


            val placeHolder = (1..5).map { Movie() }

            return listOf(
                Section("System Settings", systemSettings),
                Section("Profile",profileRelated),
                Section("Events",events)
                )

        }


        fun onDataClick(ctApi: CleverTapAPI, clickedMovie: Movie, activity: FragmentActivity?){
            when(clickedMovie.id){
                1 ->{
                    val random = Random.nextInt()
                    val map = hashMapOf<String,Any>(
                        "Name" to "new_tv_user$random",
                        "Email" to "new_tv_user@gmail.com"
                    )
                    ctApi.pushProfile(map)
                }
                2 ->{
                    val random = Random.nextInt()
                    val map = hashMapOf<String,Any>(
                        "Name" to "tv_user",
                        "Email" to "tv_user$random@gmail.com"
                    )
                    ctApi.onUserLogin(map)
                }
                3 ->{
                    ctApi.getCleverTapID { log("received id : $it") }
                }

                4 -> ctApi.pushEvent("BUTTON_PRESSED")

                5 -> {
                    val date = Date().toString()
                    ctApi.pushEvent("REMOTE_BUTTON_PRESSED", mapOf("time" to date))
                }

                6 -> {
                    val charges = hashMapOf<String,Any>("Total Number Of Items" to "4", "Total Amount" to "400")
                    val items = arrayListOf(hashMapOf<String,Any>("Item name" to "jeans", "Number of Items" to "4", "Item category" to "clothing", "Amount" to "400"))
                    ctApi.pushChargedEvent(charges,items)
                }

                7 -> ctApi.addMultiValueForKey("userTVCount","1")

                8 -> ctApi.removeValueForKey("userTVCount")

                101 ->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        activity!!.startActivity(myIntent)
                    }
                    else{
                        log("you already have permission.showing view")
                        //MyFcmMessageListenerService.floatingNotif(activity!!)
                    }
                }
            }

        }
    }
}

