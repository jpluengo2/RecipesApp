package com.example.recipes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.recipes.R // Asegúrate de que esto apunta a tu R
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ItemRecipeBinding
import com.google.android.material.chip.Chip

class RecipesAdapter(
    private val items: MutableList<Recipe> = mutableListOf(),
    private val onItemClick: (Recipe) -> Unit // <-- Cambia (Int) -> Unit por (Recipe) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = emptyList()

    fun updateList(newList: List<Recipe>) {
        recipes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recipe = items[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(recipe) // <-- Pasa el objeto completo
        }
    }

    override fun getItemCount() = recipes.size

    class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, onItemClick: (String) -> Unit) {
            binding.tvRecipeName.text = recipe.name

            // Lógica para las categorías
            binding.categoryChipGroup.removeAllViews() // Limpia los chips anteriores (importante para el reciclaje)

            if (recipe.categories.isNullOrEmpty()) {
                binding.categoryChipGroup.visibility = View.GONE // Oculta si no hay categorías
            } else {
                binding.categoryChipGroup.visibility = View.VISIBLE
                // Por cada categoría en la lista de la receta, crea un Chip y lo añade al grupo
                recipe.categories.forEach { categoryName ->
                    val chip = Chip(binding.categoryChipGroup.context).apply {
                        text = categoryName
                        setStyle(R.style.Widget_Material3_Chip_Suggestion) // Aplica el estilo
                        isClickable = false // Para que no parezca un botón
                    }
                    binding.categoryChipGroup.addView(chip)
                }
            }

            // --- LÓGICA DE IMAGEN LOCAL (MAGIA) ---
            val context = binding.root.context
            val imageResId = getDrawableId(context, recipe.image)

            Glide.with(context)
                .load(imageResId) // Cargamos el ID del recurso (ej. R.drawable.cat_arroz)
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(R.drawable.ic_launcher_background) // Imagen si falla
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivRecipeImage)
            // -------------------------------------

            binding.root.setOnClickListener { onItemClick(recipe.id) }
        }

        // Función auxiliar para convertir "foto.jpg" en R.drawable.foto
        private fun getDrawableId(context: Context, imageName: String): Int {
            // 1. Quitamos la extensión .jpg o .png si existe
            val nameWithoutExtension = imageName.substringBeforeLast(".")

            // 2. Buscamos el ID en la carpeta drawable
            val resId = context.resources.getIdentifier(nameWithoutExtension, "drawable", context.packageName)

            // 3. Si no existe (es 0), devolvemos una imagen por defecto
            return if (resId != 0) resId else R.drawable.ic_launcher_foreground
        }
    }
}