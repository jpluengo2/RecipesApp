package com.example.recipes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.adapter.RecipesAdapter
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ActivityRecipesBinding
import com.example.recipes.utils.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipesBinding
    private lateinit var adapter: RecipesAdapter
    private lateinit var db: AppDatabase

    // Guardamos la referencia al buscador para cambiar su texto dinámicamente
    private var searchView: SearchView? = null
    private var totalRecipesCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Room
        db = AppDatabase.getDatabase(this)

        initView()
    }

    private fun initView() {
        // La lambda ahora recibe directamente un objeto Recipe, por lo que podemos pasar
        // la referencia a la función onItemClickListener directamente.        adapter = RecipesAdapter(onItemClick = this::onItemClickListener)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadRecipes()
    }


    private fun loadRecipes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val recipesList = db.recipeDao().getAll()
            totalRecipesCount = recipesList.size

            withContext(Dispatchers.Main) {
                updateUI(recipesList)
                // Actualizamos el texto del buscador si ya está creado
                updateSearchHint()
            }
        }
    }

    private fun updateUI(recipes: List<Recipe>) {
        // Animación suave al cambiar visibilidad
        TransitionManager.beginDelayedTransition(binding.root)

        if (recipes.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyPlaceholder.visibility = View.VISIBLE
        } else {
            adapter.updateList(recipes)
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipes_menu, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        initSearchView(searchItem)
        return true
    }

    private fun initSearchView(searchItem: MenuItem?) {
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView

            // APLICAMOS LA MEJORA 2: Mostrar cantidad de recetas en el placeholder
            updateSearchHint()

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchView?.clearFocus()
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    // APLICAMOS LA MEJORA 3: Búsqueda en base de datos en tiempo real
                    searchRecipes(query)
                    return true
                }
            })
        }
    }

    private fun updateSearchHint() {
        // FIX: Call getString() on the activity's context and pass the resource ID.
        val hint = getString(R.string.search_hint, totalRecipesCount)
        searchView?.queryHint = hint
    }

    private fun searchRecipes(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = if (query.isEmpty()) {
                db.recipeDao().getAll()
            } else {
                db.recipeDao().searchRecipes(query)
            }

            withContext(Dispatchers.Main) {
                updateUI(filteredList)
            }
        }
    }

    private fun onItemClickListener(recipe: Recipe) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, recipe.id)
        startActivity(intent)
    }
}