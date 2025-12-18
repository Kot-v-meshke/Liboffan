package com.example.liboffan.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.liboffan.screens.Screen

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF97A1EF).copy(alpha = 0.8f),
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
            label = { Text("Главная") },
            selected = currentScreen == Screen.Home,
            onClick = { onScreenSelected(Screen.Home) },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            label = { Text("Поиск") },
            selected = currentScreen == Screen.Search,
            onClick = { onScreenSelected(Screen.Search) },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
            label = { Text("Профиль") },
            selected = currentScreen == Screen.Profile,
            onClick = { onScreenSelected(Screen.Profile) },
            alwaysShowLabel = false
        )
    }
}