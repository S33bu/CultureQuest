package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.culturequest.R
import com.example.culturequest.ui.theme.Green80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePageScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.maa),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.15f)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ProfileHeader(
                    height = 300.dp,
                    background = Green80,
                    iconSize = 50.dp,
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        .offset(y = (-90).dp)
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Player Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    ProfileItem(label = "Name", value = "Alex Johnson")
                    ProfileItem(label = "High Score", value = "12,450")
                    ProfileItem(label = "Games Played", value = "36")
                    ProfileItem(label = "Last Played", value = "October 25, 2025")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Keep exploring and unlocking new cultural adventures!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = Green80
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        Divider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.4f))
    }
}

@Composable
private fun ProfileHeader(
    height: Dp,
    background: Color,
    iconSize: Dp,
    onBackClick: () -> Unit
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
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.about_us), // could replace with arrow_back icon
                contentDescription = "Back",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }

        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
