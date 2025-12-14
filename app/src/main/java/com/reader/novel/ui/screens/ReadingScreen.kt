package com.reader.novel.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reader.novel.model.Chapter
import com.reader.novel.ui.theme.NightBackground
import com.reader.novel.ui.theme.NightText
import com.reader.novel.ui.theme.ReaderBackground
import com.reader.novel.ui.theme.ReaderPrimary
import com.reader.novel.ui.theme.ReaderText
import com.reader.novel.viewmodel.BookViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    onBackClick: () -> Unit,
    viewModel: BookViewModel = viewModel()
) {
    val chapters by viewModel.chapters.collectAsState()
    val currentChapterIndex by viewModel.currentChapterIndex.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var showChaptersSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    
    val chaptersSheetState = rememberModalBottomSheetState()
    val settingsSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    
    // 使用 Material3 的 TopAppBarScrollBehavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    
    val backgroundColor = if (isDarkMode) NightBackground else ReaderBackground
    val textColor = if (isDarkMode) NightText else ReaderText
    val surfaceColor = if (isDarkMode) Color(0xFF2A2A2A) else Color.White
    val primaryColor = if (isDarkMode) Color(0xFFBB86FC) else ReaderPrimary
    
    // 直接使用 currentChapterIndex 来获取当前章节
    val currentChapter = remember(currentChapterIndex, chapters) {
        if (chapters.isNotEmpty() && currentChapterIndex in chapters.indices) {
            chapters[currentChapterIndex]
        } else {
            null
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // 使用 LargeTopAppBar 以获得更顺滑的动画
            LargeTopAppBar(
                title = {
                    Text(
                        text = viewModel.bookTitle,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                        color = textColor
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(44.dp),
                        shape = CircleShape,
                        color = primaryColor.copy(alpha = 0.1f),
                        tonalElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "←",
                                style = MaterialTheme.typography.bodyLarge,
                                color = primaryColor
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = {
                                scope.launch {
                                    showChaptersSheet = true
                                }
                            },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .height(36.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = primaryColor.copy(alpha = 0.1f),
                            tonalElevation = 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "目录",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryColor
                                )
                            }
                        }
                        
                        Surface(
                            onClick = {
                                scope.launch {
                                    showSettingsSheet = true
                                }
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(36.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = primaryColor.copy(alpha = 0.1f),
                            tonalElevation = 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aa",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = primaryColor
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = backgroundColor,
                    scrolledContainerColor = backgroundColor,
                    navigationIconContentColor = primaryColor,
                    titleContentColor = textColor,
                    actionIconContentColor = primaryColor
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // 阅读内容
            val listState = rememberLazyListState()
            
            // 当章节切换时，自动滚动到顶部
            LaunchedEffect(currentChapterIndex) {
                listState.animateScrollToItem(0)
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    top = 0.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 100.dp
                )
            ) {
                item {
                    // 在顶部添加一些空白，防止内容被顶栏遮挡
                    Spacer(modifier = Modifier.height(140.dp))
                    
                    // 章节标题
                    Text(
                        text = currentChapter?.title ?: "加载中...",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = (fontSize + 4).sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (fontSize * 1.8).sp
                        ),
                        color = textColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // 章节内容
                    Text(
                        text = currentChapter?.content ?: "暂无内容",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.8).sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = textColor,
                        textAlign = TextAlign.Justify,
                        letterSpacing = 0.5.sp
                    )
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // 检测滚动位置，显示/隐藏底部导航
            val showBottomNav by remember {
                derivedStateOf {
                    val totalItems = listState.layoutInfo.totalItemsCount
                    if (totalItems == 0) return@derivedStateOf true
                    
                    val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val viewportHeight = listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
                    val lastItemBottom = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.offset?.plus(
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.size ?: 0
                    ) ?: 0
                    
                    lastVisibleIndex == totalItems - 1 && lastItemBottom > viewportHeight - 200
                }
            }
            
            // 底部章节导航
            androidx.compose.animation.AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BottomChapterNavigation(
                    currentChapterIndex = currentChapterIndex,
                    totalChapters = chapters.size,
                    primaryColor = primaryColor,
                    onPreviousChapter = {
                        if (currentChapterIndex > 0) {
                            viewModel.selectChapter(currentChapterIndex - 1)
                        }
                    },
                    onNextChapter = {
                        if (currentChapterIndex < chapters.size - 1) {
                            viewModel.selectChapter(currentChapterIndex + 1)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }
        }
        
        // 章节选择底部弹窗
        if (showChaptersSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { 
                        scope.launch {
                            chaptersSheetState.hide()
                        }.invokeOnCompletion {
                            if (!chaptersSheetState.isVisible) {
                                showChaptersSheet = false
                            }
                        }
                    }
            ) {
                ModalBottomSheet(
                    onDismissRequest = { 
                        showChaptersSheet = false
                    },
                    sheetState = chaptersSheetState,
                    containerColor = surfaceColor,
                    scrimColor = Color.Transparent,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                ) {
                    ChaptersSheet(
                        chapters = chapters,
                        currentChapterIndex = currentChapterIndex,
                        primaryColor = primaryColor,
                        surfaceColor = surfaceColor,
                        onChapterSelect = { index ->
                            viewModel.selectChapter(index)
                            scope.launch {
                                chaptersSheetState.hide()
                                showChaptersSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
        
        // 阅读设置底部弹窗
        if (showSettingsSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { 
                        scope.launch {
                            settingsSheetState.hide()
                        }.invokeOnCompletion {
                            if (!settingsSheetState.isVisible) {
                                showSettingsSheet = false
                            }
                        }
                    }
            ) {
                ModalBottomSheet(
                    onDismissRequest = { 
                        showSettingsSheet = false
                    },
                    sheetState = settingsSheetState,
                    containerColor = surfaceColor,
                    scrimColor = Color.Transparent,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                ) {
                    ReadingSettingsPanel(
                        fontSize = fontSize,
                        isDarkMode = isDarkMode,
                        primaryColor = primaryColor,
                        surfaceColor = surfaceColor,
                        onToggleDarkMode = { 
                            viewModel.toggleDarkMode()
                        },
                        onIncreaseFontSize = { 
                            viewModel.increaseFontSize()
                        },
                        onDecreaseFontSize = { 
                            viewModel.decreaseFontSize()
                        },
                        onClose = {
                            scope.launch {
                                settingsSheetState.hide()
                                showSettingsSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomChapterNavigation(
    currentChapterIndex: Int,
    totalChapters: Int,
    primaryColor: Color,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = primaryColor.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 上一章按钮
            Surface(
                onClick = onPreviousChapter,
                modifier = Modifier
                    .height(44.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                color = if (currentChapterIndex > 0) Color.White.copy(alpha = 0.2f) 
                       else Color.White.copy(alpha = 0.1f),
                tonalElevation = 0.dp,
                enabled = currentChapterIndex > 0
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "上一章",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (currentChapterIndex > 0) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 章节信息
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.5f)
            ) {
                Text(
                    text = "第 ${currentChapterIndex + 1} 章",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "共 $totalChapters 章",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 下一章按钮
            Surface(
                onClick = onNextChapter,
                modifier = Modifier
                    .height(44.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                color = if (currentChapterIndex < totalChapters - 1) Color.White.copy(alpha = 0.2f) 
                       else Color.White.copy(alpha = 0.1f),
                tonalElevation = 0.dp,
                enabled = currentChapterIndex < totalChapters - 1
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "下一章",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (currentChapterIndex < totalChapters - 1) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ChaptersSheet(
    chapters: List<Chapter>,
    currentChapterIndex: Int,
    primaryColor: Color,
    surfaceColor: Color,
    onChapterSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "章节列表",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(chapters) { index, chapter ->
                val isCurrent = index == currentChapterIndex
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onChapterSelect(index) },
                    color = if (isCurrent) primaryColor.copy(alpha = 0.15f) else Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 章节序号
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrent) primaryColor 
                                    else Color.Gray.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (isCurrent) Color.White else Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // 章节标题
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                color = if (isCurrent) primaryColor else MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "${chapter.wordCount}字",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                        
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingSettingsPanel(
    fontSize: Int,
    isDarkMode: Boolean,
    primaryColor: Color,
    surfaceColor: Color,
    onToggleDarkMode: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 根据暗夜模式设置文字颜色
    val titleColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black
    val labelColor = if (isDarkMode) Color(0xFFCCCCCC) else Color.Gray
    val secondaryTextColor = if (isDarkMode) Color(0xFFCCCCCC) else Color.Gray
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 拖拽指示器
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Gray.copy(alpha = 0.3f))
        )
        
        Text(
            text = "阅读设置",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            color = titleColor, // 添加颜色设置
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 字体大小调节
        Text(
            text = "字体大小",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp
            ),
            color = labelColor, // 使用 labelColor
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                onClick = onDecreaseFontSize,
                enabled = fontSize > 12,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                color = if (fontSize > 12) primaryColor.copy(alpha = 0.1f) 
                       else Color.LightGray.copy(alpha = 0.1f),
                tonalElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (fontSize > 12) primaryColor else Color.LightGray
                    )
                }
            }
            
            // 字体大小显示
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fontSize.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "字号",
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor // 使用 secondaryTextColor
                )
            }
            
            Surface(
                onClick = onIncreaseFontSize,
                enabled = fontSize < 30,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                color = if (fontSize < 30) primaryColor.copy(alpha = 0.1f) 
                       else Color.LightGray.copy(alpha = 0.1f),
                tonalElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (fontSize < 30) primaryColor else Color.LightGray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 字体大小滑块
        Slider(
            value = fontSize.toFloat(),
            onValueChange = { /* 通过按钮控制 */ },
            valueRange = 12f..30f,
            steps = 17,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor,
                inactiveTrackColor = primaryColor.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 主题切换
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "阅读模式",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp
                    ),
                    color = labelColor // 使用 labelColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isDarkMode) "夜间模式" else "日间模式",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = primaryColor
                )
            }
            
            Surface(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp)),
                color = if (isDarkMode) primaryColor else Color.LightGray.copy(alpha = 0.2f),
                tonalElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isDarkMode) "夜间" else "日间",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (isDarkMode) Color.White else Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 完成按钮
        Surface(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(14.dp)),
            color = primaryColor,
            tonalElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "完成",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
        
        // 为底部导航栏留出空间
        Spacer(modifier = Modifier.height(16.dp))
    }
}