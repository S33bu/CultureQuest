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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePageScreen(
    onBackClick: () -> Unit,
    gameViewModel: GameViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val user by gameViewModel.user.collectAsState()

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email

    val displayName = remember(email, user) {
        email?.let { nameFromEmail(it) }
            ?: user?.username
            ?: "Player"
    }

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
                    background = MaterialTheme.colorScheme.primary,
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
                    ProfileItem(label = "Name", value = displayName)
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = "Profile",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun nameFromEmail(email: String): String {
    val dotIndex = email.indexOf('.')
    val atIndex = email.indexOf('@')

    val candidates = listOf(dotIndex, atIndex).filter { it > 0 }
    val endIndex = if (candidates.isEmpty()) email.length else candidates.min()

    val raw = email.substring(0, endIndex).ifEmpty { email }
    return raw.replaceFirstChar { ch ->
        if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
    }
}
