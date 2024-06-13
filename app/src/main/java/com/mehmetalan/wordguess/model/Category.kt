package com.mehmetalan.wordguess.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.graphics.vector.ImageVector
import com.mehmetalan.wordguess.R

data class Category(
    val categoryId: Int,
    val categoryName: String,
    val categoryImage: Int,
    var categoryIcon: ImageVector = Icons.Default.KeyboardArrowDown
)

val categoryList = listOf(
    Category(
        categoryId = 1,
        categoryName = "4 Harfli Kelimeler",
        categoryImage = R.drawable.four,

    ),
    Category(
        categoryId = 2,
        categoryName = "5 Harfli Kelimeler",
        categoryImage = R.drawable.five,
    ),
    Category(
        categoryId = 3,
        categoryName = "6 Harfli Kelimeler",
        categoryImage = R.drawable.six,
    ),
    Category(
        categoryId = 4,
        categoryName = "7 Harfli Kelimeler",
        categoryImage = R.drawable.seven,
    )
)
