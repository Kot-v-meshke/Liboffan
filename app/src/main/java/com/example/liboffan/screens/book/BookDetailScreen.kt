package com.example.liboffan.screens.book

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liboffan.TokenManager
import com.example.liboffan.model.dto.StoryDetailDto
import com.example.liboffan.model.request.LibraryUpdateRequest
import com.example.liboffan.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    context: Context,
    treeId: Long,
    versionId: Long? = null,  // если задано — загружаем конкретную версию
    onReadClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val tokenManager = TokenManager(context)
    val token = tokenManager.authToken ?: ""
    val scope = rememberCoroutineScope()

    var story by remember { mutableStateOf<StoryDetailDto?>(null) }
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var isFollowing by remember { mutableStateOf(false) }
    var libraryStatus by remember { mutableStateOf<String?>(null) }
    var isLibraryMenuExpanded by remember { mutableStateOf(false) }

    fun updateLibraryStatus(newStatus: String) {  // ← String, не nullable!
        val currentStory = story ?: return
        scope.launch {
            try {
                val request = LibraryUpdateRequest(
                    treeId = currentStory.treeId,
                    status = newStatus,  // ← Всегда строка: "planned", "reading", etc.
                    versionId = currentStory.versionId
                )
                val response = RetrofitClient.storyService.updateLibraryStatus(
                    authHeader = "Bearer $token",
                    request = request
                )
                if (response.isSuccessful) {
                    libraryStatus = newStatus
                }
            } catch (e: Exception) { /* todo */ }
        }
    }

    fun removeBookFromLibrary() {
        val currentStory = story ?: return
        scope.launch {
            try {
                val response = RetrofitClient.storyService.removeBookFromLibrary(
                    authHeader = "Bearer $token",
                    versionId = currentStory.versionId
                )
                if (response.isSuccessful) {
                    libraryStatus = null  // Сбрасываем локальный статус
                }
            } catch (e: Exception) { /* todo */ }
        }
    }

    LaunchedEffect(treeId, versionId) {
        try {
            val response = if (versionId != null) {
                RetrofitClient.storyService.getStoryVersion(
                    versionId = versionId,
                    authHeader = "Bearer $token"
                )
            } else {
                // корневая версия
                RetrofitClient.storyService.getStoryDetail(
                    treeId = treeId,
                    authHeader = "Bearer $token"
                )
            }

            if (response.isSuccessful && response.body() != null) {
                story = response.body()!!
                likeCount = story!!.likeCount
                isLiked = story?.isLikedByCurrentUser ?: false
                libraryStatus = story?.currentLibraryStatus
            }
        } catch (e: Exception) {
            // Обработка ошибки
        } finally {
            isLoading = false
        }
    }
    fun toggleLike() {
        val currentStory = story ?: return
        val versionId = currentStory.versionId

        val oldIsLiked = isLiked
        val oldLikeCount = likeCount

        isLiked = !isLiked
        likeCount = if (isLiked) likeCount + 1 else likeCount - 1

        scope.launch {
            try {
                val response = RetrofitClient.storyService.toggleLike(
                    versionId = versionId,
                    authHeader = "Bearer $token"
                )
                if (!response.isSuccessful) {
                    isLiked = oldIsLiked
                    likeCount = oldLikeCount
                }
            } catch (e: Exception) {
                isLiked = oldIsLiked
                likeCount = oldLikeCount
            }
        }
    }

    // Настройки скролла и анимации
    val scrollState = rememberScrollState()
    val toolbarHeight = 56.dp
    val coverHeightDp = 300.dp

    val density = LocalDensity.current
    val coverHeightPx = with(density) { coverHeightDp.toPx() }
    val scrolledY = scrollState.value.toFloat()
    val progress = (scrolledY / coverHeightPx).coerceIn(0f, 1f)
    val coverAlpha = (1f - progress * 0.7f).coerceIn(0f, 1f)
    val toolbarAlpha = progress.coerceIn(0f, 1f)
    val coverButtonsAlpha = (1f - progress).coerceIn(0f, 1f)

    val currentStory = story

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentStory?.title ?: "Загрузка...",
                        color = Color.White.copy(alpha = toolbarAlpha),
                        maxLines = 1,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Row(modifier = Modifier.alpha(toolbarAlpha)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = { toggleLike() }) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isLiked) "Убрать лайк" else "Понравилось",
                                    tint = if (isLiked) Color.Red else Color.White
                                )
                            }
                            Text(
                                text = "$likeCount",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(onClick = { /* todo: поделиться */ }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Поделиться",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF7065AC).copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF7065AC),
                                Color(0xFF97A1EF),
                                Color(0xFFAB98EC)
                            )
                        )
                    )
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (currentStory != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                ) {
                    // Обложка
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(coverHeightDp)
                            .graphicsLayer {
                                alpha = coverAlpha
                                translationY = scrolledY * 0.3f
                            }
                    ) {
                        AsyncImage(
                            model = currentStory.coverUrl
                                ?: "https://via.placeholder.com/400x600/7065AC/FFFFFF?text=No+Cover",
                            contentDescription = "Обложка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .alpha(coverAlpha)
                        ) {
                            Text(
                                text = currentStory.title,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = "by ${currentStory.author.displayName ?: "Аноним"}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 15.sp
                            )
                        }

                        // Кнопки на обложке
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .alpha(coverButtonsAlpha),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Лайк + счётчик
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    onClick = { toggleLike() }, // ← Теперь вызывается toggleLike()
                                    shape = CircleShape,
                                    color = Color(0xFF97A1EF).copy(alpha = 0.8f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = if (isLiked) "Убрать лайк" else "Понравилось",
                                            tint = if (isLiked) Color.Red else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$likeCount",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Поделиться
                            Surface(
                                onClick = { /* поделиться */ },
                                shape = CircleShape,
                                color = Color(0xFFAB98EC).copy(alpha = 0.8f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Поделиться",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Основной контент
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        // Возрастной рейтинг
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Рейтинг",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Возраст: ${currentStory.ageRating}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        // Теги
                        if (currentStory.tags.isNotEmpty()) {
                            Text(
                                "Теги:",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentStory.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF7065AC).copy(alpha = 0.2f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(tag, color = Color(0xFF7065AC))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Фандомы
                        if (currentStory.fandoms.isNotEmpty()) {
                            Text(
                                "Фандомы:",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentStory.fandoms.forEach { fandom ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF97A1EF).copy(alpha = 0.2f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(fandom, color = Color(0xFF97A1EF))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Divider(
                            color = Color.Gray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // описание
                        val displaySynopsis = currentStory?.effectiveSynopsis

                        if (displaySynopsis?.isNotEmpty() == true) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "О произведении",
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                    if (currentStory?.versionSynopsis?.isNotBlank() == true) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF97A1EF), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Оригинальное для ветки",
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = displaySynopsis,
                                    color = Color.DarkGray,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        //Кнопки действия
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onReadClick(story?.versionId ?: return@Button) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF97A1EF)
                                ),
                                modifier = Modifier.weight(0.48f)
                            ) {
                                Text("Начать читать", color = Color.White)
                            }

                            // Кнопка коллекции с выпадающим меню
                            Box(modifier = Modifier.weight(0.48f)) {
                                OutlinedButton(
                                    onClick = { isLibraryMenuExpanded = true },
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFAB98EC)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFAB98EC)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))

                                        AnimatedContent(
                                            targetState = libraryStatus,
                                            label = "LibraryStatusAnimation",
                                            modifier = Modifier.weight(1f),
                                        ) { status ->
                                            val buttonText = when (status) {
                                                "planned" -> "Планирую"
                                                "reading" -> "Читаю"
                                                "completed" -> "Завершено"
                                                "dropped" -> "Брошено"
                                                null -> "Коллекция"
                                                else -> "Коллекция"
                                            }
                                            Text(buttonText, maxLines = 1, fontSize = 14.sp, color = Color(0xFFAB98EC) )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isLibraryMenuExpanded,
                                    onDismissRequest = { isLibraryMenuExpanded = false },
                                    shape = RoundedCornerShape(10),
                                    modifier = Modifier
                                        .background(Color(0xFF97A1EF))
                                        .width(180.dp)
                                ) {
                                    listOf(
                                        "planned" to "Планирую",
                                        "reading" to "Читаю",
                                        "completed" to "Завершено",
                                        "dropped" to "Брошено"
                                    ).forEach { (status, label) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    label,
                                                    color = Color.White,
                                                    fontWeight = if (libraryStatus == status) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                updateLibraryStatus(status)
                                                isLibraryMenuExpanded = false
                                            },
                                            colors = MenuDefaults.itemColors(
                                                textColor = Color.White,
                                                leadingIconColor = Color.White
                                            ),
                                            trailingIcon = {
                                                if (libraryStatus == status) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = "Выбрано",
                                                        tint = Color.White
                                                    )
                                                }
                                            }
                                        )
                                    }

                                    // Убрать из коллекции
                                    if (libraryStatus != null) {
                                        Divider(color = Color.White.copy(alpha = 0.3f))
                                        DropdownMenuItem(
                                            text = { Text("Убрать из коллекции", color = Color(
                                                0xFF000000
                                            )
                                            ) },
                                            onClick = {
                                                removeBookFromLibrary()
                                                isLibraryMenuExpanded = false
                                            },
                                            colors = MenuDefaults.itemColors(textColor = Color.Red)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(
                            color = Color.Gray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Автор
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Автор", color = Color.Gray, fontSize = 14.sp)
                                Text(
                                    currentStory.author.displayName ?: "Аноним",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Button(
                                onClick = { isFollowing = !isFollowing },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFollowing) Color(0xFFAB98EC) else Color(
                                        0xFF97A1EF
                                    )
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Default.Done else Icons.Default.Add,
                                        contentDescription = if (isFollowing) "Отписаться" else "Подписаться",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (isFollowing) "Подписан" else "Подписаться",
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ошибка загрузки", color = Color.White)
                }
            }
        }
    }
}