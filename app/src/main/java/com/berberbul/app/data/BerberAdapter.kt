package com.berberbul.app.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berberbul.app.R

class BerberAdapter(
    private var berberListesi: ArrayList<Berber>,
    private val onItemClick: (Berber) -> Unit
) : RecyclerView.Adapter<BerberAdapter.BerberViewHolder>() {

    class BerberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDukkanAdi: TextView = itemView.findViewById(R.id.tvDukkanAdi)
        val tvTelefon: TextView = itemView.findViewById(R.id.tvTelefon)
        val tvAdres: TextView = itemView.findViewById(R.id.tvAdres)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BerberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_berber_card, parent, false)
        return BerberViewHolder(view)
    }

    override fun onBindViewHolder(holder: BerberViewHolder, position: Int) {
        val berber = berberListesi[position]

        // Firestore'dan gelen güncel verileri ekrana basıyoruz
        holder.tvDukkanAdi.text = berber.dukkanAdi
        holder.tvTelefon.text = if (berber.telefon.isNotEmpty()) berber.telefon else "Telefon Belirtilmemiş"
        holder.tvAdres.text = if (berber.adres.isNotEmpty()) berber.adres else "Adres Belirtilmemiş"

        holder.itemView.setOnClickListener {
            onItemClick(berber)
        }
    }

    override fun getItemCount(): Int {
        return berberListesi.size
    }

    fun listeyiGuncelle(yeniListe: ArrayList<Berber>) {
        berberListesi.clear()
        berberListesi.addAll(yeniListe)
        notifyDataSetChanged()
    }
}