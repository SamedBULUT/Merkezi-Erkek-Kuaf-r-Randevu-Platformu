package com.berberbul.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berberbul.app.R
import com.berberbul.app.data.Berber

class BerberAdapter(private var berberListesi: List<Berber>) : RecyclerView.Adapter<BerberAdapter.BerberViewHolder>() {

    // Görünüm (layout) dosyasını şişirerek (inflate) ViewHolder nesnesini oluşturur.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BerberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_berber_card, parent, false)
        return BerberViewHolder(view)
    }

    // Listelenecek toplam öğe sayısını döndürür.
    override fun getItemCount(): Int {
        return berberListesi.size
    }

    // Veri setindeki öğeleri, ilgili ViewHolder içerisindeki UI bileşenlerine bağlar (Bind işlemi).
    override fun onBindViewHolder(holder: BerberViewHolder, position: Int) {
        val oAnkiBerber = berberListesi[position]

        holder.tvBerberAdi.text = oAnkiBerber.dukkanAdi
        holder.tvAdres.text = oAnkiBerber.adres
        holder.tvPuan.text = "⭐ ${oAnkiBerber.ortalamaPuan} (${oAnkiBerber.yorumSayisi} Yorum)"

        // Mesafe verisini dinamik olarak formatlayarak (metre veya km) arayüze yansıtır.
        if (oAnkiBerber.musteriyeUzaklik < 1.0) {
            val metre = (oAnkiBerber.musteriyeUzaklik * 1000).toInt()
            holder.tvMesafe.text = "$metre m"
        } else {
            val km = String.format("%.1f", oAnkiBerber.musteriyeUzaklik)
            holder.tvMesafe.text = "$km km"
        }
    }

    // Harici veri değişikliklerinde RecyclerView listesini dinamik olarak yeniler.
    fun listeyiGuncelle(yeniListe: List<Berber>) {
        berberListesi = yeniListe
        notifyDataSetChanged()
    }

    // Arayüz (UI) öğelerini tanımlayan ve referanslarını bellekte tutan statik ViewHolder sınıfı.
    class BerberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBerberAdi: TextView = itemView.findViewById(R.id.tvBerberAdi)
        val tvMesafe: TextView = itemView.findViewById(R.id.tvMesafe)
        val tvAdres: TextView = itemView.findViewById(R.id.tvAdres)
        val tvPuan: TextView = itemView.findViewById(R.id.tvPuan)
    }
}