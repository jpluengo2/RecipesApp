package com.example.recipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ItemRecipeBinding

/**
 * Adaptador para la lista de recetas en un RecyclerView.
 *
 * @param items La lista mutable de recetas que el adaptador mostrará.
 * @param onItemClick Una función lambda que se ejecuta cuando se hace clic en un elemento.
 */
class RecipesAdapter(
    private var items: MutableList<Recipe> = mutableListOf(),
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    /**
     * Actualiza la lista de recetas en el adaptador y notifica a la vista para que se actualice.
     * @param newItems La nueva lista de recetas a mostrar.
     */
    fun updateList(newItems: List<Recipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.bind(recipe)
        // Configura el listener para el clic en el elemento completo de la vista
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder para cada elemento de receta.
     * Contiene la lógica para vincular los datos de una receta a la vista (layout).
     */
    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            // Asigna el nombre de la receta al TextView
            binding.tvRecipeName.text = recipe.name

            // Carga la imagen de la receta usando Glide
            Glide.with(binding.root.context)
                .load(recipe.image) // Glide puede cargar directamente desde URLs o rutas de archivo
                .centerCrop() // Escala la imagen para que llene la vista
                .into(binding.ivRecipeImage)
        }
    }
}
