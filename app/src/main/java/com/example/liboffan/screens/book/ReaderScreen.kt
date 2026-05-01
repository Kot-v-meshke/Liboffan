package com.example.liboffan.screens.book

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liboffan.TokenManager
import com.example.liboffan.model.dto.StoryDetailDto
import com.example.liboffan.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    context: Context,
    versionId: Long,
    treeId: Long,
    onBack: () -> Unit,
    onFork: (Long, Long, StoryDetailDto?) -> Unit
) {
    val tokenManager = TokenManager(context)
    val token = tokenManager.authToken ?: ""
    val scope = rememberCoroutineScope()

    var story by remember { mutableStateOf<StoryDetailDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var fontSize by remember { mutableStateOf(16.sp) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(versionId) {
        try {
            val response = RetrofitClient.storyService.getStoryVersion(
                versionId = versionId,
                authHeader = "Bearer $token"
            )
            if (response.isSuccessful && response.body() != null) {
                story = response.body()
            }
        } catch (e: Exception) {
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = story?.title ?: "Загрузка...",
                        color = Color.White,
                        maxLines = 1,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onFork(treeId, versionId, story) },
                        enabled = story != null  // Активна, когда данные загружены
                    ) {
                        Icon(
                            Icons.Default.CallSplit,  // или Icons.Default.Edit
                            contentDescription = "Создать ответвление",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Настройки чтения",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4D438A)
                )
            )
        }
    ) { paddingValues ->

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Настройки чтения", color = Color.White) },
                text = {
                    Column {
                        Text("Размер шрифта", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    fontSize = ((fontSize.value - 2f).coerceIn(12f, 24f)).sp
                                }
                            ) {
                                Icon(Icons.Default.Remove, "Уменьшить", tint = Color.White)
                            }

                            Text(
                                text = "${fontSize.value.toInt()} sp",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                onClick = {
                                    fontSize = ((fontSize.value + 2f).coerceIn(12f, 24f)).sp
                                }
                            ) {
                                Icon(Icons.Default.Add, "Увеличить", tint = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) {
                        Text("Готово", color = Color.White)
                    }
                },
                containerColor = Color(0xFF7065AC),
                shape = RoundedCornerShape(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0E0B34),
                            Color(0xFF2F2A59),
                            Color(0xFF5B4E88)
                        )
                    )
                )
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (story != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "by ${story?.author?.displayName ?: "Аноним"}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Рейтинг
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF97A1EF), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = story?.ageRating ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (story?.tags?.isNotEmpty() == true) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            story?.tags?.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFF97A1EF).copy(alpha = 0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(tag, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Divider(
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = story?.content ?: "",
                        color = Color.White,
                        fontSize = fontSize,
                        lineHeight = (fontSize.value * 1.5).sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Не удалось загрузить текст", color = Color.White)
                }
            }
        }
    }
}