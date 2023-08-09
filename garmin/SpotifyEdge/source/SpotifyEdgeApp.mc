import Toybox.Application;
import Toybox.Lang;
import Toybox.WatchUi;
import Toybox.Communications;

class SpotifyEdgeApp extends Application.AppBase {

    function initialize() {
        AppBase.initialize();

        Communications.registerForPhoneAppMessages(method(:onPhone) as Toybox.Communications.PhoneMessageCallback);
    }

    function onStart(state as Dictionary?) as Void {
    }

    function onStop(state as Dictionary?) as Void {
    }

    function getInitialView() as Array<Views or InputDelegates>? {
        return [ new SpotifyEdgeView(), new MyBehaviorDelegate() ] as Array<Views or InputDelegates>;
    }

    function onPhone(msg) {
        /* TEST DATA
        {
            "song": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit",
            "artist":"Lorem ipsum",
            "length":5000,
            "isInLibrary":false
        }
        */

        var song = msg.data["song"];
        var artist = msg.data["artist"];
        var length = msg.data["length"];
        var isInLibrary = msg.data["isInLibrary"];
        var volume = msg.data["volume"];

        _songInfo = song + "\n\n"
            + artist + "\n\n"
            + length + "\n\n"
            + "volume " + volume + "\n\n"
            + (song == "" ? "" : isInLibrary ? "in library" : "not in library");

        WatchUi.requestUpdate();
    }
}

var _songInfo = "";

function getApp() as SpotifyEdgeApp {
    return Application.getApp() as SpotifyEdgeApp;
}