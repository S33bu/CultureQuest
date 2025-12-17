package com.example.culturequest.data

/**
 * Defines the difficulty levels for hints within the game.
 * These tiers determine when a hint is revealed to the user based on
 * their progress or point deductions.
 */
enum class HintTier { HARD, MEDIUM, EASY }

/**
 * Represents a single hint provided during a quiz session.
 *
 * Each hint is associated with a specific [HintTier] to manage the
 * game's progressive difficulty curve.
 *
 * @property text The descriptive content of the hint.
 * @property tier The difficulty classification of the hint.
 */
data class Hint(
    val text: String,
    val tier: HintTier,
)
