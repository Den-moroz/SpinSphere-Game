package core.basesyntax.whiteball.presentation.ui

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import core.basesyntax.whiteball.presentation.model.GameState
import core.basesyntax.whiteball.R
import core.basesyntax.whiteball.presentation.navigation.Screen
import core.basesyntax.whiteball.WhiteBallApplication
import core.basesyntax.whiteball.data.entity.GameHistory
import core.basesyntax.whiteball.presentation.model.Block
import core.basesyntax.whiteball.presentation.model.Constants
import core.basesyntax.whiteball.presentation.usecases.FrameClock
import core.basesyntax.whiteball.presentation.viewmodel.GameHistoryViewModel
import core.basesyntax.whiteball.presentation.viewmodel.GameHistoryViewModelFactory
import core.basesyntax.whiteball.presentation.viewmodel.GameViewModel
import core.basesyntax.whiteball.presentation.viewmodel.GameViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GameActivity(navController: NavController) {
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(LocalContext.current.applicationContext))
    var gameStarted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (!gameStarted) {
            StartGameButton {
                gameStarted = true
                gameViewModel.startGame()
            }
        } else {
            StartGame(navController, gameViewModel)

            val gameState = gameViewModel.gameState
            val showScoreAndTimer = gameState is GameState.Playing

            if (showScoreAndTimer) {
                val timerText = stringResource(id = R.string.time_representation,
                    gameViewModel.elapsedTime / 1000 / 60,
                    gameViewModel.elapsedTime / 1000 % 60)

                Text(
                    text = "Timer: ${timerText}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(end = 16.dp, top = 16.dp)
                )

                Text(
                    text = "${gameViewModel.score}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 45.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun StartGameButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onClick) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Start Game")
        }
    }
}

@Composable
fun StartGame(navController: NavController, viewModel: GameViewModel) {
    val gameHistoryViewModel: GameHistoryViewModel = viewModel(
        factory = GameHistoryViewModelFactory(
            (LocalContext.current.applicationContext as WhiteBallApplication).database.gameHistoryDao()
        ))

    when (val state = viewModel.gameState) {
        is GameState.Playing -> {
            WhiteBallGame(gameViewModel = viewModel, gameHistoryViewModel = gameHistoryViewModel)
        }
        is GameState.GameOver -> {
            GameOverScreen(
                backgroundColor = state.backgroundColor,
                elapsedTime = state.elapsedTime,
                score = state.score,
                onMenuClick = {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.MainScreen.route, inclusive = true)
                        .build()

                    navController.navigate(Screen.MainScreen.route, navOptions)
                },
                onTryAgainClick = {
                    viewModel.resetGame()
                    viewModel.startGame()
                    viewModel.gameState = GameState.Playing
                }
            )
        }
    }
}

