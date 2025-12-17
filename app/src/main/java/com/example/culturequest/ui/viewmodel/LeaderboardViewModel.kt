package com.example.culturequest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents a single leaderboard row.
 *
 * @property displayName Name shown on the leaderboard.
 * @property bestScore Highest score achieved by the user.
 * @property gamesPlayed Total number of games played by the user.
 */
data class LeaderboardEntry(
    val displayName: String,
    val bestScore: Int,
    val gamesPlayed: Int = 0,
)

/**
 * ViewModel responsible for loading and exposing the global leaderboard from Firestore.
 *
 * The ViewModel starts listening to Firestore updates immediately on creation and keeps
 * [leaders] in sync with the latest data.
 */
class LeaderboardViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _leaders = MutableStateFlow<List<LeaderboardEntry>>(emptyList())

    /**
     * Current leaderboard entries (top players), ordered by best score descending.
     */
    val leaders: StateFlow<List<LeaderboardEntry>> = _leaders.asStateFlow()

    init {
        listenToTopPlayers()
    }

    /**
     * Subscribes to Firestore changes and keeps the leaderboard state updated in real-time.
     */
    private fun listenToTopPlayers() {
        firestore
            .collection("users")
            .orderBy("bestScore", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to load leaderboard", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    _leaders.value = emptyList()
                    return@addSnapshotListener
                }

                _leaders.value = snapshot.toLeaderboardEntries()
            }
    }

    /**
     * Performs a one-shot fetch of the leaderboard.
     *
     * Intended to be called when the Profile screen is opened to ensure the list is fresh.
     */
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

                _leaders.value = snapshot.toLeaderboardEntries()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to refresh leaderboard", e)
            }
    }

    private companion object {
        private const val TAG = "LeaderboardViewModel"
    }
}

/**
 * Maps a Firestore snapshot into a list of [LeaderboardEntry].
 *
 * Missing fields fall back to safe defaults:
 * - displayName -> "Player"
 * - bestScore -> 0
 * - gamesPlayed -> 0
 *
 * @return List of leaderboard entries derived from the snapshot documents.
 */
private fun com.google.firebase.firestore.QuerySnapshot.toLeaderboardEntries(): List<LeaderboardEntry> {
    return documents.mapNotNull { doc ->
        val name = doc.getString("displayName") ?: "Player"
        val score = doc.getLong("bestScore")?.toInt() ?: 0
        val played = doc.getLong("gamesPlayed")?.toInt() ?: 0
        LeaderboardEntry(name, score, played)
    }
}
