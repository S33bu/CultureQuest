package com.example.culturequest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import com.example.culturequest.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAboutClick: () -> Unit) {  // Add onAboutClick parameter
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //dark green upper half circle for background
            TopSemicircleHeader(
                height = 300.dp,
                background = com.example.culturequest.ui.theme.Green80,
                iconSize = 50.dp,
                onAboutClick = onAboutClick // Pass the click handler
            )

            //between empty for now, need to put game score
            Spacer(Modifier.weight(1f))

            //lighter green bottom half circle
            BottomSemiCircle(
                height = 180.dp,
                background = com.example.culturequest.ui.theme.GreenYellow80
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
    onAboutClick: () -> Unit = {}   // Add onAboutClick parameter
) {
    Box(
        // Content inside the semicircle
        modifier = Modifier
            .fillMaxWidth() //as wide as the screen
            .height(height) //how tall
            .drawBehind {
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h) //to choose the diameter of the circle
                val left = (w - d) / 2f //centering horizontally
                val top = -d / 2f //moves upward

                drawArc(
                    color = background,
                    startAngle = 0f,
                    sweepAngle = 180f, //draws 180 of the circle
                    useCenter = true,
                    topLeft = Offset(left, top),
                    size = Size(d, d)
                )
            },
        contentAlignment = Alignment.TopCenter
    )
        {
            // Left: About icon
            IconButton(
                onClick = onAboutClick, // Pass the click handler
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(R.drawable.about_us),
                    contentDescription = "About",
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }

            // Right: Profile icon
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

            // Center title inside the arc
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
) {
    Box(
        // Content inside the semicircle
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
            .drawBehind {
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h)
                val left = (w - d) / 2f
                val top = h-d /2f //moves the circle down

                drawArc(
                    color = background,
                    startAngle = 180f, //need to start here, for the top needed to start at the 0f
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(left, top), // circle center on top edge
                    size = Size(d, d)
                )
            },
        contentAlignment = Alignment.Center
    )
    {
//button for starting the game
        Button(
            onClick = {},
            modifier = Modifier
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = com.example.culturequest.ui.theme.GreenYellow80
            )
        ) {Text("Play now", style = MaterialTheme.typography.bodyLarge)}
    }
}
