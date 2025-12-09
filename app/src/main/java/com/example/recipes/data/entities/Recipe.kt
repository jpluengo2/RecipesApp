package com.example.recipes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey
    @SerializedName("id") val id: Int, // Cambiado a val, el ID viene del servidor
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("prepTimeMinutes") val prepTimeMinutes: Int,
    @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("cuisine") val cuisine: String,
    @SerializedName("caloriesPerServing") val caloriesPerServing: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("reviewCount") val reviewCount: Int,
    @SerializedName("ingredients") val ingredients: List<String>,
    @SerializedName("instructions") val instructions: List<String>
) {
    // Propiedad calculada útil para la UI
    val time: Int
        get() = prepTimeMinutes + cookTimeMinutes
}

// Esta clase ayuda a Room a guardar las listas de textos
class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}