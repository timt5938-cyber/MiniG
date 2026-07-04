package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

fun getDotaHeroImageUrl(rawName: String): String {
  val clean = rawName.split("(")[0].trim().lowercase()
    .replace("-", "")
    .replace("'", "")
    .replace(" ", "_")
  val steamName = when (clean) {
    "antimage" -> "antimage"
    "shadow_fiend" -> "nevermore"
    "clockwerk" -> "rattletrap"
    "windranger" -> "windrunner"
    "zeus" -> "zuus"
    "magnus" -> "magnataur"
    "queen_of_pain" -> "queenofpain"
    "lifestealer" -> "life_stealer"
    "wraith_king" -> "skeleton_king"
    "timbersaw" -> "shredder"
    "necrophos" -> "necrolyte"
    "outworld_destroyer" -> "obsidian_destroyer"
    "underlord" -> "abyssal_underlord"
    "io" -> "wisp"
    "vengeful_spirit" -> "vengefulspirit"
    "treant_protector" -> "treant"
    "natures_prophet" -> "furion"
    "drom" -> "doom"
    "centaur_warrunner" -> "centaur"
    else -> clean
  }
  return "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/$steamName.png"
}

fun getMafiaRoleImageUrl(role: MafiaRole): String {
  val name = when (role) {
    MafiaRole.PEACEFUL -> "pudge"
    MafiaRole.BLOCKER -> "silencer"
    MafiaRole.RE_ZAK -> "night_stalker"
    MafiaRole.SHERIFF -> "legion_commander"
    MafiaRole.BES -> "bounty_hunter"
    MafiaRole.DOCTOR -> "omniknight"
  }
  return getDotaHeroImageUrl(name)
}

fun getMafiaRoleImageModel(context: android.content.Context, role: MafiaRole): Any {
  val assetName = when (role) {
    MafiaRole.PEACEFUL -> "Pudge.jpeg"
    MafiaRole.BLOCKER -> "Sile.jpeg"
    MafiaRole.RE_ZAK -> "NSmafia.jpeg"
    MafiaRole.SHERIFF -> "Legin.jpeg"
    MafiaRole.BES -> "BHmafia.jpeg"
    MafiaRole.DOCTOR -> "Omni.jpeg"
  }
  
  // 1. Check assets
  try {
    context.assets.open(assetName).close()
    return "file:///android_asset/$assetName"
  } catch (e: Exception) {
    // Ignored
  }
  
  // 2. Check local directories
  try {
    val fileNames = listOf(assetName, assetName.lowercase())
    val dirs = listOf(
      android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
      context.filesDir,
      context.cacheDir,
      java.io.File("/sdcard"),
      java.io.File("/sdcard/Download")
    )
    for (dir in dirs) {
      for (fName in fileNames) {
        val file = java.io.File(dir, fName)
        if (file.exists()) {
          return file
        }
      }
    }
  } catch (e: Exception) {
    // Ignored
  }

  // 3. Check resources
  try {
    val resName = assetName.substringBefore(".").lowercase()
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    if (resId != 0) {
      return resId
    }
  } catch (e: Exception) {
    // Ignored
  }

  // 4. Fallback to Steam Dota URLs
  return getMafiaRoleImageUrl(role)
}

fun getMgLogoModel(context: android.content.Context): Any {
  // 1. Check assets
  for (fName in listOf("MG.jpeg", "mg.jpeg", "MG.jpg", "mg.jpg")) {
    try {
      context.assets.open(fName).close()
      return "file:///android_asset/$fName"
    } catch (e: Exception) {
      // Ignored
    }
  }

  // 2. Check local directories
  try {
    val dirs = listOf(
      android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
      context.filesDir,
      context.cacheDir,
      java.io.File("/sdcard"),
      java.io.File("/sdcard/Download")
    )
    for (dir in dirs) {
      for (fName in listOf("MG.jpeg", "mg.jpeg", "MG.jpg", "mg.jpg")) {
        val file = java.io.File(dir, fName)
        if (file.exists()) {
          return file
        }
      }
    }
  } catch (e: Exception) {
    // Ignored
  }

  // 3. Check resources
  try {
    val resId = context.resources.getIdentifier("mg_logo", "drawable", context.packageName)
    if (resId != 0) return resId
    val resId2 = context.resources.getIdentifier("mg", "drawable", context.packageName)
    if (resId2 != 0) return resId2
  } catch (e: Exception) {
    // Ignored
  }

  // 4. Fallback
  return com.example.R.drawable.mg
}

// Navigation screens Enum
enum class Screen {
  MainMenu,
  AliasSetup,
  AliasScores,
  AliasGame,
  AliasReview,
  AliasWinner,
  KillerSetup,
  KillerPass,
  KillerActive,
  DraftSetup,
  DraftPass,
  DraftActive,
  DraftVoting,
  DraftResults,
  MafiaSetup,
  MafiaPass,
  MafiaActive,
  MafiaResults
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGApp(viewModel: MainViewModel) {
  var currentScreen by remember { mutableStateOf(Screen.MainMenu) }
  val screenHistory = remember { mutableStateListOf<Screen>() }

  // Initialize assets when starting
  LaunchedEffect(Unit) {
    viewModel.initializeAssets()
  }

  // Helper for type-safe nav
  fun navigateTo(screen: Screen) {
    if (currentScreen != screen) {
      if (screen == Screen.MainMenu) {
        screenHistory.clear()
      } else {
        screenHistory.add(currentScreen)
      }
      currentScreen = screen
    }
  }

  fun navigateBack() {
    if (screenHistory.isNotEmpty()) {
      currentScreen = screenHistory.removeAt(screenHistory.size - 1)
    }
  }

  BackHandler(enabled = screenHistory.isNotEmpty()) {
    navigateBack()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BgBlack)
  ) {
    // Elegant background glowing particles
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.radialGradient(
            colors = listOf(Color(0x1200F3FF), Color.Transparent),
            radius = 1200f
          )
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
      // Top Neon bar
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "MINIG",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 24.sp,
              color = NeonCyan,
              modifier = Modifier.testTag("app_title")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x1FBD00FF))
                .border(1.dp, NeonPurple, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "BETA",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = NeonMagenta
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0x99040408),
          titleContentColor = TextPrimary
        ),
        actions = {
          if (currentScreen != Screen.MainMenu) {
            IconButton(
              onClick = {
                // If in active draft or killer, confirm exit
                navigateTo(Screen.MainMenu)
              },
              modifier = Modifier.testTag("home_button")
            ) {
              Icon(Icons.Default.Home, contentDescription = "В меню", tint = NeonCyan)
            }
          }
        }
      )

      Box(
        modifier = Modifier
          .fillWeight()
          .weight(1f)
      ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
          when (screen) {
            Screen.MainMenu -> MainMenuScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.AliasSetup -> AliasSetupScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.AliasScores -> AliasScoresScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.AliasGame -> AliasGameScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.AliasReview -> AliasReviewScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.AliasWinner -> AliasWinnerScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.KillerSetup -> KillerSetupScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.KillerPass -> KillerPassScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.KillerActive -> KillerActiveScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.DraftSetup -> DraftSetupScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.DraftPass -> DraftPassScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.DraftActive -> DraftActiveScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.DraftVoting -> DraftVotingScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.DraftResults -> DraftResultsScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.MafiaSetup -> MafiaSetupScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.MafiaPass -> MafiaPassScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.MafiaActive -> MafiaActiveScreen(onNavigate = ::navigateTo, viewModel = viewModel)
            Screen.MafiaResults -> MafiaResultsScreen(onNavigate = ::navigateTo, viewModel = viewModel)
          }
        }
      }
    }
  }
}

// Extension to simulate filling space
@Composable
fun Modifier.fillWeight() = this.fillMaxWidth().fillMaxHeight()

// ==========================================
// CENTRALIZED REUSABLE GLASS UI COMPONENTS
// ==========================================

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  borderColor: Color = GlassBorder,
  glowColor: Color? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(GlassBg)
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      content = content
    )
  }
}

@Composable
fun NeonButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  color: Color = NeonCyan,
  enabled: Boolean = true,
  isOutline: Boolean = false,
  icon: (@Composable () -> Unit)? = null,
  fontSize: androidx.compose.ui.unit.TextUnit = 14.sp
) {
  val alpha = if (enabled) 1f else 0.4f
  Box(
    modifier = modifier
      .graphicsLayer(alpha = alpha)
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (isOutline) Color.Transparent else color.copy(alpha = 0.15f)
      )
      .border(
        width = 1.5.dp,
        color = color,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text.uppercase(),
        color = if (isOutline) color else TextPrimary,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.SansSerif,
        fontSize = fontSize,
        letterSpacing = 1.sp
      )
    }
  }
}

@Composable
fun NeonTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  borderColor: Color = NeonCyan,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  testTag: String = ""
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    placeholder = { Text(placeholder, color = TextSecondary) },
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    colors = OutlinedTextFieldDefaults.colors(
      focusedTextColor = TextPrimary,
      unfocusedTextColor = TextPrimary,
      focusedBorderColor = borderColor,
      unfocusedBorderColor = GlassBorder,
      focusedContainerColor = Color(0x1A000000),
      unfocusedContainerColor = Color(0x0A000000),
      cursorColor = borderColor
    ),
    shape = RoundedCornerShape(12.dp),
    keyboardOptions = keyboardOptions,
    singleLine = true
  )
}

