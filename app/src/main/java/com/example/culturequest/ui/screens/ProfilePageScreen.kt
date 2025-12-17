package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.example.culturequest.ui.viewmodel.LeaderboardEntry
import com.example.culturequest.ui.viewmodel.LeaderboardViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

/**
 * Profile screen composable.
 *
 * Shows:
 * - user display name (derived from Firebase email or local username)
 * - user stats (high score, games played)
 * - leaderboard (top players)
 *
 * The leaderboard is refreshed when the screen is opened.
 *
 * @param onBackClick Callback invoked when the user taps the back button.
 * @param gameViewModel ViewModel that provides local user state (bestScore, gamesPlayed, username).
 * @param leaderboardViewModel ViewModel that provides leaderboard entries. Defaults to [viewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePageScreen(
    onBackClick: () -> Unit,
    gameViewModel: GameViewModel,
    leaderboardViewModel: LeaderboardViewModel = viewModel(),
) {
    val scrollState = rememberScrollState()
    val user by gameViewModel.user.collectAsState()
    val leaders by leaderboardViewModel.leaders.collectAsState()

    LaunchedEffect(Unit) {
        leaderboardViewModel.refreshLeaderboard()
    }

    val email = FirebaseAuth.getInstance().currentUser?.email
    val displayName = remember(email, user) {
        email?.let { nameFromEmail(it) } ?: user?.username ?: "Player"
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Image(
                painter = painterResource(id = R.drawable.maa),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.15f),
            )

            Box(
                modifier = Modifier
                    .size(600.dp)
                    .offset(y = (-320).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileHeaderCircle(
                    onBackClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .offset(y = (-40).dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .offset(y = (-90).dp)
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    ProfileItem(label = "Name", value = displayName)
                    ProfileItem(label = "High Score", value = user?.bestScore?.toString() ?: "0")
                    ProfileItem(label = "Games Played", value = user?.gamesPlayed?.toString() ?: "0")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Keep exploring and unlocking new cultural adventures!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    LeaderboardSection(leaders = leaders)
                }
            }
        }
    }
}

/**
 * Header block for the profile screen.
 *
 * Contains a back button and the "Profile" title.
 *
 * @param onBackClick Callback invoked when the user taps the back button.
 * @param modifier Optional modifier used to position the header in the layout.
 */
@Composable
fun ProfileHeaderCircle(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.height(250.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = "Back",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Text(
            text = "Profile",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Displays a labeled value in the profile screen.
 *
 * @param label Label shown above the value.
 * @param value Value shown below the label.
 */
@Composable
private fun ProfileItem(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    }
}

/**
 * Extracts a simple display name from an email address.
 *
 * Uses the substring before the first '.' or '@' and capitalizes the first character.
 *
 * @param email Email address to parse.
 * @return A best-effort readable name derived from the email.
 */
private fun nameFromEmail(email: String): String {
    val dotIndex = email.indexOf('.')
    val atIndex = email.indexOf('@')

    val candidates = listOf(dotIndex, atIndex).filter { it > 0 }
    val endIndex = candidates.minOrNull() ?: email.length

    val raw = email.substring(0, endIndex).ifEmpty { email }
    return raw.replaceFirstChar { ch ->
        if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
    }
}

/**
 * Renders the leaderboard section listing top players and their best scores.
 *
 * @param leaders List of leaderboard entries to display.
 */
@Composable
private fun LeaderboardSection(leaders: List<LeaderboardEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Top Players",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        if (leaders.isEmpty()) {
            Text(
                text = "No leaderboard data yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            leaders.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}. ${entry.displayName}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = entry.bestScore.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }
    }
}
