package com.example.recipes.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipes.data.entities.Category

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)
}