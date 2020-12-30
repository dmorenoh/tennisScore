package org.tennis.score.service

sealed class Player(open val name: String)

object EmptyPlayer : Player("")

data class TennisPlayer(override val name: String, private val type: PlayerType, private val points: Int = 0) :
    Player(name) {

    fun points() = points
    fun reaches(limit: Int) = points >= limit
    fun name(): String = name
    fun isAheadOf(player: TennisPlayer): Boolean = points > player.points

    companion object {
        fun newServer(name: String) = TennisPlayer(name, PlayerType.SERVER)
        fun newReceiver(name: String) = TennisPlayer(name, PlayerType.RECEIVER)
    }
}

fun TennisPlayer.scores(): TennisPlayer = copy(points = this.points() + 1)
