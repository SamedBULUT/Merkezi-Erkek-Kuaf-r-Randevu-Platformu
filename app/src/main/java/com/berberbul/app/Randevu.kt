package com.berberbul.app

data class Randevu(
    val id: String = "",
    val barberName: String = "",
    val customerName: String = "",
    val date: String = "",
    val time: String = "",
    val isConfirmed: Boolean = false
)