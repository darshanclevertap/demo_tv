package com.clevertap.ct_demo_androidtv

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.clevertap.ct_demo_androidtv.utils.log
import com.clevertap.ct_demo_androidtv.utils.toBundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFcmMessageListenerService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotifUtil.floatingNotif(this,message)
    }

    private fun ctNotification(message: RemoteMessage) {
        message.data["nh_source"] = "CustomFCM_TV"
        var pushType = "fcm"
        if (pushType.equals("fcm")) {
            android.os.Handler(Looper.getMainLooper()).post {
                log("MyFcmMessageListenerService onMessageReceived createNotification on ${Thread.currentThread()}")
                CTFcmMessageHandler().createNotification(applicationContext, message)
            }
            //CTFcmMessageHandler().processPushAmp(applicationContext, message)

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        getCT().pushFcmRegistrationId(token,true)
    }



}