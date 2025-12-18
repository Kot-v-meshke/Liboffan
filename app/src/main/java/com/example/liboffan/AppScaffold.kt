package com.example.liboffan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.liboffan.components.BottomNavigationBar
import com.example.liboffan.screens.Screen
import com.example.liboffan.screens.book.BookDetailScreen
import com.example.liboffan.screens.home.HomeScreen
import com.example.liboffan.screens.profile.ProfileScreen
import com.example.liboffan.screens.search.SearchScreen

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(Screen.Profile) }
    var selectedBook by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (selectedBook == null) {
                BottomNavigationBar(currentScreen) { screen ->
                    currentScreen = screen
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                selectedBook != null -> {
                    BookDetailScreen(
                        onBack = { selectedBook = null }
                    )
                }
                currentScreen == Screen.Home -> HomeScreen { bookId -> selectedBook = bookId }
                currentScreen == Screen.Search -> SearchScreen()
                currentScreen == Screen.Profile -> ProfileScreen { bookId -> selectedBook = bookId }
            }
        }
    }
}