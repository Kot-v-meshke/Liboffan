package com.example.liboffan.screens.book

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liboffan.TokenManager
import com.example.liboffan.model.TagItem
import com.example.liboffan.model.request.CreateStoryRequest
import com.example.liboffan.model.request.ForkStoryRequest
import com.example.liboffan.network.RetrofitClient
import kotlinx.coroutines.launch

sealed class EditorMode {
    data class CreateStory(
        val initialTitle: String = "",
        val initialSynopsis: String = "",
        val initialContent: String = ""
    ) : EditorMode()

    data class ForkStory(
        val treeId: Long,
        val parentVersionId: Long,
        val originalTitle: String,
        val originalSynopsis: String? = null,
        val originalContent: String,
        val originalAgeRating: String,
        val originalTags: Set<String>
    ) : EditorMode()
}

data class StoryFormState(
    val title: String,
    val synopsis: String,
    val content: String,
    val ageRating: String,
    val selectedTags: Set<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEditorScreen(
    mode: EditorMode,
    onBack: () -> Unit,
    onSuccess: (Long) -> Unit
) {
    var formState by remember {
        mutableStateOf(
            when (mode) {
                is EditorMode.CreateStory -> StoryFormState(
                    title = mode.initialTitle,
                    synopsis = mode.initialSynopsis,
                    content = mode.initialContent,
                    ageRating = "PG",
                    selectedTags = emptySet()
                )
                is EditorMode.ForkStory -> StoryFormState(
                    title = mode.originalTitle,
                    synopsis = mode.originalSynopsis ?: "",
                    content = mode.originalContent,
                    ageRating = mode.originalAgeRating,
                    selectedTags = mode.originalTags
                )
            }
        )
    }

    var isLoadingTags by remember { mutableStateOf(true) }
    var availableTags by remember { mutableStateOf<List<TagItem>>(emptyList()) }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAIPanel by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }
    var isAIProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.storyService.getAllTags()
            if (response.isSuccessful && response.body() != null) {
                availableTags = response.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadingTags = false
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (mode) {
                            is EditorMode.CreateStory -> "Создать историю"
                            is EditorMode.ForkStory -> "Создать ответвление"
                        },
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAIPanel = !showAIPanel }) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            "AI Assistant",
                            tint = Color.White
                        )
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
                    Brush.verticalGradient(
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
                StoryFormFields(
                    mode = mode,
                    formState = formState,
                    onFormStateChange = { formState = it },
                    availableTags = availableTags,
                    isLoadingTags = isLoadingTags,
                    isEditable = !isSaving,
                    textFieldColors = textFieldColors
                )

                if (showAIPanel) {
                    AIPanel(
                        currentContent = formState.content,
                        currentSynopsis = formState.synopsis,
                        onApplySuggestion = { newContent, newSynopsis ->
                            formState = formState.copy(
                                content = newContent ?: formState.content,
                                synopsis = newSynopsis ?: formState.synopsis
                            )
                        },
                        isProcessing = isAIProcessing,
                        onProcessingChange = { isAIProcessing = it },
                        aiPrompt = aiPrompt,
                        onPromptChange = { aiPrompt = it }
                    )
                }

                SaveButton(
                    mode = mode,
                    formState = formState,
                    isSaving = isSaving,
                    onSave = {
                        scope.launch {
                            isSaving = true
                            try {
                                val result = when (mode) {
                                    is EditorMode.CreateStory -> createStory(
                                        context = context,
                                        formState = formState,
                                        availableTags = availableTags
                                    )
                                    is EditorMode.ForkStory -> forkStory(
                                        context = context,
                                        mode = mode,
                                        formState = formState,
                                        availableTags = availableTags
                                    )
                                }
                                result?.let { onSuccess(it) }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SaveButton(
    mode: EditorMode,
    formState: StoryFormState,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    val isValid = when (mode) {
        is EditorMode.CreateStory -> formState.title.isNotBlank() && formState.content.isNotBlank()
        is EditorMode.ForkStory -> formState.content.isNotBlank()
    }

    Button(
        onClick = onSave,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isValid && !isSaving) {
                Color(0xFF97A1EF)
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = isValid && !isSaving
    ) {
        if (isSaving) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                when (mode) {
                    is EditorMode.CreateStory -> "Создать историю"
                    is EditorMode.ForkStory -> "Создать ответвление"
                },
                color = Color.White
            )
        }
    }
}

@Composable
private fun StoryFormFields(
    mode: EditorMode,
    formState: StoryFormState,
    onFormStateChange: (StoryFormState) -> Unit,
    availableTags: List<TagItem>,
    isLoadingTags: Boolean,
    isEditable: Boolean,
    textFieldColors: TextFieldColors
) {
    when (mode) {
        is EditorMode.CreateStory -> {
            OutlinedTextField(
                value = formState.title,
                onValueChange = { onFormStateChange(formState.copy(title = it)) },
                label = { Text("Название истории *", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                enabled = isEditable
            )
        }
        is EditorMode.ForkStory -> {
            OutlinedTextField(
                value = formState.title,
                onValueChange = {},
                label = { Text("Название произведения", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.Lock, "Нельзя изменить", tint = Color.White.copy(alpha = 0.5f))
                },
                enabled = isEditable
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Краткое описание", color = Color.White.copy(alpha = 0.7f))
        TextButton(
            onClick = { /* AI улучшение синопсиса */ },
            enabled = isEditable && formState.synopsis.isNotBlank()
        ) {
            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Улучшить", fontSize = 12.sp)
        }
    }

    OutlinedTextField(
        value = formState.synopsis,
        onValueChange = { onFormStateChange(formState.copy(synopsis = it)) },
        placeholder = { Text("О чём эта история?", color = Color.White.copy(alpha = 0.5f)) },
        modifier = Modifier.fillMaxWidth(),
        colors = textFieldColors,
        maxLines = 5,
        minLines = 3,
        enabled = isEditable
    )

    RatingSelector(
        selectedRating = formState.ageRating,
        onRatingChange = { onFormStateChange(formState.copy(ageRating = it)) },
        isEditable = isEditable
    )

    TagsSelector(
        selectedTags = formState.selectedTags,
        onTagsChange = { onFormStateChange(formState.copy(selectedTags = it)) },
        availableTags = availableTags,
        isLoading = isLoadingTags,
        isEditable = isEditable
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Текст *", color = Color.White, fontWeight = FontWeight.SemiBold)
        Row {
            TextButton(
                onClick = { /* AI рерайт текста */ },
                enabled = isEditable && formState.content.isNotBlank()
            ) {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Рерайт", fontSize = 12.sp)
            }
            TextButton(
                onClick = { /* AI продолжение текста */ },
                enabled = isEditable && formState.content.isNotBlank()
            ) {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Продолжить", fontSize = 12.sp)
            }
        }
    }

    OutlinedTextField(
        value = formState.content,
        onValueChange = { onFormStateChange(formState.copy(content = it)) },
        placeholder = {
            Text(
                when (mode) {
                    is EditorMode.CreateStory -> "Это будет первая версия вашей истории..."
                    is EditorMode.ForkStory -> "Напишите своё продолжение или измените историю..."
                },
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 400.dp),
        colors = textFieldColors,
        minLines = 10,
        maxLines = 20,
        enabled = isEditable
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingSelector(
    selectedRating: String,
    onRatingChange: (String) -> Unit,
    isEditable: Boolean
) {
    val ageRatings = listOf("G", "PG", "PG-13", "R", "NC-17")
    var isRatingMenuExpanded by remember { mutableStateOf(false) }

    Column {
        Text("Возрастной рейтинг *", color = Color.White, fontWeight = FontWeight.SemiBold)

        Box {
            OutlinedButton(
                onClick = { isRatingMenuExpanded = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditable
            ) {
                Text(selectedRating)
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
                            onRatingChange(rating)
                            isRatingMenuExpanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = Color.White),
                        trailingIcon = {
                            if (selectedRating == rating) {
                                Icon(Icons.Default.Check, "Выбрано", tint = Color.White)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TagsSelector(
    selectedTags: Set<String>,
    onTagsChange: (Set<String>) -> Unit,
    availableTags: List<TagItem>,
    isLoading: Boolean,
    isEditable: Boolean
) {
    Column {
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
                            if (isEditable) {
                                val newTags = if (isSelected) {
                                    selectedTags - tag.displayName
                                } else {
                                    selectedTags + tag.displayName
                                }
                                onTagsChange(newTags)
                            }
                        },
                        label = {
                            Text(
                                tag.displayName,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFB8C1FF).copy(alpha = 0.35f),
                            labelColor = Color.White,
                            selectedContainerColor = Color(0xFF7065AC),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(32.dp),
                        enabled = isEditable
                    )
                }
            }
        }
    }
}

@Composable
fun AIPanel(
    currentContent: String,
    currentSynopsis: String,
    onApplySuggestion: (String?, String?) -> Unit,
    isProcessing: Boolean,
    onProcessingChange: (Boolean) -> Unit,
    aiPrompt: String,
    onPromptChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF7065AC).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "🤖 AI Ассистент",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = aiPrompt,
                onValueChange = onPromptChange,
                label = { Text("Что вы хотите изменить?", color = Color.White.copy(alpha = 0.7f)) },
                placeholder = {
                    Text(
                        "Пример: Сделай текст более драматичным / Добавь юмора / Исправь грамматику",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                ),
                minLines = 2,
                maxLines = 3,
                enabled = !isProcessing
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Улучшить стиль",
                    onClick = { /* AI call */ },
                    isProcessing = isProcessing,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Продолжить текст",
                    onClick = { /* AI call */ },
                    isProcessing = isProcessing,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Сократить",
                    onClick = { /* AI call */ },
                    isProcessing = isProcessing,
                    modifier = Modifier.weight(1f)
                )
            }

            if (isProcessing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier  // 🔥 Добавляем параметр modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,  // 🔥 Используем переданный modifier
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF97A1EF).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = !isProcessing
    ) {
        Text(text, fontSize = 12.sp, color = Color.White)
    }
}

private suspend fun createStory(
    context: Context,
    formState: StoryFormState,
    availableTags: List<TagItem>
): Long? {
    val token = TokenManager(context).authToken ?: return null

    val selectedTagNames = availableTags
        .filter { it.displayName in formState.selectedTags }
        .map { it.name }

    val request = CreateStoryRequest(
        title = formState.title.trim(),
        synopsis = formState.synopsis.takeIf { it.isNotBlank() },
        content = formState.content.trim(),
        ageRating = formState.ageRating,
        tags = selectedTagNames,
        fandoms = emptyList()
    )

    val response = RetrofitClient.storyService.createStory(
        authHeader = "Bearer $token",
        request = request
    )

    return if (response.isSuccessful && response.body() != null) {
        response.body()!!.treeId
    } else null
}

private suspend fun forkStory(
    context: Context,
    mode: EditorMode.ForkStory,
    formState: StoryFormState,
    availableTags: List<TagItem>
): Long? {
    val token = TokenManager(context).authToken ?: return null

    val selectedTagNames = availableTags
        .filter { it.displayName in formState.selectedTags }
        .map { it.name }

    val request = ForkStoryRequest(
        content = formState.content.trim(),
        versionSynopsis = formState.synopsis.takeIf { it.isNotBlank() },
        ageRating = formState.ageRating,
        tags = selectedTagNames,
        parentVersionId = mode.parentVersionId
    )

    val response = RetrofitClient.storyService.forkStory(
        treeId = mode.treeId,
        authHeader = "Bearer $token",
        request = request
    )

    return if (response.isSuccessful && response.body() != null) {
        response.body()!!.versionId
    } else null
}