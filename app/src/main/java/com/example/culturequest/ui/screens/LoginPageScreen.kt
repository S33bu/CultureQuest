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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Screen responsible for user authentication.
 *
 * Provides a form for existing users to sign in using their email and password,
 * as well as a navigation path to the signup screen for new users.
 *
 * @param onSignUpClick Callback to navigate to the registration screen.
 * @param onSignInClick Callback to trigger authentication with provided credentials.
 * @param isLoading State indicating if an authentication request is currently active.
 * @param errorMessage Optional error message to display in case of login failure.
 * @param onClearError Callback to reset the error state when user input changes.
 */
@Composable
fun LoginPageScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: (String, String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
) {
    val formScroll = rememberScrollState()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        // upper circle decorative
        Box(
            modifier =
                Modifier
                    .size(400.dp)
                    .offset(x = (-60).dp, y = (-90).dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
        )
        // bottom circle decorative
        Box(
            modifier =
                Modifier
                    .size(360.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(180.dp),
                    ),
        )

        LoginHeader(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 120.dp, start = 16.dp),
        )

        // central login form container
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
                        .padding(24.dp)
                        .verticalScroll(formScroll),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LoginFields(
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
                        onSignInClick(email.value, password.value)
                    },
                )
                Spacer(modifier = Modifier.height(20.dp))

                SignUpFooter(
                    onSignUpClick = onSignUpClick,
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        SignInFooter(
            onSignInClick = { onSignInClick(email.value, password.value) },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 16.dp),
            isLoading = isLoading,
        )
    }
}

/**
 * Header section of the Login screen containing the greeting text.
 *
 * @param modifier Modifier for positioning and layout.
 */
@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

/**
 * Component containing the input fields for email and password.
 *
 * Manages focus movement and keyboard actions for the login form.
 *
 * @param email The current value of the email field.
 * @param password The current value of the password field.
 * @param onEmailChange Callback for changes in the email field.
 * @param onPasswordChange Callback for changes in the password field.
 * @param onSubmit Callback triggered when the user finishes the password field.
 */
@Composable
fun LoginFields(
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
            label = "Password",
            placeholder = "Enter your password",
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
 * Standardized button used throughout the application.
 *
 * @param text The text to display inside the button.
 * @param onClick The callback to trigger when clicked.
 * @param modifier Modifier for customization.
 * @param enabled Whether the button is interactive.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/**
 * Section prompting users to sign up if they do not have an account.
 *
 * @param onSignUpClick Callback for the signup action.
 */
@Composable
fun SignUpFooter(onSignUpClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Don't have an account?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(text = "Sign Up", onClick = onSignUpClick)
    }
}

/**
 * Footer providing the primary sign-in action button.
 *
 * @param onSignInClick Callback to trigger login.
 * @param modifier Layout modifiers.
 * @param isLoading Whether to show a loading text state.
 */
@Composable
fun SignInFooter(
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
) {
    Row(
        modifier = modifier.padding(end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrimaryButton(
            text = if (isLoading) "Signing in..." else "Sign In ->",
            onClick = onSignInClick,
            enabled = !isLoading,
        )
    }
}

/**
 * Custom wrapper around [OutlinedTextField] for consistent branding.
 */
@Composable
fun TextField(
    value: String,
    label: String,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    labelStyle: TextStyle = MaterialTheme.typography.titleMedium,
    placeholderStyle: TextStyle = MaterialTheme.typography.titleMedium,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = labelStyle)
        },
        placeholder = {
            Text(text = placeholder, style = placeholderStyle)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = textStyle,
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    )
}
