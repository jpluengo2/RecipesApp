package com.example.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.recipes.data.serviceapis.RecipeServiceApi
import com.example.recipes.databinding.ActivityMainBinding
import com.example.recipes.utils.AppDatabase
import com.example.recipes.utils.RetrofitProvider
import com.example.recipes.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // Inicializamos la base de datos Room
        db = AppDatabase.getDatabase(this)

        // Iniciamos la sincronización inteligente
        checkAndSyncData()
    }

    private fun checkAndSyncData() {
        val service: RecipeServiceApi = RetrofitProvider.getRecipeServiceApi()

        // Usamos lifecycleScope que es más seguro para Activities que CoroutineScope global
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Preguntamos al servidor: "¿Cuántas recetas tienes?" (Descarga ligera)
                val response = service.checkCount()

                if (response.isSuccessful && response.body() != null) {
                    val remoteTotal = response.body()!!.total.toInt() // El total en el servidor
                    val localCount = db.recipeDao().count()           // El total en tu móvil

                    Log.i("SYNC", "Servidor: $remoteTotal vs Local: $localCount")

                    // 2. Comparamos
                    if (remoteTotal != localCount) {
                        // SI SON DISTINTOS: Hay novedades o faltan datos. ¡A descargar!
                        Log.i("SYNC", "Datos desactualizados. Descargando todo...")
                        downloadAllRecipes(service)
                    } else {
                        // SI SON IGUALES: Todo está al día.
                        Log.i("SYNC", "Todo actualizado. Saltando descarga.")
                        withContext(Dispatchers.Main) {
                            navigateToRecipesList()
                        }
                    }
                } else {
                    // Si el servidor falla (ej: error 500), usamos lo que tengamos localmente
                    Log.e("SYNC", "Error en el servidor al consultar cuenta.")
                    withContext(Dispatchers.Main) { navigateToRecipesList() }
                }

            } catch (e: Exception) {
                // EXCEPCIÓN: Probablemente no hay internet.
                // Esto cumple tu deseo: "Si la web deja de existir, seguimos teniendo las recetas"
                Log.e("SYNC", "No hay conexión o error de red: ${e.message}")
                withContext(Dispatchers.Main) {
                    navigateToRecipesList()
                }
            }
        }
    }

    private suspend fun downloadAllRecipes(service: RecipeServiceApi) {
        try {
            val response = service.findAll()

            if (response.body() != null) {
                val recipesList = response.body()?.results.orEmpty()

                // Guardamos en Room (Método moderno)
                // Usamos insertAll con REPLACE, así que actualiza las existentes y añade las nuevas
                db.recipeDao().insertAll(recipesList)

                // Marcamos en sesión que ya tenemos datos (por si acaso lo usamos luego)
                session.didFetchData = true

                Log.i("SYNC", "Base de datos actualizada con ${recipesList.size} recetas.")

                withContext(Dispatchers.Main) {
                    navigateToRecipesList()
                }
            }
        } catch (e: Exception) {
            Log.e("SYNC", "Error descargando las recetas: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Error actualizando datos. Usando versión offline.", Toast.LENGTH_LONG).show()
                navigateToRecipesList()
            }
        }
    }

    private fun navigateToRecipesList() {
        val intent = Intent(this, RecipesActivity::class.java)
        startActivity(intent)
        finish() // Cerramos MainActivity para que no se pueda volver atrás con el botón 'back'
    }
}