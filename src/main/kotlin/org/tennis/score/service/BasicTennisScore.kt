package org.tennis.score.service

import arrow.core.Either
import kotlin.math.abs

data class BasicTennisScore(
    val server: TennisPlayer,
    val receiver: TennisPlayer,
    val minToWin: Int = 2,
    val winner: Player = EmptyPlayer
) {
    companion object {
        const val MAX_POINTS = 4
    }

    private fun getScore(points: Int): String = when (points) {
        0 -> "0"
        1 -> "15"
        2 -> "30"
        3 -> "40"
        else -> "A"
    }

    private fun printScoreDefault(): String = "${getScore(server.points())}:${getScore(receiver.points())}"

    private fun printWinner(player: TennisPlayer): String = "${player.name()} Wins"

    private fun differenceBetweenPlayers(): Int = abs(server.points() - receiver.points())

    private fun matchIsEven(): Boolean = server.points() == receiver.points()

    fun enoughAdvantage(): Boolean = differenceBetweenPlayers() >= minToWin

    private fun printScoreAsEven(): String =
        if (server.reaches(MAX_POINTS)) "40:40"
        else this.printScoreDefault()

    fun addScoreTo(type: PlayerType): BasicTennisScore = when (type) {
        PlayerType.SERVER -> copy(server = server.scores())
        else -> copy(receiver = receiver.scores())
    }


    fun printScore(): String = when {
        winner is TennisPlayer -> printWinner(winner)
        matchIsEven() -> printScoreAsEven()
        else -> this.printScoreDefault()
    }
}

fun BasicTennisScore.playerScores(playerType: PlayerType): Either<DomainError, BasicTennisScore> =
    if (winner != EmptyPlayer) Either.left(DomainError("There is a winner already"))
    else Either.right(this.addScoreTo(playerType).calculateWinner())

fun BasicTennisScore.calculateWinner(): BasicTennisScore = when {
    receiver.isAheadOf(server) && receiver.reaches(BasicTennisScore.MAX_POINTS) && enoughAdvantage() -> this.copy(winner = receiver)
    server.isAheadOf(receiver) && server.reaches(BasicTennisScore.MAX_POINTS) && enoughAdvantage() -> this.copy(winner = server)
    else -> this
}

