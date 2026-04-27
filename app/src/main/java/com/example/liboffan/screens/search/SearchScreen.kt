package com.example.liboffan.screens.search

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liboffan.model.StoryItem
import com.example.liboffan.network.RetrofitClient
import com.example.liboffan.components.StoryItemCard
import com.example.liboffan.model.FandomItem
import com.example.liboffan.model.TagItem
import com.example.liboffan.model.service.FilterService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    context: Context,
    onBookClick: (Long) -> Unit,
    onBranchClick: ((Long, Long) -> Unit)? = null
) {
    val searchQuery = remember { mutableStateOf("") }
    val fandomInput = remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateOf(emptySet<String>()) }
    val selectedRating = remember { mutableStateOf("Любой") }
    val focusManager = LocalFocusManager.current
    val isFandomMenuVisible = remember { mutableStateOf(false) }

    var availableTags by remember { mutableStateOf<List<TagItem>>(emptyList()) }
    var availableFandoms by remember { mutableStateOf<List<FandomItem>>(emptyList()) }
    var filtersLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        filtersLoading = true
        val tags = FilterService.loadTags()
        val fandoms = FilterService.loadFandoms()
        availableTags = tags
        availableFandoms = fandoms
        filtersLoading = false
    }

    val displayTags = availableTags.map { it.displayName }

    val tagToQueryMap = availableTags.associate { it.displayName to it.name }
    val fandomToQueryMap = availableFandoms.associate { it.displayName to it.name }

    val filteredFandoms = remember(fandomInput.value, availableFandoms) {
        if (fandomInput.value.isBlank()) {
            availableFandoms.take(6)
        } else {
            availableFandoms.filter {
                it.displayName.contains(fandomInput.value, ignoreCase = true)
            }.take(6)
        }
    }

    var results by remember { mutableStateOf<List<StoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    fun performSearch() {
        scope.launch {
            isLoading = true
            try {
                val queryTags = selectedTags.value.mapNotNull { tagToQueryMap[it] }

                val queryFandom = fandomInput.value.let {
                    fandomToQueryMap[it] ?: it
                }

                val response = RetrofitClient.storyService.searchStories(
                    query = if (searchQuery.value.isBlank()) null else searchQuery.value,
                    fandom = if (queryFandom.isBlank()) null else queryFandom,
                    tags = if (queryTags.isEmpty()) null else queryTags,
                    ageRating = if (selectedRating.value == "Любой") null else selectedRating.value
                )

                if (response.isSuccessful && response.body() != null) {
                    results = response.body()!!.content
                } else {
                    results = emptyList()
                }
            } catch (e: Exception) {
                results = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    var searchJob by remember { mutableStateOf<Job?>(null) }
    DisposableEffect(
        searchQuery.value,
        fandomInput.value,
        selectedTags.value,
        selectedRating.value
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(500)
            performSearch()
        }
        onDispose { searchJob?.cancel() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { focusManager.clearFocus() }
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7065AC), Color(0xFF97A1EF), Color(0xFFAB98EC))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Поиск",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (filtersLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Название произведения") },
                    shape = RoundedCornerShape(12.dp),
                    colors = getTextFieldColors(),
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Фандом",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = fandomInput.value,
                    onValueChange = {
                        fandomInput.value = it
                        isFandomMenuVisible.value = it.isNotBlank()
                    },
                    label = { Text("Фандом") },
                    shape = RoundedCornerShape(12.dp),
                    colors = getTextFieldColors(),
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        if (fandomInput.value.isNotEmpty()) {
                            IconButton(onClick = {
                                fandomInput.value = ""
                                isFandomMenuVisible.value = false
                            }) {
                                Icon(Icons.Default.Clear, "Очистить")
                            }
                        }
                    }
                )

                if (isFandomMenuVisible.value && fandomInput.value.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clickable { },
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        LazyColumn {
                            items(filteredFandoms) { fandom ->
                                Text(
                                    text = fandom.displayName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            fandomInput.value = fandom.displayName
                                            isFandomMenuVisible.value = false
                                            focusManager.clearFocus()
                                        }
                                        .padding(12.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Возрастное ограничение",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                DropdownMenuFilter(
                    options = listOf("Любой", "G", "PG", "PG-13", "R", "NC-17"),
                    selected = selectedRating.value,
                    onSelected = { selectedRating.value = it },
                    backgroundColor = Color(0xFFAB98EC).copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Метки",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (displayTags.isEmpty()) {
                    Text("Теги загружаются...", color = Color.White.copy(alpha = 0.7f))
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        displayTags.forEach { tag ->
                            val isSelected = selectedTags.value.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTags.value = if (isSelected) {
                                        selectedTags.value - tag
                                    } else {
                                        selectedTags.value + tag
                                    }
                                },
                                label = {
                                    Text(
                                        tag,
                                        color = if (isSelected) Color(0xFFB8C1FF) else Color.White,
                                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color(0xFFB8C1FF).copy(alpha = 0.35f),
                                    labelColor = Color.White,

                                    selectedContainerColor = Color(0xFF7065AC),
                                    selectedLabelColor = Color(0xFFB8C1FF),

                                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                                    disabledLabelColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.height(32.dp),
                                border = BorderStroke(1.dp, Color(0xFF7065AC).copy(alpha = 0.7f))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // результаты поиска
            if (isLoading && results.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Text(
                    "Результаты (${results.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                results.forEach { item ->
                    StoryItemCard(
                        story = item,
                        onClick = { onBookClick(item.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (results.isEmpty() && !isLoading && !filtersLoading) {
                    Text(
                        "Ничего не найдено",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Вспомогательная функция (todo: вынеси в отдельный файл)
@Composable
fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
    focusedBorderColor = Color(0xFF97A1EF),
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    focusedLabelColor = Color(0xFF97A1EF),
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = Color.White
)

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
        onExpandedChange = { },
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
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Очистить",
                            tint = Color.White
                        )
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
            modifier = Modifier.menuAnchor(),
            readOnly = false
        )

        ExposedDropdownMenu(
            expanded = suggestions.isNotEmpty() && inputValue.isNotBlank(),
            onDismissRequest = {  },
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
            border = BorderStroke(1.dp, Color(0xFF7065AC).copy(alpha = 0.7f)),
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
                    }
                )
            }
        }
    }
}

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

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    result.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF7065AC).copy(alpha = 0.6f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tag, color = Color.White, fontSize = 10.sp)
                        }
                    }
                    if (result.tags.size > 3) {
                        Text(
                            "+${result.tags.size - 3}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF97A1EF), CircleShape)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    result.rating,
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class SearchResult(
    val title: String,
    val author: String,
    val fandom: String,
    val tags: List<String>,
    val rating: String
)