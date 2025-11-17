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
    val scrollState = rememberScrollState()
    val theme by settingsViewModel.theme.collectAsState()

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
                    background = MaterialTheme.colorScheme.primary, // Changed from hardcoded color
                    iconSize = 50.dp,
                    showBackButton = true,
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        .offset(y = (-90).dp)
                        .graphicsLayer { alpha = 0.99f }
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp) // Adjusted spacing
                ) {
                    Text(
                        text = "Welcome! Ready to explore world cultures?",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "Test your geography and cultural knowledge by guessing the country based on what you see — from bustling city streets to remote landscapes.\n" +
                                "\n" +
                                "Use cultural hints to learn fascinating facts about each location, and climb the global leaderboard as you prove your world knowledge!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

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
                            FeatureItem("Cultural quizzes")
                            FeatureItem("Interactive learning")
                            FeatureItem("Global traditions")
                            FeatureItem("Fun facts")
                        }
                    }

                    // Theme selector
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

@Composable
fun ThemeSelector(
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit
) {
    val themes = listOf(Theme.LIGHT, Theme.DARK) // Exclude SYSTEM theme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            themes.forEach { theme ->
                val isSelected = theme == selectedTheme
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground

                Box(
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
