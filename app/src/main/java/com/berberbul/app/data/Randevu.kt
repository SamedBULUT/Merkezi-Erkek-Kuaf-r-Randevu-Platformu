package com.berberbul.app.data

data class Randevu(
    val randevuId: String,
    val musteriId: String,
    val berberId: Int,
    val ustaAdi: String,
    val tarihSaat: Long,
    val durum: String // "Beklemede", "Onaylandı" vb.
)