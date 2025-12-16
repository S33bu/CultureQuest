package com.example.culturequest.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the CultureQuest application.
 *
 * Defines the corner radius values used across Material 3 components
 * such as buttons, cards, dialogs, and surfaces. These shapes help
 * maintain a consistent visual style throughout the application.
 */
val Shapes = Shapes(

    /**
     * Small shape variant.
     *
     * Used for compact UI elements such as chips or small buttons.
     */
    small = RoundedCornerShape(4.dp),

    /**
     * Medium shape variant.
     *
     * Commonly applied to standard buttons, cards, and input fields.
     */
    medium = RoundedCornerShape(8.dp),

    /**
     * Large shape variant.
     *
     * Intended for prominent surfaces such as large cards or modal dialogs.
     */
    large = RoundedCornerShape(16.dp)
)
