package com.berberbul.app.data

data class Berber(
    val id: Int = 0,
    val dukkanAdi: String = "",
    val enlem: Double = 0.0,
    val boylam: Double = 0.0,
    val ortalamaPuan: Float = 0f,
    val hizmetler: List<String> = emptyList(),
    val adres: String = "Adres bilgisi yok",
    val yorumSayisi: Int = 0,
    var musteriyeUzaklik: Double = 0.0
)