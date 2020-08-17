package com.kodakalaris.advisor.custom.cardstackview.internal

import com.kodakalaris.advisor.custom.cardstackview.StackFrom
import com.kodakalaris.advisor.custom.cardstackview.SwipeDirection

class CardStackOption {
    var visibleCount = 3
    var swipeThreshold = 0.75f // Percentage
    var translationDiff = 12f // DP
    var scaleDiff = 0.02f // Percentage
    var stackFrom = StackFrom.DEFAULT
    var isElevationEnabled = true
    var isSwipeEnabled = true
    var leftOverlay = 0 // Layout Resource ID
    var rightOverlay = 0 // Layout Resource ID
    var bottomOverlay = 0 // Layout Resource ID
    var topOverlay = 0 // Layout Resource ID
    var swipeDirection = SwipeDirection.FREEDOM
}
