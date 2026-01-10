package com.example.recipes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,         // Ej: "Cordero"
    val imagePath: String     // Ej: "cat_cordero" (nombre del drawable)
)