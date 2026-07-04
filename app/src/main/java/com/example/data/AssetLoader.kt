package com.example.data

import android.content.Context
import org.json.JSONObject

object AssetLoader {

  fun loadTeams(context: Context): List<AliasTeam> {
    return try {
      val jsonStr = context.assets.open("AliasCS.json").bufferedReader().use { it.readText() }
      val obj = JSONObject(jsonStr)
      val arr = obj.getJSONArray("teams")
      val list = mutableListOf<AliasTeam>()
      for (i in 0 until arr.length()) {
        val t = arr.getJSONObject(i)
        list.add(AliasTeam(t.getInt("id"), t.getString("name")))
      }
      list
    } catch (e: Exception) {
      e.printStackTrace()
      emptyList()
    }
  }

  fun loadRekrutWords(context: Context): List<AliasWord> {
    return try {
      val jsonStr = context.assets.open("Rekrut.json").bufferedReader().use { it.readText() }
      val obj = JSONObject(jsonStr)
      val arr = obj.getJSONArray("words")
      val list = mutableListOf<AliasWord>()
      for (i in 0 until arr.length()) {
        val w = arr.getJSONObject(i)
        list.add(AliasWord(w.getInt("id"), w.getString("word")))
      }
      list
    } catch (e: Exception) {
      e.printStackTrace()
      emptyList()
    }
  }

  fun loadKillerWords(context: Context): List<KillerWord> {
    return try {
      val jsonStr = context.assets.open("Killer.json").bufferedReader().use { it.readText() }
      val obj = JSONObject(jsonStr)
      val arr = obj.getJSONArray("words")
      val list = mutableListOf<KillerWord>()
      for (i in 0 until arr.length()) {
        val w = arr.getJSONObject(i)
        list.add(KillerWord(w.getInt("id"), w.getString("word")))
      }
      list
    } catch (e: Exception) {
      e.printStackTrace()
      emptyList()
    }
  }

  fun loadFraction(context: Context, fileName: String): DraftFraction? {
    return try {
      val jsonStr = context.assets.open(fileName).bufferedReader().use { it.readText() }
      val obj = JSONObject(jsonStr)
      val groupName = obj.getString("group_name")
      val description = obj.getString("description")
      val arr = obj.getJSONArray("characters")
      val chars = mutableListOf<DraftCharacter>()
      for (i in 0 until arr.length()) {
        val c = arr.getJSONObject(i)
        chars.add(DraftCharacter(c.getInt("id"), c.getString("name"), c.getString("goal"), c.getString("motive")))
      }
      DraftFraction(groupName, description, chars, fileName)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun loadAllFractions(context: Context): List<DraftFraction> {
    val files = listOf(
      "ChaosAgents.json",
      "Controllers.json",
      "CyberPro.json",
      "DarkPack.json",
      "JungleBeasts.json",
      "MathSchizos.json",
      "Parasites.json",
      "Savers.json"
    )
    return files.mapNotNull { loadFraction(context, it) }
  }
}
