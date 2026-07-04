package com.example.data

data class AliasTeam(
  val id: Int,
  val name: String
)

data class AliasWord(
  val id: Int,
  val word: String
)

data class KillerWord(
  val id: Int,
  val word: String
)

data class DraftCharacter(
  val id: Int,
  val name: String,
  val goal: String,
  val motive: String
)

data class DraftFraction(
  val groupName: String,
  val description: String,
  val characters: List<DraftCharacter>,
  val jsonFileName: String
)

data class DraftPlayerState(
  val id: Int,
  val name: String,
  val characterName: String,
  val characterGoal: String,
  val characterMotive: String,
  val fractionName: String,
  val completed: Boolean,
  val voteTargetName: String? = null
)

data class DraftHeroState(
  val name: String,
  val status: String // "none", "pick_radiant", "pick_dire", "ban_radiant", "ban_dire"
)

enum class MafiaRole(
  val roleName: String,
  val englishName: String,
  val alliance: String, // "СИЛЫ СВЕТА" or "СИЛЫ ТЬМЫ"
  val description: String,
  val detailDescription: String
) {
  PEACEFUL(
    roleName = "Пудж (Pudge)",
    englishName = "PEACEFUL",
    alliance = "СИЛЫ СВЕТА",
    description = "Обычный мирный. Простой житель без способностей.",
    detailDescription = "Твоя задача — обсуждать события днем, находить подозреваемых и помогать Комиссару очистить лобби от Мафии."
  ),
  BLOCKER(
    roleName = "Сайленсер (Silencer)",
    englishName = "BLOCKER",
    alliance = "СИЛЫ СВЕТА",
    description = "Блокировщик. Может запретить игроку использовать ночные способности.",
    detailDescription = "Ночью выбираешь игрока, которого хочешь заблокировать. Он теряет возможность применить способность этой ночью."
  ),
  RE_ZAK(
    roleName = "Найт Сталкер (Night Stalker)",
    englishName = "NIGHT STALKER",
    alliance = "СИЛЫ ТЬМЫ",
    description = "Мафия. Активный убийца.",
    detailDescription = "Ночью выбираешь жертву вместе с другими членами Мафии. Днем притворяйся мирным!"
  ),
  SHERIFF(
    roleName = "Легионка (Legion Commander)",
    englishName = "SHERIFF",
    alliance = "СИЛЫ СВЕТА",
    description = "Комиссар/Шериф. Может проверять роли ночью.",
    detailDescription = "Ночью выбираешь игрока для проверки его фракции. Тебе откроется, является ли он Мафией или Мирным."
  ),
  BES(
    roleName = "Баунти Хантер (Bounty Hunter)",
    englishName = "BOUNTY HUNTER",
    alliance = "СИЛЫ ТЬМЫ",
    description = "Вспомогательный убийца/шпион.",
    detailDescription = "Второй член Мафии. Согласовывай цели с Найт Сталкером. Если Найт Сталкер заблокирован, ты берешь инициативу."
  ),
  DOCTOR(
    roleName = "Омникнайт (Omniknight)",
    englishName = "DOCTOR",
    alliance = "СИЛЫ СВЕТА",
    description = "Доктор. Может лечить одного игрока ночью.",
    detailDescription = "Ночью выбираешь игрока для исцеления. Себя лечить можно только раз за игру!"
  )
}

data class MafiaPlayerState(
  val id: Int,
  val name: String,
  val role: MafiaRole,
  val isAlive: Boolean = true,
  val isProtected: Boolean = false,
  val isBlocked: Boolean = false,
  val isCheckedBySheriff: Boolean = false,
  val voteTargetId: Int? = null,
  val cardRevealed: Boolean = false
)

