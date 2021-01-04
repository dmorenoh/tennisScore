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

    private fun printScoreDefault(): String = "${server.printScore()}:${receiver.printScore()}"

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
        winner != EmptyPlayer -> printWinner(winner as TennisPlayer)
        matchIsEven() -> printScoreAsEven()
        else -> this.printScoreDefault()
    }

    fun playerScores(playerType: PlayerType): Either<DomainError, BasicTennisScore> =
        if (winner != EmptyPlayer) Either.left(DomainError("There is a winner already"))
        else Either.right(addScoreTo(playerType).processWinner())

    private fun playerAhead(): Player = when {
        matchIsEven() -> EmptyPlayer
        receiver.isAheadOf(server) -> receiver
        else -> server
    }

    private fun winner(): Player =
        playerAhead().takeIf { it is TennisPlayer && it.reaches(MAX_POINTS) && enoughAdvantage() }
            ?: EmptyPlayer

    private fun processWinner(): BasicTennisScore = copy(winner = winner())

}





