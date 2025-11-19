package com.example.culturequest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                .offset(y = (-320).dp)
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
            )

            Spacer(modifier = Modifier.height(24.dp))

            //score display card
            Box(
                modifier = Modifier.fillMaxWidth().padding(top=50.dp), contentAlignment = Alignment.Center
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
                    modifier = Modifier.size(50.dp),
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
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        //title
        Text(
            text = "Culture \n Quest",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
fun ScoreCard(
    lastGameScore: Int,
    bestScore: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //game info
            Text(
                text = "Last Game Score: $lastGameScore",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Best Score: $bestScore",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
