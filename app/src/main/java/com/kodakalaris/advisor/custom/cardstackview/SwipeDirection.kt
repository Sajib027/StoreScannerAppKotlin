package com.kodakalaris.advisor.custom.cardstackview

import java.util.*

enum class SwipeDirection {
    Left, Right, Top, Bottom;

    companion object {
        val FREEDOM = Arrays.asList(*values())
        val FREEDOM_NO_BOTTOM = Arrays.asList(Top, Left, Right)
        val HORIZONTAL = Arrays.asList(Left, Right)
        val VERTICAL = Arrays.asList(Top, Bottom)

        fun from(value: Int): List<SwipeDirection> {
            return when (value) {
                0 -> FREEDOM
                1 -> FREEDOM_NO_BOTTOM
                2 -> HORIZONTAL
                3 -> VERTICAL
                else -> FREEDOM
            }
        }
    }
}