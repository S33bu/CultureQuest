package com.example.culturequest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Represents a single leaderboard entry loaded from Firestore.
data class LeaderboardEntry(
    val displayName: String,
    val bestScore: Int,
    val gamesPlayed: Int = 0,
)

// ViewModel responsible for loading and exposing the global leaderboard.
class LeaderboardViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Backing StateFlow for the list of top players.
    private val _leaders = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaders: StateFlow<List<LeaderboardEntry>> = _leaders.asStateFlow()

    init {
        // Start listening to top players as soon as the ViewModel is created.
        listenToTopPlayers()
    }

    // Subscribes to Firestore changes and keeps the leaderboard in sync.
    private fun listenToTopPlayers() {
        firestore
            .collection("users")
            .orderBy("bestScore", Query.Direction.DESCENDING)
            .limit(10) // top 10 players
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("LeaderboardViewModel", "Failed to load leaderboard", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    _leaders.value = emptyList()
                    return@addSnapshotListener
                }

                val entries =
                    snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("displayName") ?: "Player"
                        val score = doc.getLong("bestScore")?.toInt() ?: 0
                        val played = doc.getLong("gamesPlayed")?.toInt() ?: 0
                        LeaderboardEntry(name, score, played)
                    }

                _leaders.value = entries
            }
    }

    // One–shot refresh used when Profile screen is opened
    fun refreshLeaderboard() {
        firestore
            .collection("users")
            .orderBy("bestScore", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot == null) {
                    _leaders.value = emptyList()
                    return@addOnSuccessListener
                }

                val entries =
                    snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("displayName") ?: "Player"
                        val score = doc.getLong("bestScore")?.toInt() ?: 0
                        val played = doc.getLong("gamesPlayed")?.toInt() ?: 0
                        LeaderboardEntry(name, score, played)
                    }

                _leaders.value = entries
            }.addOnFailureListener { e ->
                Log.e("LeaderboardViewModel", "Failed to refresh leaderboard", e)
            }
    }
}
