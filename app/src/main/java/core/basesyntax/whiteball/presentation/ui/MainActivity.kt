package core.basesyntax.whiteball.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import core.basesyntax.whiteball.presentation.navigation.Screen
import core.basesyntax.whiteball.ui.theme.WhiteBallTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteBallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadingScreen(onLoadingComplete = { navigateToMainScreen() })
                }
            }
        }
    }

    private fun navigateToMainScreen() {
        setContent {
            WhiteBallTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) { MainScreen(navController) }
                    composable(route = Screen.GameScreen.route) { GameActivity(navController) }
                    composable(route = Screen.WebView.route) { WebViewComponent() }
                    composable(route = Screen.GameHistory.route) { GameHistoryScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(onLoadingComplete: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
        onLoadingComplete.invoke()
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}
