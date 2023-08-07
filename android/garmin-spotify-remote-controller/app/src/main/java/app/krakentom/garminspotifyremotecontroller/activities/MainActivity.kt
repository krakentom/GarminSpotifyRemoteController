package app.krakentom.garminspotifyremotecontroller.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.krakentom.garminspotifyremotecontroller.R
import app.krakentom.garminspotifyremotecontroller.services.MyService
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private val SPOTIFY_CLIENT_ID = "TODO YOUR SPOTIFY ID"
    private val SPOTIFY_REDIRECT_URI = "http://localhost/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spotifyConnect()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.start_service -> {
                MyService.startService(this)
                true
            }
            R.id.stop_service -> {
                MyService.stopService(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun spotifyConnect() {
        SpotifyAppRemote.disconnect(MyService.spotifyAppRemote)
        lifecycleScope.launch {
            try {
                MyService.spotifyAppRemote = spotifyConnectToAppRemote(true)
                MyService.startService(this@MainActivity)
            } catch (error: Throwable) {
                Log.e(TAG, "",error)
            }
        }
    }

    private suspend fun spotifyConnectToAppRemote(showAuthView: Boolean): SpotifyAppRemote? =
        suspendCoroutine { cont: Continuation<SpotifyAppRemote> ->
            SpotifyAppRemote.connect(
                application,
                ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                    .setRedirectUri(SPOTIFY_REDIRECT_URI)
                    .showAuthView(showAuthView)
                    .build(),
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        cont.resume(spotifyAppRemote)
                    }

                    override fun onFailure(error: Throwable) {
                        cont.resumeWithException(error)
                    }
                })
        }
}