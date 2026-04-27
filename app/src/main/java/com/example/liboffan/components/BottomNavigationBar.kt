package com.example.liboffan.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.liboffan.screens.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF6650C0).copy(alpha = 0.8f),
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            selectedTextColor = Color.White,
            unselectedTextColor = Color.White.copy(alpha = 0.7f),
            indicatorColor = Color.Transparent,        // фон- пилюля
            disabledIconColor = Color.White.copy(alpha = 0.3f)
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Главная",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Главная", fontSize = 12.sp) },
            selected = currentScreen == Screen.Home,
            onClick = { onScreenSelected(Screen.Home) },
            alwaysShowLabel = false,
            colors = navItemColors
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Поиск",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Поиск", fontSize = 12.sp) },
            selected = currentScreen == Screen.Search,
            onClick = { onScreenSelected(Screen.Search) },
            alwaysShowLabel = false,
            colors = navItemColors
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Профиль",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Профиль", fontSize = 12.sp) },
            selected = currentScreen == Screen.Profile,
            onClick = { onScreenSelected(Screen.Profile) },
            alwaysShowLabel = false,
            colors = navItemColors
        )
    }
}