package com.mehmetalan.wordguess.data

import androidx.compose.ui.graphics.Color

const val max_no_of_words = 10

val fourWords: Set<String> =
    setOf(
        "Kütüphane",
        "Bilgisayar",
        "Müzik",
        "Deniz",
        "Çocukluk",
        "Karanlık",
        "Kütahya",
        "Yarasa",
        "Sabunluk",
        "Mimarlık",
        "İstiklal",
        "Elektrik",
        "Dolap",
        "Pencere",
        "Meraklı",
        "Otomobil",
        "Yaprak",
        "Akvaryum",
        "Baskı",
        "Zeytinlik",
        "Tatil",
        "Kombinasyon",
        "Bozukluk",
        "Tren istasyonu",
        "Ahlak"
    )

val colorList: List<Color> = listOf(
    Color(0xFF80DEEA), // Medium Cyan
    Color(0xFFF48FB1), // Medium Pink
    Color(0xFFE1BEE7), // Lavender
    Color(0xFFFFCDD2)  // Light Coral
)