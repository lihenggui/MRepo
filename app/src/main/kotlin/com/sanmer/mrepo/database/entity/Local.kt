package com.sanmer.mrepo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.State

@Entity(tableName = "localModules")
data class LocalModuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val state: String
)

fun LocalModule.toEntity() = LocalModuleEntity(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description,
    state = state.name
)

fun LocalModuleEntity.toModule() = LocalModule(
    id = id,
    name = name,
    version = version,
    versionCode = versionCode,
    author = author,
    description = description
).apply {
    state = State.valueOf(this@toModule.state)
}