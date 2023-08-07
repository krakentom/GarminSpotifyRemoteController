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
            dc.getHeight() / 2 -100,
            Graphics.FONT_SMALL,
            _song,
            Graphics.TEXT_JUSTIFY_CENTER
        );
        dc.drawText(
            dc.getWidth() / 2,
            dc.getHeight() / 2 - 50,
            Graphics.FONT_SMALL,
            _artist,
            Graphics.TEXT_JUSTIFY_CENTER
        );
        dc.drawText(
            dc.getWidth() / 2,
            dc.getHeight() / 2,
            Graphics.FONT_SMALL,
            _length,
            Graphics.TEXT_JUSTIFY_CENTER
        );
        dc.drawText(
            dc.getWidth() / 2,
            dc.getHeight() / 2 + 50,
            Graphics.FONT_SMALL,
            _isInLibrary ? "In library" : "Not in library",
            Graphics.TEXT_JUSTIFY_CENTER
        );
    }
}