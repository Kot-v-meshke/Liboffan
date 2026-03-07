package com.example.liboffan.screens.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liboffan.components.WorkItem
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun LibraryScreen(
    onBookClick: (String) -> Unit
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val categories = listOf("Коллекции", "Избранное", "История", "Читаю сейчас")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7065AC),
                        Color(0xFF97A1EF),
                        Color(0xFFAB98EC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Моя библиотека",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                IconButton(
                    onClick = { /* Создать новую коллекцию */ },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF97A1EF).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Быстрые действия
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Collections,
                    label = "Все",
                    isSelected = selectedCategory == 0,
                    onClick = { selectedCategory = 0 }
                )
                QuickActionButton(
                    icon = Icons.Default.Favorite,
                    label = "Избранное",
                    isSelected = selectedCategory == 1,
                    onClick = { selectedCategory = 1 }
                )
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = "История",
                    isSelected = selectedCategory == 2,
                    onClick = { selectedCategory = 2 }
                )
                QuickActionButton(
                    icon = Icons.Default.List,
                    label = "Читаю",
                    isSelected = selectedCategory == 3,
                    onClick = { selectedCategory = 3 }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Прочитано",
                    value = "24",
                    color = Color(0xFF97A1EF)
                )
                StatCard(
                    title = "В процессе",
                    value = "5",
                    color = Color(0xFFAB98EC)
                )
                StatCard(
                    title = "В планах",
                    value = "12",
                    color = Color(0xFF7065AC)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Коллекции пользователя
            Text(
                text = "Мои коллекции",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listOf(
                    CollectionItem("Лучшие фанфики 2024", 8, Icons.Default.Star),
                    CollectionItem("Романтика", 15, Icons.Default.Favorite),
                    CollectionItem("Фэнтези", 12, Icons.Default.Bookmark),
                    CollectionItem("Для вечера", 6, Icons.Default.Collections)
                )) { collection ->
                    CollectionCard(
                        collection = collection,
                        onClick = { /* Открыть коллекцию */ }
                    )
                }

                // Кнопка создания новой коллекции
                item {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Создать коллекцию */ }
                            .border( // ДОБАВЬТЕ border как модификатор!
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                        // УБРАТЬ параметр border отсюда!
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Создать коллекцию",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Создать новую коллекцию",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Недавно добавленные",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Недавно добавленные работы
                items(5) { i ->
                    WorkItem(
                        title = "Недавний фанфик $i",
                        author = "Автор $i",
                        onClick = { onBookClick("recent_$i") },
                        modifier = Modifier
                            .background(
                                Color(0xFF97A1EF).copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    Color(0xFF97A1EF)
                else
                    Color(0xFF97A1EF).copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 6.dp else 2.dp
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}
@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

data class CollectionItem(
    val name: String,
    val count: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CollectionCard(
    collection: CollectionItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF97A1EF).copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFAB98EC).copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = collection.icon,
                    contentDescription = collection.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = collection.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${collection.count} работ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Открыть",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .rotate(180f)
                    .size(20.dp)
            )
        }
    }
}