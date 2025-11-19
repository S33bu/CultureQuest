package com.example.culturequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.example.culturequest.ui.viewmodel.GameViewModel
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAboutClick: () -> Unit, //navigate to about
    onGameClick: () -> Unit,    //navigate to game
    onProfileClick: () -> Unit, //navigate to profile
    lastGameScore: Int = 0,
    gameViewModel: GameViewModel = viewModel(), // gets the GameViewModel
) {
    val user by gameViewModel.user.collectAsState()


    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    )
    {
        //top circle
        Box(
            modifier = Modifier
                .size(600.dp)
                .offset(y = (-320).dp) // Move bubble down
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        //bottom circle
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 120.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(180.dp)
                )
        )

        //main content on top of circles
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER INSIDE TOP CIRCLE
            PageHeader(
                onAboutClick = onAboutClick,
                onProfileClick = onProfileClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .offset(y = (-20).dp) // Move title up
            )

            Spacer(modifier = Modifier.height(24.dp))

            //score display card
            Box(
                modifier = Modifier.fillMaxWidth().padding(top=50.dp, end = 80.dp), // Move more to the left
                contentAlignment = Alignment.Center
            ) {
                ScoreCard(
                    lastGameScore = lastGameScore,
                    bestScore = user?.bestScore ?: 0
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            //play button at the bottom
            StartGameFooter(
                onGameClick = onGameClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, end = 8.dp)
            )

        }

    }
}

@Composable
fun PageHeader(
    onAboutClick: () -> Unit,
    onProfileClick: () -> Unit,
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
                onClick = onAboutClick,
            )
            {
                Icon(
                    painter = painterResource(R.drawable.about_us),
                    contentDescription = "About",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(
                onClick = onProfileClick,
            )
            {
                Icon(
                    painter = painterResource(R.drawable.profile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        //title
        Text(
            text = "CultureQuest",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center) // Center in the Box
        )

    }
}

@Composable
fun ScoreCard(
    lastGameScore: Int,
    bestScore: Int,
) {
    Surface(
        modifier = Modifier.size(240.dp), // Bigger circular shape
        shape = CircleShape, // Circular shape
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //game info
            Text(
                text = "Last Game Score: $lastGameScore",
                style = MaterialTheme.typography.headlineSmall, // Adjusted size
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center // Center text
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Best Score: $bestScore",
                style = MaterialTheme.typography.headlineSmall, // Adjusted size
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center // Center text
            )
        }

    }
}

@Composable
fun StartGameFooter(
    onGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(end = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimaryButton(
            text = ("Play now"),
            onClick = onGameClick,
        )
    }
}