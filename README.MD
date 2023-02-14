# CleverTap SDK and Notification Integration With FIRE TV

This is a sample project that shows how to integrate CleverTap SDK  With Fire TV and use it to send events.
For instructions on how to build an app for Google/Android tv, [Checkout Master branch's README](https://github.com/darshanclevertap/demo_tv/blob/master/README.MD) .

---

Fire TV is a firmware which can turn any TV into a smart TV. It comprises of a remote and a USB device, in which the USB gets inserted in a tv or hub. The USB essentially contains FireOS, a fork of AOSP (The latest FireTV  is a fork of AOSP Android Pie). thus it is able to make a normal tv into a FireOS based tv controllable by a standard Remote.

## Developing Apps For FireOS and Using CleverTap SDK with Fire OS Apps

Since Fire OS is a fork of Android OS, the applications are created for Fire OS in a similar manner ([Checkout Master branch's readme for more info](https://github.com/darshanclevertap/demo_tv/blob/master/README.MD) ) . 

However, here are some differences to consider while creating an app for Fire OS, which are mentioned here: https://developer.amazon.com/docs/fire-tv/differences-from-android-tv-development.html  

The major difference concerning CleverTap is that **FireOS does not support FCM Apis** and thus **we cannot send FCM based notifications via CleverTap**

---

**Send User Profile**
```kotlin
val map = hashMapOf<String,Any>("Name" to "new_tv_user", "Email" to "new_tv_user@gmail.com")
cleverTapAPI.pushProfile(map)
``` 

**Push Event**
```kotlin
cleverTapAPI.pushEvent("BUTTON_PRESSED")
``` 

**Push Event With Parameters**
```kotlin
val date = Date().toString()
cleverTapAPI.pushEvent("REMOTE_BUTTON_PRESSED", mapOf("time" to date))
``` 


**Push Charged Event**
```kotlin
val charges = hashMapOf<String,Any>("Total Number Of Items" to "4", "Total Amount" to "400")
val items = arrayListOf(hashMapOf<String,Any>("Item name" to "jeans", "Number of Items" to "4", "Item category" to "clothing", "Amount" to "400"))
cleverTapAPI.pushChargedEvent(charges,items)
``` 


**Add Profile Properties**
```kotlin
cleverTapAPI.addMultiValueForKey("userTVCount","1")
``` 


**Remove Profile Properties**
```kotlin
cleverTapAPI.removeValueForKey("userTVCount")
```
