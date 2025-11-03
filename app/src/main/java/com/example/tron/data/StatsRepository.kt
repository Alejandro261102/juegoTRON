package com.example.tron.data

import android.content.Context
import android.content.SharedPreferences

// Data class para guardar las estadísticas
data class GameStats(val playerWins: Int, val aiWins: Int, val gamesPlayed: Int)

class StatsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("tron_game_stats", Context.MODE_PRIVATE)

    companion object {
        private const val PLAYER_WINS = "player_wins"
        private const val AI_WINS = "ai_wins"
        private const val GAMES_PLAYED = "games_played"
    }

    fun getStats(): GameStats {
        val playerWins = prefs.getInt(PLAYER_WINS, 0)
        val aiWins = prefs.getInt(AI_WINS, 0)
        val gamesPlayed = prefs.getInt(GAMES_PLAYED, 0)
        return GameStats(playerWins = playerWins, aiWins = aiWins, gamesPlayed = gamesPlayed)
    }

    fun recordGameResult(didPlayerWin: Boolean, isAiOpponent: Boolean) {
        val currentStats = getStats()
        with(prefs.edit()) {
            putInt(GAMES_PLAYED, currentStats.gamesPlayed + 1)

            if (isAiOpponent) {
                if (didPlayerWin) {
                    putInt(PLAYER_WINS, currentStats.playerWins + 1)
                } else {
                    putInt(AI_WINS, currentStats.aiWins + 1)
                }
            }

            apply()
        }
    }
}