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
    onAboutClick: () -> Unit,
    onGameClick: () -> Unit,
    gameViewModel: GameViewModel = viewModel(),
) {
    val user by gameViewModel.user.collectAsState()
    val score = user?.score ?: 0

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TopSemicircleHeader(
                height = 300.dp,
                background = com.example.culturequest.ui.theme.Green80,
                iconSize = 50.dp,
                onAboutClick = onAboutClick
            )

            // Display the score below the top semicircle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1f))

            BottomSemiCircle(
                height = 180.dp,
                background = com.example.culturequest.ui.theme.GreenYellow80,
                onGameClick = onGameClick
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopSemicircleHeader(
    height: Dp,
    background: Color,
    iconSize: Dp,
    onAboutClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
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
            },
        contentAlignment = Alignment.TopCenter
    ) {
        IconButton(
            onClick = onAboutClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.about_us),
                contentDescription = "About",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }

        IconButton(
            onClick = {},
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.profile_icon),
                contentDescription = "Profile",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }

        Text(
            text = "Culture\nQuest",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
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
    height: Dp,
    background: Color,
    onGameClick: () -> Unit
) {
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
            },
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onGameClick,
            modifier = Modifier.padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = com.example.culturequest.ui.theme.GreenYellow80
            )
        ) {
            Text("Play now", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
