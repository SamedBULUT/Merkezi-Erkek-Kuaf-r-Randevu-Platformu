package com.berberbul.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RandevuAdapter(
    private var randevuListesi: ArrayList<Randevu>,
    private val onIptalClick: (Randevu) -> Unit
) : RecyclerView.Adapter<RandevuAdapter.RandevuViewHolder>() {

    class RandevuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBerberAdi: TextView = itemView.findViewById(R.id.tvBerberAdi)
        val tvTarihSaat: TextView = itemView.findViewById(R.id.tvTarihSaat)
        val tvDurum: TextView = itemView.findViewById(R.id.tvDurum)
        val btnIptalEt: Button = itemView.findViewById(R.id.btnIptalEt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandevuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_randevu, parent, false)
        return RandevuViewHolder(view)
    }

    override fun onBindViewHolder(holder: RandevuViewHolder, position: Int) {
        val randevu = randevuListesi[position]
        holder.tvBerberAdi.text = randevu.barberName
        holder.tvTarihSaat.text = "${randevu.date} - ${randevu.time}"
        holder.tvDurum.text = randevu.status

        when (randevu.status) {
            "Bekliyor" -> {
                holder.tvDurum.setTextColor(Color.parseColor("#F39C12"))
                holder.btnIptalEt.visibility = View.VISIBLE
            }
            "Onaylandı" -> {
                holder.tvDurum.setTextColor(Color.parseColor("#27AE60"))
                holder.btnIptalEt.visibility = View.VISIBLE
            }
            "Onaylanmadı" -> {
                holder.tvDurum.setTextColor(Color.parseColor("#E74C3C"))
                holder.btnIptalEt.visibility = View.GONE
            }
            "İptal Edildi" -> {
                holder.tvDurum.setTextColor(Color.parseColor("#757575"))
                holder.btnIptalEt.visibility = View.GONE
            }
            else -> {
                holder.tvDurum.setTextColor(Color.parseColor("#757575"))
                holder.btnIptalEt.visibility = View.GONE
            }
        }

        holder.btnIptalEt.setOnClickListener {
            onIptalClick(randevu)
        }
    }

    override fun getItemCount(): Int {
        return randevuListesi.size
    }

    fun listeyiGuncelle(yeniListe: ArrayList<Randevu>) {
        randevuListesi.clear()
        randevuListesi.addAll(yeniListe)
        notifyDataSetChanged()
    }
}