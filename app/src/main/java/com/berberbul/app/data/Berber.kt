package com.berberbul.app.data

data class Berber(
    val id: Int,
    val dukkanAdi: String,
    val enlem: Double,      // Harita pin yerleşimi için
    val boylam: Double,     // Harita pin yerleşimi için
    val ortalamaPuan: Float,
    val hizmetler: List<String>
)