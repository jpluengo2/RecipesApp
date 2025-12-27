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

            // Lógica de expansión
            var isExpanded = false

            // Acción al hacer clic en el item (usa 'onClick' del constructor)
            root.setOnClickListener { onClick(recipe) }

            ivExpand.setOnClickListener {
                isExpanded = !isExpanded
                tvRecipeDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 2
                ivExpand.rotation = if (isExpanded) 180f else 0f
            }

            // Manejo de Categorías (Chips)
            cgCategories.removeAllViews()
            val categoriesString = recipe.categories?.toString() ?: ""
            categoriesString.split(",").forEach { categoryName ->
                if (categoryName.isNotBlank()) {
                    val chip = Chip(root.context).apply {
                        text = categoryName.trim()
                    }
                    cgCategories.addView(chip)
                }
            }

            // Imagen con Glide
            Glide.with(ivRecipeImage.context)
                .load(recipe.image)
                .placeholder(R.drawable.placeholder_food) // Asegúrate de tener este drawable
                .into(ivRecipeImage)
        }
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
