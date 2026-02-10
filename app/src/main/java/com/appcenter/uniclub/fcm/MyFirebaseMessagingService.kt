package com.appcenter.uniclub.fcm

import android.util.Log
import com.appcenter.uniclub.App
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.notification.NotificationEventBus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        val app = applicationContext as App
        val repo = ServiceLocator.fcmRepository(app)

        CoroutineScope(Dispatchers.IO).launch {
            repo.registerIfLoggedIn(token)
                .onFailure { e -> Log.w("FCM", "registerIfLoggedIn failed", e) }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "onMessageReceived data=${message.data}")

        NotificationEventBus.notifyChanged()
    }
}