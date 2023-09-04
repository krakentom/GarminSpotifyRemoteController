# GarminSpotifyRemoteController

## How it works

The Garmin application sends commands such as "play/stop," "next song," "volume up," "volume down," and "add to library" to the phone and waits for a response. The phone sends information about the currently playing track back to the Garmin application. The Android app uses the Garmin Connect IQ SDK to communicate with the Garmin device and waits for commands from it. When a command is received, the app calls the appropriate function through the Spotify SDK to control Spotify.

## A) Download source code from GarminSpotifyRemoteController from github
https://github.com/krakentom/GarminSpotifyRemoteController

## B) Build GarminApp

1. Download and install visual studio code
https://code.visualstudio.com/

2. Install MonkeyC extension from Garmin

3. Register on Garmin, download and install Connect IQ SDK Manager
https://developer.garmin.com/connect-iq/sdk/

4. Open folder GarminSpotifyRemoteController\garmin\SpotifyEdge in visual studio code

5. Replace "TODO YOUR WATCH ID" with new application id in Manifest.xml.

6. Press CTRL + SHIFT + P and type "Monkey C: Generate a Developer Key"

7. Press CTRL + SHIFT + P and run the command Monkey C: Build for Device

8. Connect your garmin to computer using USB.

9. Copy SpotifyEdge.prg to GARMIN/APPS/ folder.

## C) Build AndroidApp

1. Download and install android studio
https://developer.android.com/studio

2. Copy GARMIN_WATCH_ID from the garmin app to MyService.kt

3. Register your android app on spotify developer
https://developer.spotify.com/dashboard

4. Set Redirect URIs to
http://localhost/

5. Set Android packages to
app.krakentom.garminspotifyremotecontroller

6. Copy Client ID from spotify developer (Basic Information) to SPOTIFY_CLIENT_ID variable.

7. In android studio click on Gradle in right toolbar. On top menu select "Execute Gradle Task" and run "gradle signingreport". 
Copy SHA1 from console.

8. Set Package SHA1 fingerprint from the gradle console in spotify developer settting section.

9. Connect phone with USB to computer

10. Enable developer mode on the phone and enable usb debuging.

11. Run app from android studio on phone.
