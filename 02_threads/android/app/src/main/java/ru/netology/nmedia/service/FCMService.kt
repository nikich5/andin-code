package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    @Inject
    lateinit var auth: AppAuth

    override fun onNewToken(token: String) {
        auth.sendPushToken(token)
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val push = gson.fromJson(message.data[content], PushObject::class.java)
        val currentUserId = auth.authStateFlow.value.id

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Test notification")
            .setContentText(push.content)


        if (push.recipientId == null || push.recipientId == currentUserId) {
            with(NotificationManagerCompat.from(this)) {
                println("SUCCESS! current user id is $currentUserId")
                notify(Random.nextInt(), builder.build())
            }
        }

        if (push.recipientId != currentUserId && (push.recipientId == 0L || push.recipientId != 0L)) {
            println("FAILED! current user id is $currentUserId ...sending new token....")
            auth.sendPushToken()
            return
        }

    }
}

data class PushObject(
    val recipientId: Long? = null,
    val content: String
)