package com.clevertap.ct_demo_androidtv

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.interfaces.NotificationHandler
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.clevertap.android.sdk.pushnotification.PushConstants
import com.clevertap.android.sdk.pushnotification.amp.CTPushAmpListener
import com.google.firebase.FirebaseApp


class MainApp : Application() {

    var ctCoreApi:CleverTapAPI? = null

    override fun onCreate() {

        //FirebaseApp.initializeApp(this)
        log("ctInitLogging: ")
        CleverTapAPI.setDebugLevel(com.clevertap.android.sdk.CleverTapAPI.LogLevel.VERBOSE)

        log("ctAttachLifeCycleListener: ")
        ActivityLifecycleCallback.register(this)

        super.onCreate()

        log("ctInitGlobalInstance: ")
        ctCoreApi = CleverTapAPI.getDefaultInstance(applicationContext)

        ctNotificiations()
    }




    private fun ctNotificiations() {
        log("ctNotifCreateChannels: ")
        val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_MAX else 5
        CleverTapAPI.createNotificationChannel(applicationContext, "c_general_id", "c_general_name", "", importance, true)
        CleverTapAPI.createNotificationChannel(applicationContext, "GSTTesting", "GSTTesting", "", importance, true)
        CleverTapAPI.createNotificationChannel(applicationContext, "BRTesting", "Core", "Core notifications", importance, true)
        CleverTapAPI.createNotificationChannel(applicationContext, "PTTesting", "Push templates", "All push templates", importance, true)


        log("setNotificationHandler: ")
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler);

        log("enableXiaomiPushOn: ")
        CleverTapAPI.enableXiaomiPushOn(PushConstants.ALL_DEVICES)

        log("ctNotifAttachListeners: ")
        ctCoreApi?.ctPushNotificationListener = CTPushNotificationListener { receivedNotifInfo ->
            log("received notification: $receivedNotifInfo")
            receivedNotifInfo.toBundle().logBundle("received notif bundle")
        }

        log("ctPushAmpListener: ")
        ctCoreApi?.ctPushAmpListener = CTPushAmpListener { extras: Bundle? ->
            CleverTapAPI.processPushNotification(applicationContext, extras)
            extras?.logBundle("forced push notif bundle :")
            applicationContext.showNotif(title = "push amplification notification", channelId = "forced_notif_channel")//todo use forced push amp's bundle

        }


    }
}

fun Context?.getCT():CleverTapAPI{
    val ctx = this?.applicationContext as? MainApp ?:  error("can't cast context to MainApp")
    return ctx.ctCoreApi!!

}

/*
//profile and events
fun Context?.ctProfileListener(idCallback:(String)->Unit={}) {
    val ctCoreApi  = getCT()

    log("sync referrer init:")
    ctCoreApi.syncListener = object : SyncListener {
        override fun profileDataUpdated(updates: JSONObject?) {/*no op*/ }
        override fun profileDidInitialize(id: String?) {
            println("CleverTap SyncListener DeviceID from Application class= $id")
        }
    }
    ctCoreApi.getCleverTapID {id: String ->
        idCallback.invoke(id)
        log("user id from callback is : $id")
    }
}
fun Fragment.profileUpdate(onLogin:Boolean, map: Map<String, Any> = mapOf()){
    if(onLogin) context.getCT().onUserLogin(map)
    else context.getCT().pushProfile(map)

}
fun Fragment.ctRecordScreen(authScreen:String) {
    log("recordScreen() called with: authScreen = $authScreen")
    context.getCT().recordScreen(authScreen)
}
fun Fragment.ctPushEvent(name:String, map:Map<String,Any>?=null){
    if(map==null){
        context.getCT().pushEvent(name)
    }else{
        context.getCT().pushEvent(name,map)
    }
}
fun Fragment.pushError(name: String,code:Int?=null){
    if(code!=null){
        context.getCT().pushError(name,code)
    }
}


//inbox
fun Context?.ctInboxInit() {
    log("ctInBoxInitialise: ")
    getCT().initializeInbox()
    //ResultFragment: contains inbox related stuff
}


//inapp
fun Context?.ctInAppListener() {
    log("ctInAppNotificationListener: ")
    val ct = getCT()

    ct.inAppNotificationListener = object : InAppNotificationListener {
        override fun beforeShow(extras: MutableMap<String, Any>?): Boolean {
            log( "inAppNotificationListener : beforeShow() called with: extras = $extras")
            return true
        }

        override fun onDismissed(extras: MutableMap<String, Any>?, actionExtras: MutableMap<String, Any>?) {
            log( "inAppNotificationListenercalled with: extras = $extras")
            log( "inAppNotificationListenercalled with: actionExtras = $actionExtras")
        }

    }
    ct.setInAppNotificationButtonListener { log( "setInAppNotificationButtonListener() called with extras : $it") }
}

//location and stuff
fun Context?.ctGeofencing() {
    log( "ctGeofencingInit() called")
    this?:return
    val ct = getCT()

    val ctGeofenceApi =  CTGeofenceAPI.getInstance(applicationContext)

    val config = CTGeofenceSettings.Builder().let {
        it.enableBackgroundLocationUpdates(true)
        it.setFastestInterval(30)
        it.setGeofenceMonitoringCount(50)
        it.setGeofenceNotificationResponsiveness(0)
        it.setId(ct.accountId)
        it.setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)
        it.setLogLevel(Logger.VERBOSE)
        it.setLocationFetchMode(CTGeofenceSettings.FETCH_LAST_LOCATION_PERIODIC)
        it.setSmallestDisplacement(200f)
        it.build()
    }
    ctGeofenceApi.init(config,ct)

    log("creating a geofence trigger:")
    ctGeofenceApi.setOnGeofenceApiInitializedListener {
        log("setOnGeofenceApiInitializedListener : triggerred")
        runCatching { ctGeofenceApi.triggerLocation() }
            .exceptionOrNull()
            ?.let { ct.pushGeoFenceError(105,it.message) }

    }
    log("attaching geofence listeners")
    val eventsListener = object :CTGeofenceEventsListener{
        override fun onGeofenceEnteredEvent(geofenceEnteredEventProperties: JSONObject?) {
            ct.pushGeofenceEnteredEvent(geofenceEnteredEventProperties)
        }

        override fun onGeofenceExitedEvent(geofenceExitedEventProperties: JSONObject?) {
            ct.pushGeoFenceExitedEvent(geofenceExitedEventProperties)

        }
    }
    val locationListener = CTLocationUpdatesListener { location : Location? -> ct.location = location }
    ctGeofenceApi.setCtGeofenceEventsListener(eventsListener)
    ctGeofenceApi.setCtLocationUpdatesListener (locationListener)
}
fun Context?.ctLocation() {
    log("ctLocation() called")
    this?:return
    requestLocationFromContext {
        log("setting location: $it")
        getCT().location = it
    }
}





* */