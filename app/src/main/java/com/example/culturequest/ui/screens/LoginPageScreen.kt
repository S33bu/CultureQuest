package com.example.culturequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults


@Composable
fun LoginPageScreen(
    onSignUpClick: () -> Unit, //what happens when "sign up" is clicked
    onSignInClick: (String, String) -> Unit    //what happens when "login" is clicked

) {
    //this page idea gotten from: https://www.oversimplifiedcoding.com/2024/03/Jetpack-Compose-Login-Screen-Example.html
    // UI state for the form inputs
    val formScroll = rememberScrollState()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

//root container
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
//upper circle decorative
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-60).dp, y = (-90).dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        //bottom circle decorative
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 120.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(180.dp)
                )
        )

//header text
        LoginHeader(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 120.dp, start = 16.dp),
        )
//the form part which is in a box, so if text is long only this part enlargens
        Box(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp).background(color = MaterialTheme.colorScheme.surface.copy(alpha=0.9f), shape = RoundedCornerShape(16.dp))
        )
        {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(formScroll),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            //email + password fields
            LoginFields(
                email.value,
                password.value,
                onEmailChange = { email.value = it },
                onPasswordChange = { password.value = it },
               /* onForgotPasswordClick = { /*TODO*/
                }*/
            )
            Spacer(modifier = Modifier.height(20.dp))
            //dont have account sign up
            SignUpFooter(
                onSignUpClick = onSignUpClick
            )


        }

        }
        //sign in button on the bottom right
        SignInFooter(
            onSignInClick = { onSignInClick(email.value, password.value) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp)
        )
    }
}

//login header text
@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) { Text(text = "Welcome Back", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary )

    }
}

//all of the fields in the form (email + password)
@Composable
fun LoginFields(
    email: String, password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
    //onForgotPasswordClick: () -> Unit,

) {
    val focusManager = LocalFocusManager.current
    Column {
        //email input
        TextField(
            value = email,
            label = "Email",
            placeholder = "Enter your email address",
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions (
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            textStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.titleMedium,
            placeholderStyle = MaterialTheme.typography.titleMedium

        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            //password input
            value = password,
            label = "Password",
            placeholder = "Enter your password",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions (
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            textStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.titleMedium,
            placeholderStyle = MaterialTheme.typography.titleMedium

        )
/* Might have functionality for now, right now the main flows are creation of a new account,
and logging in wiht user who has existing account
        TextButton(onClick = onForgotPasswordClick, modifier = Modifier.align(Alignment.End)) {
           Text(text = "Forgot Password?", style = MaterialTheme.typography.bodyLarge)
        }*/
    }
}

//button style so the button would be used across screens for same UI
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

//the singing up part of the form
@Composable
fun SignUpFooter(
    onSignUpClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Don't have an account?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(text = "Sign Up", onClick = onSignUpClick)
    }
}

//the sign in button on the bottom right
@Composable
fun SignInFooter(

    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(end= 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
        PrimaryButton(text = "Sign In ->", onClick = onSignInClick)
    }
}

//custom textfield wrapper adding theme colors

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
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style=labelStyle)
        },
        placeholder = {
            Text(text = placeholder, style =placeholderStyle)
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = textStyle,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )

    )
}