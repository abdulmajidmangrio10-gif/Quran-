package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: String,
    val updatedAt: Long,
    val durationMs: Long,
    val videoUri: String?,
    val thumbnailUri: String?,
    val projectJson: String
)
