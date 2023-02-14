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
import androidx.core.os.bundleOf
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.clevertap.ct_demo_androidtv.utils.log
import com.clevertap.ct_demo_androidtv.utils.toBundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFcmMessageListenerService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        floatingNotif(this,message)
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

    companion object{
        fun floatingNotif(context: Context, message: RemoteMessage = RemoteMessage(bundleOf())){

            Handler(Looper.getMainLooper()).post {
                val windowManager = context.getSystemService(FirebaseMessagingService.WINDOW_SERVICE) as WindowManager

                val w = WindowManager.LayoutParams.WRAP_CONTENT
                val h = WindowManager.LayoutParams.WRAP_CONTENT
                val f0 = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                val f1 = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                val f2 = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                val overlay =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE
                    else   WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

                val color = PixelFormat.TRANSLUCENT

                val params = WindowManager.LayoutParams(w, h, overlay, f0, color).also {
                    it.gravity = Gravity.START or Gravity.BOTTOM
                }

                val inflater = context.getSystemService(FirebaseMessagingService.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val floatingView: View = inflater.inflate(R.layout.floating_view, null)

                floatingView.apply {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        windowManager.removeView(floatingView)
                        val intent = Intent(it.context,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        it.context.startActivity(intent)
                        context.getCT().pushNotificationClickedEvent(message.data.toBundle())

                    }
                }

                floatingView.findViewById<ImageButton>(R.id.btCross).apply {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { windowManager.removeView(floatingView) }
                }

                windowManager.addView(floatingView, params)
            }

        }

    }





}
fun RemoteMessage.log(){
    this.let {
        log(value = it.collapseKey, key = "it.collapseKey")
        log(value = it.data, key = " it.data")
        log(value = it.from, key = "it.from")
        log(value = it.messageId, key = "it.messageId")
        log(value = it.messageType, key = "it.messageType")
        log(value = it.originalPriority, key = "it.originalPriority")
        log(value = it.priority, key = "")
        log(value = it.rawData?.toList(), key = "it.rawData?.toList()")
        log(value = it.senderId, key = "it.senderId")
        log(value = it.sentTime, key = "it.sentTime")
        log(value = it.to, key = "it.to")
        log(value = it.ttl, key = "it.tt")

        log(value = it.notification?.body, key = "notification?.body")
        log(value = it.notification?.bodyLocalizationArgs?.toList(), key = ".notification?.bodyLocalizationArgs")
        log(value = it.notification?.bodyLocalizationKey, key = "notification?.bodyLocalizationKey")
        log(value = it.notification?.channelId, key = "notification?.channelId")
        log(value = it.notification?.clickAction, key = "notification?.clickAction")
        log(value = it.notification?.color, key = "notification?.color")
        log(value = it.notification?.defaultLightSettings, key = "notification?.defaultLightSettings")
        log(value = it.notification?.defaultVibrateSettings, key = "notification?.defaultVibrateSettings")
        log(value = it.notification?.eventTime, key = "notification?.eventTime")
        log(value = it.notification?.defaultSound, key = "notification?.defaultSound")
        log(value = it.notification?.icon, key = "notification?.icon")
        log(value = it.notification?.imageUrl, key = ".notification?.imageUrl")
        log(value = it.notification?.lightSettings?.toList(), key = "notification?.lightSettings")
        log(value = it.notification?.link, key = "notification?.link")
        log(value = it.notification?.localOnly, key = "notification?.localOnly")
        log(value = it.notification?.notificationCount, key = "notification?.notificationCoun")
        log(value = it.notification?.notificationPriority, key = "notification?.notificationPriority")
        log(value = it.notification?.sound, key = "notification?.sound")
        log(value = it.notification?.sticky, key = "notification?.sticky")
        log(value = it.notification?.tag, key = "notification?.tag")
        log(value = it.notification?.ticker, key = "notification?.ticker")
        log(value = it.notification?.title, key = "notification?.title")
        log(value = it.notification?.titleLocalizationArgs?.toList(), key = "notification?.titleLocalizationArgs")
        log(value = it.notification?.titleLocalizationKey, key = "notification?.titleLocalizationKey")
        log(value = it.notification?.vibrateTimings, key = "notification?.vibrateTimings")
        log(value = it.notification?.visibility, key = "notification?.visibility")

    }
}