@Composable
fun CompactDraftTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  borderColor: Color = NeonCyan
) {
  BasicTextField(
    value = value,
    onValueChange = onValueChange,
    textStyle = TextStyle(
      color = TextPrimary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    ),
    cursorBrush = SolidColor(borderColor),
    singleLine = true,
    decorationBox = { innerTextField ->
      Row(
        modifier = modifier
          .fillMaxWidth()
          .background(Color(0x0E000000), RoundedCornerShape(6.dp))
          .border(1.dp, GlassBorder, RoundedCornerShape(6.dp))
          .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (value.isEmpty()) {
          Text(placeholder, color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        innerTextField()
      }
    }
  )
}

// ==========================================
// SCREEN 1: MAIN MENU
// ==========================================
@Composable
fun MainMenuScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val context = LocalContext.current
  val hasActiveDraft by viewModel.draftGameActive.collectAsState()
  val hasActiveKiller by remember {
    derivedStateOf {
      viewModel.killerPlayers.value.isNotEmpty()
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Main Promo Header
      GlassCard(borderColor = NeonPurple) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Text(
            text = "ДОБРО ПОЖАЛОВАТЬ В MINIG",
            color = NeonMagenta,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            letterSpacing = 1.2.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Сборник угарных настольных игр для киберспортсменов и настоящих дотеров.",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 14.sp
          )
        }
      }
    }

    // GAME 1: ALIAS CS
    item {
      GameMenuCard(
        title = "Alias CS: Рекрут",
        description = "Ураганная дотерская игра на объяснение слов. Специфический киберспортивный сленг, мемы про мидеров и Пуджа, штрафы за пропуск и опасные киллер-слова!",
        playersInfo = "2 команды",
        durationInfo = "15-30 мин",
        accentColor = NeonCyan,
        onPlay = { onNavigate(Screen.AliasSetup) },
        actionText = "Играть",
        testTag = "alias_play_card"
      )
    }

    // GAME 2: KILLER
    item {
      val isGameInProgress = viewModel.killerPlayers.collectAsState().value.isNotEmpty()
      GameMenuCard(
        title = "Киллер Контракт",
        description = "Тайный убийца в реальной жизни. Получи контракт на фразу, заставь цель сказать её во время игры в Доту или посиделок за столом, забери её контракт и стань лучшим киллером лобби!",
        playersInfo = "3-12 игроков",
        durationInfo = "Фоновая игра",
        accentColor = NeonGreen,
        onPlay = {
          if (isGameInProgress) {
            onNavigate(Screen.KillerActive)
          } else {
            onNavigate(Screen.KillerSetup)
          }
        },
        actionText = if (isGameInProgress) "Продолжить сессию" else "Играть",
        testTag = "killer_play_card",
        badgeText = if (isGameInProgress) "АКТИВНА" else null
      )
    }

    // GAME 3: HIDDEN DRAFT
    item {
      val isGameInProgress = hasActiveDraft
      GameMenuCard(
        title = "Скрытый Драфт",
        description = "Стратегический режим на драфт героев Dota 2! Каждый игрок получает секретную цель (контракт) на состав итогового пика сил Света или Тьмы, а также скрытый мотив. Вы должны незаметно координировать выбор персонажей, блефовать, продвигать свои интересы, мешать планам оппонентов и собирать победные очки за выполненные контракты!",
        playersInfo = "4-8 игроков",
        durationInfo = "30-50 мин",
        accentColor = NeonPurple,
        onPlay = {
          if (isGameInProgress) {
            onNavigate(Screen.DraftActive)
          } else {
            onNavigate(Screen.DraftSetup)
          }
        },
        actionText = if (isGameInProgress) "Продолжить драфт" else "Играть",
        testTag = "draft_play_card",
        badgeText = if (isGameInProgress) "АКТИВНА" else null
      )
    }

    // GAME 4: DOTA MAFIA WITH PICTURES
    item {
      val isMafiaInProgress = viewModel.mafiaPlayersState.collectAsState().value.isNotEmpty() && viewModel.mafiaCurrentPhase.collectAsState().value != "Setup" && viewModel.mafiaCurrentPhase.collectAsState().value != "GameOver"
      GameMenuCard(
        title = "Дотерская Мафия",
        description = "Классическая игра Мафия в уникальном дотерском сеттинге! Игроки получают секретные роли культовых персонажей Dota 2: добрый житель Пудж, Сайленсер (Блокировщик), коварный Найт Сталкер (Мафия/Убийца), хитрый Баунти Хантер (Мафия/Шпион), Комиссар Легионка и мудрый Доктор Омникнайт. Каждый со своей стилизованной винтажной карточкой! Вычисляйте мафиози днем, используйте уникальные способности ночью и приведите свой лагерь к победе!",
        playersInfo = "3-16 игроков",
        durationInfo = "20-40 мин",
        accentColor = NeonMagenta,
        onPlay = {
          if (isMafiaInProgress) {
            onNavigate(Screen.MafiaActive)
          } else {
            onNavigate(Screen.MafiaSetup)
          }
        },
        actionText = if (isMafiaInProgress) "Продолжить мафию" else "Играть",
        testTag = "mafia_play_card",
        badgeText = if (isMafiaInProgress) "АКТИВНА" else null
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun GameMenuCard(
  title: String,
  description: String,
  playersInfo: String,
  durationInfo: String,
  accentColor: Color,
  onPlay: () -> Unit,
  actionText: String,
  testTag: String,
  badgeText: String? = null
) {
  GlassCard(borderColor = accentColor) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title.uppercase(),
        color = accentColor,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 1.sp
      )

      if (badgeText != null) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(accentColor.copy(alpha = 0.2f))
            .border(1.dp, accentColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = badgeText,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = description,
      color = TextPrimary,
      fontSize = 13.sp,
      lineHeight = 18.sp
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Info Chips
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x0DFFFFFF))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(playersInfo, color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x0DFFFFFF))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(durationInfo, color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }

      NeonButton(
        text = actionText,
        onClick = onPlay,
        color = accentColor,
        modifier = Modifier.testTag(testTag)
      )
    }
  }
}

// ==========================================
// GAME 1: ALIAS CS SCREENS
// ==========================================

@Composable
fun AliasSetupScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  var newTeamName by remember { mutableStateOf("") }
  val teamsList by viewModel.aliasSetupTeams.collectAsState()
  var duration by remember { mutableStateOf(60f) }
  var penalty by remember { mutableStateOf(true) }
  var lastWordCommon by remember { mutableStateOf(false) }
  var scoreLimit by remember { mutableStateOf(25) }

  val funnyTeams = viewModel.allTeams

  fun addTeam() {
    val clean = newTeamName.trim()
    if (clean.isNotEmpty() && clean.length >= 2 && !teamsList.contains(clean)) {
      viewModel.aliasSetupTeams.value = viewModel.aliasSetupTeams.value + clean
      newTeamName = ""
    }
  }

  fun removeTeam(name: String) {
    viewModel.aliasSetupTeams.value = viewModel.aliasSetupTeams.value - name
  }

  fun randomizeAndAddTeam() {
    if (funnyTeams.isNotEmpty()) {
      val namesInGame = teamsList.map { it.lowercase() }
      val available = funnyTeams.filter { !namesInGame.contains(it.name.lowercase()) }
      val chosen = if (available.isNotEmpty()) available.random().name else funnyTeams.random().name
      if (!teamsList.contains(chosen)) {
        viewModel.aliasSetupTeams.value = viewModel.aliasSetupTeams.value + chosen
      }
    } else {
      // Fallback funny dota team names
      val fallbacks = listOf("322 Отмывщики", "Dead Inside", "Крипы на миду", "Внуки Габена", "Смок и ТП в лесок")
      val namesInGame = teamsList.map { it.lowercase() }
      val available = fallbacks.filter { !namesInGame.contains(it.lowercase()) }
      val chosen = if (available.isNotEmpty()) available.random() else fallbacks.random()
      if (!teamsList.contains(chosen)) {
        viewModel.aliasSetupTeams.value = viewModel.aliasSetupTeams.value + chosen
      }
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Text(
        text = "НАСТРОЙКА ALIAS CS",
        color = NeonCyan,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    }

    item {
      GlassCard(borderColor = NeonCyan) {
        Text("КОМАНДЫ (МИНИМУМ 2)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          NeonTextField(
            value = newTeamName,
            onValueChange = { newTeamName = it },
            placeholder = "Название команды",
            modifier = Modifier.weight(1f),
            borderColor = NeonCyan,
            testTag = "alias_new_team_input"
          )
          Spacer(modifier = Modifier.width(8.dp))
          NeonButton(
            text = "+Команда",
            onClick = { addTeam() },
            color = NeonCyan,
            modifier = Modifier.testTag("alias_add_team_btn")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(onClick = { randomizeAndAddTeam() }) {
            Text("Случайная команда", color = NeonMagenta, fontSize = 11.sp)
          }
          TextButton(onClick = { viewModel.aliasSetupTeams.value = emptyList() }) {
            Text("Очистить все", color = NeonRed, fontSize = 11.sp)
          }
        }
      }
    }

    item {
      if (teamsList.isNotEmpty()) {
        GlassCard(borderColor = GlassBorder) {
          Text("СПИСОК КОМАНД В ИГРЕ", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))

          teamsList.forEach { name ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(NeonCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, color = TextPrimary, fontSize = 14.sp)
              }
              IconButton(onClick = { removeTeam(name) }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = NeonRed, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }

    item {
      GlassCard(borderColor = NeonPurple) {
        Text("ПАРАМЕТРЫ МАТЧА", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Timer duration
        Text(
          text = "Время хода: ${duration.toInt()} сек",
          color = TextPrimary,
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace
        )
        Slider(
          value = duration,
          onValueChange = { duration = it },
          valueRange = 10f..180f,
          steps = 17,
          colors = SliderDefaults.colors(
            thumbColor = NeonCyan,
            activeTrackColor = NeonCyan,
            inactiveTrackColor = Color(0x33FFFFFF)
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Points to win
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Очков для победы:", color = TextPrimary, fontSize = 13.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 25, 50, 80).forEach { pts ->
              val isSelected = scoreLimit == pts
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else Color.Transparent)
                  .border(
                    width = 1.dp,
                    color = if (isSelected) NeonPurple else GlassBorder,
                    shape = RoundedCornerShape(8.dp)
                  )
                  .clickable { scoreLimit = pts }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = pts.toString(),
                  color = if (isSelected) NeonPurple else TextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Penalty switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Штраф за пропуск", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("-1 очко за каждое пропущенное слово", color = TextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = penalty,
            onCheckedChange = { penalty = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = NeonGreen,
              checkedTrackColor = NeonGreen.copy(alpha = 0.3f)
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Common last word switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Последнее слово общее", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("После таймера слово может угадать любая команда", color = TextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = lastWordCommon,
            onCheckedChange = { lastWordCommon = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = NeonGreen,
              checkedTrackColor = NeonGreen.copy(alpha = 0.3f)
            )
          )
        }
      }
    }

    item {
      val isReady = teamsList.size >= 2
      NeonButton(
        text = "Создать лобби",
        onClick = {
          viewModel.setupAliasGame(
            teams = teamsList,
            duration = duration.toInt(),
            penalty = penalty,
            commonLast = lastWordCommon,
            points = scoreLimit
          )
          onNavigate(Screen.AliasScores)
        },
        color = NeonCyan,
        enabled = isReady,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
          .testTag("alias_start_lobby")
      )
    }
  }
}

@Composable
fun AliasScoresScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val teams by viewModel.aliasTeamsState.collectAsState()
  val activeIndex = viewModel.aliasActiveTeamIndex.value
  val pointsToWin = viewModel.aliasPointsToWin.value

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "ТАБЛИЦА ЛОББИ",
        color = NeonPurple,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      teams.forEachIndexed { idx, (name, score) ->
        val isActive = idx == activeIndex
        val borderColor = if (isActive) NeonCyan else GlassBorder

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0x1F00F3FF) else GlassBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isActive) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(NeonGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
              }
              Text(
                text = name.uppercase(),
                color = if (isActive) NeonCyan else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "$score / $pointsToWin",
                color = if (isActive) NeonCyan else TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
              )
              if (isActive) {
                Text(
                  text = "ОБЪЯСНЯЕТ",
                  color = NeonGreen,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              }
            }
          }
        }
      }
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val activeTeamName = teams.getOrNull(activeIndex)?.first ?: "Команда"
      Text(
        text = "Приготовьтесь! Очередь команды:",
        color = TextSecondary,
        fontSize = 13.sp,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = activeTeamName.uppercase(),
        color = NeonCyan,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      NeonButton(
        text = "Начать ход",
        onClick = {
          viewModel.startAliasTurn()
          onNavigate(Screen.AliasGame)
        },
        color = NeonGreen,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("alias_start_turn")
      )
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
fun AliasGameScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val timerValue = viewModel.aliasTimerValue.value
  val turnDuration = viewModel.aliasTurnDuration.value
  val currentWord = viewModel.aliasCurrentWord.value
  val teams by viewModel.aliasTeamsState.collectAsState()
  val activeIndex = viewModel.aliasActiveTeamIndex.value
  val isTimerRunning = viewModel.aliasIsTimerRunning.value

  val activeTeamName = teams.getOrNull(activeIndex)?.first ?: "Команда"

  // Trigger Navigation when turn ends automatically
  LaunchedEffect(isTimerRunning, timerValue) {
    if (!isTimerRunning && timerValue == 0) {
      onNavigate(Screen.AliasReview)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header status
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("КОМАНДА", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(activeTeamName, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }

      // Neon Timer Circle / Text
      val progress = timerValue.toFloat() / turnDuration.toFloat()
      val timerColor = when {
        progress > 0.5f -> NeonGreen
        progress > 0.2f -> NeonYellow
        else -> NeonRed
      }

      Box(contentAlignment = Alignment.Center, modifier = Modifier.size(54.dp)) {
        CircularProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxSize(),
          color = timerColor,
          strokeWidth = 3.dp,
          trackColor = Color(0x1AFFFFFF)
        )
        Text(
          text = timerValue.toString(),
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          color = timerColor
        )
      }
    }

    // Centered Display word
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      if (currentWord != null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = currentWord.word.uppercase(),
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .testTag("alias_current_word")
          )
          Spacer(modifier = Modifier.height(12.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0x0DFFFFFF))
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(
              text = "СЛОЖНОСТЬ: РЕКРУТ",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = NeonCyan
            )
          }
        }
      } else {
        Text(
          text = "ЗАГРУЗКА...",
          color = TextSecondary,
          fontSize = 20.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    // Gameplay Control buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Skip Button
        NeonButton(
          text = "Пропустить",
          onClick = { viewModel.skipAliasWord() },
          color = NeonMagenta,
          isOutline = true,
          modifier = Modifier
            .weight(1f)
            .testTag("alias_skip_btn")
        )

        // Guess Button
        NeonButton(
          text = "Угадал!",
          onClick = { viewModel.guessAliasWord() },
          color = NeonGreen,
          modifier = Modifier
            .weight(1.2f)
            .testTag("alias_guess_btn")
        )
      }

      // OOPS/KILLER Button
      NeonButton(
        text = "Упс! (Киллер-слово)",
        onClick = {
          viewModel.triggerKillerWord()
          onNavigate(Screen.AliasReview)
        },
        color = NeonRed,
        isOutline = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("alias_killer_btn"),
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = NeonRed, modifier = Modifier.size(16.dp)) }
      )
    }
  }
}

