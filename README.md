# GarminSpotifyRemoteController

## How it works

The Garmin application sends commands such as "play/stop," "next song," "volume up," "volume down," and "add to library" to the phone and waits for a response. The phone sends information about the currently playing track back to the Garmin application. The Android app uses the Garmin Connect IQ SDK to communicate with the Garmin device and waits for commands from it. When a command is received, the app calls the appropriate function through the Spotify SDK to control Spotify.

## A) Download source code from GarminSpotifyRemoteController from github
https://github.com/krakentom/GarminSpotifyRemoteController

## B) Build GarminApp

1. Download and install visual studio code
https://code.visualstudio.com/

2. Install MonkeyC extension from Garmin (Extension ID: Garmin.monkey-c)

3. Register on Garmin, download and install Connect IQ SDK Manager
https://developer.garmin.com/connect-iq/sdk/

4. Extract "connectiq-sdk-manager-windows.zip" and run "sdkmanager.exe" click on the "Login" button and continue with instalation.

5. Download SDK and Device for Garmin Edge 530

6. Open folder GarminSpotifyRemoteController\garmin\SpotifyEdge in visual studio code

7. Press CTRL + P and type "manifest.xml" end press "ENTER", it will open manifest.xml file. Locate the "App UUID" section and click on the "Regenerate" button. This will replace "TODO YOUR WATCH ID" with the new application id. Copy this ID for future use in the Android app.

8. Press CTRL + SHIFT + P and type "Monkey C: Generate a Developer Key"

9. Press CTRL + SHIFT + P and run the command Monkey C: Build for Device

10. Connect your garmin to computer using USB.

11. Copy SpotifyEdge.prg to GARMIN/APPS/ folder.
    
12. Disconnect your Garmin device from USB.

## C) Build AndroidApp

1. Download and install android studio
https://developer.android.com/studio

2. Paste the "GARMIN_WATCH_ID" from the "manifest.xml" file in the garmin project app into the MyService.kt file in the Android project.

3. In android studio click on Gradle in right toolbar. On top menu select "Execute Gradle Task" and run "gradle signingreport". 
Copy SHA1 hash from console.

4. Register your android app on spotify developer
https://developer.spotify.com/dashboard

5. In Spotify developer dashboard add new android package with package name "app.krakentom.garminspotifyremotecontroller" and package sha1 fingerprint from gradle console.
Save changes.

7. Set Redirect URIs to
http://localhost/

8. Copy the Client ID from the spotify developer (Basic Information) and paste it into the "SPOTIFY_CLIENT_ID" variable in the "MainActivity.kt" file.

9. Connect phone with USB to computer

10. Enable developer mode on the phone and enable usb debuging.

11. Run app from android studio on phone.
