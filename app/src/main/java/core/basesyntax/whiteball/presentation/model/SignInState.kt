package core.basesyntax.whiteball.presentation.model

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
