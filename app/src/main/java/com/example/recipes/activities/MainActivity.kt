package com.example.recipes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.adapters.RecipesAdapter
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
    private lateinit var adapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        setupRecyclerView()

        // Cargamos los datos
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = RecipesAdapter { recipeId ->
            // Check if recipeId is not null before creating and starting the intent.
            recipeId?.let { id ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, id) // 'id' is now a non-nullable Int
                startActivity(intent)
            }
        }
        binding.rvRecipes.layoutManager = LinearLayoutManager(this)
        binding.rvRecipes.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val count = db.recipeDao().countRecipes()

            // Variable para guardar las recetas que mostraremos
            val recipesToShow: List<Recipe>

            if (count == 0) {
                Log.i("RECETAS", "Base de datos vacía. Cargando desde JSON local...")
                val loadedRecipes = loadRecipesFromJson()
                db.recipeDao().insertAll(loadedRecipes)
                recipesToShow = loadedRecipes
            } else {
                Log.i("RECETAS", "Datos encontrados en BD local. Mostrando...")
                recipesToShow = db.recipeDao().getAllRecipes()
            }

            // Volvemos al hilo principal para actualizar la pantalla
            withContext(Dispatchers.Main) {
                adapter.updateList(recipesToShow)

                // CAMBIO IMPORTANTE:
                // 1. Ocultamos la pantalla de carga completa (loadingLayout)
                binding.loadingLayout.visibility = View.GONE
                // 2. Hacemos visible la lista
                binding.rvRecipes.visibility = View.VISIBLE
            }
        }
    }

    private fun loadRecipesFromJson(): List<Recipe> {
        return try {
            val inputStream = resources.openRawResource(R.raw.recetas)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val listType = object : TypeToken<List<Recipe>>() {}.type
            Gson().fromJson(reader, listType)
        } catch (e: Exception) {
            Log.e("RECETAS", "Error leyendo JSON: ${e.message}")
            emptyList()
        }
    }
}