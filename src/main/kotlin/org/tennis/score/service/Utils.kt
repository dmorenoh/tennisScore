package org.tennis.score.service

fun getScore(points: Int): String = when (points) {
    0 -> "0"
    1 -> "15"
    2 -> "30"
    3 -> "40"
    else -> "A"
}

fun printWinner(player: TennisPlayer): String = "${player.name()} Wins"

