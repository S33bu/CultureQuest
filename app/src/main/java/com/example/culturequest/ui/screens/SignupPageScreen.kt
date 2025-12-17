package com.example.culturequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.culturequest.R

/**
 * Screen that handles new user registration.
 *
 * Provides fields for email and password entry, decorative background elements,
 * and integration with authentication state for error handling and loading states.
 *
 * @param onBackClick Callback to return to the previous screen (typically Login).
 * @param onSignupClick Callback to trigger the registration with user credentials.
 * @param isLoading Boolean state representing if a registration request is in progress.
 * @param errorMessage An optional string containing error messages from the auth service.
 * @param onClearError Callback to reset the error state when the user resumes typing.
 */
@Composable
fun SignupPageScreen(
    onBackClick: () -> Unit,
    onSignupClick: (String, String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val topRingColor = MaterialTheme.colorScheme.primary
    val bottomRingColor = MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
    ) {
        // upper decorative ring element
        Box(
            modifier =
                Modifier
                    .size(400.dp)
                    .offset((-60).dp, (-90).dp)
                    .drawBehind {
                        drawCircle(
                            color = topRingColor,
                            style = Stroke(width = 40.dp.toPx()), // thickness of the ring
                        )
                    },
        )
        // bottom decorative ring element
        Box(
            modifier =
                Modifier
                    .size(360.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 120.dp)
                    .drawBehind {
                        drawCircle(
                            color = bottomRingColor,
                            style = Stroke(width = 32.dp.toPx()),
                        )
                    },
        )
        SignUpHeader(
            onBackClick = onBackClick,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 50.dp, start = 16.dp),
        )
        // central container for input fields
        Box(
            modifier =
                Modifier
                    .align(
                        Alignment.Center,
                    ).padding(
                        horizontal = 32.dp,
                    ).background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp),
                    ),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SignupFields(
                    email.value,
                    password.value,
                    onEmailChange = {
                        email.value = it
                        onClearError()
                    },
                    onPasswordChange = {
                        password.value = it
                        onClearError()
                    },
                    onSubmit = {
                        onSignupClick(email.value, password.value)
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        SignupFooter(
            onSignupClick = { onSignupClick(email.value, password.value) },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 16.dp),
            isLoading = isLoading,
        )
    }
}

/**
 * Navigation and title header for the Signup screen.
 *
 * @param onBackClick Callback for the back navigation button.
 * @param modifier Layout modifiers for positioning.
 */
@Composable
fun SignUpHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.backbutton),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * Input fields for the signup form.
 *
 * Includes keyboard management to move focus between email and password inputs.
 *
 * @param email Current email input value.
 * @param password Current password input value.
 * @param onEmailChange Callback for email text changes.
 * @param onPasswordChange Callback for password text changes.
 * @param onSubmit Callback triggered by the IME 'Done' action.
 */
@Composable
fun SignupFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Column {
        TextField(
            value = email,
            label = "Email",
            placeholder = "Enter your email address",
            onValueChange = onEmailChange,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                ),
            textStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.titleMedium,
            placeholderStyle = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            label = "Create your password",
            placeholder = "Enter your wanted password",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        onSubmit()
                    },
                ),
            textStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.titleMedium,
            placeholderStyle = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * Footer for the signup screen containing the action button.
 *
 * @param onSignupClick Callback to initiate signup.
 * @param modifier Layout modifiers for alignment.
 * @param isLoading Whether the screen should display a loading state.
 */
@Composable
fun SignupFooter(
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
) {
    Row(
        modifier = modifier.padding(end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrimaryButton(
            text = if (isLoading) "Signing up..." else "Sign Up ->",
            onClick = onSignupClick,
            enabled = !isLoading,
        )
    }
}
