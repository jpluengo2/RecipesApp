package com.example.recipes.data.serviceapis

import com.example.recipes.data.entities.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeServiceApi {
    // Descarga TODAS las recetas (para actualizar)
    @GET("recipes/?limit=0")
    suspend fun findAll() : Response<RecipeResponse>

    // NUEVO: Descarga solo 1 receta para ver el campo "total" rápidamente
    @GET("recipes/?limit=1")
    suspend fun checkCount() : Response<RecipeResponse>

    @GET("recipes/search")
    suspend fun findAllByName(@Query("q") query:String) : Response<RecipeResponse>

    @GET("recipes/{id}")
    suspend fun findById(@Path("id") id:Int) : Response<Recipe>
}