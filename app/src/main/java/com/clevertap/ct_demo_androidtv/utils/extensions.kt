package com.clevertap.ct_demo_androidtv.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment


fun log(str:String){
    Log.e("CleverTap_Sample",str)
}
fun Fragment.toast(text:String, length:Int= Toast.LENGTH_LONG){
    Toast.makeText(activity, text, length).show()
}

fun Map<String, Any>?.toBundle(): Bundle {
    this?:return  bundleOf()
    val extras = Bundle()
    for ((key, value) in this) {
        when(value){
            is String -> extras.putString(key, value)
            is Float -> extras.putFloat(key, value)
            is Char -> extras.putChar(key, value)
            is Int -> extras.putInt(key, value)
            else -> extras.putString(key,value.toString())
        }
    }
    return extras

}
fun Bundle?.logBundle(initialText:String = "bundle") {
    this ?: kotlin.run {
        log(initialText,   "{}")
        return
    }
    log("$initialText \t{")
    this.keySet().forEach {
        log("\t\t $it : ${get(it).toString()}")
    }
    log("\t{")
}



fun log(key: String, value: Any? = null, tag: String = "CleverTapMyWord") {
    if(value == null) Log.e(tag, key )
    else Log.e(tag, "$key:$value ")
}

fun Context?.showNotif(
    title: String = "title",
    body: String? = "body",
    @DrawableRes smallIcon: Int = android.R.drawable.ic_notification_clear_all,
    channelId: String = "default",
    channelInfo: String = "channel info",
    priorityFromBundle: Int? = null,
    notificationId: Int = 0,
    soundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
    autoCancel: Boolean = true,
    onClickPendingIntent: PendingIntent? = null,
) {
    this ?: return
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    log("showNotif() called with: body = $body, smallIcon = $smallIcon, channelId = $channelId, channelInfo = $channelInfo, priorityFromBundle = $priorityFromBundle, title = $title, notificationId = $notificationId, soundUri = $soundUri, autoCancel = $autoCancel, onClickPendingIntent = $onClickPendingIntent")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val priorityFinal = priorityFromBundle ?: NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelInfo, priorityFinal)
        manager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setAutoCancel(autoCancel)

    if (soundUri != null) notificationBuilder.setSound(soundUri)
    if (body != null) notificationBuilder.setContentText(body)
    if (onClickPendingIntent != null) notificationBuilder.setContentIntent(onClickPendingIntent)

    manager.notify(notificationId, notificationBuilder.build())
}