@Composable
fun AliasReviewScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val reviewedWords by viewModel.wordsReviewedThisTurn
  val teams by viewModel.aliasTeamsState.collectAsState()
  val activeIndex = viewModel.aliasActiveTeamIndex.value
  val activeTeamName = teams.getOrNull(activeIndex)?.first ?: "Команда"

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "ПРОВЕРКА ОТВЕТОВ",
        color = NeonCyan,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      Text(
        text = "Команда: $activeTeamName. Нажмите на слово, чтобы изменить статус.",
        color = TextSecondary,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      if (reviewedWords.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("Нет отвеченных слов", color = TextSecondary, fontSize = 14.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .background(Color(0x0AFFFFFF))
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          itemsIndexed(reviewedWords) { index, item ->
            val (wordObj, guessed) = item
            val color = if (guessed) NeonGreen else NeonRed
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.08f))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .clickable { viewModel.toggleReviewedWord(index) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = wordObj.word.uppercase(),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (guessed) "+1" else if (viewModel.aliasPenaltyForSkip.value) "-1" else "0",
                  color = color,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = if (guessed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                  contentDescription = null,
                  tint = color,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
      NeonButton(
        text = "Подтвердить итоги",
        onClick = {
          viewModel.confirmAliasTurnResults()
          if (viewModel.aliasGameOver.value) {
            onNavigate(Screen.AliasWinner)
          } else {
            onNavigate(Screen.AliasScores)
          }
        },
        color = NeonGreen,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("alias_confirm_review")
      )
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

@Composable
fun AliasWinnerScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val winnerName = viewModel.aliasWinnerTeamName.value

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(50.dp))
        .background(NeonGreen.copy(alpha = 0.15f))
        .border(2.dp, NeonGreen, RoundedCornerShape(50.dp))
        .padding(16.dp)
    ) {
      Icon(Icons.Default.Star, contentDescription = null, tint = NeonYellow, modifier = Modifier.size(64.dp))
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "ЧЕМПИОН ЛОББИ!",
      color = NeonYellow,
      fontWeight = FontWeight.Black,
      fontSize = 24.sp,
      letterSpacing = 2.sp,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = winnerName.uppercase(),
      color = TextPrimary,
      fontWeight = FontWeight.Black,
      fontSize = 28.sp,
      textAlign = TextAlign.Center,
      letterSpacing = 1.sp,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .testTag("alias_winner_name")
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Раскатали раков в салат, закончили катку за 20 минут и заденаили трон врагов!",
      color = TextSecondary,
      fontSize = 13.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(48.dp))

    NeonButton(
      text = "В главное меню",
      onClick = { onNavigate(Screen.MainMenu) },
      color = NeonCyan,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("alias_to_menu")
    )
  }
}

// ==========================================
// GAME 2: KILLER CONTRACT SCREENS
// ==========================================

@Composable
fun KillerSetupScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  var newPlayerName by remember { mutableStateOf("") }
  val playersList by viewModel.killerSetupPlayers.collectAsState()

  fun addPlayer() {
    val clean = newPlayerName.trim()
    if (clean.isNotEmpty() && clean.length >= 2 && !playersList.contains(clean)) {
      viewModel.killerSetupPlayers.value = viewModel.killerSetupPlayers.value + clean
      newPlayerName = ""
    }
  }

  fun removePlayer(name: String) {
    viewModel.killerSetupPlayers.value = viewModel.killerSetupPlayers.value - name
  }

  fun fillTemplate() {
    viewModel.killerSetupPlayers.value = listOf("Алексей Пудж", "Сергей Мидер", "Марина ЦМка", "Иван Гуль", "Дмитрий Саппорт")
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Text(
        text = "ЛОББИ КИЛЛЕРОВ",
        color = NeonGreen,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    }

    item {
      GlassCard(borderColor = NeonGreen) {
        Text("ДОБАВЛЕНИЕ УЧАСТНИКОВ (3-12)", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          NeonTextField(
            value = newPlayerName,
            onValueChange = { newPlayerName = it },
            placeholder = "Имя киллера",
            modifier = Modifier.weight(1f),
            borderColor = NeonGreen,
            testTag = "killer_player_input"
          )
          Spacer(modifier = Modifier.width(8.dp))
          NeonButton(
            text = "Добавить",
            onClick = { addPlayer() },
            color = NeonGreen,
            modifier = Modifier.testTag("killer_add_btn")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(onClick = { fillTemplate() }) {
            Text("Заполнить тестовыми", color = NeonMagenta, fontSize = 12.sp)
          }
          TextButton(onClick = { viewModel.killerSetupPlayers.value = emptyList() }) {
            Text("Очистить все", color = NeonRed, fontSize = 12.sp)
          }
        }
      }
    }

    item {
      if (playersList.isNotEmpty()) {
        GlassCard(borderColor = GlassBorder) {
          Text("СПИСОК УЧАСТНИКОВ", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))

          playersList.forEach { name ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(NeonGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, color = TextPrimary, fontSize = 14.sp)
              }
              IconButton(onClick = { removePlayer(name) }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = NeonRed, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }

    item {
      val isReady = playersList.size >= 3
      NeonButton(
        text = "Сформировать контракты",
        onClick = {
          viewModel.startKillerGame(playersList.toList())
          onNavigate(Screen.KillerPass)
        },
        color = NeonGreen,
        enabled = isReady,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
          .testTag("killer_generate_contracts")
      )
    }
  }
}

@Composable
fun KillerPassScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.killerPlayers.collectAsState()
  var currentPlayerIndex by remember { mutableStateOf(0) }
  var isRevealed by remember { mutableStateOf(false) }

  val activePlayer = players.getOrNull(currentPlayerIndex)

  if (activePlayer == null) {
    LaunchedEffect(Unit) {
      onNavigate(Screen.KillerActive)
    }
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "РАСПРЕДЕЛЕНИЕ КОНТРАКТОВ",
      color = NeonGreen,
      fontWeight = FontWeight.Black,
      fontSize = 20.sp,
      letterSpacing = 1.5.sp,
      modifier = Modifier.padding(vertical = 8.dp)
    )

    // Secure Pass phone card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(vertical = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      GlassCard(borderColor = NeonGreen) {
        if (!isRevealed) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "ПЕРЕДАЙТЕ ТЕЛЕФОН ИГРОКУ:",
              color = TextSecondary,
              fontSize = 11.sp,
              textAlign = TextAlign.Center,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = activePlayer.name.uppercase(),
              color = NeonGreen,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Убедитесь, что никто не подглядывает в экран. Нажмите кнопку ниже для просмотра контракта.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeonButton(
              text = "Показать контракт",
              onClick = { isRevealed = true },
              color = NeonGreen,
              modifier = Modifier.testTag("killer_reveal_btn")
            )
          }
        } else {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "ТВОЙ КОНТРАКТ, ${activePlayer.name.uppercase()}",
              color = NeonMagenta,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Contract Target
            Text("ТВОЯ ЖЕРТВА:", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
            Text(
              text = activePlayer.targetName.uppercase(),
              color = NeonCyan,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contract Phrase
            Text("СЕКРЕТНАЯ ФРАЗА:", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
            Text(
              text = "\"${activePlayer.secretPhrase}\"",
              color = NeonGreen,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
              textAlign = TextAlign.Center,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
              text = "Заставь жертву произнести эту фразу во время общения или игры. Если она её скажет — жертва убита! Сразу нажми 'Убить' в приложении.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeonButton(
              text = "Я запомнил, скрыть",
              onClick = {
                isRevealed = false
                if (currentPlayerIndex < players.size - 1) {
                  currentPlayerIndex += 1
                } else {
                  // Finish pass, go to active game
                  onNavigate(Screen.KillerActive)
                }
              },
              color = NeonGreen,
              isOutline = true,
              modifier = Modifier.testTag("killer_hide_btn")
            )
          }
        }
      }
    }

    Text(
      text = "Игрок ${currentPlayerIndex + 1} из ${players.size}",
      color = TextSecondary,
      fontSize = 12.sp,
      fontFamily = FontFamily.Monospace
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KillerActiveScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.killerPlayers.collectAsState()
  val logs by viewModel.killerLogs.collectAsState()

  val alivePlayers = players.filter { it.isAlive }
  val deadPlayers = players.filter { !it.isAlive }

  var selectedVictimToKill by remember { mutableStateOf<KillerPlayerEntity?>(null) }
  var showKillConfirmDialog by remember { mutableStateOf(false) }

  // Secure reveal overlay
  var showContractChecker by remember { mutableStateOf(false) }
  var checkerSelectedPlayer by remember { mutableStateOf<KillerPlayerEntity?>(null) }
  var checkerIsRevealed by remember { mutableStateOf(false) }

  if (players.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Нет активной сессии Киллера", color = TextSecondary)
    }
    return
  }

  // Handle win condition
  val isGameOver = alivePlayers.size == 1
  val winner = alivePlayers.firstOrNull()

  if (isGameOver && winner != null) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(Icons.Default.Star, contentDescription = null, tint = NeonYellow, modifier = Modifier.size(64.dp))
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "ПОБЕДА КИЛЛЕРА!",
        color = NeonGreen,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = 2.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = winner.name.uppercase(),
        color = TextPrimary,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Все цели ликвидированы, контракты выполнены, лобби зачищено!",
        color = TextSecondary,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.height(48.dp))

      NeonButton(
        text = "Новая игра",
        onClick = { viewModel.resetKillerGame(); onNavigate(Screen.KillerSetup) },
        color = NeonGreen,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(12.dp))
      NeonButton(
        text = "В меню",
        onClick = { onNavigate(Screen.MainMenu) },
        color = NeonCyan,
        isOutline = true,
        modifier = Modifier.fillMaxWidth()
      )
    }
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = "КОНТРАКТЫ В ПРОЦЕССЕ",
        color = NeonGreen,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 1.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Status Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Text("ЖИВЫХ: ${alivePlayers.size}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text("ЛИКВИДИРОВАНО: ${deadPlayers.size}", color = NeonRed, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Active targets lists
      Text(
        text = "КЛИКНИТЕ НА ЖЕРТВУ, ЧТОБЫ УБИТЬ",
        color = TextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(6.dp))

      var revealedPlayers by remember { mutableStateOf(setOf<Int>()) }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 240.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(alivePlayers) { p ->
          val isRevealed = revealedPlayers.contains(p.id)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(GlassBg)
              .border(1.dp, if (isRevealed) NeonMagenta else GlassBorder, RoundedCornerShape(8.dp))
              .padding(4.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  selectedVictimToKill = p
                  showKillConfirmDialog = true
                }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(NeonGreen)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = p.name,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                  onClick = {
                    revealedPlayers = if (isRevealed) {
                      revealedPlayers - p.id
                    } else {
                      revealedPlayers + p.id
                    }
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.RemoveRedEye,
                    contentDescription = "Показать контракт",
                    tint = if (isRevealed) NeonMagenta else TextSecondary,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(NeonGreen.copy(alpha = 0.1f))
                    .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("ЖИВ", color = NeonGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
              }
            }

            if (isRevealed) {
              HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(horizontal = 8.dp))
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0x0AFFFFFF))
                  .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text("ЖЕРТВА: ", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Text(p.targetName.uppercase(), color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text("ФРАЗА: ", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Text("\"${p.secretPhrase}\"", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                }
              }
            }
          }
        }

        // Dead players
        items(deadPlayers) { p ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .graphicsLayer(alpha = 0.5f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x05FFFFFF))
              .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(8.dp))
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Close, contentDescription = null, tint = NeonRed, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = p.name,
                color = TextSecondary,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(NeonRed.copy(alpha = 0.1f))
                .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("УБИТ", color = NeonRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }
    }

    // Monospace terminal of kill log
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = "ЛОГ СПЕЦОПЕРАЦИЙ (ЖУРНАЛ КИЛЛЕРА)",
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(4.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
          .background(Color(0x14000000))
          .padding(8.dp)
      ) {
        if (logs.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Журнал пуст. Жертв пока нет.", color = TextSecondary, fontSize = 11.sp)
          }
        } else {
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logs) { log ->
              val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))
              Text(
                text = "[$timeStr] ${log.killerName.uppercase()} прикончил ${log.victimName.uppercase()} (фраза: \"${log.phrase}\")",
                color = NeonGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                lineHeight = 14.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action panel
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Clear Game
        NeonButton(
          text = "Очистить игру",
          onClick = { viewModel.resetKillerGame(); onNavigate(Screen.MainMenu) },
          color = NeonRed,
          isOutline = true,
          modifier = Modifier.weight(1f)
        )

        // Show My Contract Secure panel
        NeonButton(
          text = "Мой контракт",
          onClick = {
            checkerSelectedPlayer = null
            checkerIsRevealed = false
            showContractChecker = true
          },
          color = NeonMagenta,
          modifier = Modifier.weight(1.2f)
        )
      }
    }
  }

  // Assassinate Dialog
  if (showKillConfirmDialog && selectedVictimToKill != null) {
    val victim = selectedVictimToKill!!
    AlertDialog(
      onDismissRequest = { showKillConfirmDialog = false },
      title = { Text("ЗАЯВИТЬ О ЛИКВИДАЦИИ", color = NeonRed, fontWeight = FontWeight.Black) },
      text = {
        Text(
          text = "Вы подтверждаете, что игрок ${victim.name.uppercase()} произнес кодовую фразу киллера и вы его ликвидируете?\n\nВы заберете цель и контракт убитой жертвы.",
          color = TextPrimary
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.assassinatePlayer(victim.name)
            showKillConfirmDialog = false
            selectedVictimToKill = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
        ) {
          Text("ЛИКВИДИРОВАТЬ", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showKillConfirmDialog = false }) {
          Text("ОТМЕНА", color = TextSecondary)
        }
      },
      containerColor = DarkSurface
    )
  }

  // Secure My Contract dialog
  if (showContractChecker) {
    AlertDialog(
      onDismissRequest = { showContractChecker = false },
      title = { Text("КТО ТЫ, КИЛЛЕР?", color = NeonMagenta, fontWeight = FontWeight.Bold) },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text("Выбери свое имя, чтобы тайно просмотреть контракт.", color = TextSecondary, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(12.dp))

          // Dropdown of alive players
          var expanded by remember { mutableStateOf(false) }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(GlassBg)
              .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
              .clickable { expanded = true }
              .padding(16.dp)
          ) {
            Text(
              text = checkerSelectedPlayer?.name?.uppercase() ?: "ВЫБЕРИТЕ СЕБЯ",
              color = if (checkerSelectedPlayer != null) NeonCyan else TextSecondary,
              fontWeight = FontWeight.Bold
            )
            DropdownMenu(
              expanded = expanded,
              onDismissRequest = { expanded = false },
              modifier = Modifier.background(DarkSurface)
            ) {
              alivePlayers.forEach { ap ->
                DropdownMenuItem(
                  text = { Text(ap.name, color = TextPrimary) },
                  onClick = {
                    checkerSelectedPlayer = ap
                    checkerIsRevealed = false
                    expanded = false
                  }
                )
              }
            }
          }

          if (checkerSelectedPlayer != null) {
            Spacer(modifier = Modifier.height(16.dp))
            if (!checkerIsRevealed) {
              NeonButton(
                text = "Показать контракт",
                onClick = { checkerIsRevealed = true },
                color = NeonGreen,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              val currentContract = checkerSelectedPlayer!!
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(1.dp, NeonGreen, RoundedCornerShape(8.dp))
                  .background(Color(0x0A00FF00))
                  .padding(12.dp)
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                  Text("ТВОЯ ЦЕЛЬ:", color = TextSecondary, fontSize = 11.sp)
                  Text(currentContract.targetName.uppercase(), color = NeonCyan, fontWeight = FontWeight.Black, fontSize = 18.sp)
                  Spacer(modifier = Modifier.height(10.dp))
                  Text("СЕКРЕТНАЯ ФРАЗА:", color = TextSecondary, fontSize = 11.sp)
                  Text("\"${currentContract.secretPhrase}\"", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 14.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
                }
              }
              Spacer(modifier = Modifier.height(12.dp))
              NeonButton(
                text = "Скрыть контракт",
                onClick = { checkerIsRevealed = false; showContractChecker = false },
                color = NeonMagenta,
                isOutline = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showContractChecker = false }) {
          Text("ЗАКРЫТЬ", color = TextSecondary)
        }
      },
      containerColor = DarkSurface
    )
  }
}

