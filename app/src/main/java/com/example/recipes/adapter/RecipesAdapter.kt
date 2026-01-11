package com.example.recipes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipes.R
import com.example.recipes.data.entities.Recipe
import com.example.recipes.databinding.ItemRecipeBinding
import android.content.res.ColorStateList

class RecipesAdapter(
    private var recipes: List<Recipe>,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    private var filteredRecipes: List<Recipe> = recipes

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
            // 1. Configuración de Textos
            tvRecipeName.text = recipe.name
            tvImageCategory.text = recipe.category
            tvRecipeDescription.text = recipe.description

            // CORRECCIÓN 1: Lógica Dinámica de Líneas sin espacios muertos
            tvRecipeName.post {
                val titleLines = tvRecipeName.lineCount

                // Ajustamos el maxLines de la descripción dinámicamente
                val descMaxLines = when (titleLines) {
                    1 -> 5
                    2 -> 4
                    3 -> 3
                    else -> 2
                }
                tvRecipeDescription.maxLines = descMaxLines

                // Forzamos que la descripción no tenga un margen superior excesivo si el título es corto
                // y que el título no reserve espacio para líneas que no usa (minLines=1 en XML)
            }

            // CORRECCIÓN 2: Nutrientes con Colores Diferenciados y Sufijos
            val context = root.context

            // Definimos un tamaño pequeño para los iconos (ej: 14dp convertido a pixeles)
            val iconSize = (14 * context.resources.displayMetrics.density).toInt()

            // Función auxiliar para redimensionar los iconos de cada TextView
            fun resizeIcon(textView: android.widget.TextView) {
                val drawables = textView.compoundDrawablesRelative
                drawables[0]?.let { // El drawable de la izquierda (index 0)
                    it.setBounds(0, 0, iconSize, iconSize)
                    textView.setCompoundDrawablesRelative(it, null, null, null)
                }
            }

            // Aplicamos a cada nutriente
                        resizeIcon(tvCalories)
                        resizeIcon(tvProtein)
                        resizeIcon(tvFat)
                        resizeIcon(tvSalt)

            // Reducimos ligeramente los textos (si no lo hiciste en el XML)
                        tvCalories.textSize = 10f
                        tvProtein.textSize = 10f
                        tvFat.textSize = 10f
                        tvSalt.textSize = 10f

            // Calorías - Naranja/Rojo
            tvCalories.text = "${recipe.calories?.toInt() ?: 0} kcal"
            tvCalories.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            tvCalories.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark))

            // Proteínas - Azul
            tvProtein.text = "${recipe.proteins?.toInt() ?: 0}g Prot"
            tvProtein.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            tvProtein.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_green_dark))

            // Grasas - Amarillo/Dorado
            tvFat.text = "${recipe.fat?.toInt() ?: 0}g Fat"
            tvFat.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
            tvFat.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_orange_dark))

            // Sal - Gris/Verde
            tvSalt.text = "${recipe.salt?.toInt() ?: 0}g Salt"
            tvSalt.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            tvSalt.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_blue_dark))

            // Lógica de Expansión
            llNutrients.visibility = View.VISIBLE
            ivExpand.rotation = 0f

            ivExpand.setOnClickListener {
                val isCollapsed = tvRecipeDescription.maxLines <= 5

                if (isCollapsed) {
                    tvRecipeDescription.maxLines = Integer.MAX_VALUE
                    llNutrients.visibility = View.GONE
                    ivExpand.rotation = 180f
                } else {
                    val titleLines = tvRecipeName.lineCount
                    tvRecipeDescription.maxLines = when (titleLines) {
                        1 -> 5
                        2 -> 4
                        3 -> 3
                        else -> 2
                    }
                    llNutrients.visibility = View.VISIBLE
                    ivExpand.rotation = 0f
                }
            }

            // 5. Imagen con Glide
            Glide.with(ivRecipeImage.context)
                .load(getDrawableId(ivRecipeImage.context, recipe.image))
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(ivRecipeImage)

            root.setOnClickListener { onClick(recipe) }
        }
    }

    private fun getDrawableId(context: android.content.Context, imageName: String?): Int {
        if (imageName.isNullOrEmpty()) return R.drawable.placeholder_food
        val cleanName = imageName.substringAfterLast("/").substringBeforeLast(".")
        val resId = context.resources.getIdentifier(cleanName, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.placeholder_food
    }

    override fun getItemCount() = filteredRecipes.size

    fun filter(query: String) {
        filteredRecipes = if (query.isEmpty()) recipes
        else recipes.filter {
            it.name.lowercase().contains(query.lowercase()) ||
                    it.ingredients.lowercase().contains(query.lowercase()) ||
                    it.instructions.lowercase().contains(query.lowercase())
        }
        notifyDataSetChanged()
    }
}
