package com.example.recipes.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.recipes.R
import com.example.recipes.databinding.ActivityDetailBinding
import com.example.recipes.utils.AppDatabase
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        val recipeId = intent.getStringExtra(EXTRA_RECIPE_ID)

        if (recipeId != null) {
            loadRecipeData(recipeId)
        } else {
            finish()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadRecipeData(id: String) {
        lifecycleScope.launch {
            val recipe = db.recipeDao().getRecipeById(id)

            // Asignación de Título
            binding.tvDetailTitle.text = recipe.name

            // Métricas sobre la imagen
            binding.tvCalories.text = "${recipe.calories?.toInt() ?: 0}\nKcal"
            binding.tvproteins.text = "${recipe.proteins?.toInt() ?: 0}g\nProt"
            binding.tvfats.text = "${recipe.fat?.toInt() ?: 0}g\nFat"
            binding.tvsalt.text = "${recipe.salt?.toInt() ?: 0}g\nSalt"

            binding.ratingBar.rating = recipe.rating?.toFloat() ?: 0f

            // Formatear Ingredientes con puntos (bullet points)
            binding.tvIngredientsContent.text = formatAsList(recipe.ingredients, "•")

            // Formatear Instrucciones con números (si quieres puntos, cambia a "•")
            binding.tvInstructionsContent.text = formatAsList(recipe.instructions, "➤")

            // Imagen con Glide
            val imageResId = getDrawableId(this@DetailActivity, recipe.image)
            Glide.with(this@DetailActivity)
                .load(imageResId)
                .transform(CenterCrop())
                .into(binding.ivDetailImage)
        }
    }

    /**
     * Función para convertir un texto largo separado por puntos o saltos de línea
     * en una lista con marcadores visuales.
     */
    private fun formatAsList(text: String?, marker: String): String {
        if (text.isNullOrEmpty()) return ""

        // Dividimos por saltos de línea o por el punto seguido de espacio
        val lines = text.split(Regex("[\n]")).filter { it.isNotBlank() }

        return lines.joinToString("\n") { line ->
            "$marker  ${line.trim().removePrefix("-").removePrefix("•").trim()}"
        }
    }

    private fun getDrawableId(context: Context, imageName: String): Int {
        if (imageName.isNullOrEmpty()) return R.drawable.placeholder_food
        val cleanName = imageName
            .substringAfterLast("/")
            .substringBeforeLast(".")
        val resId = context.resources.getIdentifier(cleanName, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.placeholder_food
    }
}
