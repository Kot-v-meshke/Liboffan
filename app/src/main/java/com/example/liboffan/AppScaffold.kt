package com.example.liboffan

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.liboffan.components.BottomNavigationBar
import com.example.liboffan.screens.Auth.LoginScreen
import com.example.liboffan.screens.Auth.RegisterScreen
import com.example.liboffan.screens.Screen
import com.example.liboffan.screens.book.BookDetailScreen
import com.example.liboffan.screens.home.HomeScreen
import com.example.liboffan.screens.profile.ProfileScreen
import com.example.liboffan.screens.search.SearchScreen
import kotlinx.coroutines.launch
import com.example.liboffan.model.LoginRequest
import com.example.liboffan.model.RegisterRequest
import com.example.liboffan.network.RetrofitClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*



@Composable
fun MainApp(context: Context) {
    val tokenManager = remember { TokenManager(context) }

    var isLoggedIn by remember { mutableStateOf(false) }
    var authScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

    // Проверка сессии при старте
    LaunchedEffect(Unit) {
        if (!tokenManager.authToken.isNullOrBlank()) {
            isLoggedIn = true
        }
    }

    if (!isLoggedIn) {
        when (authScreen) {
            AuthScreen.Login -> {
                LoginScreen(
                    onLogin = { email, password ->
                        login(tokenManager, email, password) { success ->
                            if (success) isLoggedIn = true
                        }
                    },
                    onNavigateToRegister = { authScreen = AuthScreen.Register },
                    onNavigateToForgotPassword = { /* TODO */ }
                )
            }

            AuthScreen.Register -> {
                RegisterScreen(
                    onRegister = { email, password, displayName ->
                        register(tokenManager, email, password, displayName) { success ->
                            if (success) isLoggedIn = true
                        }
                    },
                    onNavigateToLogin = { authScreen = AuthScreen.Login }
                )
            }
        }
    } else {
        MainAppContent(
            onLogout = {
                tokenManager.clear()
                isLoggedIn = false
            }
        )
    }
}

// Основной контент приложения
@Composable
fun MainAppContent(onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedBook by remember { mutableStateOf<String?>(null) }

    if (selectedBook != null) {
        BookDetailScreen(onBack = { selectedBook = null })
    } else {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavigationBar(currentScreen) { screen ->
                    currentScreen = screen
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen) {
                    Screen.Home -> HomeScreen { bookId -> selectedBook = bookId }
                    Screen.Search -> SearchScreen()
                    Screen.Profile -> ProfileScreen(
                        onBookClick = { bookId -> selectedBook = bookId },
                        onLogout = onLogout // передаём onLogout, если ProfileScreen его использует
                    )
                }
            }
        }
    }
}

// Состояния авторизации
sealed interface AuthScreen {
    object Login : AuthScreen
    object Register : AuthScreen
}

// Вспомогательные функции (должны быть в отдельном файле, но пока здесь)

private fun login(
    tokenManager: TokenManager,
    email: String,
    password: String,
    onComplete: (Boolean) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                withContext(Dispatchers.Main) {
                    tokenManager.authToken = auth.token
                    tokenManager.userEmail = auth.email
                    tokenManager.userDisplayName = auth.displayName
                    onComplete(true)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onComplete(false)
            }
        }
    }
}

private fun register(
    tokenManager: TokenManager,
    email: String,
    password: String,
    displayName: String,
    onComplete: (Boolean) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitClient.instance.register(RegisterRequest(email, password, displayName))
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                withContext(Dispatchers.Main) {
                    tokenManager.authToken = auth.token
                    tokenManager.userEmail = auth.email
                    tokenManager.userDisplayName = auth.displayName
                    onComplete(true)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onComplete(false)
            }
        }
    }
}