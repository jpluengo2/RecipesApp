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

        // Recuperamos el ID como String (antes era getIntExtra)
        val recipeId = intent.getStringExtra(EXTRA_RECIPE_ID)

        if (recipeId != null) {
            loadRecipeData(recipeId)
        } else {
            finish() // Si no hay ID, cerramos
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadRecipeData(id: String) {
        lifecycleScope.launch {
            val recipe = db.recipeDao().getRecipeById(id)

            // Asignación de textos
            binding.tvDetailTitle.text = recipe.name
            binding.tvDetailDescription.text = recipe.description

            // Nuevos campos nutricionales (Formateamos bonito)
            binding.tvCalories.text = "${recipe.calories?.toInt() ?: 0} Kcal"
            binding.tvproteins.text = "${recipe.proteins?.toInt() ?: 0}g Prot" // Reutilizamos icono reloj para proteina o lo cambias
            binding.tvfats.text = "${recipe.fat?.toInt() ?: 0}g fat"
            binding.tvsalt.text = "${recipe.salt?.toInt() ?: 0}g Salt"
            binding.ratingBar.rating = recipe.rating?.toFloat() ?: 0f

            // Ingredientes y Pasos (Ahora son texto plano, no listas)
            binding.tvIngredientsContent.text = recipe.ingredients
            binding.tvInstructionsContent.text = recipe.instructions

            // --- IMAGEN LOCAL ---
            val imageResId = getDrawableId(this@DetailActivity, recipe.image)
            Glide.with(this@DetailActivity)
                .load(imageResId)
                .transform(CenterCrop())
                .into(binding.ivDetailImage)
        }
    }

    // Misma función auxiliar que en el Adapter (podríamos moverla a un archivo Utils)
    // Copia esta función en RecipesAdapter y DetailActivity
    private fun getDrawableId(context: Context, imageName: String): Int {
        if (imageName.isNullOrEmpty()) return R.drawable.placeholder_food

        // 1. LIMPIEZA INTELIGENTE:
        // Si viene "assets/images/foto.jpg", se queda con "foto"
        // Si viene "foto.png", se queda con "foto"
        val cleanName = imageName
            .substringAfterLast("/")  // Quita carpetas previas
            .substringBeforeLast(".") // Quita la extensión

        // 2. BUSCA EL RECURSO:
        val resId = context.resources.getIdentifier(cleanName, "drawable", context.packageName)

        // 3. RETORNO SEGURO: Si no existe, devuelve el placeholder
        return if (resId != 0) resId else R.drawable.placeholder_food
    }
}