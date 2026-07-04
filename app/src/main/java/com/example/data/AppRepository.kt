package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
  // Killer
  val allKillerPlayers: Flow<List<KillerPlayerEntity>> = appDao.getAllKillerPlayers()
  val allKillerLogs: Flow<List<KillerLogEntity>> = appDao.getKillerLogs()

  suspend fun insertKillerPlayers(players: List<KillerPlayerEntity>) {
    appDao.insertKillerPlayers(players)
  }

  suspend fun updateKillerPlayer(player: KillerPlayerEntity) {
    appDao.updateKillerPlayer(player)
  }

  suspend fun insertKillerLog(log: KillerLogEntity) {
    appDao.insertKillerLog(log)
  }

  suspend fun clearKillerGame() {
    appDao.clearKillerGame()
  }

  // Hidden Draft
  val draftSessionFlow: Flow<DraftSessionEntity?> = appDao.getDraftSessionFlow()

  suspend fun getDraftSessionDirect(): DraftSessionEntity? {
    return appDao.getDraftSessionDirect()
  }

  suspend fun saveDraftSession(session: DraftSessionEntity) {
    appDao.insertDraftSession(session)
  }

  suspend fun deleteDraftSession() {
    appDao.deleteDraftSession()
  }
}
