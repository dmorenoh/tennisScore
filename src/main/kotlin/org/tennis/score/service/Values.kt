package org.tennis.score.service

enum class PlayerType {
    SERVER,
    RECEIVER
}

data class DomainError(private val message: String)