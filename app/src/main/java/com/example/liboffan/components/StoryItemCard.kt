package com.example.liboffan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liboffan.model.StoryItem


@Composable
fun StoryItemCard(
    story: StoryItem,
    onClick: () -> Unit,
    onBranchClick: ((Long, Long) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB8C1FF).copy(alpha = 0.35f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (story.coverUrl != null) {
                AsyncImage(
                    model = story.coverUrl,
                    contentDescription = "Обложка",
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFAB98EC).copy(alpha = 0.6f))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = story.title, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 2)
                Text(text = "by ${story.author.displayName ?: "Аноним"}", color = Color.LightGray, fontSize = 12.sp)

                if (story.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        story.tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFB8C1FF).copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = tag, color = Color.White, fontSize = 10.sp)
                            }
                        }
                        if (story.tags.size > 3) {
                            Text(text = "+${story.tags.size - 3}", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        }
                    }
                }

                if (story.branches.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ответвления (${story.branches.size}${if (story.hasMoreBranches) "+" else ""})",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Свернуть" else "Развернуть",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (expanded) {
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            story.branches.forEach { branch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onBranchClick?.invoke(branch.versionId, story.id)
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallSplit,
                                        contentDescription = "Ветка",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = branch.title, color = Color.White, fontSize = 12.sp, maxLines = 1)
                                        Text(text = "от ${branch.authorName} • ${branch.ageRating ?: ""}", color = Color.LightGray, fontSize = 10.sp)
                                    }

                                    if (branch.tags.isNotEmpty()) {
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            branch.tags.take(2).forEach { tag ->
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFFB8C1FF).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(text = tag, color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (story.hasMoreBranches) {
                                Text(
                                    text = "Показать ещё...",
                                    color = Color(0xFF7065AC),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp).clickable { /* TODO */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}