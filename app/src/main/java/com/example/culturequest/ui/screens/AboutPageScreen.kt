package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.example.culturequest.data.Theme
import com.example.culturequest.ui.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPageScreen(
    onBackClick: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // State for the scrollable column
    val scrollState = rememberScrollState()
    // Observe the current theme from the view model
    val theme by settingsViewModel.theme.collectAsState()

    Scaffold { padding ->
        // Main container Box
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Image(
                painter = painterResource(id = R.drawable.maa),
                contentDescription = null, // decorative
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.15f)
            )


                //top circle
                Box(
                    modifier = Modifier
                        .size(600.dp)
                        .offset(y = (-320).dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Column(
                    modifier = Modifier.fillMaxSize()

                ) {
                    // HEADER INSIDE TOP CIRCLE
                    PageHeader(
                        onBackClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )

                    Spacer(modifier = Modifier.height(100.dp))


                    // Scrollable content section
                    Column(
                        modifier = Modifier
                            .offset(y = (-90).dp)
                            .graphicsLayer { alpha = 0.99f }
                            .verticalScroll(scrollState)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp) // Adjusted spacing
                    ) {
                        // Welcome message
                        Text(
                            text = "Ready to explore the world — one random corner at a time?",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )

                        // App description text
                        Text(
                            text = "CultureQuest drops you into real Google Maps panoramas from anywhere in the world — \n" +
                                    "quiet villages, busy streets, beaches, or even parks where people are doing yoga.\n" +
                                    "\n" +
                                    "Some places are easy to recognise; others look so familiar that guessing feels impossible. \n" +
                                    "That’s why you get tiered hints: area and landlocked status, then region, borders, currency, \n" +
                                    "and finally subregion and capital." +  "\n" +
                                    "You have 1 minute to guess as many countries as you can. Your best score appears on the \n" +
                                    "global leaderboard, so you can see how you rank against players worldwide.",

                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        // Features section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Features:",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                FeatureItem("Random Google Maps panoramas")
                                FeatureItem("Tiered hints: from area to capital")
                                FeatureItem("1-minute time-based rounds")
                                FeatureItem("Global leaderboard and best scores")
                            }
                        }

                        // Component to select the app theme (Light/Dark)
                        ThemeSelector(
                            selectedTheme = theme,
                            onThemeSelected = { settingsViewModel.saveTheme(it) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

            }
        }
    }
}

@Composable
fun PageHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        //icons at the very top
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
            )
            {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = "About",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

        }
        Text(
            text = "Culture \n Quest",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

    }
}

// Composable for displaying a single feature item with a bullet point.
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f)
        )
    }
}

// Composable for the top header, drawn as a large semicircle.
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
                // Custom drawing logic to create a semicircle arc
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
        // Show a back button if enabled
        if (showBackButton) {
            IconButton(
                onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Box to center the title text within the header
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Text(
                text = "About CultureQuest",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Composable that allows the user to select between Light and Dark themes.
@Composable
fun ThemeSelector(
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit
) {
    // Defines the available theme options.
    val themes = listOf(Theme.LIGHT, Theme.DARK) // Exclude SYSTEM theme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Title for the theme selector section
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        // Row to display theme options side-by-side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            themes.forEach { theme ->
                val isSelected = theme == selectedTheme
                // Determine colors based on whether the theme is selected
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground

                Box(
                    // Styling for the theme option button
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .selectable(
                            selected = isSelected,
                            onClick = { onThemeSelected(theme) }
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = theme.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                }
            }
        }
    }
}