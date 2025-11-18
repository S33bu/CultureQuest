package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

// The main composable for the Profile Page.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePageScreen(
    onBackClick: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val user by gameViewModel.user.collectAsState()

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    // Retrieve user's email from Firebase Authentication.
    val email = firebaseUser?.email

    val displayName = remember(email, user) {
        email?.let { nameFromEmail(it) }
            ?: user?.username
            ?: "Player"
        // Determine the display name, prioritizing email parsing, then stored username.
    }

    Scaffold { padding ->
        Box(
            // The root container for the screen.
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

            // Main content layout.
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ProfileHeader(
                    height = 300.dp,
                    // Custom header for the profile screen.
                    background = MaterialTheme.colorScheme.primary,
                    iconSize = 50.dp,
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        // Offset to overlap with the header.
                        .offset(y = (-90).dp)
                        .verticalScroll(scrollState)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileItem(label = "Name", value = displayName)
                    // Display user's statistics.
                    ProfileItem(
                        label = "High Score",
                        value = user?.bestScore?.toString() ?: "0"
                    )

                    ProfileItem(
                        label = "Games Played",
                        value = user?.gamesPlayed?.toString() ?: "0"
                    )

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

// A reusable composable to display a labeled piece of user information.
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
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    }
}

// The header section of the profile screen with a curved background.
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
                // Custom drawing to create an arc shape for the header background.
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            // The title text of the header.
            text = "Profile",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// A helper function to extract a user's name from their email address.
private fun nameFromEmail(email: String): String {
    val dotIndex = email.indexOf('.')
    val atIndex = email.indexOf('@')

    val candidates = listOf(dotIndex, atIndex).filter { it > 0 }
    val endIndex = if (candidates.isEmpty()) email.length else candidates.min()

    // Get the part of the email before the first '.' or '@'.
    val raw = email.substring(0, endIndex).ifEmpty { email }
    // Capitalize the first letter of the extracted name.
    return raw.replaceFirstChar { ch ->
        if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
    }
}
