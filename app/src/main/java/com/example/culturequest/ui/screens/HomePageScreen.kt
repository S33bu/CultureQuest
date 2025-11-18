package com.example.culturequest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.example.culturequest.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAboutClick: () -> Unit, //what happens when "About" is clicked
    onGameClick: () -> Unit,    //what happens when "Play now" is clicked
    onProfileClick: () -> Unit,
    lastGameScore: Int = 0,
    onBackToLoginClick: () -> Unit,   // ← NEW
    gameViewModel: GameViewModel = viewModel(), // gets the GameViewModel
) {
    val user by gameViewModel.user.collectAsState()

    //Scaffold gives layout structure (top, bottom, main content)
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TopSemicircleHeader(
                height = 300.dp,
                background = MaterialTheme.colorScheme.primary,
                iconSize = 50.dp,
                onAboutClick = onAboutClick,
                onProfileClick = onProfileClick
            )

            // Display the score below the top semicircle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Last Game Score: $lastGameScore",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Best Score: ${user?.bestScore ?: 0}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }

            }

            Spacer(Modifier.weight(1f))

            BottomSemiCircle(
                height = 180.dp,
                background = MaterialTheme.colorScheme.secondaryContainer,
                onGameClick = onGameClick
            )
            // Temporary Back to Login button
            TextButton(
                onClick = { onBackToLoginClick() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text("← Back to Login")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopSemicircleHeader(
    height: Dp, background: Color, iconSize: Dp, onAboutClick: () -> Unit = {}, onProfileClick: () -> Unit = {}
) {
    //Box draws the semicircle background and places icons/text inside it
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            //draw top semicircle
            .drawBehind {
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h)
                val left = (w - d) / 2f
                val top = -d / 2f

                drawArc(
                    color = background,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(left, top),
                    size = Size(d, d)
                )
            }, contentAlignment = Alignment.TopCenter
    ) {
        //About button
        IconButton(
            onClick = onAboutClick, modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.about_us),
                contentDescription = "About",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        //Profile button
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.profile_icon),
                contentDescription = "Profile",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        //Title in the middle
        Text(
            text = "Culture\nQuest",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
private fun BottomSemiCircle(
    height: Dp, background: Color, onGameClick: () -> Unit
) {
    //Box draws the bottom semicircle and places the button inside it
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
            .drawBehind {
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h)
                val left = (w - d) / 2f
                val top = h - d / 2f

                drawArc(
                    color = background,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(left, top),
                    size = Size(d, d)
                )
            }, contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onGameClick,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(200.dp) // Set a fixed width for the button
                .height(60.dp), // Set a fixed height for the button
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Play now", style = MaterialTheme.typography.titleLarge) // Increased text size
        }

    }
}
