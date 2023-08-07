package app.krakentom.garminspotifyremotecontroller.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import app.krakentom.garminspotifyremotecontroller.R
import app.krakentom.garminspotifyremotecontroller.activities.MainActivity
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.garmin.android.connectiq.exception.InvalidStateException
import com.garmin.android.connectiq.exception.ServiceUnavailableException
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState

class MyService : Service() {

    private val garminConnectedDevices = mutableListOf<IQDevice>()

    private val CHANNEL_ID = "Garmin Spotify Remote Controller"
    private val GARMIN_WATCH_ID = "TODO YOUR WATCH ID"

    private lateinit var garminConnectIQ: ConnectIQ
    private lateinit var garminApp: IQApp

    private val spotifyErrorCallback = { throwable: Throwable -> spotifyLogError(throwable) }
    private var spotifyPlayerStateSubscription: Subscription<PlayerState>? = null

    companion object {
        var spotifyAppRemote: SpotifyAppRemote? = null

        fun startService(context: Context) {
            val startIntent = Intent(context, MyService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, MyService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        spotifyPlayerStateSubscription = spotifyCancelAndResetSubscription(spotifyPlayerStateSubscription)
        spotifyPlayerStateSubscription = spotifyAssertAppRemoteConnected()
            .playerApi
            .subscribeToPlayerState()
            .setEventCallback(spotifyPlayerStateEventCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Garmin Spotify Remote Controller")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        garminApp = IQApp(GARMIN_WATCH_ID)
        garminConnectIQ = ConnectIQ.getInstance(this, ConnectIQ.IQConnectType.WIRELESS)
        garminConnectIQ.initialize(this, true, garminConnectIQListener)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        garminReleaseConnectIQSdk()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "GarminSpotifyRemoteController Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    private val garminConnectIQListener: ConnectIQ.ConnectIQListener =
        object : ConnectIQ.ConnectIQListener {
            override fun onInitializeError(errStatus: ConnectIQ.IQSdkErrorStatus) {
                Log.e(TAG, "Garmin SDK initialization error!")
            }

            override fun onSdkReady() {
                Log.i(TAG, "Garmin SDK ready")
                garminLoadDevices()
            }

            override fun onSdkShutDown() {
                Log.i(TAG, "Garmin SDK shut down")
            }
        }

    fun garminLoadDevices() {
        try {
            val devices = garminConnectIQ.knownDevices ?: listOf()

            garminConnectedDevices.clear()
            devices.forEach {
                it.status = garminConnectIQ.getDeviceStatus(it)

                garminConnectIQ.registerForDeviceEvents(it) { device, status ->
                    if (status == IQDevice.IQDeviceStatus.CONNECTED) {
                        garminListenByMyAppEvents(device)
                    }
                }

                garminGetAppStatus(it)
            }
        } catch (exception: InvalidStateException) {
            Log.e(TAG, "", exception)
        } catch (exception: ServiceUnavailableException) {
            Log.e(TAG, "", exception)
        }
    }

    private fun garminListenByMyAppEvents(device: IQDevice) {
        try {
            garminConnectIQ.registerForAppEvents(device, garminApp) { _, _, message, _ ->
                val builder = StringBuilder()
                if (message.size > 0) {
                    for (o in message) {
                        builder.append(o.toString())
                        builder.append("\r\n")
                    }
                }

                val command = builder.toString().removeSuffix("\r\n")

                when (command) {
                    "playPause" -> {
                        spotifyPause()
                    }
                    "nextSong" -> {
                        spotifyNext()
                    }
                    "volumeUp" -> {
                        spotifyVolumeUp()
                    }
                    "volumeDown" -> {
                        spotifyVolumeDown()
                    }
                    "likeUnlikeSong" -> {
                        spotifyLikeUnlikeSong()
                    }
                }

            }
        } catch (exception: InvalidStateException) {
            Log.e(TAG, "", exception)
        }
    }

    private fun garminReleaseConnectIQSdk() {
        try {
            garminConnectIQ.unregisterAllForEvents()
            garminConnectIQ.shutdown(this)
        } catch (exception: InvalidStateException) {
            Log.e(TAG, "", exception)
        }
    }

    private fun spotifyAssertAppRemoteConnected(): SpotifyAppRemote {
        spotifyAppRemote?.let {
            if (it.isConnected) {
                return it
            }
        }
        throw SpotifyDisconnectedException()
    }

    private fun spotifyPause() {
        spotifyAssertAppRemoteConnected().let {
            it.playerApi
                .playerState
                .setResultCallback { playerState ->
                    if (playerState.isPaused) {
                        it.playerApi
                            .resume()
                            .setErrorCallback(spotifyErrorCallback)
                    } else {
                        it.playerApi
                            .pause()
                            .setErrorCallback(spotifyErrorCallback)
                    }
                }
        }
    }

    private fun spotifyNext() {
        spotifyAssertAppRemoteConnected()
            .playerApi
            .skipNext()
            .setErrorCallback(spotifyErrorCallback)
    }

    private fun spotifyVolumeUp(){
        spotifyAssertAppRemoteConnected()
            .connectApi
            .connectIncreaseVolume()
            .setErrorCallback(spotifyErrorCallback)
    }

    private fun spotifyVolumeDown(){
        spotifyAssertAppRemoteConnected()
            .connectApi
            .connectDecreaseVolume()
            .setErrorCallback(spotifyErrorCallback)
    }

    private fun spotifyLikeUnlikeSong(){
        spotifyAssertAppRemoteConnected().let {
            it.playerApi
                .playerState
                .setResultCallback { playerState ->
                    it.userApi.getLibraryState(playerState.track.uri).setResultCallback { libraryState ->
                        if (libraryState.isAdded) {
                            it.userApi
                                .removeFromLibrary(playerState.track.uri)
                                .setErrorCallback(spotifyErrorCallback)
                        } else {
                            it.userApi
                                .addToLibrary(playerState.track.uri)
                                .setErrorCallback(spotifyErrorCallback)
                        }
                    }
                }
        }
    }

    private val spotifyPlayerStateEventCallback = Subscription.EventCallback<PlayerState> { playerState ->

        spotifyAssertAppRemoteConnected().let {
            it.playerApi
                .playerState
                .setResultCallback { playerState ->
                    it.userApi.getLibraryState(playerState.track.uri).setResultCallback { libraryState ->
                        var isInLibrary = false;

                        if (libraryState.isAdded) {
                            isInLibrary = true
                        }

                        val seconds = playerState.track.duration / 1000
                        val minutes = seconds / 60
                        val remainingSeconds = seconds % 60

                        val message = mapOf(
                            "song" to playerState.track.name,
                            "artist" to playerState.track.artist.name,
                            "length" to "%02d:%02d".format(minutes, remainingSeconds),
                            "isInLibrary" to isInLibrary
                        )

                        garminConnectedDevices.forEach {
                            try {
                                garminConnectIQ.sendMessage(it, garminApp, message) { _, _, status -> }
                            } catch (e: InvalidStateException) {
                            } catch (e: ServiceUnavailableException) {
                            }
                        }
                    }
                }
        }
    }

    private fun <T : Any?> spotifyCancelAndResetSubscription(subscription: Subscription<T>?): Subscription<T>? {
        return subscription?.let {
            if (!it.isCanceled) {
                it.cancel()
            }
            null
        }
    }

    private fun garminGetAppStatus(device: IQDevice) {
        try {
            garminConnectIQ.getApplicationInfo(GARMIN_WATCH_ID, device, object :
                ConnectIQ.IQApplicationInfoListener {
                override fun onApplicationInfoReceived(app: IQApp) {
                    garminConnectedDevices.add(device)
                }

                override fun onApplicationNotInstalled(applicationId: String) {
                }
            })
        } catch (_: InvalidStateException) {
        } catch (_: ServiceUnavailableException) {
        }
    }

    private fun spotifyLogError(throwable: Throwable) {
        Log.e(TAG, "", throwable)
    }
}