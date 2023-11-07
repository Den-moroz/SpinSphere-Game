package core.basesyntax.whiteball.presentation.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object GameScreen : Screen("game_screen")
    object WebView : Screen("web_screen")
    object GameHistory : Screen("game_history")
    object SignInScreen: Screen("sign_in_screen")
}
