package com.example.recipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.R // Asegúrate de que este sea el paquete correcto de tu app
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ItemRecipeBinding
import com.google.android.material.chip.Chip

class RecipesAdapter(
    private var recipes: List<Recipe>,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() { // Corregido a RecipeViewHolder

    // Lista que realmente se muestra (la original o la filtrada)
    private var filteredRecipes: List<Recipe> = recipes

    // Función para actualizar la lista completa
    fun updateList(newItems: List<Recipe>) {
        this.recipes = newItems
        this.filteredRecipes = newItems
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = filteredRecipes[position]
        with(holder.binding) {
            tvRecipeName.text = recipe.name
            tvRecipeDescription.text = recipe.description

            // Limpiamos y añadimos categorías
            cgCategories.removeAllViews()

            // Si categories es String, lo dividimos. Si ya es List, usamos recipe.categories directamente.
            val categoryList = recipe.categories?.split(",")?.map { it.trim() } ?: emptyList()

            categoryList.forEach { cat ->
                if (cat.isNotEmpty()) {
                    val chip = Chip(holder.itemView.context).apply {
                        text = cat
                        textSize = 11f
                        chipMinHeight = 24f
                        // Estilo visual más compacto
                        setChipBackgroundColorResource(android.R.color.holo_orange_light)
                        setTextColor(resources.getColor(android.R.color.black, null))
                    }
                    cgCategories.addView(chip)
                }
            }

            // Carga de imagen con Glide
            Glide.with(ivRecipeImage.context)
                .load(recipe.image)
                .centerCrop()
                .into(ivRecipeImage)
        }
        holder.itemView.setOnClickListener { onClick(recipe) }
    }


    override fun getItemCount() = filteredRecipes.size

    // FUNCIÓN DE BÚSQUEDA
    fun filter(query: String) {
        filteredRecipes = if (query.isEmpty()) {
            recipes
        } else {
            recipes.filter {
                it.name.lowercase().contains(query.lowercase()) ||
                        it.ingredients.lowercase().contains(query.lowercase()) ||
                        it.instructions.lowercase().contains(query.lowercase())
            }
        }
        notifyDataSetChanged()
    }


}
