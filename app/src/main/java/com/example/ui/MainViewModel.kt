package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: AppRepository

  init {
    val database = AppDatabase.getDatabase(application)
    repository = AppRepository(database.appDao())
  }

  // --- Static assets loaded once ---
  var allTeams: List<AliasTeam> = emptyList()
    private set
  var allRekrutWords: List<AliasWord> = emptyList()
    private set
  var allKillerPhrases: List<KillerWord> = emptyList()
    private set
  var allFractions: List<DraftFraction> = emptyList()
    private set
  var allDotaHeroes: List<String> = emptyList()
    private set

  // --- Lobby Setup Lists (persistent in memory) ---
  val killerSetupPlayers = MutableStateFlow<List<String>>(listOf("Алексей Пудж", "Сергей Мидер", "Марина ЦМка", "Иван Гуль", "Дмитрий Саппорт"))
  val draftSetupPlayers = MutableStateFlow<List<String>>(listOf("Алексей Пудж", "Сергей Мидер", "Марина ЦМка", "Иван Гуль", "Дмитрий Саппорт"))
  val aliasSetupTeams = MutableStateFlow<List<String>>(listOf("322 Отмывщики", "Dead Inside"))

  fun getCanonicalDotaHeroes(): List<String> {
    return listOf(
      "Abaddon (Абаддон)",
      "Alchemist (Алхимик)",
      "Ancient Apparition (Аппарат)",
      "Anti-Mage (Антимаг)",
      "Arc Warden (Арк Варден)",
      "Axe (Акс)",
      "Bane (Бейн)",
      "Batrider (Бэтрайдер)",
      "Beastmaster (Бистмастер)",
      "Bloodseeker (Бладсикер)",
      "Bounty Hunter (Баунти Хантер)",
      "Brewmaster (Брюмастер)",
      "Bristleback (Бристлбэк)",
      "Broodmother (Брудматер)",
      "Centaur Warrunner (Кентавр)",
      "Chaos Knight (Хаос Найт)",
      "Chen (Чен)",
      "Clinkz (Клинкз)",
      "Clockwerk (Клокверк)",
      "Crystal Maiden (ЦМка)",
      "Dark Seer (Дарк Сир)",
      "Dark Willow (Вилка)",
      "Dawnbreaker (Донбрейкер)",
      "Dazzle (Даззл)",
      "Death Prophet (Банша)",
      "Disruptor (Дисраптор)",
      "Doom (Дум)",
      "Dragon Knight (ДК)",
      "Drow Ranger (Дровка)",
      "Earth Spirit (Земеля)",
      "Earthshaker (Шейкер)",
      "Elder Titan (Титан)",
      "Ember Spirit (Эмбер)",
      "Enchantress (Коза)",
      "Enigma (Энигма)",
      "Faceless Void (Войд)",
      "Grimstroke (Гримстроук)",
      "Gyrocopter (Гирокоптер)",
      "Hoodwink (Худвинк)",
      "Huskar (Хускар)",
      "Invoker (Инвокер)",
      "Io (Виспо)",
      "Jakiro (Джакиро)",
      "Juggernaut (Джаггернаут)",
      "Keeper of the Light (Котл)",
      "Kez (Кеза)",
      "Kunkka (Кунка)",
      "Largo (Ларго)",
      "Legion Commander (Лега)",
      "Leshrac (Лешрак)",
      "Lich (Лич)",
      "Lifestealer (Гуль)",
      "Lina (Лина)",
      "Lion (Лион)",
      "Lone Druid (Мишка)",
      "Luna (Луна)",
      "Lycan (Ликан)",
      "Magnus (Магнус)",
      "Marci (Марси)",
      "Mars (Марс)",
      "Medusa (Медуза)",
      "Meepo (Мипо)",
      "Mirana (Мирана)",
      "Monkey King (МК)",
      "Morphling (Морф)",
      "Muerta (Муэрта)",
      "Naga Siren (Нага)",
      "Nature's Prophet (Фурион)",
      "Necrophos (Некр)",
      "Night Stalker (Баланар)",
      "Nyx Assassin (Нюкс)",
      "Ogre Magi (Огр Маг)",
      "Omniknight (Омник)",
      "Oracle (Оракул)",
      "Outworld Destroyer (ОД)",
      "Pangolier (Панго)",
      "Phantom Assassin (Фантомка)",
      "Phantom Lancer (Лансер)",
      "Phoenix (Феникс)",
      "Primal Beast (Праймал Бист)",
      "Puck (Пак)",
      "Pudge (Пудж)",
      "Pugna (Пугна)",
      "Queen of Pain (Квопа)",
      "Razor (Разор)",
      "Riki (Рики)",
      "Ringmaster (Рингмастер / Редмастер)",
      "Rubick (Рубик)",
      "Sand King (СК)",
      "Shadow Demon (ШД)",
      "Shadow Fiend (СФ)",
      "Shadow Shaman (Раста)",
      "Silencer (Сало)",
      "Skywrath Mage (Скай)",
      "Slardar (Сладар)",
      "Slark (Сларк)",
      "Snapfire (Бабка)",
      "Sniper (Снайпер)",
      "Spectre (Спектра)",
      "Spirit Breaker (Бара)",
      "Storm Spirit (Шторм)",
      "Sven (Свен)",
      "Techies (Течис)",
      "Templar Assassin (ТА)",
      "Terrorblade (ТБ)",
      "Tidehunter (Тайд)",
      "Timbersaw (Тимбер)",
      "Tinker (Тинкер)",
      "Tiny (Тини)",
      "Treant Protector (Трент)",
      "Troll Warlord (Тролль)",
      "Tusk (Туск)",
      "Underlord (Андерлорд)",
      "Undying (Андаинг)",
      "Ursa (Урса)",
      "Vengeful Spirit (Венга)",
      "Venomancer (Веник)",
      "Viper (Вайпер)",
      "Visage (Визаж)",
      "Void Spirit (Войд Спирит)",
      "Warlock (Варлок)",
      "Weaver (Вивер)",
      "Windranger (ВРка)",
      "Winter Wyvern (Виверна)",
      "Witch Doctor (ВД)",
      "Wraith King (ВК)",
      "Zeus (Зевс)"
    ).sorted()
  }

  fun initializeAssets() {
    viewModelScope.launch {
      val context = getApplication<Application>().applicationContext
      allTeams = AssetLoader.loadTeams(context)
      allRekrutWords = AssetLoader.loadRekrutWords(context)
      allKillerPhrases = AssetLoader.loadKillerWords(context)
      allFractions = AssetLoader.loadAllFractions(context)

      // Set to canonical duplicate-free list of all heroes
      allDotaHeroes = getCanonicalDotaHeroes()

      // Merge into draftHeroesState
      val currentList = draftHeroesState.value.toMutableList()
      val existingNames = currentList.map { it.name.lowercase() }.toSet()
      var modified = false
      allDotaHeroes.forEach { heroName ->
        if (!existingNames.contains(heroName.lowercase())) {
          currentList.add(DraftHeroState(name = heroName, status = "none"))
          modified = true
        }
      }
      if (modified || draftHeroesState.value.isEmpty()) {
        draftHeroesState.value = currentList.sortedBy { it.name }
      }
    }
  }

  // ==========================================
  // 1. ALIAS GAME STATE
  // ==========================================
  private val _aliasTeamsState = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
  val aliasTeamsState = _aliasTeamsState.asStateFlow()

  val aliasTurnDuration = mutableStateOf(60)
  val aliasActiveTeamIndex = mutableStateOf(0)
  val aliasTimerValue = mutableStateOf(60)
  val aliasIsTimerRunning = mutableStateOf(false)
  val aliasPenaltyForSkip = mutableStateOf(true)
  val aliasLastWordCommon = mutableStateOf(false)
  val aliasPointsToWin = mutableStateOf(25) // e.g. 15, 25, 50, 80

  val aliasCurrentWord = mutableStateOf<AliasWord?>(null)
  private var wordsPool = mutableListOf<AliasWord>()

  // Word review for active turn
  val wordsReviewedThisTurn = mutableStateOf<List<Pair<AliasWord, Boolean>>>(emptyList()) // Pair of Word to GuessedStatus

  val aliasGameOver = mutableStateOf(false)
  val aliasWinnerTeamName = mutableStateOf("")
  val aliasReviewModeActive = mutableStateOf(false)

  private var timerJob: Job? = null

  fun setupAliasGame(teams: List<String>, duration: Int, penalty: Boolean, commonLast: Boolean, points: Int) {
    _aliasTeamsState.value = teams.map { it to 0 }
    aliasTurnDuration.value = duration
    aliasTimerValue.value = duration
    aliasPenaltyForSkip.value = penalty
    aliasLastWordCommon.value = commonLast
    aliasPointsToWin.value = points
    aliasActiveTeamIndex.value = 0
    aliasGameOver.value = false
    aliasWinnerTeamName.value = ""
    aliasReviewModeActive.value = false
    wordsReviewedThisTurn.value = emptyList()

    // Shuffle word list
    wordsPool = allRekrutWords.shuffled().toMutableList()
    drawNextAliasWord()
  }

  fun startAliasTurn() {
    aliasTimerValue.value = aliasTurnDuration.value
    aliasIsTimerRunning.value = true
    wordsReviewedThisTurn.value = emptyList()
    drawNextAliasWord()

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (aliasTimerValue.value > 0 && aliasIsTimerRunning.value) {
        delay(1000)
        aliasTimerValue.value -= 1
      }
      if (aliasTimerValue.value == 0) {
        endAliasTurn()
      }
    }
  }

  private fun drawNextAliasWord() {
    if (wordsPool.isEmpty()) {
      wordsPool = allRekrutWords.shuffled().toMutableList()
    }
    aliasCurrentWord.value = if (wordsPool.isNotEmpty()) wordsPool.removeAt(0) else null
  }

  fun guessAliasWord() {
    val word = aliasCurrentWord.value ?: return
    // Add to reviewed list as Guessed (true)
    wordsReviewedThisTurn.value = wordsReviewedThisTurn.value + (word to true)
    drawNextAliasWord()
  }

  fun skipAliasWord() {
    val word = aliasCurrentWord.value ?: return
    // Add to reviewed list as Skipped (false)
    wordsReviewedThisTurn.value = wordsReviewedThisTurn.value + (word to false)
    drawNextAliasWord()
  }

  fun triggerKillerWord() {
    val word = aliasCurrentWord.value ?: return
    // Add as killer penalty: -3 immediately
    wordsReviewedThisTurn.value = wordsReviewedThisTurn.value + (word to false)
    // Immediate end of turn
    endAliasTurn(isKillerPenalty = true)
  }

  private fun endAliasTurn(isKillerPenalty: Boolean = false) {
    aliasIsTimerRunning.value = false
    timerJob?.cancel()
    aliasReviewModeActive.value = true

    if (isKillerPenalty) {
      // We can append a special flag or handle directly in confirmation
    }
  }

  fun toggleReviewedWord(index: Int) {
    val list = wordsReviewedThisTurn.value.toMutableList()
    if (index in list.indices) {
      val pair = list[index]
      list[index] = pair.first to !pair.second
      wordsReviewedThisTurn.value = list
    }
  }

  fun confirmAliasTurnResults(isKillerPenalty: Boolean = false) {
    aliasReviewModeActive.value = false

    // Calculate delta
    var scoreDelta = 0
    wordsReviewedThisTurn.value.forEach { (_, guessed) ->
      if (guessed) {
        scoreDelta += 1
      } else {
        if (aliasPenaltyForSkip.value) {
          scoreDelta -= 1
        }
      }
    }

    if (isKillerPenalty) {
      scoreDelta -= 3
    }

    // Apply score to active team
    val currentTeams = _aliasTeamsState.value.toMutableList()
    val activeIdx = aliasActiveTeamIndex.value
    if (activeIdx in currentTeams.indices) {
      val (name, score) = currentTeams[activeIdx]
      val newScore = (score + scoreDelta).coerceAtLeast(0)
      currentTeams[activeIdx] = name to newScore
      _aliasTeamsState.value = currentTeams

      // Check win condition
      if (newScore >= aliasPointsToWin.value) {
        aliasWinnerTeamName.value = name
        aliasGameOver.value = true
        return
      }
    }

    // Switch to next team
    aliasActiveTeamIndex.value = (activeIdx + 1) % currentTeams.size
    aliasTimerValue.value = aliasTurnDuration.value
  }

  // ==========================================
  // 2. KILLER GAME STATE
  // ==========================================
  val killerPlayers = repository.allKillerPlayers.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val killerLogs = repository.allKillerLogs.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  fun startKillerGame(playerNames: List<String>) {
    viewModelScope.launch {
      repository.clearKillerGame()

      if (playerNames.size < 2) return@launch

      val shuffledPhrases = allKillerPhrases.shuffled()
      val randomizedNames = playerNames.shuffled() // Shuffle for random targets

      val playersToInsert = mutableListOf<KillerPlayerEntity>()
      val n = randomizedNames.size

      for (i in randomizedNames.indices) {
        val name = randomizedNames[i]
        val targetName = randomizedNames[(i + 1) % n] // Circle arrangement!
        val secretPhrase = shuffledPhrases.getOrNull(i % shuffledPhrases.size)?.word ?: "Пудж"

        playersToInsert.add(
          KillerPlayerEntity(
            name = name,
            secretPhrase = secretPhrase,
            targetName = targetName,
            isAlive = true
          )
        )
      }

      repository.insertKillerPlayers(playersToInsert)
    }
  }

  fun assassinatePlayer(victimName: String) {
    viewModelScope.launch {
      val list = killerPlayers.value
      val victim = list.find { it.name.equals(victimName, ignoreCase = true) && it.isAlive } ?: return@launch
      val killer = list.find { it.targetName.equals(victimName, ignoreCase = true) && it.isAlive } ?: return@launch

      // Mark victim as dead
      repository.updateKillerPlayer(victim.copy(isAlive = false, killerName = killer.name))

      // Add Kill Log
      repository.insertKillerLog(
        KillerLogEntity(
          killerName = killer.name,
          victimName = victim.name,
          phrase = killer.secretPhrase
        )
      )

      // Killer inherits contract and target of victim
      repository.updateKillerPlayer(
        killer.copy(
          targetName = victim.targetName,
          secretPhrase = victim.secretPhrase
        )
      )
    }
  }

  fun resetKillerGame() {
    viewModelScope.launch {
      repository.clearKillerGame()
    }
  }

  // ==========================================
  // 3. HIDDEN DRAFT GAME STATE
  // ==========================================
  // Package-level data classes are used for state definitions

  val draftPlayersState = MutableStateFlow<List<DraftPlayerState>>(emptyList())
  val draftHeroesState = MutableStateFlow<List<DraftHeroState>>(emptyList())
  val draftGameActive = MutableStateFlow(false)

  // Load active session from Room
  init {
    viewModelScope.launch {
      repository.draftSessionFlow.collect { session ->
        if (session != null && session.isActive) {
          draftGameActive.value = true
          deserializeDraftSession(session)
        } else {
          draftGameActive.value = false
        }
      }
    }
  }

  fun startDraftGame(playerNames: List<String>, selectedFractions: List<DraftFraction>) {
    viewModelScope.launch {
      if (playerNames.isEmpty() || selectedFractions.isEmpty()) return@launch

      val players = mutableListOf<DraftPlayerState>()
      val shuffledFractions = selectedFractions.shuffled()

      for (i in playerNames.indices) {
        val name = playerNames[i]
        // Draw fraction
        val frac = shuffledFractions[i % shuffledFractions.size]
        // Draw random character from fraction
        val char = frac.characters.random()

        players.add(
          DraftPlayerState(
            id = i + 1,
            name = name,
            characterName = char.name,
            characterGoal = char.goal,
            characterMotive = char.motive,
            fractionName = frac.groupName,
            completed = false
          )
        )
      }

      draftPlayersState.value = players

      // Set up base hero states
      if (allDotaHeroes.isEmpty()) {
        allDotaHeroes = getCanonicalDotaHeroes()
      }
      val heroes = allDotaHeroes.map { DraftHeroState(it, "none") }
      draftHeroesState.value = heroes

      draftGameActive.value = true
      saveDraftSessionToRoom()
    }
  }

  fun toggleDraftHeroStatus(heroName: String, currentSelection: String) {
    val trimmedName = heroName.trim()
    if (trimmedName.isEmpty()) return

    // If currentSelection is a base status, find the first empty slot
    val targetSelection = if (currentSelection in listOf("pick_radiant", "pick_dire", "ban_radiant", "ban_dire")) {
      var foundSlot = currentSelection + "_0"
      for (i in 0 until 5) {
        val slotName = "${currentSelection}_$i"
        val isOccupied = draftHeroesState.value.any { it.status == slotName }
        if (!isOccupied) {
          foundSlot = slotName
          break
        }
      }
      foundSlot
    } else {
      currentSelection
    }

    val exists = draftHeroesState.value.any { it.name.equals(trimmedName, ignoreCase = true) }
    val updated = if (exists) {
      draftHeroesState.value.map {
        if (it.name.equals(trimmedName, ignoreCase = true)) {
          it.copy(status = targetSelection)
        } else {
          it
        }
      }
    } else {
      draftHeroesState.value + DraftHeroState(name = trimmedName, status = targetSelection)
    }
    draftHeroesState.value = updated
    saveDraftSessionToRoom()
  }

  fun setDraftHeroInSlot(slot: String, heroName: String) {
    val trimmedName = heroName.trim()
    // Find if any hero already has this slot, clear its status to "none"
    val clearedList = draftHeroesState.value.map {
      if (it.status == slot) {
        it.copy(status = "none")
      } else {
        it
      }
    }

    // Now set the new hero to this slot
    val updated = if (trimmedName.isNotEmpty()) {
      val exists = clearedList.any { it.name.equals(trimmedName, ignoreCase = true) }
      if (exists) {
        clearedList.map {
          if (it.name.equals(trimmedName, ignoreCase = true)) {
            it.copy(status = slot)
          } else {
            it
          }
        }
      } else {
        clearedList + DraftHeroState(name = trimmedName, status = slot)
      }
    } else {
      clearedList
    }

    draftHeroesState.value = updated
    saveDraftSessionToRoom()
  }

  fun setDraftBansRaw(team: String, bansString: String) {
    val prefix = "ban_${team}"
    // Clear all existing status of prefix
    val clearedList = draftHeroesState.value.map {
      if (it.status == "${prefix}_raw" || it.status == prefix) {
        it.copy(status = "none")
      } else {
        it
      }
    }

    // Save the raw string as a special slot
    var updated = clearedList + DraftHeroState(name = bansString, status = "${prefix}_raw")

    // Also parse and add individual heroes if they are not empty
    val individualBans = bansString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    individualBans.forEach { heroName ->
      val exists = updated.any { it.name.equals(heroName, ignoreCase = true) }
      updated = if (exists) {
        updated.map {
          if (it.name.equals(heroName, ignoreCase = true) && it.status == "none") {
            it.copy(status = prefix)
          } else {
            it
          }
        }
      } else {
        updated + DraftHeroState(name = heroName, status = prefix)
      }
    }

    draftHeroesState.value = updated
    saveDraftSessionToRoom()
  }

  fun togglePlayerContractCompleted(playerId: Int) {
    val updated = draftPlayersState.value.map {
      if (it.id == playerId) {
        it.copy(completed = !it.completed)
      } else {
        it
      }
    }
    draftPlayersState.value = updated
    saveDraftSessionToRoom()
  }

  fun setPlayerVote(playerId: Int, targetName: String?) {
    val updated = draftPlayersState.value.map {
      if (it.id == playerId) {
        it.copy(voteTargetName = targetName)
      } else {
        it
      }
    }
    draftPlayersState.value = updated
    saveDraftSessionToRoom()
  }

  fun resetDraftGame() {
    viewModelScope.launch {
      repository.deleteDraftSession()
      draftPlayersState.value = emptyList()
      draftHeroesState.value = emptyList()
      draftGameActive.value = false
    }
  }

  private fun saveDraftSessionToRoom() {
    viewModelScope.launch {
      val playersArr = JSONArray()
      draftPlayersState.value.forEach { p ->
        val obj = JSONObject()
        obj.put("id", p.id)
        obj.put("name", p.name)
        obj.put("characterName", p.characterName)
        obj.put("characterGoal", p.characterGoal)
        obj.put("characterMotive", p.characterMotive)
        obj.put("fractionName", p.fractionName)
        obj.put("completed", p.completed)
        obj.put("voteTargetName", p.voteTargetName ?: JSONObject.NULL)
        playersArr.put(obj)
      }

      val heroesArr = JSONArray()
      draftHeroesState.value.forEach { h ->
        val obj = JSONObject()
        obj.put("name", h.name)
        obj.put("status", h.status)
        heroesArr.put(obj)
      }

      val session = DraftSessionEntity(
        id = 1,
        playersJson = playersArr.toString(),
        draftStateJson = heroesArr.toString(),
        isActive = true
      )
      repository.saveDraftSession(session)
    }
  }

  private fun deserializeDraftSession(session: DraftSessionEntity) {
    try {
      val playersList = mutableListOf<DraftPlayerState>()
      val playersArr = JSONArray(session.playersJson)
      for (i in 0 until playersArr.length()) {
        val obj = playersArr.getJSONObject(i)
        val vote = if (obj.isNull("voteTargetName")) null else obj.getString("voteTargetName")
        playersList.add(
          DraftPlayerState(
            id = obj.getInt("id"),
            name = obj.getString("name"),
            characterName = obj.getString("characterName"),
            characterGoal = obj.getString("characterGoal"),
            characterMotive = obj.getString("characterMotive"),
            fractionName = obj.getString("fractionName"),
            completed = obj.getBoolean("completed"),
            voteTargetName = vote
          )
        )
      }
      draftPlayersState.value = playersList

      val heroesList = mutableListOf<DraftHeroState>()
      val heroesArr = JSONArray(session.draftStateJson)
      for (i in 0 until heroesArr.length()) {
        val obj = heroesArr.getJSONObject(i)
        heroesList.add(
          DraftHeroState(
            name = obj.getString("name"),
            status = obj.getString("status")
          )
        )
      }
      
      // Defensive check: ensure all active Dota heroes exist in the list
      if (allDotaHeroes.isEmpty()) {
        allDotaHeroes = getCanonicalDotaHeroes()
      }
      val existingNames = heroesList.map { it.name.lowercase() }.toSet()
      allDotaHeroes.forEach { heroName ->
        if (!existingNames.contains(heroName.lowercase())) {
          heroesList.add(DraftHeroState(name = heroName, status = "none"))
        }
      }
      
      draftHeroesState.value = heroesList.sortedBy { it.name }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  // ==========================================
  // 4. MAFIA GAME STATE
  // ==========================================
  val mafiaSetupPlayers = MutableStateFlow<List<String>>(listOf("Алексей Пудж", "Сергей Мидер", "Марина ЦМка", "Иван Гуль", "Дмитрий Саппорт", "Анна Тракса"))
  val mafiaPlayersState = MutableStateFlow<List<MafiaPlayerState>>(emptyList())
  val mafiaCurrentPhase = MutableStateFlow<String>("Setup") // "Setup", "PassRoles", "NightIntro", "NightActive", "DayDiscussion", "DayVoting", "GameOver"
  val mafiaWinnerAlliance = MutableStateFlow<String>("") // "СИЛЫ СВЕТА" or "СИЛЫ ТЬМЫ"
  val mafiaLogs = MutableStateFlow<List<String>>(emptyList())
  
  // Track actions during night
  var mafiaNightBlockerTargetId: Int? = null
  var mafiaNightMafiaTargetId: Int? = null
  var mafiaNightDoctorTargetId: Int? = null
  var mafiaNightSheriffTargetId: Int? = null

  val mafiaIsWithHost = MutableStateFlow(false)
  val mafiaIncludeDoctor = MutableStateFlow(true)
  val mafiaIncludeSheriff = MutableStateFlow(true)
  val mafiaIncludeBlocker = MutableStateFlow(true)
  val mafiaIncludeBes = MutableStateFlow(true)
  val mafiaCountState = MutableStateFlow(1)

  // Track active night role index
  val mafiaNightRolesSequence = MutableStateFlow<List<MafiaRole>>(listOf(MafiaRole.BLOCKER, MafiaRole.RE_ZAK, MafiaRole.DOCTOR, MafiaRole.SHERIFF))
  val mafiaActiveNightRoleIndex = MutableStateFlow<Int>(0) // points to index in mafiaNightRolesSequence

  fun startMafiaGame(playerNames: List<String>) {
    val count = playerNames.size
    if (count < 3) return

    val roles = mutableListOf<MafiaRole>()
    val targetMafiaCount = mafiaCountState.value.coerceIn(1, count - 1)
    if (mafiaIncludeBes.value && targetMafiaCount >= 2) {
      roles.add(MafiaRole.BES)
      for (i in 0 until (targetMafiaCount - 1)) {
        roles.add(MafiaRole.RE_ZAK)
      }
    } else {
      for (i in 0 until targetMafiaCount) {
        roles.add(MafiaRole.RE_ZAK)
      }
    }

    if (mafiaIncludeDoctor.value && roles.size < count) {
      roles.add(MafiaRole.DOCTOR)
    }
    if (mafiaIncludeSheriff.value && roles.size < count) {
      roles.add(MafiaRole.SHERIFF)
    }
    if (mafiaIncludeBlocker.value && roles.size < count) {
      roles.add(MafiaRole.BLOCKER)
    }

    while (roles.size < count) {
      roles.add(MafiaRole.PEACEFUL)
    }

    val shuffledRoles = roles.shuffled()
    val players = playerNames.mapIndexed { idx, name ->
      MafiaPlayerState(
        id = idx + 1,
        name = name,
        role = shuffledRoles[idx],
        isAlive = true,
        cardRevealed = false
      )
    }

    val seq = mutableListOf<MafiaRole>()
    if (players.any { it.role == MafiaRole.BLOCKER }) seq.add(MafiaRole.BLOCKER)
    if (players.any { it.role == MafiaRole.RE_ZAK || players.any { it.role == MafiaRole.BES } }) seq.add(MafiaRole.RE_ZAK)
    if (players.any { it.role == MafiaRole.DOCTOR }) seq.add(MafiaRole.DOCTOR)
    if (players.any { it.role == MafiaRole.SHERIFF }) seq.add(MafiaRole.SHERIFF)
    mafiaNightRolesSequence.value = seq

    mafiaPlayersState.value = players
    mafiaLogs.value = listOf("Игра началась! Запомните свои роли.")
    mafiaWinnerAlliance.value = ""
    resetMafiaNightChoices()
    mafiaCurrentPhase.value = "PassRoles"
  }

  private fun resetMafiaNightChoices() {
    mafiaNightBlockerTargetId = null
    mafiaNightMafiaTargetId = null
    mafiaNightDoctorTargetId = null
    mafiaNightSheriffTargetId = null
    mafiaActiveNightRoleIndex.value = 0
  }

  fun revealMafiaPlayerRole(playerId: Int) {
    mafiaPlayersState.value = mafiaPlayersState.value.map {
      if (it.id == playerId) it.copy(cardRevealed = true) else it
    }
  }

  fun startMafiaNight() {
    resetMafiaNightChoices()
    // Clear protections and blocks from player states
    mafiaPlayersState.value = mafiaPlayersState.value.map {
      it.copy(isProtected = false, isBlocked = false)
    }
    mafiaCurrentPhase.value = "NightActive"
    advanceMafiaNightRole()
  }

  fun advanceMafiaNightRole() {
    // Check if we need to skip dead roles
    var index = mafiaActiveNightRoleIndex.value
    val seq = mafiaNightRolesSequence.value
    while (index < seq.size) {
      val role = seq[index]
      val roleAlive = isMafiaRoleAlive(role)
      if (roleAlive) {
        mafiaActiveNightRoleIndex.value = index
        return
      }
      index++
    }

    // If we've exhausted all roles, process night actions!
    mafiaActiveNightRoleIndex.value = index
    processMafiaNightResults()
  }

  private fun isMafiaRoleAlive(role: MafiaRole): Boolean {
    val players = mafiaPlayersState.value
    return when (role) {
      MafiaRole.RE_ZAK -> players.any { (it.role == MafiaRole.RE_ZAK || it.role == MafiaRole.BES) && it.isAlive }
      else -> players.any { it.role == role && it.isAlive }
    }
  }

  fun submitMafiaNightAction(role: MafiaRole, targetId: Int?) {
    when (role) {
      MafiaRole.BLOCKER -> mafiaNightBlockerTargetId = targetId
      MafiaRole.RE_ZAK -> mafiaNightMafiaTargetId = targetId
      MafiaRole.DOCTOR -> mafiaNightDoctorTargetId = targetId
      MafiaRole.SHERIFF -> mafiaNightSheriffTargetId = targetId
      else -> {}
    }
    mafiaActiveNightRoleIndex.value += 1
    advanceMafiaNightRole()
  }

  private fun processMafiaNightResults() {
    val players = mafiaPlayersState.value.toMutableList()
    val logs = mafiaLogs.value.toMutableList()

    logs.add("--- СОБЫТИЯ НОЧИ ---")

    // 1. Apply Blocker (Silencer)
    val blockerPlayer = players.find { it.role == MafiaRole.BLOCKER && it.isAlive }
    val blockedPlayerId = mafiaNightBlockerTargetId
    if (blockerPlayer != null && blockedPlayerId != null) {
      val blockedName = players.find { it.id == blockedPlayerId }?.name ?: ""
      logs.add("Сайленсер заблокировал действия игрока $blockedName.")
      
      // Update state for isBlocked
      val targetIdx = players.indexOfFirst { it.id == blockedPlayerId }
      if (targetIdx != -1) {
        players[targetIdx] = players[targetIdx].copy(isBlocked = true)
      }
    }

    // Helper to check if a role is blocked
    fun isPlayerRoleBlocked(role: MafiaRole): Boolean {
      return players.any { it.role == role && it.isBlocked }
    }

    // 2. Apply Doctor Protection
    val doctorPlayer = players.find { it.role == MafiaRole.DOCTOR && it.isAlive }
    val protectedPlayerId = mafiaNightDoctorTargetId
    val isDoctorBlocked = doctorPlayer != null && isPlayerRoleBlocked(MafiaRole.DOCTOR)
    if (doctorPlayer != null && protectedPlayerId != null && !isDoctorBlocked) {
      val protectedIdx = players.indexOfFirst { it.id == protectedPlayerId }
      if (protectedIdx != -1) {
        players[protectedIdx] = players[protectedIdx].copy(isProtected = true)
      }
    } else if (isDoctorBlocked) {
      logs.add("Доктор был заблокирован Сайленсером и не смог вылечить!")
    }

    // 3. Apply Mafia Kill
    val mafiaTargetId = mafiaNightMafiaTargetId
    val isMafiaBlocked = players.filter { (it.role == MafiaRole.RE_ZAK || it.role == MafiaRole.BES) && it.isAlive }.all { it.isBlocked }
    
    if (mafiaTargetId != null && !isMafiaBlocked) {
      val targetPlayer = players.find { it.id == mafiaTargetId }
      if (targetPlayer != null) {
        if (targetPlayer.isProtected) {
          logs.add("Ночью совершено покушение на ${targetPlayer.name}, но Доктор спас его!")
        } else {
          val targetIdx = players.indexOfFirst { it.id == mafiaTargetId }
          if (targetIdx != -1) {
            players[targetIdx] = players[targetIdx].copy(isAlive = false)
            logs.add("Ночью пала жертва: ${targetPlayer.name} был безжалостно устранен!")
          }
        }
      }
    } else if (isMafiaBlocked) {
      logs.add("Нападение Мафии сорвано — исполнители были заблокированы Сайленсером!")
    } else {
      logs.add("Ночь прошла тихо, покушений не совершалось.")
    }

    // 4. Sheriff Check (Legion Commander / Commissioner)
    val sheriffPlayer = players.find { it.role == MafiaRole.SHERIFF && it.isAlive }
    val checkedId = mafiaNightSheriffTargetId
    val isSheriffBlocked = sheriffPlayer != null && isPlayerRoleBlocked(MafiaRole.SHERIFF)
    if (sheriffPlayer != null && checkedId != null) {
      val checkedIdx = players.indexOfFirst { it.id == checkedId }
      if (checkedIdx != -1) {
        if (!isSheriffBlocked) {
          players[checkedIdx] = players[checkedIdx].copy(isCheckedBySheriff = true)
          val chName = players[checkedIdx].name
          val chAlliance = players[checkedIdx].role.alliance
          logs.add("Комиссар проверил игрока $chName и выяснил, что его фракция: $chAlliance.")
        } else {
          val chName = players[checkedIdx].name
          logs.add("Комиссар проверил игрока $chName, но из-за блокировки Сайленсера получил результат: Не знаю.")
        }
      }
    } else if (isSheriffBlocked) {
      logs.add("Комиссар был заблокирован Сайленсером и не успел провести проверку!")
    }

    mafiaPlayersState.value = players
    mafiaLogs.value = logs

    // Check Victory
    val continues = checkMafiaGameVictory()
    if (continues) {
      mafiaCurrentPhase.value = "DayDiscussion"
    }
  }

  fun submitMafiaVoting(votes: Map<Int, Int?>) {
    // votes maps VoterPlayerId -> TargetPlayerId
    val players = mafiaPlayersState.value.toMutableList()
    val logs = mafiaLogs.value.toMutableList()

    logs.add("--- ИТОГИ ГОЛОСОВАНИЯ ДНЯ ---")

    // Count votes
    val voteCounts = mutableMapOf<Int, Int>()
    votes.forEach { (voterId, targetId) ->
      if (targetId != null) {
        val voter = players.find { it.id == voterId }
        val isVoterBlocked = voter?.isBlocked == true
        if (!isVoterBlocked) {
          voteCounts[targetId] = voteCounts.getOrDefault(targetId, 0) + 1
        } else {
          val voterName = voter?.name ?: "Игрок"
          logs.add("Голос игрока $voterName не был учтен, так как он заблокирован Сайленсером!")
        }
      }
    }

    if (voteCounts.isEmpty()) {
      logs.add("Горожане не смогли договориться. Никто не был изгнан.")
    } else {
      val maxVotes = voteCounts.values.maxOrNull() ?: 0
      val candidatesWithMax = voteCounts.filter { it.value == maxVotes }.keys.toList()

      if (candidatesWithMax.size > 1) {
        logs.add("Ничья при голосовании ($maxVotes голосов). Никто не был изгнан.")
      } else {
        val executedId = candidatesWithMax.first()
        val executedPlayer = players.find { it.id == executedId }
        if (executedPlayer != null) {
          val idx = players.indexOfFirst { it.id == executedId }
          if (idx != -1) {
            players[idx] = players[idx].copy(isAlive = false)
            logs.add("Большинством голосов (${maxVotes} голосов) был казнен ${executedPlayer.name}. Его роль была: ${executedPlayer.role.roleName} (${executedPlayer.role.alliance}).")
          }
        }
      }
    }

    mafiaPlayersState.value = players
    mafiaLogs.value = logs

    // Check Victory
    val continues = checkMafiaGameVictory()
    if (continues) {
      mafiaCurrentPhase.value = "NightIntro"
    }
  }

  private fun checkMafiaGameVictory(): Boolean {
    val players = mafiaPlayersState.value
    val livingMafia = players.count { (it.role == MafiaRole.RE_ZAK || it.role == MafiaRole.BES) && it.isAlive }
    val livingCitizens = players.count { (it.role != MafiaRole.RE_ZAK && it.role != MafiaRole.BES) && it.isAlive }

    if (livingMafia == 0) {
      mafiaWinnerAlliance.value = "СИЛЫ СВЕТА"
      mafiaCurrentPhase.value = "GameOver"
      mafiaLogs.value = mafiaLogs.value + "ПОБЕДА СИЛ СВЕТА! Вся Мафия успешно устранена!"
      return false
    } else if (livingMafia >= livingCitizens) {
      mafiaWinnerAlliance.value = "СИЛЫ ТЬМЫ"
      mafiaCurrentPhase.value = "GameOver"
      mafiaLogs.value = mafiaLogs.value + "ПОБЕДА СИЛ ТЬМЫ! Мафия захватила контроль над городом!"
      return false
    }
    return true
  }
}

