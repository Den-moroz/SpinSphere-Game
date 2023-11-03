package core.basesyntax.whiteball.presentation.usecases

import android.util.Log

class FrameClock {
    var frameCount = 0
    var lastTimeMillis = System.currentTimeMillis()

    fun update() {
        frameCount++
        val currentTimeMillis = System.currentTimeMillis()
        val elapsedTime = currentTimeMillis - lastTimeMillis
        if (elapsedTime >= 1000) {
            val fps = frameCount * 1000 / elapsedTime
            Log.d("FPS", "FPS: $fps")
            frameCount = 0
            lastTimeMillis = currentTimeMillis
        }
    }
}
