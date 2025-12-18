package com.example.liboffan.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.liboffan.components.WorkItem

@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit
) {
    val books = (1..15).map { "book_$it" }

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
        items(books) { bookId ->
            WorkItem(
                title = "Название фанфика $bookId",
                author = "Автор $bookId",
                onClick = { onBookClick(bookId) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}