package com.example.recipes.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Necesario para corutinas
import com.example.recipes.R
import com.example.recipes.data.entities.Category
import com.example.recipes.data.entities.Recipe // Usamos directamente esta clase
import com.example.recipes.utils.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Iniciamos la carga de datos en segundo plano
        lifecycleScope.launch(Dispatchers.IO) {
            seedDatabase()

            // Una vez terminada la carga, vamos a la pantalla de recetas
            withContext(Dispatchers.Main) {
                val intent = Intent(this@MainActivity, RecipesActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta pantalla para que el usuario no pueda volver a ella
            }
        }
    }

    private suspend fun seedDatabase() {
        val database = AppDatabase.getDatabase(applicationContext)
        val recipeDao = database.recipeDao()
        val categoryDao = database.categoryDao()

        // 1. Verificar si ya existen recetas para no duplicar los datos
        // Usamos count() que es más eficiente que cargar toda la lista con getAll()
        if (recipeDao.countRecipes() > 0) {
            return // Si ya hay datos, no hacemos nada más y procedemos a la siguiente pantalla
        }

        // 2. Cargar y guardar las CATEGORÍAS desde categorias.json
        try {
            val catInputStream = resources.openRawResource(R.raw.categorias)
            val catReader = BufferedReader(InputStreamReader(catInputStream))
            // 'use' se encarga de cerrar el reader automáticamente
            val catJsonString = catReader.use { it.readText() }

            val catListType = object : TypeToken<List<Category>>() {}.type
            val categories: List<Category> = Gson().fromJson(catJsonString, catListType)

            categoryDao.insertAll(categories)
        } catch (e: Exception) {
            // Imprime el error en la consola de Logcat para depuración
            e.printStackTrace()
        }

        // 3. Cargar y guardar las RECETAS desde recetas.json
        try {
            val recipesInputStream = resources.openRawResource(R.raw.recetas)
            val recipesReader = BufferedReader(InputStreamReader(recipesInputStream))
            val recipesJsonString = recipesReader.use { it.readText() }

            // IMPORTANTE: Ahora le decimos a GSON que el JSON corresponde a una lista de 'Recipe'
            val recipeListType = object : TypeToken<List<Recipe>>() {}.type
            val recipes: List<Recipe> = Gson().fromJson(recipesJsonString, recipeListType)

            // Ya no es necesario mapear (convertir) de una clase a otra.
            // Los objetos 'Recipe' ya están listos para ser insertados en la base de datos.
            recipeDao.insertAll(recipes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
