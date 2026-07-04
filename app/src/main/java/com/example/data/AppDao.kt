package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
  // Killer
  @Query("SELECT * FROM killer_players ORDER BY name ASC")
  fun getAllKillerPlayers(): Flow<List<KillerPlayerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKillerPlayers(players: List<KillerPlayerEntity>)

  @Update
  suspend fun updateKillerPlayer(player: KillerPlayerEntity)

  @Query("SELECT * FROM killer_logs ORDER BY timestamp DESC")
  fun getKillerLogs(): Flow<List<KillerLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKillerLog(log: KillerLogEntity)

  @Query("DELETE FROM killer_players")
  suspend fun clearKillerPlayers()

  @Query("DELETE FROM killer_logs")
  suspend fun clearKillerLogs()

  @Transaction
  suspend fun clearKillerGame() {
    clearKillerPlayers()
    clearKillerLogs()
  }

  // Hidden Draft
  @Query("SELECT * FROM draft_sessions WHERE id = 1 LIMIT 1")
  fun getDraftSessionFlow(): Flow<DraftSessionEntity?>

  @Query("SELECT * FROM draft_sessions WHERE id = 1 LIMIT 1")
  suspend fun getDraftSessionDirect(): DraftSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDraftSession(session: DraftSessionEntity)

  @Query("DELETE FROM draft_sessions WHERE id = 1")
  suspend fun deleteDraftSession()
}
