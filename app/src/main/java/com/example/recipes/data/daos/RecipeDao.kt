package com.example.recipes.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipes.data.entities.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>

    // CAMBIO IMPORTANTE: El ID ahora es String
    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipeById(id: String): Recipe

    @Query("SELECT count(*) FROM recipe")
    suspend fun countRecipes(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    // Búsqueda simple por nombre o ingredientes (opcional, para el futuro)
    @Query("SELECT * FROM recipe WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR categories LIKE '%' || :query || '%' OR ingredients LIKE '%' ||:query || '%'")
    suspend fun searchRecipes(query: String): List<Recipe>
}
