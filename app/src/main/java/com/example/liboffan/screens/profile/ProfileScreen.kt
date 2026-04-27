package com.example.liboffan.screens.profile

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liboffan.TokenManager
import com.example.liboffan.components.BookLibraryItem
import com.example.liboffan.model.UserFullResponse
import com.example.liboffan.model.UserLibraryItem
import com.example.liboffan.network.RetrofitClient
import com.example.liboffan.screens.book.StoryScreen
import kotlinx.coroutines.launch
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    context: Context,
    onBookClick: (Long, Long?) -> Unit,
    onLogout: () -> Unit,
    drawerExpanded: Boolean = false,
    onDrawerStateChange: (Boolean) -> Unit,
    onCreateStory: () -> Unit
) {
    val tokenManager = TokenManager(context)
    val token = tokenManager.authToken ?: ""
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<ProfileSubScreen>(ProfileSubScreen.Main) }

    var userFullName by remember { mutableStateOf("Загрузка...") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }
    var library by remember { mutableStateOf<List<UserLibraryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var drawerExpanded by remember { mutableStateOf(false) }
    var showCreateStorySheet by remember { mutableStateOf(false) }

    val menuOffset by animateDpAsState(
        targetValue = if (drawerExpanded) 0.dp else 320.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (drawerExpanded) 0.4f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {
        if (token.isNotBlank()) {
            try {
                val response: Response<UserFullResponse> = RetrofitClient.instance.getUserFull("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    userFullName = data.profile.displayName ?: data.profile.email
                    avatarUrl = data.profile.avatarUrl
                    library = data.library
                }
            } catch (e: Exception) {
                userFullName = "Ошибка загрузки"
            } finally {
                isLoading = false
            }
        } else {
            userFullName = "Не авторизован"
            isLoading = false
        }
    }

    fun removeBookFromLibrary(treeId: Long, versionId: Long, currentStatus: String) {
        scope.launch {
            try {
                val response = RetrofitClient.storyService.removeBookFromLibrary(
                    authHeader = "Bearer $token",
                    versionId = versionId
                )

                if (response.isSuccessful) {
                    library = library.filter { !(it.treeId == treeId && it.versionId == versionId) }
                } else {
                    println("Ошибка удаления: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Исключение при удалении: ${e.message}")
            }
        }
    }

    

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { currentScreen = ProfileSubScreen.CreateStory },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF97A1EF).copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать историю",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { drawerExpanded = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF97A1EF).copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF97A1EF).copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = avatarUrl ?: "https://abrakadabra.fun/uploads/posts/2021-12/1640528661_1-abrakadabra-fun-p-serii-chelovek-na-avu-1.png",
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userFullName,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // вкладки
            val tabMapping = listOf(
                "В планах" to "planned",
                "В процессе" to "reading",
                "Прочитано" to "completed",
                "Заброшено" to "dropped"
            )
            val tabNames = tabMapping.map { it.first }
            var selectedTab by remember { mutableIntStateOf(0) }
            val currentStatus = tabMapping[selectedTab].second

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF97A1EF).copy(alpha = 0.5f),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = Color.White
                    )
                }
            ) {
                tabNames.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Color.White else Color(0xFFC9D8FF)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val filteredLibrary = library.filter { it.status == currentStatus }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF97A1EF).copy(alpha = 0.2f))
                    .padding(16.dp)
            ) {
                items(filteredLibrary.size) { i ->
                    val item = filteredLibrary[i]

                    BookLibraryItem(
                        item = item,
                        onClick = { onBookClick(item.treeId, item.versionId) },
                        onRemove = {
                            removeBookFromLibrary(item.treeId, item.versionId!!, currentStatus)   }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (filteredLibrary.isEmpty() && !isLoading) {
                    item {
                        Text("Нет работ", color = Color.White, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }


        if (backgroundAlpha > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundAlpha))
                    .clickable(
                        indication = null, // убираем эффект нажатия
                        interactionSource = remember { MutableInteractionSource() }
                    ) { drawerExpanded = false }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .offset(x = menuOffset)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7065AC),
                            Color(0xFF7B6CB0),
                            Color(0xFF97A1EF)
                        )
                    )
                )
                .align(Alignment.CenterEnd)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    clip = true
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Настройки",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { drawerExpanded = false },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                val menuItems = listOf(
                    Pair(Icons.Default.Person, "Профиль"),
                    Pair(Icons.Default.Settings, "Настройки"),
                    Pair(Icons.Default.Help, "Помощь"),
                    Pair(Icons.Default.Info, "О приложении"),
                    Pair(Icons.Default.Star, "Избранное"),
                    Pair(Icons.Default.History, "История"),
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    menuItems.forEach { (icon, text) ->
                        MenuItem(
                            icon = icon,
                            text = text,
                            onClick = {
                                drawerExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                MenuItem(
                    icon = Icons.Default.ExitToApp,
                    text = "Выйти",
                    textColor = Color(0xFFFFFFFF),
                    iconColor = Color(0xFFFFFFFF),
                    onClick = {
                        onLogout()
                        drawerExpanded = false
                    },
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFFFF).copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp)
                )
            }
        }
        if (currentScreen is ProfileSubScreen.CreateStory) {
            StoryScreen(
                onBack = { currentScreen = ProfileSubScreen.Main },
                onCreateSuccess = { storyId ->
                    currentScreen = ProfileSubScreen.Main
                }
            )
        }
    }
}
@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    iconColor: Color = Color.White.copy(alpha = 0.9f),
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

