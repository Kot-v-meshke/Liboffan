package com.example.liboffan.screens.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(onBack: () -> Unit) {
    var isLiked by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }

    val coverUrl = "https://picsum.photos/400/600"
    val title = "Тайна лунного блюда"
    val authorName = "Автор Фанфикшн"
    val description = """
        Длинное описание фанфика... Здесь может быть очень много текста. Фанфик о том, как главный герой находит магическое блюдо, способное менять вкусовые предпочтения всей вселенной. Но сила эта имеет цену...
        
        История начинается в маленькой деревушке, где главный герой, обычный повар по имени Артур, случайно обнаруживает древний рецепт лунного блюда. Это блюдо, приготовленное в полнолуние, обладает невероятными свойствами: оно может изменить вкусовые предпочтения любого, кто его попробует.
        
        Сначала Артур использует это знание для помощи своим друзьям и соседям, но вскоре о его способностях узнают могущественные силы. Королевские повара, маги кулинарных гильдий и даже темные лорды начинают охоту на Артура и его секретный рецепт.
        
        В процессе своих приключений Артур встречает загадочную девушку Луну, которая, как оказывается, является хранительницей древних кулинарных знаний. Вместе они должны защитить рецепт от падения в недобрые руки и предотвратить кулинарную катастрофу мирового масштаба.
        
        Фанфик наполнен напряженными моментами, романтическими сценами и, конечно же, подробными описаниями приготовления волшебных блюд. Каждая глава - новое кулинарное открытие и новый шаг в развитии отношений между главными героями.
        
        Особенностью этой работы являются детальные описания процессов готовки, которые настолько реалистичны, что читатели начинают чувствовать ароматы блюд. Автор мастерски смешивает фэнтези элементы с кулинарным искусством, создавая уникальный мир, где магия проявляется через вкус и запах.
    """.trimIndent()

    val scrollState = rememberScrollState()
    val toolbarHeight = 60.dp
    val coverHeightDp = 300.dp

    val density = LocalDensity.current
    val coverHeightPx = with(density) { coverHeightDp.toPx() }
    val toolbarHeightPx = with(density) { toolbarHeight.toPx() }

    val scrolledY = scrollState.value.toFloat()
    val coverAlpha = (1 - (scrolledY / (coverHeightPx * 0.7f))).coerceIn(0f, 1f)
    val titleAlpha = (scrolledY / (coverHeightPx * 0.3f)).coerceIn(0f, 1f)

    val isToolbarVisible = scrolledY > coverHeightPx - toolbarHeightPx

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isToolbarVisible) {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            color = Color.White,
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
                        IconButton(onClick = { isLiked = !isLiked }) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isLiked) "Убрать лайк" else "Понравилось",
                                tint = if (isLiked) Color.Red else Color.White
                            )
                        }
                        IconButton(onClick = { /* поделиться */ }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Поделиться",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF7065AC)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Фоновый градиент
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
            ) {
                // Обложка с плавным скрытием
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverHeightDp)
                ) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = "Обложка фанфика",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(coverAlpha),
                        contentScale = ContentScale.Crop
                    )

                    // Темный оверлей для лучшей читаемости текста
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    ),
                                    startY = 0f,
                                    endY = coverHeightPx
                                )
                            )
                    )

                    // Кнопка "Назад" (только при прокрутке)
                    if (!isToolbarVisible) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Кнопка "Назад" как строка
                            Surface(
                                onClick = onBack,
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .alpha(coverAlpha)
                                    .clickable { onBack() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Назад",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Назад",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Кнопки "Лайк" и "Поделиться"
                            Row {
                                Surface(
                                    onClick = { isLiked = !isLiked },
                                    shape = CircleShape,
                                    color = Color(0xFF97A1EF).copy(alpha = 0.9f * coverAlpha),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { isLiked = !isLiked }
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

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    onClick = { /* поделиться */ },
                                    shape = CircleShape,
                                    color = Color(0xFFAB98EC).copy(alpha = 0.9f * coverAlpha),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { /* поделиться */ }
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
                    }

                    // Название работы (появляется при прокрутке)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .alpha(coverAlpha)
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "by $authorName",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp
                        )
                    }
                }

                // Описание фанфика
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Описание",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = description,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Кнопки "Начать читать" и "Читать потом"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { /* начать читать */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF97A1EF)
                            ),
                            modifier = Modifier.weight(0.48f)
                        ) {
                            Text("Начать читать", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { /* прочитать позже */ },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFFAB98EC)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFAB98EC)
                            ),
                            modifier = Modifier.weight(0.48f)
                        ) {
                            Text("Читать потом")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Разделитель
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
                            Text(
                                "Автор",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                authorName,
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { isFollowing = !isFollowing },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) Color(0xFFAB98EC) else Color(0xFF97A1EF)
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
        }
    }
}
