package core.basesyntax.whiteball.presentation.model

import androidx.compose.ui.graphics.Color

sealed class GameState {
    object Playing : GameState()
    data class GameOver(val backgroundColor: Color, val elapsedTime: Long, val score: Int) : GameState()
}
