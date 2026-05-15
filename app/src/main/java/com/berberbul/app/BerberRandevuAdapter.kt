package com.berberbul.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BerberRandevuAdapter(
    private val randevuListesi: List<Randevu>,
    private val onOnaylaClick: (Randevu) -> Unit,
    private val onReddetClick: (Randevu) -> Unit
) : RecyclerView.Adapter<BerberRandevuAdapter.BerberRandevuViewHolder>() {

    class BerberRandevuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMusteriAdi: TextView = view.findViewById(R.id.tvMusteriAdi)
        val tvMusteriTel: TextView = view.findViewById(R.id.tvMusteriTel)
        val tvZaman: TextView = view.findViewById(R.id.tvBerberRandevuZamani)
        val btnOnayla: Button = view.findViewById(R.id.btnOnayla)
        val btnReddet: Button = view.findViewById(R.id.btnReddet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BerberRandevuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berber_randevu, parent, false)
        return BerberRandevuViewHolder(view)
    }

    override fun onBindViewHolder(holder: BerberRandevuViewHolder, position: Int) {
        val randevu = randevuListesi[position]

        holder.tvMusteriAdi.text = randevu.customerName ?: "Müşteri"
        holder.tvMusteriTel.text = randevu.customerPhone ?: "Telefon Yok"

        when (randevu.status) {
            "İptal Edildi" -> {
                holder.tvZaman.setTextColor(Color.RED)
                holder.tvZaman.text = "${randevu.date} - ${randevu.time} (İPTAL EDİLDİ)"
                holder.btnOnayla.visibility = View.GONE
                holder.btnReddet.visibility = View.GONE
            }
            "Onaylandı" -> {
                holder.tvZaman.setTextColor(Color.parseColor("#27AE60"))
                holder.tvZaman.text = "${randevu.date} - ${randevu.time} (Onaylı)"
                holder.btnOnayla.visibility = View.GONE
                holder.btnReddet.visibility = View.GONE
            }
            else -> {
                holder.tvZaman.setTextColor(Color.GRAY)
                holder.tvZaman.text = "${randevu.date} - ${randevu.time}"
                holder.btnOnayla.visibility = View.VISIBLE
                holder.btnReddet.visibility = View.VISIBLE
            }
        }

        holder.btnOnayla.setOnClickListener {
            onOnaylaClick(randevu)
        }

        holder.btnReddet.setOnClickListener {
            onReddetClick(randevu)
        }
    }

    override fun getItemCount(): Int {
        return randevuListesi.size
    }
}