package org.tennis.score.service

import arrow.core.Either
import kotlin.math.abs

data class BasicTennisScore(
    private val server: TennisPlayer,
    private val receiver: TennisPlayer,
    private val minToWin: Int = 2,
    private val winner: Player = EmptyPlayer
) {
    companion object {
        const val MAX_POINTS = 4
    }

    private fun printScoreDefault(): String = "${getScore(server.points())}:${getScore(receiver.points())}"

    private fun differenceBetweenPlayers(): Int = abs(server.points() - receiver.points())

    private fun matchIsEven(): Boolean = server.points() == receiver.points()

    private fun enoughAdvantage(): Boolean = differenceBetweenPlayers() >= minToWin

    private fun printScoreAsEven(): String =
        if (server.reaches(MAX_POINTS)) "40:40"
        else this.printScoreDefault()

    private fun addScoreTo(type: PlayerType): BasicTennisScore = when (type) {
        PlayerType.SERVER -> copy(server = server.scores())
        else -> copy(receiver = receiver.scores())
    }

    fun printScore(): String = when {
        winner is TennisPlayer -> printWinner(winner)
        matchIsEven() -> printScoreAsEven()
        else -> this.printScoreDefault()
    }

    fun playerScores(playerType: PlayerType): Either<DomainError, BasicTennisScore> =
        if (winner != EmptyPlayer) Either.left(DomainError("There is a winner already"))
        else Either.right(this.addScoreTo(playerType).calculateWinner())

    private fun calculateWinner(): BasicTennisScore = when {
        receiver.isAheadOf(server) && receiver.reaches(MAX_POINTS) && enoughAdvantage() -> this.copy(winner = receiver)
        server.isAheadOf(receiver) && server.reaches(MAX_POINTS) && enoughAdvantage() -> this.copy(winner = server)
        else -> this
    }
}





