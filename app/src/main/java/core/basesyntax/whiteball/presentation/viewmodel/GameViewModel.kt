package core.basesyntax.whiteball.presentation.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import core.basesyntax.whiteball.presentation.model.GameState
import core.basesyntax.whiteball.R
import core.basesyntax.whiteball.presentation.model.Block
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameViewModel(private val context: Context) : ViewModel() {
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var isBallClockwise by mutableStateOf(true)
    var isBlockClockwise by mutableStateOf(true)
    var gameState by mutableStateOf<GameState>(GameState.Playing)

    private val _backgroundColor = mutableStateOf(randomColor())
    val backgroundColor get() = _backgroundColor.value

    var yellowSquarePosition by mutableStateOf(generateRandomPositionOnCircle())
    var canSpawnYellowSquare by mutableStateOf(true)

    var elapsedTime by mutableStateOf(0L)
    private var mediaPlayer: MediaPlayer? = null

    var blocks: List<Block> = emptyList()

    init {
        resetGame()
    }

    fun generateRandomPositionOnCircle(): Offset {
        val angle = Random.nextFloat() * 360f
        val x = cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = sin(Math.toRadians(angle.toDouble())).toFloat()
        return Offset(x, y)
    }

    fun increaseScore() {
        score++
    }

    fun resetGame() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        score = 0
        isGameOver = false
        isBallClockwise = true
        isBlockClockwise = true
        _backgroundColor.value = randomColor()
    }

    fun toggleBallRotationDirection() {
        isBallClockwise = !isBallClockwise
    }

    fun stopGame() {
        isGameOver = true
        gameState = GameState.GameOver(backgroundColor, elapsedTime, score)

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    fun startGame() {
        mediaPlayer = MediaPlayer.create(context, R.raw.background_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        viewModelScope.launch {
            elapsedTime = 0L
            while (!isGameOver) {
                delay(1000L)
                elapsedTime += 1000L
            }

            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    private fun randomColor(): Color {
        val random = Random.Default
        return Color(
            red = random.nextFloat(),
            green = random.nextFloat(),
            blue = random.nextFloat(),
            alpha = 1f
        )
    }
}

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
