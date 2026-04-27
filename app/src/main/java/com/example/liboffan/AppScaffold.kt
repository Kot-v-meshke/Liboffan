package com.example.liboffan

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
import androidx.compose.ui.platform.LocalContext
import com.example.liboffan.screens.book.StoryScreen
import com.example.liboffan.screens.book.ReaderScreen


@Composable
fun MainApp() {

    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var isLoggedIn by remember { mutableStateOf(false) }
    var authScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

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

@Composable
fun MainAppContent(onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    var selectedTreeId by remember { mutableStateOf<Long?>(null) }

    var readVersionId by remember { mutableStateOf<Long?>(null) }

    var detailVersionId by remember { mutableStateOf<Long?>(null) }

    var showCreateStoryScreen by remember { mutableStateOf(false) }
    var drawerExpanded by remember { mutableStateOf(false) }

    when {
        readVersionId != null && selectedTreeId != null -> {
            ReaderScreen(
                context = LocalContext.current,
                versionId = readVersionId!!,
                treeId = selectedTreeId!!,
                onBack = { readVersionId = null },
                onFork = { /* создать ветку */ }
            )
        }

        selectedTreeId != null -> {
            BookDetailScreen(
                context = LocalContext.current,
                treeId = selectedTreeId!!,
                versionId = detailVersionId,
                onReadClick = { versionId ->
                    readVersionId = versionId
                },
                onBack = {
                    selectedTreeId = null
                    detailVersionId = null
                }
            )
        }

        else -> {
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
                        Screen.Home -> HomeScreen(
                            context = LocalContext.current,
                            onBookClick = { treeId ->
                                selectedTreeId = treeId
                                detailVersionId = null
                                readVersionId = null
                            },
                            onBranchClick = { versionId, treeId ->
                                selectedTreeId = treeId
                                detailVersionId = versionId
                                readVersionId = null
                            }
                        )
                        Screen.Search -> SearchScreen(
                            context = LocalContext.current,
                            onBookClick = { treeId ->
                                selectedTreeId = treeId
                                detailVersionId = null
                                readVersionId = null
                            },
                            onBranchClick = { versionId, treeId ->
                                selectedTreeId = treeId
                                detailVersionId = versionId
                                readVersionId = null
                            }
                        )
                        Screen.Profile -> ProfileScreen(
                            context = LocalContext.current,
                            onBookClick = { treeId, versionId ->
                                selectedTreeId = treeId
                                detailVersionId = versionId
                                readVersionId = null
                            },
                            onLogout = onLogout,
                            drawerExpanded = drawerExpanded,
                            onDrawerStateChange = { expanded -> drawerExpanded = expanded },
                            onCreateStory = { showCreateStoryScreen = true }
                        )

                        Screen.Library -> TODO()
                    }
                }
            }
        }
    }

    if (showCreateStoryScreen) {
        StoryScreen(
            onBack = { showCreateStoryScreen = false },
            onCreateSuccess = { storyId ->
                showCreateStoryScreen = false
                selectedTreeId = storyId
            }
        )
    }
}

sealed interface AuthScreen {
    object Login : AuthScreen
    object Register : AuthScreen
}

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