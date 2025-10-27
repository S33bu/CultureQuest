package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.example.culturequest.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
/**
 * Composable function that displays the "About" page of the application.
 * This screen provides users with information about the CultureQuest app, including a welcome message,
 * a brief description, and a list of its key features. It features a semi-circular header,
 * a background image, and scrollable content.
 * @param onBackClick A lambda function to be invoked when the back button is clicked.
 */
@Composable
fun AboutPageScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.maa),
                contentDescription = null, // decorative
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.15f)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopSemicircleHeader(
                    height = 300.dp,
                    background = com.example.culturequest.ui.theme.Green80,
                    iconSize = 50.dp,
                    showBackButton = true,
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        .offset(y = (-90).dp) // Move the content up to overlap with the header
                        .graphicsLayer { alpha = 0.99f } // prevent rendering artifacts
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Text(
                        text = "Welcome! Ready to explore world cultures?",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "CultureQuest is an engaging educational game that helps you learn about different cultures around the world through fun quizzes and interactive content.",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Features:",
                            style = MaterialTheme.typography.titleLarge,
                            color = com.example.culturequest.ui.theme.Green80
                        )

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
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * A private composable function to display a single feature item with a bullet point.
 * This creates a consistent layout for listing features within the "About" page.
 *
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
            text = text, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f)
        )
    }
}

/**
 * A private composable function that creates a semi-circular header.
 * This header is drawn using a `drawBehind` modifier to create the arc shape.
 * It includes slots for an optional back button and a profile icon, providing consistent navigation elements.
 * The diameter of the arc's circle is determined by the smaller of the width or twice the height,
 * ensuring the arc fits correctly.
 * The title "About Us" is centered within this header.
 *
 * @param height The height of the header composable, which influences the arc's curvature.
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
                val w = size.width
                val h = size.height
                val d = minOf(w, 2f * h) // Calculate the diameter for the circle
                val left = (w - d) / 2f // Center the arc horizontally
                val top = -d / 2f // Position the arc's top half above the visible area

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
        if (showBackButton) {
            IconButton(
                onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(R.drawable.about_us), // You might want a back icon
                    contentDescription = "Back",
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.TopStart)
            )
        }

        IconButton(
            onClick = {}, modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(R.drawable.profile_icon),
                contentDescription = "Profile",
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Text(
                text = "About Us",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}