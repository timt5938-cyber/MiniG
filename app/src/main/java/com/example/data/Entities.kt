package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "killer_players")
data class KillerPlayerEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val secretPhrase: String,
  val targetName: String,
  val isAlive: Boolean,
  val killerName: String? = null
)

@Entity(tableName = "killer_logs")
data class KillerLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val killerName: String,
  val victimName: String,
  val phrase: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "draft_sessions")
data class DraftSessionEntity(
  @PrimaryKey val id: Int = 1,
  val playersJson: String,  // List of players with names, roles, goals, completion status
  val draftStateJson: String, // Picks/bans, active turn, draft phase
  val isActive: Boolean = false
)
