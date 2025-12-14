package com.reader.novel.model

data class Chapter(
    val id: String,
    val title: String,
    val content: String,
    val chapterNumber: Int,
    val wordCount: Int = 0
)