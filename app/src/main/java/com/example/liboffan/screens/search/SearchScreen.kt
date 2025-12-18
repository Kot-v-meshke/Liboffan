package com.example.liboffan.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun SearchScreen() {
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Поиск",
            fontSize = 24.sp,
            color = Color.White
        )
    }
}