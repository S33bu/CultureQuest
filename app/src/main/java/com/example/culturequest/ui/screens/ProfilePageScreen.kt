package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            //top circle
            Box(
                modifier = Modifier
                    .size(600.dp)
                    .offset(y = (-320).dp) // Move bubble down
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )


            // Main content layout.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeaderCircle(
                    onBackClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .offset(y = (-40).dp) // Move title further up
                )

                Spacer(modifier = Modifier.height(24.dp))

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


@Composable
fun ProfileHeaderCircle(
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
                onClick = onBackClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = "Back",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Title text
        Text(
            text = "Profile",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center) // Center in the Box
        )
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
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
