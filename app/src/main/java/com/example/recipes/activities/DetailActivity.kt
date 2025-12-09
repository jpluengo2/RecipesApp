package com.example.recipes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ActivityDetailBinding
import com.example.recipes.utils.AppDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "RECIPE_ID"
        const val EXTRA_NAME = "RECIPE_NAME"
        const val EXTRA_IMAGE = "RECIPE_IMAGE"
    }

    private lateinit var binding: ActivityDetailBinding

    private var recipeId: Int = -1
    private lateinit var recipe: Recipe
    private lateinit var db: AppDatabase // Nueva referencia a Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeId = intent.getIntExtra(EXTRA_ID, -1)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // Inicializamos la base de datos Room
        db = AppDatabase.getDatabase(this)

        if (recipeId != -1) {
            loadRecipeData()
        }
    }

    private fun loadRecipeData() {
        // Usamos una corrutina para leer la base de datos en segundo plano (IO)
        lifecycleScope.launch(Dispatchers.IO) {
            // Obtenemos la receta usando el nuevo DAO
            recipe = db.recipeDao().findById(recipeId)

            // Volvemos al hilo principal (Main) para pintar la pantalla
            withContext(Dispatchers.Main) {
                renderData()
            }
        }
    }

    private fun renderData() {
        // Verificamos que la receta se haya cargado correctamente
        if (::recipe.isInitialized) {
            Picasso.get().load(recipe.image).into(binding.photoImageView)
            binding.nameTextView.text = recipe.name
            binding.descriptionTextView.text = recipe.cuisine
            binding.ratingBar.rating = recipe.rating
            binding.reviewsTextView.text = "(${recipe.reviewCount} Reviews)"

            binding.prepTimeTextView.text = "${recipe.prepTimeMinutes} min"
            binding.cookTimeTextView.text = "${recipe.cookTimeMinutes} min"
            binding.difficultyTextView.text = recipe.difficulty
            binding.servingsTextView.text = "Recipe for ${recipe.servings} servings"

            var ingredientsText = ""
            recipe.ingredients.forEachIndexed { index, element ->
                if (index > 0) ingredientsText += "\n"
                ingredientsText += " - $element"
            }
            binding.ingredientsTextView.text = ingredientsText

            var instructionsText = ""
            recipe.instructions.forEachIndexed { index, element ->
                if (index > 0) instructionsText += "\n"
                instructionsText += " - $element"
            }
            binding.instructionsTextView.text = instructionsText
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}