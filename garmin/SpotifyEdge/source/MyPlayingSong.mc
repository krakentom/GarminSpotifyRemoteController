using Toybox.WatchUi;
using Toybox.Graphics;
import Toybox.Lang;

class MyPlayingSong extends WatchUi.Drawable {

    public function initialize(params as Dictionary) {
        Drawable.initialize(params);
    }

    function draw(dc as Graphics.Dc) as Void {
        dc.clear();
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_BLACK);
        dc.drawText(
            dc.getWidth() / 2,
            dc.getHeight() / 2,
            Graphics.FONT_MEDIUM,
            Graphics.fitTextToArea(_songInfo, Graphics.FONT_MEDIUM, dc.getWidth()-10, dc.getHeight(), true),
            Graphics.TEXT_JUSTIFY_CENTER | Graphics.TEXT_JUSTIFY_VCENTER
        );
    }
}