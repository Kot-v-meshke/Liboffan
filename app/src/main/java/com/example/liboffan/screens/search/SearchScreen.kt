package com.example.liboffan.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    val searchQuery = remember { mutableStateOf("") }
    val fandomInput = remember { mutableStateOf("") } // пользовательский ввод
    val selectedFandom = remember { mutableStateOf("") } // финальный выбор (может быть и не из списка)
    val isDropdownExpanded = remember { mutableStateOf(false) }

    val selectedTags = remember { mutableStateOf(mutableSetOf<String>()) }
    val selectedRating = remember { mutableStateOf("Любой") }

    // Список всех фандомов (реально — из API или БД)
    val allFandoms = listOf(
        "Гарри Поттер",
        "Сильмариллион",
        "Марвел",
        "Аниме",
        "Оригинал",
        "Доктор Кто",
        "Звёздные войны",
        "Гаррик Поропил",
        "Шерлок",
        "Атака титанов",
        "Лорд Рингс",
        "Мстители",
        "Стрэндж",
        "Хогвартс"
    )

    // Отфильтрованные подсказки
    val filteredFandoms = remember(fandomInput.value) {
        if (fandomInput.value.isBlank()) {
            emptyList<String>()
        } else {
            allFandoms
                .filter { it.contains(fandomInput.value, ignoreCase = true) }
                .take(6) // не больше 6 подсказок
        }
    }

    val results = (1..8).map { i ->
        SearchResult(
            title = "Фанфик $i",
            author = "Автор $i",
            fandom = allFandoms.random(),
            tags = listOf("Драма", "Романтика", "Психология").shuffled().take(2),
            rating = listOf("PG-13", "R", "NC-17").random()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7065AC),
                        Color(0xFF97A1EF),
                        Color(0xFFAB98EC)
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Поиск", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // === Поиск по названию/автору ===
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Название или автор") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                focusedBorderColor = Color(0xFF97A1EF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFF97A1EF),
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === Автокомплит для фандома ===
        Text("Фандом", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
        // Состояния
        val fandomInput = remember { mutableStateOf("") }

        val filteredFandoms = remember(fandomInput.value) {
            if (fandomInput.value.isBlank()) emptyList()
            else allFandoms.filter { it.contains(fandomInput.value, ignoreCase = true) }.take(6)
        }

        AutocompleteFandomField(
            inputValue = fandomInput.value,
            onInputValueChange = { fandomInput.value = it },
            suggestions = filteredFandoms,
            onSuggestionSelected = { /* обработка выбора, если нужно */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === Возрастной рейтинг ===
        Text("Возрастное ограничение", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
        DropdownMenuFilter(
            options = listOf("Любой", "G", "PG", "PG-13", "R", "NC-17"),
            selected = selectedRating.value,
            onSelected = { selectedRating.value = it },
            backgroundColor = Color(0xFFAB98EC).copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // === Метки ===
        Text("Метки", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Драма", "Романтика", "Психология", "Флафф", "Трагедия", "Плохая концовка", "Юмор", "Ангст").forEach { tag ->
                val isSelected = selectedTags.value.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedTags.value.remove(tag) else selectedTags.value.add(tag)
                    },
                    label = { Text(tag, color = if (isSelected) Color.White else Color.Black) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF97A1EF),
                        containerColor = Color.White.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === Результаты ===
        Text("Результаты", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
        results.forEach { item ->
            SearchResultItem(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// === НОВЫЙ КОМПОНЕНТ: Автокомплит для фандома ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteFandomField(
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = suggestions.isNotEmpty() && inputValue.isNotBlank(),
        onExpandedChange = { /* не управляем вручную */ },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = inputValue,
            onValueChange = onInputValueChange,
            label = { Text("Введите фандом") },
            trailingIcon = {
                if (inputValue.isNotEmpty()) {
                    IconButton(onClick = {
                        onInputValueChange("")
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить", tint = Color.White)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                focusedBorderColor = Color(0xFF97A1EF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFF97A1EF),
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.menuAnchor(), // ← ← ← КЛЮЧЕВОЙ МОМЕНТ
            readOnly = false // можно печатать
        )

        // Выпадающий список
        ExposedDropdownMenu(
            expanded = suggestions.isNotEmpty() && inputValue.isNotBlank(),
            onDismissRequest = { /* не нужно закрывать при клике вне — TextField сам управляет */ },
            modifier = Modifier
                .exposedDropdownSize()
                .background(Color(0xFF7065AC))
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion, color = Color.White) },
                    onClick = {
                        onInputValueChange(suggestion)
                        onSuggestionSelected(suggestion)
                    },
                    colors = MenuDefaults.itemColors(textColor = Color.White)
                )
            }
        }
    }
}
// Вспомогательный компонент: выпадающий фильтр
@Composable
fun DropdownMenuFilter(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    backgroundColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected, maxLines = 1)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7065AC))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.White,
                        trailingIconColor = Color.White
                    )
                )
            }
        }
    }
}

// Элемент результата поиска
@Composable
fun SearchResultItem(result: SearchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF97A1EF).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Мини-обложка
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFAB98EC).copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "by ${result.author}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = result.fandom,
                    color = Color(0xFFAB98EC),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Теги
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    result.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF7065AC).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tag, color = Color.White, fontSize = 10.sp)
                        }
                    }
                    if (result.tags.size > 3) {
                        Text("+${result.tags.size - 3}", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                    }
                }
            }

            // Рейтинг
            Box(
                modifier = Modifier
                    .background(Color(0xFF97A1EF), CircleShape)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(result.rating, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Модель данных
data class SearchResult(
    val title: String,
    val author: String,
    val fandom: String,
    val tags: List<String>,
    val rating: String
)