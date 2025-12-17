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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.culturequest.R
import com.example.culturequest.data.CountryRepository
import com.example.culturequest.ui.viewmodel.HomeViewModel

/**
 * Main landing screen for the CultureQuest application.
 *
 * Displays the application title, user score statistics, and navigation
 * triggers for the game, profile, and about pages.
 *
 * @param onAboutClick Callback invoked to navigate to the About page.
 * @param onGameClick Callback invoked to start a new game session.
 * @param homeViewModel The ViewModel providing home-specific user data.
 * @param onProfileClick Callback invoked to navigate to the Profile page.
 * @param lastGameScore The score achieved in the most recently played session.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAboutClick: () -> Unit,
    onGameClick: () -> Unit,
    homeViewModel: HomeViewModel,
    onProfileClick: () -> Unit,
    lastGameScore: Int = 0,
) {
    val bestScore by homeViewModel.bestScore.collectAsState()
    val isDataReady = CountryRepository.isLoaded()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        // top circle
        Box(
            modifier =
                Modifier
                    .size(600.dp)
                    .offset(y = (-320).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
        )
        // bottom circle
        Box(
            modifier =
                Modifier
                    .size(360.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(180.dp),
                    ),
        )

        // main content on top of circles
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PageHeader(
                onAboutClick = onAboutClick,
                onProfileClick = onProfileClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .offset(y = (-20).dp), // Move title up
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, end = 80.dp),
                contentAlignment = Alignment.Center,
            ) {
                ScoreCard(
                    lastGameScore = lastGameScore,
                    bestScore = bestScore,
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            StartGameFooter(
                onGameClick = onGameClick,
                isDataReady = isDataReady,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, end = 8.dp),
            )
        }
    }
}

/**
 * Header component containing navigation icons and the app title.
 *
 * @param onAboutClick Callback for the about icon button.
 * @param onProfileClick Callback for the profile icon button.
 * @param modifier Modifier for layout adjustments.
 */
@Composable
fun PageHeader(
    onAboutClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.height(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onAboutClick) {
                Icon(
                    painter = painterResource(R.drawable.about_us),
                    contentDescription = "About",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(R.drawable.profile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
        Text(
            text = "CultureQuest",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

/**
 * A circular card component that displays score statistics.
 *
 * @param lastGameScore The score from the previous game.
 * @param bestScore The highest score recorded for the current user.
 */
@Composable
fun ScoreCard(
    lastGameScore: Int,
    bestScore: Int,
) {
    Surface(
        modifier = Modifier.size(240.dp),
        shape = CircleShape, // Circular shape
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Last Game Score: $lastGameScore",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Best Score: $bestScore",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Footer component containing the primary play button.
 *
 * @param onGameClick Callback to trigger when the play button is clicked.
 * @param modifier Modifier for layout adjustments.
 */
@Composable
fun StartGameFooter(
    onGameClick: () -> Unit,
    isDataReady: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrimaryButton(
            text = if (isDataReady) "Play Now" else "Loading Data...",
            onClick = onGameClick,
            enabled = isDataReady,
        )
    }
}
