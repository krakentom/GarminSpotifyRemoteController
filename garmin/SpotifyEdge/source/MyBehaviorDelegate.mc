using Toybox.System;
using Toybox.WatchUi;

class MyBehaviorDelegate extends WatchUi.BehaviorDelegate {
    function initialize() {
        WatchUi.BehaviorDelegate.initialize();
    }

    function onKey(keyEvent) {
        var menu = new WatchUi.Menu();

        menu.addItem("Next song", :nextSong);
        menu.addItem("Play/Pause", :playPause);
        menu.addItem("Volume up", :volumeUp);
        menu.addItem("Volume down", :volumeDown);
        menu.addItem("Like/Unlike song", :likeUnlikeSong);
        
        WatchUi.pushView(menu, new MyMenuDelegate(), SLIDE_IMMEDIATE);

        return true;
    }
}