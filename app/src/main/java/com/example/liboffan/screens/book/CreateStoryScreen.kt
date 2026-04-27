package com.example.liboffan.screens.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liboffan.TokenManager
import com.example.liboffan.model.TagItem
import com.example.liboffan.model.request.CreateStoryRequest
import com.example.liboffan.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    onBack: () -> Unit,
    onCreateSuccess: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var selectedAgeRating by remember { mutableStateOf("PG") }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }

    var isLoading by remember { mutableStateOf(true) }
    var availableTags by remember { mutableStateOf<List<TagItem>>(emptyList()) }
    var isCreating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.storyService.getAllTags()
            if (response.isSuccessful && response.body() != null) {
                availableTags = response.body()!!
            }
        } catch (e: Exception) {
            // todo
        } finally {
            isLoading = false
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )

    val ageRatings = listOf("G", "PG", "PG-13", "R", "NC-17")
    var isRatingMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать историю", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7065AC))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7065AC), Color(0xFF97A1EF), Color(0xFFAB98EC))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название истории *", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Краткое описание (необязательно)", color = Color.White.copy(alpha = 0.7f)) },
                    placeholder = { Text("О чём эта история?", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    maxLines = 5,
                    minLines = 3
                )

                Text("Возрастной рейтинг *", color = Color.White, fontWeight = FontWeight.SemiBold)

                Box {
                    OutlinedButton(
                        onClick = { isRatingMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCreating
                    ) {
                        Text(selectedAgeRating)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowDropDown, "Выбрать", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = isRatingMenuExpanded,
                        onDismissRequest = { isRatingMenuExpanded = false },
                        modifier = Modifier.background(Color(0xFF7065AC)).width(150.dp)
                    ) {
                        ageRatings.forEach { rating ->
                            DropdownMenuItem(
                                text = { Text(rating, color = Color.White) },
                                onClick = {
                                    selectedAgeRating = rating
                                    isRatingMenuExpanded = false
                                },
                                colors = MenuDefaults.itemColors(textColor = Color.White),
                                trailingIcon = {
                                    if (selectedAgeRating == rating) {
                                        Icon(Icons.Default.Check, "Выбрано", tint = Color.White)
                                    }
                                }
                            )
                        }
                    }
                }

                Text("Метки (теги)", color = Color.White, fontWeight = FontWeight.SemiBold)

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    }
                } else if (availableTags.isEmpty()) {
                    Text("Нет доступных тегов", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val isSelected = selectedTags.contains(tag.displayName)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTags = if (isSelected) {
                                        selectedTags - tag.displayName
                                    } else {
                                        selectedTags + tag.displayName
                                    }
                                },
                                label = { Text(tag.displayName, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color(0xFFB8C1FF).copy(alpha = 0.35f),
                                    labelColor = Color.White,
                                    selectedContainerColor = Color(0xFF7065AC),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.height(32.dp),
                                enabled = !isCreating
                            )
                        }
                    }
                }

                Text("Текст истории *", color = Color.White, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Начните писать...", color = Color.White.copy(alpha = 0.7f)) },
                    placeholder = {
                        Text(
                            "Это будет первая версия вашей истории...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = textFieldColors,
                    minLines = 10,
                    maxLines = 15,
                    enabled = !isCreating
                )

                Button(
                    onClick = {
                        if (title.isBlank() || content.isBlank()) return@Button
                        isCreating = true

                        scope.launch {
                            val token = TokenManager(context).authToken ?: ""

                            try {
                                val selectedTagNames = availableTags
                                    .filter { it.displayName in selectedTags }
                                    .map { it.name }

                                val request = CreateStoryRequest(
                                    title = title.trim(),
                                    synopsis = synopsis.takeIf { it.isNotBlank() },
                                    content = content.trim(),
                                    ageRating = selectedAgeRating,
                                    tags = selectedTagNames,
                                    fandoms = emptyList()
                                )

                                val response = RetrofitClient.storyService.createStory(
                                    authHeader = "Bearer $token",
                                    request = request
                                )

                                if (response.isSuccessful && response.body() != null) {
                                    onCreateSuccess(response.body()!!.treeId)
                                } else {
                                    println("Ошибка: ${response.code()} - ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                println("Исключение: ${e.message}")
                                e.printStackTrace()
                            } finally {
                                isCreating = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (title.isNotBlank() && content.isNotBlank() && !isCreating) {
                            Color(0xFF97A1EF)
                        } else {
                            Color.Gray.copy(alpha = 0.5f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = title.isNotBlank() && content.isNotBlank() && !isCreating
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Создать историю", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}