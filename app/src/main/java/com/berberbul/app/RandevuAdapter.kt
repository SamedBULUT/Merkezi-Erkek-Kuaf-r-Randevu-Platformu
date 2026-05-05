package com.berberbul.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RandevuAdapter(private val randevuListesi: List<Randevu>) : RecyclerView.Adapter<RandevuAdapter.RandevuViewHolder>() {

    class RandevuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBerberAdi: TextView = view.findViewById(R.id.tvRandevuBerberAdi)
        val tvTarihSaat: TextView = view.findViewById(R.id.tvRandevuTarihSaat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandevuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_randevu, parent, false)
        return RandevuViewHolder(view)
    }

    override fun onBindViewHolder(holder: RandevuViewHolder, position: Int) {
        val randevu = randevuListesi[position]
        holder.tvBerberAdi.text = randevu.barberName
        holder.tvTarihSaat.text = "${randevu.date} - ${randevu.time}"
    }

    override fun getItemCount(): Int {
        return randevuListesi.size
    }
}