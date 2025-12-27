package com.example.recipes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipe")
data class Recipe(
    // El ID en tu nuevo JSON es un String ("18"), así que cambiamos Int por String
    @PrimaryKey
    @SerializedName("id") val id: String,

    @SerializedName("Titulo") val name: String,

    // Si alguna receta no tiene descripción, ponemos valor por defecto
    @SerializedName("Descripcion") val description: String? = "",

    @SerializedName("Categorias") val categories: String?,

    // Ahora es un texto largo, no una lista. Mucho más fácil de guardar.
    @SerializedName("Ingredientes") val ingredients: String,

    @SerializedName("Instrucciones") val instructions: String,

    // Datos nutricionales (usamos Double por si tienen decimales)
    @SerializedName("Calorias") val calories: Double? = 0.0,
    @SerializedName("Proteinas") val proteins: Double? = 0.0,
    @SerializedName("Grasa") val fat: Double? = 0.0,
    @SerializedName("Sal") val salt: Double? = 0.0,

    @SerializedName("Rating") val rating: Double? = 0.0,

    // La ruta de la imagen que viene del JSON
    @SerializedName("Imagen") val image: String
)