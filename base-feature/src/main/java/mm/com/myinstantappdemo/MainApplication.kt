package mm.com.myinstantappdemo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import mm.com.myinstantappdemo.R.string
import java.util.TimeZone

class MainApplication : Application() {

    init {
        instance = this
    }

    companion object {

        private lateinit var instance: MainApplication
        fun instance(): MainApplication = instance
        const val CHANNEL_1_ID = "channel1"
        const val CHANNEL_2_ID = "channel2"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        TimeZone.setDefault(TimeZone.getDefault())
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                getString(string.notification_channel_one),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = getString(R.string.notification_channel_desc_one)
            val channel2 = NotificationChannel(
                CHANNEL_2_ID,
                getString(string.notification_channel_two),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel2.description = getString(R.string.notification_channel_desc_two)

            val manager: NotificationManager = getSystemService(NotificationManager::class.java)!!
            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }
}