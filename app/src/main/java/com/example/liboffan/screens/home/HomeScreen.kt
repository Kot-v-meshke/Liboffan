package com.example.liboffan.screens.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liboffan.components.StoryItemCard
import com.example.liboffan.model.StoryItem
import com.example.liboffan.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    context: Context,
    onBookClick: (Long) -> Unit,
    onBranchClick: ((Long, Long) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var stories by remember { mutableStateOf<List<StoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.storyService.getLatestStories()
            if (response.isSuccessful && response.body() != null) {
                stories = response.body()!!.content
            }
        } catch (e: Exception) {
            // Можно показать Snackbar с ошибкой
        } finally {
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7065AC), Color(0xFF97A1EF), Color(0xFFAB98EC))
                )
            ),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else {
            items(stories) { story ->
                StoryItemCard(
                    story = story,
                    onClick = { onBookClick(story.id) },
                    onBranchClick = onBranchClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (stories.isEmpty()) {
                item {
                    Text(
                        text = "Нет книг",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
