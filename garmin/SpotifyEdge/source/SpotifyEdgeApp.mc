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
        _song = msg.data["song"];
        _artist = msg.data["artist"];
        _length = msg.data["length"];
        _isInLibrary = msg.data["isInLibrary"];
        WatchUi.requestUpdate();
    }
}

var _song = "";
var _artist = "";
var _length = "";
var _isInLibrary = "";

function getApp() as SpotifyEdgeApp {
    return Application.getApp() as SpotifyEdgeApp;
}