// ==========================================
// GAME 3: HIDDEN DRAFT SCREENS
// ==========================================

@Composable
fun DraftSetupScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  var newPlayerName by remember { mutableStateOf("") }
  val playersList by viewModel.draftSetupPlayers.collectAsState()

  val allFractions = viewModel.allFractions
  val activeFractionSelections = remember { mutableStateMapOf<String, Boolean>() }

  // Autoselect active fractions when loaded
  LaunchedEffect(allFractions) {
    allFractions.forEach { frac ->
      if (!activeFractionSelections.containsKey(frac.groupName)) {
        activeFractionSelections[frac.groupName] = true
      }
    }
  }

  fun addPlayer() {
    val clean = newPlayerName.trim()
    if (clean.isNotEmpty() && clean.length >= 2 && !playersList.contains(clean)) {
      viewModel.draftSetupPlayers.value = viewModel.draftSetupPlayers.value + clean
      newPlayerName = ""
    }
  }

  fun removePlayer(name: String) {
    viewModel.draftSetupPlayers.value = viewModel.draftSetupPlayers.value - name
  }

  fun fillTemplate() {
    viewModel.draftSetupPlayers.value = listOf("Алексей", "Сергей", "Марина", "Иван", "Дмитрий")
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Text(
        text = "НАСТРОЙКА СКРЫТОГО ДРАФТА",
        color = NeonPurple,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
      )
    }

    item {
      GlassCard(borderColor = NeonPurple) {
        Text("ДОБАВЛЕНИЕ ИГРОКОВ (4-8)", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          NeonTextField(
            value = newPlayerName,
            onValueChange = { newPlayerName = it },
            placeholder = "Имя игрока",
            modifier = Modifier.weight(1f),
            borderColor = NeonPurple,
            testTag = "draft_player_input"
          )
          Spacer(modifier = Modifier.width(8.dp))
          NeonButton(
            text = "Добавить",
            onClick = { addPlayer() },
            color = NeonPurple,
            modifier = Modifier.testTag("draft_add_btn")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(onClick = { fillTemplate() }) {
            Text("Заполнить тестовыми", color = NeonMagenta, fontSize = 11.sp)
          }
          TextButton(onClick = { viewModel.draftSetupPlayers.value = emptyList() }) {
            Text("Очистить все", color = NeonRed, fontSize = 11.sp)
          }
        }
      }
    }

    item {
      if (playersList.isNotEmpty()) {
        GlassCard(borderColor = GlassBorder) {
          Text("УЧАСТНИКИ ДРАФТА", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))

          playersList.forEach { name ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(NeonPurple)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, color = TextPrimary, fontSize = 14.sp)
              }
              IconButton(onClick = { removePlayer(name) }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = NeonRed, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }

    item {
      GlassCard(borderColor = NeonMagenta) {
        Text("АКТИВНЫЕ ФРАКЦИИ РОЛЕЙ", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Роли будут распределены только из выбранных фракций.", color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))

        allFractions.forEach { frac ->
          val isSelected = activeFractionSelections[frac.groupName] ?: true
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { activeFractionSelections[frac.groupName] = !isSelected }
              .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Checkbox(
              checked = isSelected,
              onCheckedChange = { activeFractionSelections[frac.groupName] = it },
              colors = CheckboxDefaults.colors(
                checkedColor = NeonMagenta,
                checkmarkColor = Color.Black
              )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text(frac.groupName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(frac.description, color = TextSecondary, fontSize = 11.sp)
            }
          }
        }
      }
    }

    item {
      val selectedFracsList = allFractions.filter { activeFractionSelections[it.groupName] ?: true }
      val isReady = playersList.size >= 4 && playersList.size <= 8 && selectedFracsList.size >= playersList.size

      NeonButton(
        text = "Распределить роли",
        onClick = {
          viewModel.startDraftGame(playersList.toList(), selectedFracsList)
          onNavigate(Screen.DraftPass)
        },
        color = NeonPurple,
        enabled = isReady,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
          .testTag("draft_generate_roles")
      )
    }
  }
}

@Composable
fun DraftPassScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.draftPlayersState.collectAsState()
  var currentPlayerIndex by remember { mutableStateOf(0) }
  var isRevealed by remember { mutableStateOf(false) }

  val activePlayer = players.getOrNull(currentPlayerIndex)

  if (activePlayer == null) {
    LaunchedEffect(Unit) {
      onNavigate(Screen.DraftActive)
    }
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "НАЗНАЧЕНИЕ РОЛЕЙ",
      color = NeonPurple,
      fontWeight = FontWeight.Black,
      fontSize = 20.sp,
      letterSpacing = 1.5.sp,
      modifier = Modifier.padding(vertical = 8.dp)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(vertical = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      GlassCard(borderColor = NeonPurple) {
        if (!isRevealed) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "ПЕРЕДАЙТЕ ТЕЛЕФОН ИГРОКУ:",
              color = TextSecondary,
              fontSize = 11.sp,
              textAlign = TextAlign.Center,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = activePlayer.name.uppercase(),
              color = NeonPurple,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Держите экран в секрете от других. Нажмите кнопку ниже, чтобы узнать своего персонажа, цель на пики и скрытый мотив.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeonButton(
              text = "Показать мою роль",
              onClick = { isRevealed = true },
              color = NeonPurple,
              modifier = Modifier.testTag("draft_reveal_btn")
            )
          }
        } else {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "ТВОЯ РОЛЬ, ${activePlayer.name.uppercase()}",
              color = NeonMagenta,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fraction
            Text("ФРАКЦИЯ:", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(
              text = activePlayer.fractionName.uppercase(),
              color = NeonMagenta,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Character
            Text("ПЕРСОНАЖ:", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            val characterImageUrl = getDotaHeroImageUrl(activePlayer.characterName)
            androidx.compose.foundation.layout.Box(
              modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x2F000000))
                .border(2.dp, NeonCyan, RoundedCornerShape(12.dp))
            ) {
              coil.compose.AsyncImage(
                model = characterImageUrl,
                contentDescription = activePlayer.characterName,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = activePlayer.characterName.uppercase(),
              color = NeonCyan,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Goal
            Text("ТАЙНАЯ ЦЕЛЬ НА ДРАФТ:", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(
              text = activePlayer.characterGoal,
              color = TextPrimary,
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .padding(horizontal = 8.dp)
                .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .background(Color(0x05FFFFFF))
                .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Motive
            Text("СКРЫТЫЙ МОТИВ:", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(
              text = activePlayer.characterMotive,
              color = NeonGreen,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeonButton(
              text = "Я запомнил, скрыть",
              onClick = {
                isRevealed = false
                if (currentPlayerIndex < players.size - 1) {
                  currentPlayerIndex += 1
                } else {
                  onNavigate(Screen.DraftActive)
                }
              },
              color = NeonPurple,
              isOutline = true,
              modifier = Modifier.testTag("draft_hide_btn")
            )
          }
        }
      }
    }

    Text(
      text = "Игрок ${currentPlayerIndex + 1} из ${players.size}",
      color = TextSecondary,
      fontSize = 12.sp,
      fontFamily = FontFamily.Monospace
    )
  }
}

@Composable
fun DraftActiveScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val heroes by viewModel.draftHeroesState.collectAsState()
  val players by viewModel.draftPlayersState.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  val filteredHeroes = remember(searchQuery, heroes) {
    if (searchQuery.trim().isEmpty()) {
      heroes
    } else {
      heroes.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }
  }

  var selectedHeroToAssign by remember { mutableStateOf<DraftHeroState?>(null) }
  var showAssignDialog by remember { mutableStateOf(false) }

  // Extract picks/bans for easy layout representation (backward compatible with legacy draft entries)
  val radiantPicks = remember(heroes) {
    val list = (0 until 5).map { i -> heroes.find { it.status == "pick_radiant_$i" }?.name ?: "" }.toMutableList()
    val legacy = heroes.filter { it.status == "pick_radiant" }.map { it.name }
    for (i in 0 until 5) {
      if (list[i].isEmpty() && i < legacy.size) {
        list[i] = legacy[i]
      }
    }
    list
  }
  val direPicks = remember(heroes) {
    val list = (0 until 5).map { i -> heroes.find { it.status == "pick_dire_$i" }?.name ?: "" }.toMutableList()
    val legacy = heroes.filter { it.status == "pick_dire" }.map { it.name }
    for (i in 0 until 5) {
      if (list[i].isEmpty() && i < legacy.size) {
        list[i] = legacy[i]
      }
    }
    list
  }
  val radiantBansRaw = remember(heroes) {
    val raw = heroes.find { it.status == "ban_radiant_raw" }?.name
    if (raw != null) return@remember raw
    heroes.filter { it.status == "ban_radiant" }.joinToString { it.name }
  }
  val direBansRaw = remember(heroes) {
    val raw = heroes.find { it.status == "ban_dire_raw" }?.name
    if (raw != null) return@remember raw
    heroes.filter { it.status == "ban_dire" }.joinToString { it.name }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top 5v5 draft status board
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(GlassBg)
        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Radiant Column (Left)
      Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
        Text("СИЛЫ СВЕТА (RADIANT)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        // Picks
        Text("ПИКИ:", color = TextSecondary, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        for (i in 0 until 5) {
          val h = radiantPicks.getOrNull(i) ?: ""
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp)
          ) {
            Text(
              text = "${i + 1}.",
              color = NeonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.width(16.dp)
            )
            CompactDraftTextField(
              value = h,
              onValueChange = { newValue ->
                viewModel.setDraftHeroInSlot("pick_radiant_$i", newValue)
              },
              placeholder = "...",
              borderColor = NeonCyan,
              modifier = Modifier.weight(1f)
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Bans
        Text("БАНЫ:", color = TextSecondary, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        CompactDraftTextField(
          value = radiantBansRaw,
          onValueChange = { newValue ->
            viewModel.setDraftBansRaw("radiant", newValue)
          },
          placeholder = "Через запятую...",
          borderColor = NeonCyan.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        modifier = Modifier
          .width(1.dp)
          .align(Alignment.CenterVertically)
          .height(180.dp)
          .background(GlassBorder)
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Dire Column (Right)
      Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
        Text("СИЛЫ ТЬМЫ (DIRE)", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        // Picks
        Text("ПИКИ:", color = TextSecondary, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(4.dp))
        for (i in 0 until 5) {
          val h = direPicks.getOrNull(i) ?: ""
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp)
          ) {
            Text(
              text = "${i + 1}.",
              color = NeonMagenta,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.width(16.dp)
            )
            CompactDraftTextField(
              value = h,
              onValueChange = { newValue ->
                viewModel.setDraftHeroInSlot("pick_dire_$i", newValue)
              },
              placeholder = "...",
              borderColor = NeonMagenta,
              modifier = Modifier.weight(1f)
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Bans
        Text("БАНЫ:", color = TextSecondary, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        CompactDraftTextField(
          value = direBansRaw,
          onValueChange = { newValue ->
            viewModel.setDraftBansRaw("dire", newValue)
          },
          placeholder = "Через запятую...",
          borderColor = NeonMagenta.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive hero grid with search
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      NeonTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = "Поиск героя (например, Пудж...)",
        borderColor = NeonPurple,
        testTag = "draft_hero_search"
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
          .background(Color(0x06FFFFFF))
          .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(filteredHeroes) { h ->
          val isAssigned = h.status != "none"
          val color = when {
            h.status.startsWith("pick_radiant") -> NeonCyan
            h.status.startsWith("pick_dire") -> NeonMagenta
            h.status.startsWith("ban_radiant") -> NeonCyan.copy(alpha = 0.5f)
            h.status.startsWith("ban_dire") -> NeonMagenta.copy(alpha = 0.5f)
            else -> TextPrimary
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isAssigned) color.copy(alpha = 0.12f) else GlassBg)
              .border(
                1.dp,
                if (isAssigned) color else GlassBorder,
                RoundedCornerShape(10.dp)
              )
              .clickable {
                selectedHeroToAssign = h
                showAssignDialog = true
              }
              .padding(6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              val imageUrl = getDotaHeroImageUrl(h.name)
              androidx.compose.foundation.layout.Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0x1F000000))
                  .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
              ) {
                coil.compose.AsyncImage(
                  model = imageUrl,
                  contentDescription = h.name,
                  modifier = Modifier.fillMaxSize(),
                  contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
              }
              
              Spacer(modifier = Modifier.height(4.dp))
              
              val primaryName = h.name.substringBefore("(").trim().uppercase()
              val secondaryName = h.name.substringAfter("(", "").substringBefore(")", "").trim()
              
              Text(
                text = primaryName,
                color = if (isAssigned) color else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                fontFamily = FontFamily.Monospace
              )
              
              if (secondaryName.isNotEmpty()) {
                Text(
                  text = secondaryName.uppercase(),
                  color = (if (isAssigned) color else TextSecondary).copy(alpha = 0.7f),
                  fontSize = 7.sp,
                  textAlign = TextAlign.Center,
                  maxLines = 1,
                  overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                  fontFamily = FontFamily.Monospace
                )
              }
              
              if (h.status != "none") {
                val statusLabel = when {
                  h.status.startsWith("pick_radiant") -> {
                    val idx = h.status.removePrefix("pick_radiant_").toIntOrNull()
                    if (idx != null) "СВЕТ: ПИК ${idx + 1}" else "СВЕТ: ПИК"
                  }
                  h.status.startsWith("pick_dire") -> {
                    val idx = h.status.removePrefix("pick_dire_").toIntOrNull()
                    if (idx != null) "ТЬМА: ПИК ${idx + 1}" else "ТЬМА: ПИК"
                  }
                  h.status.startsWith("ban_radiant") -> "СВЕТ: БАН"
                  h.status.startsWith("ban_dire") -> "ТЬМА: БАН"
                  else -> h.status.replace("_", " ").uppercase()
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = statusLabel,
                  color = color,
                  fontSize = 7.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Navigation and help buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      NeonButton(
        text = "Сброс",
        onClick = { viewModel.resetDraftGame(); onNavigate(Screen.MainMenu) },
        color = NeonRed,
        isOutline = true,
        modifier = Modifier.weight(0.8f)
      )

      NeonButton(
        text = "К обсуждению драфта",
        onClick = { onNavigate(Screen.DraftVoting) },
        color = NeonGreen,
        modifier = Modifier
          .weight(1.5f)
          .testTag("draft_finish_draft_btn")
      )
    }
  }

  // Assign Hero status dialog
  val heroToAssign = selectedHeroToAssign
  if (showAssignDialog && heroToAssign != null) {
    AlertDialog(
      onDismissRequest = { showAssignDialog = false },
      title = { Text(heroToAssign.name.uppercase(), color = NeonPurple, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Выберите статус героя на драфте:", color = TextSecondary)

          NeonButton(
            text = "СИЛЫ СВЕТА - ПИК (RADIANT PICK)",
            onClick = {
              viewModel.toggleDraftHeroStatus(heroToAssign.name, "pick_radiant")
              showAssignDialog = false
            },
            color = NeonCyan,
            modifier = Modifier.fillMaxWidth()
          )

          NeonButton(
            text = "СИЛЫ СВЕТА - БАН (RADIANT BAN)",
            onClick = {
              viewModel.toggleDraftHeroStatus(heroToAssign.name, "ban_radiant")
              showAssignDialog = false
            },
            color = NeonCyan,
            isOutline = true,
            modifier = Modifier.fillMaxWidth()
          )

          NeonButton(
            text = "СИЛЫ ТЬМЫ - ПИК (DIRE PICK)",
            onClick = {
              viewModel.toggleDraftHeroStatus(heroToAssign.name, "pick_dire")
              showAssignDialog = false
            },
            color = NeonMagenta,
            modifier = Modifier.fillMaxWidth()
          )

          NeonButton(
            text = "СИЛЫ ТЬМЫ - БАН (DIRE BAN)",
            onClick = {
              viewModel.toggleDraftHeroStatus(heroToAssign.name, "ban_dire")
              showAssignDialog = false
            },
            color = NeonMagenta,
            isOutline = true,
            modifier = Modifier.fillMaxWidth()
          )

          NeonButton(
            text = "УБРАТЬ ИЗ ДРАФТА",
            onClick = {
              viewModel.toggleDraftHeroStatus(heroToAssign.name, "none")
              showAssignDialog = false
            },
            color = NeonRed,
            isOutline = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showAssignDialog = false }) {
          Text("ЗАКРЫТЬ", color = TextSecondary)
        }
      },
      containerColor = DarkSurface
    )
  }
}

@Composable
fun DraftVotingScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.draftPlayersState.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Text(
        text = "ГОЛОСОВАНИЕ И ПРОВЕРКА",
        color = NeonPurple,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        letterSpacing = 1.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Text(
        text = "Игроки обсуждают результаты драфта и голосуют. Также подтверждают выполнение личных целей.",
        color = TextSecondary,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }

    items(players) { p ->
      GlassCard(borderColor = if (p.completed) NeonGreen else GlassBorder) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1.2f)) {
            Text(p.name.uppercase(), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Проверяет личные цели", color = TextSecondary, fontSize = 11.sp)
          }

          // Goal completion toggle
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = if (p.completed) "ЦЕЛЬ ВЫПОЛНЕНА" else "ЦЕЛЬ ПРОВАЛЕНА",
              color = if (p.completed) NeonGreen else NeonRed,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
            Switch(
              checked = p.completed,
              onCheckedChange = { viewModel.togglePlayerContractCompleted(p.id) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonGreen,
                checkedTrackColor = NeonGreen.copy(alpha = 0.3f)
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Who did they vote for?
        var expanded by remember { mutableStateOf(false) }
        Text("Подозрение / Голос игрока:", color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GlassBg)
            .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(12.dp)
        ) {
          Text(
            text = if (p.voteTargetName != null) "ПОДОЗРЕВАЕТ: ${p.voteTargetName.uppercase()}" else "ВЫБЕРИТЕ ПОДОЗРЕВАЕМОГО",
            color = if (p.voteTargetName != null) NeonMagenta else TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurface)
          ) {
            DropdownMenuItem(
              text = { Text("НИКОГО", color = TextSecondary) },
              onClick = {
                viewModel.setPlayerVote(p.id, null)
                expanded = false
              }
            )
            players.filter { it.id != p.id }.forEach { target ->
              DropdownMenuItem(
                text = { Text(target.name, color = TextPrimary) },
                onClick = {
                  viewModel.setPlayerVote(p.id, target.name)
                  expanded = false
                }
              )
            }
          }
        }
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        NeonButton(
          text = "Вернуться к пикам",
          onClick = { onNavigate(Screen.DraftActive) },
          color = NeonPurple,
          isOutline = true,
          modifier = Modifier.weight(1f)
        )

        NeonButton(
          text = "Подвести итоги",
          onClick = { onNavigate(Screen.DraftResults) },
          color = NeonGreen,
          modifier = Modifier
            .weight(1.2f)
            .testTag("draft_show_results_btn")
        )
      }
    }
  }
}

@Composable
fun DraftResultsScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.draftPlayersState.collectAsState()

  // Score calculation:
  // - Completed contract: +3 points
  // - Each vote received: we show how many people suspected this player.
  // - Guessing impostor: if we guessed a player whose contract was completed (or we can just show details)

  // Calculate votes received count for each player name
  val votesCount = remember(players) {
    val map = mutableMapOf<String, Int>()
    players.forEach { p ->
      val target = p.voteTargetName
      if (target != null) {
        map[target] = (map[target] ?: 0) + 1
      }
    }
    map
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Text(
        text = "РЕЗУЛЬТАТЫ ДРАФТА",
        color = NeonPurple,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 1.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }

    items(players) { p ->
      val received = votesCount[p.name] ?: 0
      val points = if (p.completed) 3 else 0

      GlassCard(borderColor = if (p.completed) NeonGreen else NeonRed) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            val charImgUrl = getDotaHeroImageUrl(p.characterName)
            androidx.compose.foundation.layout.Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x1F000000))
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            ) {
              coil.compose.AsyncImage(
                model = charImgUrl,
                contentDescription = p.characterName,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(p.name.uppercase(), color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Фракция: ${p.fractionName}", color = NeonMagenta, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (p.completed) NeonGreen.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f))
              .border(
                1.dp,
                if (p.completed) NeonGreen else NeonRed,
                RoundedCornerShape(8.dp)
              )
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "$points ОЧКОВ",
              color = if (p.completed) NeonGreen else NeonRed,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Character detail
        Text("ПЕРСОНАЖ: ${p.characterName.uppercase()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("ЦЕЛЬ: ${p.characterGoal}", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("МОТИВ: ${p.characterMotive}", color = NeonGreen, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // Votes info
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Подозревали игрока: $received чел.",
            color = if (received > 0) NeonYellow else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )

          if (p.voteTargetName != null) {
            Text(
              text = "Сам голосовал за: ${p.voteTargetName.uppercase()}",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    item {
      NeonButton(
        text = "Новая игра",
        onClick = { viewModel.resetDraftGame(); onNavigate(Screen.DraftSetup) },
        color = NeonPurple,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(6.dp))
      NeonButton(
        text = "В главное меню",
        onClick = { viewModel.resetDraftGame(); onNavigate(Screen.MainMenu) },
        color = NeonCyan,
        isOutline = true,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

// ==========================================
// 5. MAFIA GAME SCREENS
// ==========================================

@Composable
fun MafiaCardCompose(role: MafiaRole, showRoleDetails: Boolean = true) {
  val accentColor = when (role.alliance) {
    "СИЛЫ СВЕТА" -> NeonCyan
    else -> NeonRed
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF2E1C0C), Color(0xFF130902))
        )
      )
      .border(2.dp, accentColor, RoundedCornerShape(16.dp))
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      // 1. Full-width role image using its native wanted-poster aspect ratio (1792x2400)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1792f / 2400f)
          .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
      ) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val imageModel = getMafiaRoleImageModel(context, role)
        coil.compose.AsyncImage(
          model = imageModel,
          contentDescription = role.roleName,
          modifier = Modifier.fillMaxSize(),
          contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
      }

      // 2. Russian role description styled on a dark glass background
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF150A02).copy(alpha = 0.95f))
          .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = role.roleName.uppercase(),
          color = NeonYellow,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = role.description,
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        if (showRoleDetails) {
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Text(
              text = role.detailDescription,
              color = TextSecondary,
              fontWeight = FontWeight.Medium,
              fontSize = 10.sp,
              textAlign = TextAlign.Center,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun MafiaSetupScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val playerNames by viewModel.mafiaSetupPlayers.collectAsState()
  var newPlayerName by remember { mutableStateOf("") }

  fun addPlayer() {
    val trimmed = newPlayerName.trim()
    if (trimmed.isNotEmpty() && !playerNames.contains(trimmed)) {
      viewModel.mafiaSetupPlayers.value = playerNames + trimmed
      newPlayerName = ""
    }
  }

  fun removePlayer(name: String) {
    viewModel.mafiaSetupPlayers.value = playerNames.filter { it != name }
  }

  fun fillTemplate() {
    viewModel.mafiaSetupPlayers.value = listOf(
      "Алексей Пудж",
      "Сергей Мидер",
      "Марина ЦМка",
      "Иван Гуль",
      "Дмитрий Саппорт",
      "Анна Тракса",
      "Коля Керри"
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(onClick = { onNavigate(Screen.MainMenu) }) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = NeonCyan)
        }
        Text(
          text = "НАСТРОЙКА МАФИИ",
          color = NeonCyan,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          letterSpacing = 1.sp
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    item {
      GlassCard {
        Text(
          text = "СПИСОК ИГРОКОВ (ОТ 3 ДО 16)",
          color = TextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          NeonTextField(
            value = newPlayerName,
            onValueChange = { newPlayerName = it },
            placeholder = "Имя игрока...",
            modifier = Modifier.weight(1f).testTag("mafia_player_input"),
            borderColor = NeonCyan
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = { addPlayer() },
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(NeonCyan.copy(alpha = 0.2f))
              .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
          ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить", tint = NeonCyan)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(onClick = { fillTemplate() }) {
            Text("Заполнить шаблон (7 игроков)", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
          TextButton(onClick = { viewModel.mafiaSetupPlayers.value = emptyList() }) {
            Text("Очистить все", color = NeonRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    items(playerNames.size) { idx ->
      val name = playerNames[idx]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(GlassBg)
          .border(1.0.dp, GlassBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(androidx.compose.foundation.shape.CircleShape)
              .background(NeonCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "${idx + 1}", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(text = name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        IconButton(onClick = { removePlayer(name) }) {
          Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = NeonRed, modifier = Modifier.size(20.dp))
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))

      val isWithHost by viewModel.mafiaIsWithHost.collectAsState()
      val includeDoctor by viewModel.mafiaIncludeDoctor.collectAsState()
      val includeSheriff by viewModel.mafiaIncludeSheriff.collectAsState()
      val includeBlocker by viewModel.mafiaIncludeBlocker.collectAsState()
      val includeBes by viewModel.mafiaIncludeBes.collectAsState()
      val mafiaCount by viewModel.mafiaCountState.collectAsState()

      GlassCard {
        Text("РЕЖИМ ИГРЫ:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (!isWithHost) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
              .border(1.dp, if (!isWithHost) NeonCyan else GlassBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.mafiaIsWithHost.value = false }
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = if (!isWithHost) NeonCyan else TextSecondary, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.height(4.dp))
              Text("На телефоне", color = if (!isWithHost) TextPrimary else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isWithHost) NeonPurple.copy(alpha = 0.15f) else Color.Transparent)
              .border(1.dp, if (isWithHost) NeonPurple else GlassBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.mafiaIsWithHost.value = true }
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Person, contentDescription = null, tint = if (isWithHost) NeonPurple else TextSecondary, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.height(4.dp))
              Text("С Ведущим", color = if (isWithHost) TextPrimary else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      GlassCard {
        Text("НАСТРОЙКА РОЛЕЙ:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Количество Мафии", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Найт Сталкеры и Баунти Хантеры", color = TextSecondary, fontSize = 9.sp)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { if (mafiaCount > 1) viewModel.mafiaCountState.value = mafiaCount - 1 },
              enabled = mafiaCount > 1
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Меньше", tint = if (mafiaCount > 1) NeonCyan else TextSecondary)
            }
            Text(text = "$mafiaCount", color = NeonCyan, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(
              onClick = { viewModel.mafiaCountState.value = mafiaCount + 1 },
              enabled = mafiaCount < (playerNames.size - 2).coerceAtLeast(1)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Больше", tint = if (mafiaCount < (playerNames.size - 2).coerceAtLeast(1)) NeonCyan else TextSecondary)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = GlassBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("🩺 Доктор (Omniknight)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Спасает одного игрока от гибели ночью", color = TextSecondary, fontSize = 9.sp)
          }
          Switch(
            checked = includeDoctor,
            onCheckedChange = { viewModel.mafiaIncludeDoctor.value = it },
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("🔎 Комиссар (Legion Commander)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Проверяет принадлежность к Мафии ночью", color = TextSecondary, fontSize = 9.sp)
          }
          Switch(
            checked = includeSheriff,
            onCheckedChange = { viewModel.mafiaIncludeSheriff.value = it },
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("🛡️ Сайленсер (Silencer)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Запрещает игроку использовать ночные способности", color = TextSecondary, fontSize = 9.sp)
          }
          Switch(
            checked = includeBlocker,
            onCheckedChange = { viewModel.mafiaIncludeBlocker.value = it },
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("🕵️ Баунти Хантер (Bounty Hunter)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Второй член Мафии (Шпион/Помощник)", color = TextSecondary, fontSize = 9.sp)
          }
          Switch(
            checked = includeBes,
            onCheckedChange = { viewModel.mafiaIncludeBes.value = it },
            enabled = mafiaCount >= 2,
            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      val isReady = playerNames.size >= 3
      val count = playerNames.size

      GlassCard {
        Text("ИТОГОВОЕ РАСПРЕДЕЛЕНИЕ РОЛЕЙ:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        val roleDescList = mutableListOf<String>()
        if (mafiaCount >= 2 && includeBes) {
          roleDescList.add("1 Баунти Хантер")
          roleDescList.add("${mafiaCount - 1} Найт Сталкер")
        } else {
          roleDescList.add("$mafiaCount Найт Сталкер")
        }
        if (includeDoctor) roleDescList.add("1 Доктор")
        if (includeSheriff) roleDescList.add("1 Комиссар")
        if (includeBlocker) roleDescList.add("1 Сайленсер")
        
        val activeSpecialRolesCount = mafiaCount + (if (includeDoctor) 1 else 0) + (if (includeSheriff) 1 else 0) + (if (includeBlocker) 1 else 0)
        val peacefulCount = (count - activeSpecialRolesCount).coerceAtLeast(0)
        if (peacefulCount > 0) {
          roleDescList.add("$peacefulCount Пудж")
        }

        Text(
          text = if (count < 3) "Добавьте минимум 3 игроков, чтобы играть." else "Будет выдано: " + roleDescList.joinToString(", "),
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      NeonButton(
        text = "Раздать роли и начать",
        onClick = {
          viewModel.startMafiaGame(playerNames)
          onNavigate(Screen.MafiaPass)
        },
        enabled = isReady,
        color = NeonMagenta,
        modifier = Modifier.fillMaxWidth().testTag("mafia_start_button")
      )
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
fun MafiaPassScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.mafiaPlayersState.collectAsState()
  val isWithHost by viewModel.mafiaIsWithHost.collectAsState()
  var currentPlayerIdx by remember { mutableStateOf(0) }
  var isRevealed by remember { mutableStateOf(false) }

  if (players.isEmpty()) {
    onNavigate(Screen.MafiaSetup)
    return
  }

  if (isWithHost) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "ЭКРАН ВЕДУЩЕГО (HOST SCREEN)",
          color = NeonPurple,
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Ведущий, покажи этот экран только себе! Ниже перечислены роли всех игроков.",
          color = TextSecondary,
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )
      }

      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(vertical = 16.dp)
      ) {
        item {
          GlassCard(borderColor = NeonPurple) {
            Text(
              text = "РАСПРЕДЕЛЕНИЕ РОЛЕЙ:",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            players.forEach { p ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = p.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                val roleColor = if (p.role == MafiaRole.RE_ZAK || p.role == MafiaRole.BES) NeonRed else NeonCyan
                Text(text = p.role.roleName, color = roleColor, fontWeight = FontWeight.Black, fontSize = 11.sp)
              }
            }
          }
        }
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        NeonButton(
          text = "Начать Первую Ночь",
          onClick = {
            viewModel.startMafiaNight()
            onNavigate(Screen.MafiaActive)
          },
          color = NeonPurple,
          modifier = Modifier.fillMaxWidth().testTag("mafia_host_start_btn")
        )
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
    return
  }

  val activePlayer = players.getOrNull(currentPlayerIdx) ?: return

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "ПОЛУЧЕНИЕ СЕКРЕТНЫХ РОЛЕЙ",
        color = NeonMagenta,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Игрок ${currentPlayerIdx + 1} из ${players.size}",
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(vertical = 24.dp),
      verticalArrangement = Arrangement.Center
    ) {
      if (!isRevealed) {
        GlassCard(borderColor = NeonMagenta) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = activePlayer.name.uppercase(),
              color = NeonMagenta,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Передайте смартфон этому игроку. Никто другой не должен видеть экран. Нажмите кнопку ниже, чтобы узнать секретную роль.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            NeonButton(
              text = "Показать роль",
              onClick = { isRevealed = true },
              color = NeonMagenta,
              modifier = Modifier.testTag("mafia_reveal_btn")
            )
          }
        }
      } else {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "ТВОЯ РОЛЬ, ${activePlayer.name.uppercase()}",
            color = NeonCyan,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          MafiaCardCompose(role = activePlayer.role, showRoleDetails = true)

          Spacer(modifier = Modifier.height(16.dp))
          NeonButton(
            text = "Я понял, скрыть роль",
            onClick = {
              viewModel.revealMafiaPlayerRole(activePlayer.id)
              if (currentPlayerIdx < players.size - 1) {
                isRevealed = false
                currentPlayerIdx++
              } else {
                viewModel.startMafiaNight()
                onNavigate(Screen.MafiaActive)
              }
            },
            color = NeonGreen,
            modifier = Modifier.fillMaxWidth().testTag("mafia_confirm_role_btn")
          )
        }
      }
    }

    Text(
      text = "Играйте честно, не подглядывайте!",
      color = TextSecondary.copy(alpha = 0.5f),
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun MafiaActiveScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val players by viewModel.mafiaPlayersState.collectAsState()
  val phase by viewModel.mafiaCurrentPhase.collectAsState()
  val activeNightIndex by viewModel.mafiaActiveNightRoleIndex.collectAsState()
  val logs by viewModel.mafiaLogs.collectAsState()
  val isWithHost by viewModel.mafiaIsWithHost.collectAsState()

  val nightSequence by viewModel.mafiaNightRolesSequence.collectAsState()
  val activeNightRole = nightSequence.getOrNull(activeNightIndex)

  // Voting states
  var voterIndex by remember { mutableStateOf(0) }
  val activeVoters = remember(players, phase) { players.filter { it.isAlive && !it.isBlocked } }
  val voter = activeVoters.getOrNull(voterIndex)
  val votingMap = remember { mutableStateMapOf<Int, Int?>() } // voterId -> targetId

  var isRevealedForNightAction by remember { mutableStateOf(false) }
  var selectedTargetId by remember { mutableStateOf<Int?>(null) }

  // Check victory in active screen just in case
  if (phase == "GameOver") {
    onNavigate(Screen.MafiaResults)
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ДОТЕРСКАЯ МАФИЯ",
        color = NeonMagenta,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp
      )
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(NeonMagenta.copy(alpha = 0.15f))
          .border(1.dp, NeonMagenta, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = when (phase) {
            "NightIntro" -> "НАЧАЛО НОЧИ"
            "NightActive" -> "НОЧНЫЕ ХОДЫ"
            "DayDiscussion" -> "ОБСУЖДЕНИЕ ДНЕМ"
            "DayVoting" -> "ДНЕВНОЕ ГОЛОСОВАНИЕ"
            else -> "МАФИЯ"
          }.uppercase(),
          color = NeonMagenta,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Main interaction area
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      when (phase) {
        "NightIntro" -> {
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = if (isWithHost) "ВЕДУЩИЙ, ОБЪЯВИ НАСТУПЛЕНИЕ НОЧИ" else "ГОРОД ЗАСЫПАЕТ. НАСТУПАЕТ НОЧЬ.",
              color = NeonPurple,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (isWithHost) "Произнесите вслух громко и четко:\n'Город засыпает. Наступает ночь. Все закрывают глаза.'\nПосле этого нажмите кнопку ниже." else "Все игроки закрывают глаза. Телефон передается только тем ролям, которые совершают выбор этой ночью.",
              color = TextSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            NeonButton(
              text = "Начать ночные ходы",
              onClick = {
                selectedTargetId = null
                isRevealedForNightAction = false
                viewModel.startMafiaNight()
              },
              color = NeonPurple,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        "NightActive" -> {
          if (activeNightRole != null) {
            val playersToPerform = players.filter { it.role == activeNightRole && it.isAlive }
            val nightActionName = when (activeNightRole) {
              MafiaRole.BLOCKER -> "САЙЛЕНСЕР (Silencer)"
              MafiaRole.RE_ZAK -> "МАФИЯ (Найт Сталкер и Баунти Хантер)"
              MafiaRole.DOCTOR -> "ДОКТОР (Omniknight)"
              MafiaRole.SHERIFF -> "КОМИССАР (Legion Commander)"
              else -> ""
            }

            if (isWithHost) {
              LazyColumn(
                modifier = Modifier.fillMaxSize()
              ) {
                item {
                  Text(
                    text = "ХОД РОЛИ: $nightActionName",
                    color = NeonPurple,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = when (activeNightRole) {
                      MafiaRole.BLOCKER -> "Произнесите: 'Просыпается Сайленсер (Silencer)'. Попросите его показать жест-цель и выберите ее на экране:"
                      MafiaRole.RE_ZAK -> "Произнесите: 'Просыпается Мафия (Найт Сталкер, Баунти Хантер)'. Попросите их показать жест-цель и выберите ее на экране:"
                      MafiaRole.DOCTOR -> "Произнесите: 'Просыпается Доктор (Omniknight)'. Попросите его показать жест-цель и выберите ее на экране:"
                      MafiaRole.SHERIFF -> "Произнесите: 'Просыпается Комиссар (Legion Commander)'. Попросите его показать жест-цель и выберите ее на экране:"
                      else -> ""
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )

                  if (activeNightRole == MafiaRole.SHERIFF && selectedTargetId != null) {
                    val checkedP = players.find { it.id == selectedTargetId }
                    if (checkedP != null) {
                      val sheriffPlayer = players.find { it.role == MafiaRole.SHERIFF && it.isAlive }
                      val isCommissionerBlocked = sheriffPlayer != null && viewModel.mafiaNightBlockerTargetId == sheriffPlayer.id

                      val isMafia = checkedP.role == MafiaRole.RE_ZAK || checkedP.role == MafiaRole.BES
                      val allianceColor = if (isCommissionerBlocked) TextSecondary else (if (isMafia) NeonRed else NeonGreen)
                      val allianceText = if (isCommissionerBlocked) "НЕ ЗНАЮ" else checkedP.role.alliance
                      val hintText = if (isCommissionerBlocked) "Комиссар заблокирован Сайленсером! Покажите жест 'Не знаю'." else "Молча покажите Комиссару палец вверх (Светлые) или вниз (Мафия)"

                      Spacer(modifier = Modifier.height(10.dp))
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(allianceColor.copy(alpha = 0.15f))
                          .border(1.dp, allianceColor, RoundedCornerShape(8.dp))
                          .padding(10.dp)
                      ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                          Text(text = "РЕЗУЛЬТАТ ПРОВЕРКИ ДЛЯ КОМИССАРА:", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                          Spacer(modifier = Modifier.height(2.dp))
                          Text(
                            text = "${checkedP.name} — $allianceText",
                            color = allianceColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                          )
                          Spacer(modifier = Modifier.height(2.dp))
                          Text(text = hintText, color = TextPrimary, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))
                }

                val choices = players.filter { it.isAlive }
                items(choices.size) { index ->
                  val target = choices[index]
                  val isSelected = selectedTargetId == target.id
                  val borderColor = if (isSelected) NeonCyan else GlassBorder

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (isSelected) NeonCyan.copy(alpha = 0.1f) else GlassBg)
                      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                      .clickable { selectedTargetId = target.id }
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = target.name,
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    if (isSelected) {
                      Icon(Icons.Default.Check, contentDescription = "Выбран", tint = NeonCyan, modifier = Modifier.size(18.dp))
                    }
                  }
                }

                item {
                  Spacer(modifier = Modifier.height(24.dp))
                  NeonButton(
                    text = "Подтвердить ход и усыпить роль",
                    onClick = {
                      viewModel.submitMafiaNightAction(activeNightRole, selectedTargetId)
                      selectedTargetId = null
                    },
                    enabled = selectedTargetId != null,
                    color = NeonPurple,
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }
            } else {
              // Phone pass mode
              if (!isRevealedForNightAction) {
                Column(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(64.dp))
                  Spacer(modifier = Modifier.height(16.dp))
                  Text(
                    text = "НОЧНОЙ ХОД: $nightActionName",
                    color = NeonPurple,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Передайте телефон игроку с этой ролью в секрете от других игроков.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(24.dp))
                  NeonButton(
                    text = "Я эта роль, открыть выбор",
                    onClick = { isRevealedForNightAction = true },
                    color = NeonPurple,
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              } else {
                LazyColumn(
                  modifier = Modifier.fillMaxSize()
                ) {
                  item {
                    Text(
                      text = "ТВОЙ ХОД, ${activeNightRole.roleName.uppercase()}",
                      color = NeonCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = when (activeNightRole) {
                        MafiaRole.BLOCKER -> "Выберите игрока, чтобы заблокировать его ночное действие."
                        MafiaRole.RE_ZAK -> "Выберите игрока, которого хотите уничтожить."
                        MafiaRole.DOCTOR -> "Выберите игрока, которого хотите исцелить этой ночью."
                        MafiaRole.SHERIFF -> "Выберите игрока, роль которого хотите проверить."
                        else -> ""
                      },
                      color = TextSecondary,
                      fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                  }

                  val choices = players.filter { it.isAlive }
                  items(choices.size) { index ->
                    val target = choices[index]
                    val isSelected = selectedTargetId == target.id
                    val borderColor = if (isSelected) NeonCyan else GlassBorder

                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.1f) else GlassBg)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { selectedTargetId = target.id }
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = target.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = "Выбран", tint = NeonCyan, modifier = Modifier.size(18.dp))
                      }
                    }
                  }

                  item {
                    if (activeNightRole == MafiaRole.SHERIFF && selectedTargetId != null) {
                      val checkedP = players.find { it.id == selectedTargetId }
                      if (checkedP != null) {
                        val sheriffPlayer = players.find { it.role == MafiaRole.SHERIFF && it.isAlive }
                        val isCommissionerBlocked = sheriffPlayer != null && viewModel.mafiaNightBlockerTargetId == sheriffPlayer.id

                        val isMafia = checkedP.role == MafiaRole.RE_ZAK || checkedP.role == MafiaRole.BES
                        val allianceColor = if (isCommissionerBlocked) TextSecondary else (if (isMafia) NeonRed else NeonGreen)
                        val roleText = if (isCommissionerBlocked) "НЕ ЗНАЮ" else (if (isMafia) "МАФИЯ" else "НЕ МАФИЯ")
                        val descText = if (isCommissionerBlocked) "Вы были заблокированы Сайленсером и ваши чувства подвели вас." else "Остальные игроки не увидят этот результат. Запомните его."

                        Box(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .background(allianceColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, allianceColor, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                        ) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                              text = "РЕЗУЛЬТАТ ПРОВЕРКИ ДЛЯ КОМИССАРА:",
                              color = TextSecondary,
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                              text = "${checkedP.name} — $roleText",
                              color = allianceColor,
                              fontWeight = FontWeight.Black,
                              fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                              text = descText,
                              color = TextSecondary,
                              fontSize = 10.sp,
                              textAlign = TextAlign.Center
                            )
                          }
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    NeonButton(
                      text = "Подтвердить выбор",
                      onClick = {
                        viewModel.submitMafiaNightAction(activeNightRole, selectedTargetId)
                        selectedTargetId = null
                        isRevealedForNightAction = false
                      },
                      enabled = selectedTargetId != null,
                      color = NeonPurple,
                      modifier = Modifier.fillMaxWidth()
                    )
                  }
                }
              }
            }
          }
        }

        "DayDiscussion" -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize()
          ) {
            item {
              Text(
                text = "СОБЫТИЯ ПОСЛЕДНЕЙ НОЧИ:",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              // Show only the latest logs of the night
              val nightLogs = if (!isWithHost) {
                // В событии последней ночи должен показываться только Сайленсер (Блокер). Доктор тоже не отображать в событиях ночи.
                logs.filter { 
                  !it.startsWith("---") && 
                  !it.contains("изгнан") && 
                  !it.contains("казнен") && 
                  it.contains("Сайленсер") &&
                  !it.contains("Доктор")
                }.takeLast(4)
              } else {
                logs.filter { !it.startsWith("---") && !it.contains("изгнан") && !it.contains("казнен") }.takeLast(4)
              }
              GlassCard(borderColor = NeonCyan) {
                if (nightLogs.isEmpty()) {
                  Text("Ночь прошла абсолютно спокойно.", color = TextPrimary, fontSize = 12.sp)
                } else {
                  nightLogs.forEach { log ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                      Text("• ", color = NeonCyan, fontWeight = FontWeight.Bold)
                      Text(log, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                  }
                }
              }
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "ЖИВЫЕ И КАЗНЕННЫЕ ИГРОКИ:",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
            }

            items(players.size) { idx ->
              val p = players[idx]
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (p.isAlive) GlassBg else Color(0x1F000000))
                  .border(1.dp, if (p.isAlive) GlassBorder else NeonRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = p.name,
                    color = if (p.isAlive) TextPrimary else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    style = if (p.isAlive) androidx.compose.ui.text.TextStyle.Default else androidx.compose.ui.text.TextStyle.Default.copy(
                      textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                  )
                  if (!p.isAlive) {
                    Text(
                      text = "Убит / Роль: ${p.role.roleName} (${p.role.alliance})",
                      color = NeonRed,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  } else {
                    if (p.isBlocked) {
                      Text(
                        text = "🤐 ЗАБЛОКИРОВАН Сайленсером (не голосует)",
                        color = NeonRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    if (p.isCheckedBySheriff) {
                      Text(
                        text = "Проверен Комиссаром: ${p.role.alliance}",
                        color = NeonYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (p.isAlive) NeonGreen.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f))
                    .border(1.dp, if (p.isAlive) NeonGreen else NeonRed, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (p.isAlive) "ЖИВ" else "МЕРТВ",
                    color = if (p.isAlive) NeonGreen else NeonRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(24.dp))
              NeonButton(
                text = "Перейти к голосованию",
                onClick = {
                  voterIndex = 0
                  votingMap.clear()
                  viewModel.mafiaCurrentPhase.value = "DayVoting"
                },
                color = NeonCyan,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(24.dp))
            }
          }
        }

        "DayVoting" -> {
          if (isWithHost) {
            Column(
              modifier = Modifier.fillMaxSize()
            ) {
              Text(
                text = "ИЗГНАНИЕ ИГРОКА (ГОЛОСОВАНИЕ)",
                color = NeonYellow,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Проведите открытое голосование в комнате. Выберите игрока, которого решило казнить большинство, или выберите 'Никто не изгнан'.",
                color = TextSecondary,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(12.dp))

              var votingTargetId by remember { mutableStateOf<Int?>(null) }

              LazyColumn(
                modifier = Modifier.weight(1f)
              ) {
                val targets = players.filter { it.isAlive }
                items(targets.size) { idx ->
                  val t = targets[idx]
                  val isSelected = votingTargetId == t.id
                  val borderColor = if (isSelected) NeonYellow else GlassBorder

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (isSelected) NeonYellow.copy(alpha = 0.10f) else GlassBg)
                      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                      .clickable { votingTargetId = t.id }
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = t.name,
                      color = TextPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    if (isSelected) {
                      Icon(Icons.Default.Check, contentDescription = "Выбран", tint = NeonYellow, modifier = Modifier.size(18.dp))
                    }
                  }
                }

                item {
                  val isNoOne = votingTargetId == null
                  val borderColor = if (isNoOne) NeonYellow else GlassBorder
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(if (isNoOne) NeonYellow.copy(alpha = 0.10f) else GlassBg)
                      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                      .clickable { votingTargetId = null }
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Никто не изгнан (Ничья / Пропустить)",
                      color = TextSecondary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      style = androidx.compose.ui.text.TextStyle.Default.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                      )
                    )
                    if (isNoOne) {
                      Icon(Icons.Default.Close, contentDescription = "Никто", tint = NeonYellow, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              NeonButton(
                text = "Подтвердить казнь",
                onClick = {
                  val simulatedVotes = mutableMapOf<Int, Int?>()
                  if (votingTargetId != null) {
                    val alive = players.filter { it.isAlive }
                    alive.forEach { voterP ->
                      simulatedVotes[voterP.id] = votingTargetId
                    }
                  }
                  viewModel.submitMafiaVoting(simulatedVotes)
                },
                color = NeonYellow,
                modifier = Modifier.fillMaxWidth()
              )
            }
          } else {
            if (voter != null) {
              Column(
                modifier = Modifier.fillMaxSize()
              ) {
                Text(
                  text = "ДНЕВНОЕ ГОЛОСОВАНИЕ",
                  color = NeonYellow,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Голосует игрок: ${voter.name.uppercase()} (${voterIndex + 1} из ${activeVoters.size})",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Выберите игрока, которого вы подозреваете в причастности к Мафии, или воздержитесь.",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                  modifier = Modifier.weight(1f)
                ) {
                  val targets = players.filter { it.isAlive && it.id != voter.id }
                  items(targets.size) { idx ->
                    val t = targets[idx]
                    val isVotedForThis = votingMap[voter.id] == t.id
                    val borderColor = if (isVotedForThis) NeonYellow else GlassBorder

                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isVotedForThis) NeonYellow.copy(alpha = 0.10f) else GlassBg)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { votingMap[voter.id] = t.id }
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = t.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      if (isVotedForThis) {
                        Icon(Icons.Default.Check, contentDescription = "Выбран", tint = NeonYellow, modifier = Modifier.size(18.dp))
                      }
                    }
                  }

                  item {
                    val isAbstained = votingMap[voter.id] == null
                    val borderColor = if (isAbstained) NeonYellow else GlassBorder
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAbstained) NeonYellow.copy(alpha = 0.10f) else GlassBg)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable { votingMap[voter.id] = null }
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Воздержаться от голосования",
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        style = androidx.compose.ui.text.TextStyle.Default.copy(
                          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                      )
                      if (isAbstained) {
                        Icon(Icons.Default.Close, contentDescription = "Воздержался", tint = NeonYellow, modifier = Modifier.size(18.dp))
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NeonButton(
                  text = if (voterIndex < activeVoters.size - 1) "Следующий игрок" else "Подсчитать голоса",
                  onClick = {
                    if (voterIndex < activeVoters.size - 1) {
                      voterIndex++
                    } else {
                      viewModel.submitMafiaVoting(votingMap.toMap())
                    }
                  },
                  color = NeonYellow,
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Logs & Back button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      TextButton(onClick = { onNavigate(Screen.MainMenu) }) {
        Text("Выйти в меню", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
      }

      val livingCount = players.count { it.isAlive }
      val mafiaCount = players.count { (it.role == MafiaRole.RE_ZAK || it.role == MafiaRole.BES) && it.isAlive }

      Text(
        text = "Живых: $livingCount | Мафии: $mafiaCount",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun MafiaResultsScreen(onNavigate: (Screen) -> Unit, viewModel: MainViewModel) {
  val winnerAlliance by viewModel.mafiaWinnerAlliance.collectAsState()
  val players by viewModel.mafiaPlayersState.collectAsState()
  val logs by viewModel.mafiaLogs.collectAsState()

  val isRadiantWinner = winnerAlliance == "СИЛЫ СВЕТА"
  val winColor = if (isRadiantWinner) NeonCyan else NeonRed

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = if (isRadiantWinner) Icons.Default.CheckCircle else Icons.Default.Warning,
          contentDescription = null,
          tint = winColor,
          modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = if (isRadiantWinner) "ПОБЕДА СИЛ СВЕТА!" else "ПОБЕДА СИЛ ТЬМЫ!",
          color = winColor,
          fontWeight = FontWeight.Black,
          fontSize = 24.sp,
          textAlign = TextAlign.Center
        )
        Text(
          text = if (isRadiantWinner) "Сплоченные горожане очистили город!" else "Коварная Мафия захватила контроль над лобби!",
          color = TextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    item {
      Text(
        text = "РОЛИ ВСЕХ УЧАСТНИКОВ:",
        color = winColor,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(players.size) { index ->
      val p = players[index]
      val isMafia = p.role == MafiaRole.RE_ZAK || p.role == MafiaRole.BES
      val roleColor = if (isMafia) NeonRed else NeonCyan

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(GlassBg)
          .border(1.dp, if (p.isAlive) roleColor.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(12.dp))
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = p.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "${p.role.roleName} (${p.role.alliance})", color = roleColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (p.isAlive) NeonGreen.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f))
            .border(1.dp, if (p.isAlive) NeonGreen else NeonRed, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (p.isAlive) "ВЫЖИЛ" else "УБИТ",
            color = if (p.isAlive) NeonGreen else NeonRed,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "ИСТОРИЯ И ХРОНОЛОГИЯ ИГРЫ:",
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      GlassCard(borderColor = winColor.copy(alpha = 0.3f)) {
        logs.forEach { log ->
          Text(
            text = log,
            color = if (log.startsWith("---")) winColor else TextPrimary,
            fontSize = 11.sp,
            fontWeight = if (log.startsWith("---")) FontWeight.ExtraBold else FontWeight.Medium,
            modifier = Modifier.padding(vertical = 2.dp),
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      NeonButton(
        text = "Играть снова",
        onClick = { onNavigate(Screen.MafiaSetup) },
        color = NeonMagenta,
        modifier = Modifier.fillMaxWidth().testTag("mafia_restart_btn")
      )
      Spacer(modifier = Modifier.height(6.dp))
      NeonButton(
        text = "В главное меню",
        onClick = { onNavigate(Screen.MainMenu) },
        color = NeonCyan,
        isOutline = true,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
