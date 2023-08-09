package app.krakentom.garminspotifyremotecontroller.models

class PlayerInfo(
    var song: String = "",
    var artist: String = "",
    var duration: Long = 0,
    var isInLibrary: Boolean = false,
    var volume: Float = 0f
) {
    fun toMap(): Map<String, Any> {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        val volumePercentage = (volume * 100).toInt()

        val map = mapOf(
            "song" to song,
            "artist" to artist,
            "length" to "%02d:%02d".format(minutes, remainingSeconds),
            "isInLibrary" to isInLibrary,
            "volume" to "$volumePercentage%"
        )

        return map
    }
}