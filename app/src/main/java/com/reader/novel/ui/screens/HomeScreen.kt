package com.reader.novel.ui.screens

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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reader.novel.ui.theme.NightBackground
import com.reader.novel.ui.theme.NightText
import com.reader.novel.ui.theme.ReaderBackground
import com.reader.novel.ui.theme.ReaderPrimary
import com.reader.novel.ui.theme.ReaderText
import com.reader.novel.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartReading: () -> Unit,
    viewModel: BookViewModel = viewModel()
) {
    val fontSize by viewModel.fontSize.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    // 根据暗夜模式选择颜色
    val backgroundColor = if (isDarkMode) NightBackground else ReaderBackground
    val textColor = if (isDarkMode) NightText else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
    val primaryColor = if (isDarkMode) Color(0xFFBB86FC) else ReaderPrimary
    val lightPrimaryColor = if (isDarkMode) Color(0xFFBB86FC).copy(alpha = 0.1f) else ReaderPrimary.copy(alpha = 0.1f)
    val secondaryTextColor = if (isDarkMode) Color(0xFFCCCCCC) else Color.DarkGray
    val dividerColor = if (isDarkMode) Color(0xFF555555) else Color.LightGray.copy(alpha = 0.3f)
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "轻阅",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        color = textColor
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    actionIconContentColor = textColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 40.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 书籍封面
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = lightPrimaryColor,
                    tonalElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "书",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp),
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 书籍标题
                Text(
                    text = viewModel.bookTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = textColor
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // 作者
                Text(
                    text = viewModel.bookAuthor,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                    color = secondaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 简介
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = cardColor,
                    tonalElevation = if (isDarkMode) 4.dp else 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "作品简介",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = viewModel.bookDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = if (isDarkMode) Color(0xFFCCCCCC) else Color.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 开始阅读按钮
                FilledTonalButton(
                    onClick = onStartReading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "开始阅读",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 章节统计
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = cardColor,
                    tonalElevation = if (isDarkMode) 4.dp else 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "9",
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "章节",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = if (isDarkMode) Color(0xFFCCCCCC) else Color.Black
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(dividerColor)
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "31k",
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "字数",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = if (isDarkMode) Color(0xFFCCCCCC) else Color.Black
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(dividerColor)
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "连载中",
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "状态",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = if (isDarkMode) Color(0xFFCCCCCC) else Color.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(onStartReading = {})
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenDarkPreview() {
    MaterialTheme {
        HomeScreen(onStartReading = {})
    }
}