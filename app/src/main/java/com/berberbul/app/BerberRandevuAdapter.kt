package com.berberbul.app

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

        // Randevu sınıfındaki müşteri adı değişkenine göre (customerName) veriyi ekrana basıyoruz
        holder.tvMusteriAdi.text = randevu.customerName ?: "Müşteri"
        holder.tvZaman.text = "${randevu.date} - ${randevu.time}"

        // Onayla butonuna tıklama olayı
        holder.btnOnayla.setOnClickListener {
            onOnaylaClick(randevu)
        }

        // Reddet butonuna tıklama olayı
        holder.btnReddet.setOnClickListener {
            onReddetClick(randevu)
        }
    }

    override fun getItemCount(): Int {
        return randevuListesi.size
    }
}