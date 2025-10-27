package com.example.culturequest.data

// Defines difficulty levels for hints
enum class HintTier { HARD, MEDIUM, EASY }

// Represents one hint text + its tier
data class Hint(val text: String, val tier: HintTier)
