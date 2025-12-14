package com.reader.novel.model

data class ReadingProgress(
    val chapterIndex: Int,
    val progressPercent: Float = 0f,
    val lastReadPosition: Int = 0
)