package com.berberbul.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BerberProfilFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var secilenTarihBilgisi = ""

    private var berberId: Int = 0
    private var dukkanAdi: String = ""
    private var enlem: Double = 0.0
    private var boylam: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_berber_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            berberId = it.getInt("berberId")
            dukkanAdi = it.getString("dukkanAdi") ?: ""
            enlem = it.getDouble("enlem")
            boylam = it.getDouble("boylam")
            val adres = it.getString("adres") ?: ""

            view.findViewById<TextView>(R.id.tvProfilDukkanAdi).text = dukkanAdi
            val tvAdres = view.findViewById<TextView>(R.id.tvProfilAdres)
            tvAdres.text = "Adres: $adres\n(Haritada görmek için dokun)"

            tvAdres.setOnClickListener {
                val uriStr = "geo:$enlem,$boylam?q=$enlem,$boylam($dukkanAdi)"
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
                mapIntent.setPackage("com.google.android.apps.maps")

                try {
                    startActivity(mapIntent)
                } catch (e: Exception) {
                    val tarayiciIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$enlem,$boylam"))
                    startActivity(tarayiciIntent)
                }
            }
        }

        val tvTarih = view.findViewById<TextView>(R.id.tvProfilTarih)
        val btnRandevu = view.findViewById<Button>(R.id.btnProfilRandevuAl)

        tvTarih.setOnClickListener {
            val takvim = Calendar.getInstance()
            val dpd = android.app.DatePickerDialog(requireContext(), { _, year, month, day ->
                secilenTarihBilgisi = "$year-${month + 1}-$day"
                tvTarih.text = "Seçilen Tarih: $secilenTarihBilgisi"
            }, takvim.get(Calendar.YEAR), takvim.get(Calendar.MONTH), takvim.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        btnRandevu.setOnClickListener {
            if (secilenTarihBilgisi.isEmpty()) {
                Toast.makeText(context, "Lütfen tarih seçin!", Toast.LENGTH_SHORT).show()
            } else {
                randevuKaydet()
            }
        }
    }

    private fun randevuKaydet() {
        val secilenSaat = "14:00"

        db.collection("randevular")
            .whereEqualTo("date", secilenTarihBilgisi)
            .whereEqualTo("time", secilenSaat)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) gercekKaydiYap(secilenTarihBilgisi, secilenSaat)
                else Toast.makeText(context, "Bu saat dolu!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun gercekKaydiYap(tarih: String, saat: String) {
        val yeniRandevu = hashMapOf(
            "berberId" to berberId,
            "barberName" to dukkanAdi,
            "customerName" to "Müşteri Can",
            "date" to tarih,
            "time" to saat,
            "isConfirmed" to false
        )

        db.collection("randevular").add(yeniRandevu).addOnSuccessListener {
            Toast.makeText(context, "Randevu Başarıyla Alındı!", Toast.LENGTH_SHORT).show()
        }
    }
}