@Composable
fun WhiteBallGame(gameViewModel: GameViewModel, gameHistoryViewModel: GameHistoryViewModel) {
    var ballRotationAngle by remember { mutableStateOf(0f) }
    var blockRotationAngle by remember { mutableStateOf(0f) }

    val circleRadius = Constants.CIRCLE_RADIUS
    val ballRadius = Constants.BALL_RADIUS
    val blockWidth = Constants.BLOCK_WIDTH
    val blockHeight = Constants.BLOCK_HEIGHT
    val yellowSquareSize = Constants.YELLOW_SQUARE_SIZE
    val padding = Constants.PADDING
    val increasedPadding = Constants.INCREASED_PADDING

    val frameClock = remember { FrameClock() }
    var fps by remember { mutableStateOf(0) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (!gameViewModel.isGameOver) {
                        gameViewModel.toggleBallRotationDirection()
                    }
                }
            }
    ) {
        frameClock.update()
        val currentTimeMillis = System.currentTimeMillis()
        val elapsedTime = currentTimeMillis - frameClock.lastTimeMillis
        if (elapsedTime > 0) {
            fps = (frameClock.frameCount * 1000 / elapsedTime).toInt()
        } else {
            fps = 0
        }

        drawRect(color = gameViewModel.backgroundColor, size = size)

        val centerX = center.x
        val centerY = center.y

        drawArc(
            color = Color(0f, 0f, 0f, 0.25f),
            startAngle = 0f,
            sweepAngle = 360f,
            topLeft = Offset(centerX - circleRadius, centerY - circleRadius),
            size = Size(circleRadius * 2, circleRadius * 2),
            useCenter = false,
            style = Stroke(width = 40.dp.toPx())
        )

        val firstBlockX = centerX + circleRadius * cos(Math.toRadians(blockRotationAngle.toDouble())).toFloat()
        val firstBlockY = centerY - 200f - padding
        drawRect(color = Color.Black, size = Size(blockWidth, blockHeight), topLeft = Offset(firstBlockX, firstBlockY))

        val secondBlockX = centerX + circleRadius * cos(Math.toRadians(blockRotationAngle + 120f.toDouble())).toFloat()
        val secondBlockY = firstBlockY + blockHeight + increasedPadding
        drawRect(color = Color.Black, size = Size(blockWidth, blockHeight), topLeft = Offset(secondBlockX, secondBlockY))

        val thirdBlockX = centerX + circleRadius * cos(Math.toRadians(blockRotationAngle - 120f.toDouble())).toFloat()
        val thirdBlockY = secondBlockY + blockHeight + increasedPadding
        drawRect(color = Color.Black, size = Size(blockWidth, blockHeight), topLeft = Offset(thirdBlockX, thirdBlockY))

        gameViewModel.blocks = listOf(
            Block(x = firstBlockX, y = firstBlockY),
            Block(x = secondBlockX, y = secondBlockY),
            Block(x = thirdBlockX, y = thirdBlockY)
        )

        if (!gameViewModel.isGameOver) {
            if (gameViewModel.isBallClockwise) {
                ballRotationAngle += 2f
            } else {
                ballRotationAngle -= 2f
            }

            if (gameViewModel.isBlockClockwise) {
                blockRotationAngle += 2f
            } else {
                blockRotationAngle -= 2f
            }

            val ballX = centerX + circleRadius * cos(Math.toRadians(ballRotationAngle.toDouble())).toFloat()
            val ballY = centerY + circleRadius * sin(Math.toRadians(ballRotationAngle.toDouble())).toFloat()

            for (block in gameViewModel.blocks) {
                val blockBoundingBox = RectF(block.x, block.y, block.x + blockWidth, block.y + blockHeight)
                val ballBoundingBox = RectF(ballX - ballRadius, ballY - ballRadius, ballX + ballRadius, ballY + ballRadius)

                if (ballBoundingBox.intersect(blockBoundingBox)) {
                    endGame(gameHistoryViewModel, gameViewModel)
                    return@Canvas
                }
            }

            val distanceToYellowSquare = sqrt(
                (ballX - (centerX + gameViewModel.yellowSquarePosition.x * circleRadius)).pow(2) +
                        (ballY - (centerY + gameViewModel.yellowSquarePosition.y * circleRadius)).pow(2)
            )

            val collisionThreshold = ballRadius + yellowSquareSize / 2

            if (distanceToYellowSquare < collisionThreshold) {
                if (gameViewModel.canSpawnYellowSquare) {
                    gameViewModel.increaseScore()
                    gameViewModel.canSpawnYellowSquare = false
                    gameViewModel.yellowSquarePosition = gameViewModel.generateRandomPositionOnCircle()

                    gameViewModel.viewModelScope.launch {
                        delay(500L)
                        gameViewModel.canSpawnYellowSquare = true
                    }
                }
            }

            val distanceToFirstBlock = sqrt((ballX - firstBlockX).pow(2) + (ballY - firstBlockY).pow(2))
            if (distanceToFirstBlock < ballRadius + blockWidth / 2) {
                endGame(gameHistoryViewModel, gameViewModel)
            }

            val distanceToSecondBlock = sqrt((ballX - secondBlockX).pow(2) + (ballY - secondBlockY).pow(2))
            if (distanceToSecondBlock < ballRadius + blockWidth / 2) {
                endGame(gameHistoryViewModel, gameViewModel)
            }

            val distanceToThirdBlock = sqrt((ballX - thirdBlockX).pow(2) + (ballY - thirdBlockY).pow(2))
            if (distanceToThirdBlock < ballRadius + blockWidth / 2) {
                endGame(gameHistoryViewModel, gameViewModel)
            }

            drawRect(
                color = Color.Yellow,
                size = Size(yellowSquareSize, yellowSquareSize),
                topLeft = Offset(
                    centerX + gameViewModel.yellowSquarePosition.x * circleRadius - yellowSquareSize / 2,
                    centerY + gameViewModel.yellowSquarePosition.y * circleRadius - yellowSquareSize / 2
                )
            )

            drawCircle(color = Color.White, radius = ballRadius, center = Offset(ballX, ballY))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Text(
            text = "fps: $fps",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun GameOverScreen(
    backgroundColor: Color,
    elapsedTime: Long,
    score: Int,
    onMenuClick: () -> Unit,
    onTryAgainClick: () -> Unit
) {
    var buttonsEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = backgroundColor, size = size)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Game Over",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.White
                )
            )

            Text(
                text = "Timer: ${String.format("%02d:%02d", elapsedTime / 1000 / 60, elapsedTime / 1000 % 60)}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            )

            Text(
                text = "Score: $score",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomButton(
                    text = "Menu",
                    onClick = {
                        if (buttonsEnabled) {
                            buttonsEnabled = false
                            onMenuClick()
                        }
                    },
                    icon = Icons.Default.ExitToApp,
                    backgroundColor = Color.Red,
                    isEnabled = buttonsEnabled
                )
                CustomButton(
                    text = "Try Again",
                    onClick = {
                        if (buttonsEnabled) {
                            buttonsEnabled = false
                            onTryAgainClick()
                        }
                    },
                    icon = Icons.Default.PlayArrow,
                    backgroundColor = Color.Green,
                    isEnabled = buttonsEnabled
                )
            }
        }
    }
}

private fun endGame(gameHistoryViewModel: GameHistoryViewModel, gameViewModel: GameViewModel) {
    val colorInt = (gameViewModel.backgroundColor.alpha.times(255).toInt() shl 24) or
            (gameViewModel.backgroundColor.red.times(255).toInt() shl 16) or
            (gameViewModel.backgroundColor.green.times(255).toInt() shl 8) or
            (gameViewModel.backgroundColor.blue.times(255).toInt())

    gameHistoryViewModel.insert(GameHistory(
        points = gameViewModel.score,
        characterLifespan = gameViewModel.elapsedTime,
        backgroundColor = colorInt))
    gameViewModel.stopGame()
}
