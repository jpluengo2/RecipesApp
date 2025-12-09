package com.example.recipes.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipes.data.entities.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun findById(id: Int): Recipe

    // MEJORA DEL BUSCADOR: Busca el texto en nombre, ingredientes, cocina o instrucciones
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' OR cuisine LIKE '%' || :query || '%' OR instructions LIKE '%' || :query || '%'")
    suspend fun searchInAllFields(query: String): List<Recipe>

    // Contar recetas (para tu mejora 1 y 2)
    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()
}