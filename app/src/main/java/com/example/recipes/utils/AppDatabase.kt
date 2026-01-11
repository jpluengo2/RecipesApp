package com.example.recipes.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipes.data.daos.CategoryDao
import com.example.recipes.data.daos.RecipeDao
import com.example.recipes.data.entities.Category
import com.example.recipes.data.entities.Recipe

@Database(entities = [Recipe::class, Category::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun categoryDao(): CategoryDao // Nuevo DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipes_database"
                )
                    .fallbackToDestructiveMigration() // Importante: Permite borrar la BD vieja al cambiar la estructura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}