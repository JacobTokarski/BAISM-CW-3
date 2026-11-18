package com.example.baism_cw_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private fun buildHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = buildHttpClient()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ContentScreen(client)
                }
            }
        }
    }
}

suspend fun fetchAllCharacters(client: OkHttpClient): List<CharacterItem> = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("https://rickandmortyapi.com/api/character")
        .build()

    client.newCall(request).execute().use { response ->
        val body = response.body?.string() ?: throw Exception("Pusta odpowiedź serwera")
        val results = JSONObject(body).getJSONArray("results")
        val list = mutableListOf<CharacterItem>()

        for (i in 0 until results.length()) {
            val obj = results.getJSONObject(i)
            list.add(
                CharacterItem(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    status = obj.getString("status"),
                    species = obj.getString("species")
                )
            )
        }
        list
    }
}

suspend fun fetchCharacterById(client: OkHttpClient, id: String): String = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("https://rickandmortyapi.com/api/character/$id")
        .build()

    client.newCall(request).execute().use { response ->
        val body = response.body?.string() ?: throw Exception("Pusta odpowiedź serwera")
        val obj = JSONObject(body)
        val origin = obj.getJSONObject("origin").getString("name")
        "ID: ${obj.getInt("id")}\n" + "Imię: ${obj.getString("name")}\nStatus: ${obj.getString("status")}\nGatunek: ${obj.getString("species")}\nPochodzenie: $origin"
    }
}