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
import com.clevertap.ct_demo_androidtv.utils.toBundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotifUtil {
    companion object{
        fun floatingNotif(context: Context, message: RemoteMessage){

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