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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.culturequest.R

@OptIn(ExperimentalMaterial3Api::class)
/**
 * Composable function that displays the "About" page of the application.
 * It includes a description of the app, its features, and a header with navigation.
 * @param onBackClick A lambda function to be invoked when the back button is clicked.
 */
@Composable
fun AboutPageScreen(onBackClick: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Same dark green upper half circle as homepage
            TopSemicircleHeader(
                height = 300.dp,
                background = com.example.culturequest.ui.theme.Green80,
                iconSize = 50.dp,
                showBackButton = true,
                onBackClick = onBackClick
            )

            // About content - IMPROVED SPACING
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp), // Increased padding from 16dp to 32dp
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp) // Space between all main elements (before was "Center")
            ) {
                Text(
                    text = "About CultureQuest",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = com.example.culturequest.ui.theme.Green80,
                    modifier = Modifier.padding(bottom = 8.dp)  // Added padding to the bottom
                )

                //Spacer(modifier = Modifier.height(24.dp))
                // Description with better line spacing

                Text(
                    text = "CultureQuest is an engaging educational game that helps you learn about different cultures around the world through fun quizzes and interactive content.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp)
                )

                //Spacer(modifier = Modifier.height(16.dp))
                // Features section with better structure
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.headlineSmall,
                    color = com.example.culturequest.ui.theme.Green80
                )

                    // Features list with proper spacing
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        FeatureItem("Cultural quizzes")
                        FeatureItem("Interactive learning")
                        FeatureItem("Global traditions")
                        FeatureItem("Fun facts")
                    }
                }
                // Optional: Add some extra space at the bottom
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * A private composable function to display a single feature item with a bullet point.
 * @param text The string content of the feature to display.
 */
@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            color = com.example.culturequest.ui.theme.Green80,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * A private composable function that creates a semi-circular header.
 * It can optionally display a back button and always shows a profile icon.
 * @param height The height of the header box.
 * @param background The background color of the semi-circle.
 * @param iconSize The size for the icons in the header.
 * @param showBackButton A boolean to determine if the back button should be shown.
 * @param onBackClick A lambda function to be executed when the back button is pressed.
 */
@Composable
private fun TopSemicircleHeader(
    height: Dp,
    background: Color,
    iconSize: Dp,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .drawBehind {
                // Complex logic to draw a semi-circle that adapts to the screen width.
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h) // Calculate the diameter for the circle
                val left = (w - d) / 2f // Center the arc horizontally
                val top = -d / 2f // Position the arc's top half above the visible area

                // Draw the arc to form the semi-circle
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
        // Left: Back button or empty space
        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(R.drawable.about_us), // You might want a back icon
                    contentDescription = "Back",
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }
        } else {
            // Empty space to balance layout
            Spacer(
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.TopStart)
            )
        }

        // Right: Profile icon (consistent with homepage)
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

        // Center title
        Text(
            text = "About Us",
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