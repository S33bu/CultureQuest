
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import com.example.culturequest.R
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun SignupPageScreen(
    onBackClick: () -> Unit,
    onSignupClick: (String, String) -> Unit
) {
    //form state
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val topRingColor = MaterialTheme.colorScheme.primary
    val bottomRingColor = MaterialTheme.colorScheme.secondaryContainer

    //root container
    Box(modifier = Modifier.fillMaxSize().background(color=MaterialTheme.colorScheme.background)) {
//upper circle decorative but now only the thick line part
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset((-60).dp, (-90).dp)
                .drawBehind {
                    drawCircle(
                        color = topRingColor,
                        style = Stroke(width = 40.dp.toPx()) // thickness of the ring
                    )
                }
        )
        //bottom circle decorative but now only the thick line aprt
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 120.dp)
                .drawBehind {
                    drawCircle(
                        color = bottomRingColor,
                        style = Stroke(width = 32.dp.toPx())
                    )
                }
        )

        //create account text + back icon
        SignUpHeader(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart).padding(top = 50.dp, start = 16.dp),
        )

        //the same box as in login page but now without dont have account part
        Box(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp).background(color = MaterialTheme.colorScheme.surface.copy(alpha=0.9f), shape = RoundedCornerShape(16.dp))
        ) {
                Column(
                    modifier = Modifier
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    //email + password fields
                    SignupFields(
                        email.value,
                        password.value,
                        onEmailChange = { email.value = it },
                        onPasswordChange = { password.value = it }
                    )
                }
            }
            //sign in button on the bottom right
            SignupFooter(
                onSignupClick = { onSignupClick(email.value, password.value) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 16.dp)
            )
        }
    }


@Composable
fun SignUpHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) { IconButton(onClick = onBackClick) {
        Icon( //back icon
            painter = painterResource(id = R.drawable.backbutton),
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary
        )
    }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Create account", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary )

    }
}

//basically the same fieldsa as in lgin but without sign up
@Composable
fun SignupFields(
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
            label = "Create your password",
            placeholder = "Enter your wanted password",
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
    }
}

//the sign in button on the bottom right
@Composable
fun SignupFooter(
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(end= 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
        PrimaryButton(text = "Sign In ->", onClick = onSignupClick)
    }
}