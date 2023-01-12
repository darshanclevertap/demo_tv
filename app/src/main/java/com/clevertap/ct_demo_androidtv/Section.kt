package com.clevertap.ct_demo_androidtv

import com.clevertap.android.sdk.CleverTapAPI
import java.io.Serializable
import java.util.Date
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
            val profileRelated = listOf(
                Movie(1,"pushProfile","update existing profile"),
                Movie(2,"onUserLogin","crete new User Profile"),
                Movie(3,"getCleverTapID","Asyncronously Fetch User id and log in terminal"),
            )
            profileRelated.forEach { it.cardImageUrl = R.drawable.ct_bg_1 }

            val events = listOf(
                Movie(4,"pushEvent","push an event with parameters"),
                Movie(5,"pushChargedEvent","push a charged Event")
            )
            events.forEach { it.cardImageUrl = R.drawable.ct_bg_2 }


            val placeHolder = (1..5).map { Movie() }

            return listOf(
                Section("Profile",profileRelated),
                Section("Events",events),
                Section("Push Notifications",placeHolder),
                Section("App inbox",placeHolder),
                Section("In Apps",placeHolder),
                Section("Feature Flags & product config",placeHolder),
                Section("Native Display",placeHolder),
                Section("Geo Fence",placeHolder),

                )

        }


        fun onDataClick(ctApi:CleverTapAPI, clickedMovie:Movie){
            when(clickedMovie.id){
                1 ->{
                    val random = Random.nextInt()
                    val map = hashMapOf<String,Any>("Name" to "tv_user$random", "Email" to "tv_user@gmail.com",)
                    ctApi.pushProfile(map)
                }
                2 ->{
                    val random = Random.nextInt()
                    val map = hashMapOf<String,Any>("Name" to "tv_user", "Email" to "tv_user@gmail.com$random",)
                    ctApi.onUserLogin(map)
                }
                3 ->{
                    ctApi.getCleverTapID { log("received id : $it") }
                }

                4 -> {
                    val date = Date().toString()
                    ctApi.pushEvent("REMOTE_BUTTON_PRESSED", mapOf("time" to date))
                }
                5 -> {
                    val charges = hashMapOf<String,Any>("Total Number Of Items" to "4", "Total Amount" to "400")
                    val items = arrayListOf(hashMapOf<String,Any>("Item name" to "jeans", "Number of Items" to "4", "Item category" to "clothing", "Amount" to "400"))
                    ctApi.pushChargedEvent(charges,items)
                }
            }

        }
    }
}

