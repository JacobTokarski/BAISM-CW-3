package com.example.baism_cw_3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@Composable
fun ContentScreen(
    client: OkHttpClient
) {
    val scope = rememberCoroutineScope()
    var charactersList by remember { mutableStateOf<List<CharacterItem>>(emptyList()) }
    var singleCharacterInfo by remember { mutableStateOf("Brak pobranych danych pojedynczej postaci") }
    var characterIdInput by remember { mutableStateOf("1") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            text = "BAISM-CW-3 Rick & Morty API - Ekran główny",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {
                errorMessage = null
                scope.launch {
                    try {
                        val list = fetchAllCharacters(client)
                        charactersList = list
                    } catch (e: Exception) {
                        errorMessage = "Błąd pobierania postaci"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Pobieranie listy postaci"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = characterIdInput,
                onValueChange = { characterIdInput = it },
                label = { Text("ID") },
                modifier = Modifier.width(80.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    errorMessage = null
                    scope.launch {
                        try {
                            val info = fetchCharacterById(client, characterIdInput.trim())
                            singleCharacterInfo = info
                        } catch (e: Exception) {
                            errorMessage = "Błąd sieci/SSL: ${e.javaClass.simpleName} - ${e.message}"
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Pobieranie postaci po konkretnym ID"
                )
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = singleCharacterInfo,
                modifier = Modifier
                    .padding(12.dp),
                fontWeight = FontWeight.Normal
            )
        }

        HorizontalDivider()

        Text(
            text = "Lista postaci (${charactersList.size}):",
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(charactersList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "#${item.id} | ${item.name} (${item.species} - ${item.status})",
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}