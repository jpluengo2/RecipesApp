package com.example.recipes.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.recipes.R
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ActivityMainBinding
import com.example.recipes.utils.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Iniciamos la carga
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val count = db.recipeDao().countRecipes()

            if (count == 0) {
                // Si está vacía, cargamos del JSON y guardamos en BD
                val recipes = loadRecipesFromJson()
                db.recipeDao().insertAll(recipes)
            }

            // Simulación pequeña pausa para que se vea el logo (opcional)
            // Thread.sleep(1500)

            withContext(Dispatchers.Main) {
                // ¡DATOS LISTOS! Navegamos a la pantalla de recetas
                goToRecipesActivity()
            }
        }
    }

    private fun goToRecipesActivity() {
        val intent = Intent(this, RecipesActivity::class.java)
        startActivity(intent)
        finish() // Cerramos MainActivity para que no se pueda volver atrás a la pantalla de carga
    }

    private fun loadRecipesFromJson(): List<Recipe> {
        return try {
            val inputStream = resources.openRawResource(R.raw.recetas)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val listType = object : TypeToken<List<Recipe>>() {}.type
            Gson().fromJson(reader, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}