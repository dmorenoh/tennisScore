package org.tennis.score.service

import arrow.core.flatMap
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import org.junit.jupiter.api.Test
import org.tennis.score.service.TennisPlayer.Companion.newReceiver
import org.tennis.score.service.TennisPlayer.Companion.newServer

class BasicTennisScoreTest {

    @Test
    fun `should fail when there is a winner`() {
        val server = newServer("Server")
        val receiver = newReceiver("Receiver")
        val basicTennisScore = BasicTennisScore(server = server, receiver = receiver, winner = server)
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeLeft()
    }

    @Test
    fun `test score`() {
        val basicTennisScore = BasicTennisScore(newServer("Server"), newReceiver("Receiver"))
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "15:0")
        }
    }

    @Test
    fun `should receiver add points when scores`() {
        val basicTennisScore = BasicTennisScore(newServer("Server"), newReceiver("Receiver"))
        val result = basicTennisScore.playerScores(PlayerType.RECEIVER)
            .flatMap { it.playerScores(PlayerType.RECEIVER) }
            .flatMap { it.playerScores(PlayerType.SERVER) }
        result.shouldBeRight {
            assert(it.printScore() == "15:30")
        }

    }

    @Test
    fun `should receiver show as advantage when deuce and scores`() {

        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 3),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 3)
        )
        val result = basicTennisScore.playerScores(PlayerType.RECEIVER)
        result.shouldBeRight {
            assert(it.printScore() == "40:A")
        }
    }

    @Test
    fun `should server show as advantage when deuce and scores`() {

        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 3),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 3)
        )
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "A:40")
        }
    }

    @Test
    fun `should return deuce when receiver has advantage and server scores`() {

        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 3),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 4)
        )
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "40:40")
        }
    }

    @Test
    fun `should return deuce when server has advantage and receiver scores`() {

        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 4),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 3)
        )
        val result = basicTennisScore.playerScores(PlayerType.RECEIVER)
        result.shouldBeRight {
            assert(it.printScore() == "40:40")
        }
    }

    @Test
    fun `should return default score when no one has advantage and receiver scores`() {
        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 2),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 1)
        )
        val result = basicTennisScore.playerScores(PlayerType.RECEIVER)
        result.shouldBeRight {
            assert(it.printScore() == "30:30")
        }
    }

    @Test
    fun `should return default score when no one has advantage and server scores`() {
        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 1),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 2)
        )
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "30:30")
        }
    }

    @Test
    fun `should return server as winner when server scores and has more than 2 as difference`() {

        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 3),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 2)
        )
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "Server Wins")
        }
    }

    @Test
    fun `should return receiver as winner when receiver scores and has more than 2 as difference`() {
        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 2),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 3)
        )
        val result = basicTennisScore.playerScores(PlayerType.RECEIVER)
        result.shouldBeRight {
            assert(it.printScore() == "Receiver Wins")
        }
    }

    @Test
    fun `should return server as winner when server scores and there is no min diff`() {
        val basicTennisScore = BasicTennisScore(
            server = TennisPlayer("Server", PlayerType.SERVER, 3),
            receiver = TennisPlayer("Receiver", PlayerType.RECEIVER, 2),
            minToWin = 0
        )
        val result = basicTennisScore.playerScores(PlayerType.SERVER)
        result.shouldBeRight {
            assert(it.printScore() == "Server Wins")
        }
    }
}