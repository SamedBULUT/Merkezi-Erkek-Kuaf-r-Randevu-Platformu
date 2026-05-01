package com.berberbul.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
    private var secilenSaatBilgisi = ""

    private var berberId: Int = 0
    private var dukkanAdi: String = ""
    private var enlem: Double = 0.0
    private var boylam: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val tvSaat = view.findViewById<TextView>(R.id.tvProfilSaat)
        val btnRandevu = view.findViewById<Button>(R.id.btnProfilRandevuAl)

        tvTarih.setOnClickListener {
            val takvim = Calendar.getInstance()
            val dpd = DatePickerDialog(requireContext(), { _, year, month, day ->
                secilenTarihBilgisi = "$year-${month + 1}-$day"
                tvTarih.text = "Seçilen Tarih: $secilenTarihBilgisi"
                secilenSaatBilgisi = ""
                tvSaat.text = "Saat Seçmek İçin Dokunun"
            }, takvim.get(Calendar.YEAR), takvim.get(Calendar.MONTH), takvim.get(Calendar.DAY_OF_MONTH))

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        tvSaat.setOnClickListener {
            if (secilenTarihBilgisi.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen önce bir tarih seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val suAn = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, secilenSaat, secilenDakika ->
                val bugunString = "${suAn.get(Calendar.YEAR)}-${suAn.get(Calendar.MONTH) + 1}-${suAn.get(Calendar.DAY_OF_MONTH)}"

                if (secilenTarihBilgisi == bugunString) {
                    val suAnkiSaat = suAn.get(Calendar.HOUR_OF_DAY)
                    val suAnkiDakika = suAn.get(Calendar.MINUTE)

                    if (secilenSaat < suAnkiSaat || (secilenSaat == suAnkiSaat && secilenDakika < suAnkiDakika)) {
                        Toast.makeText(requireContext(), "Geçmiş bir saate randevu alamazsınız!", Toast.LENGTH_LONG).show()
                        return@TimePickerDialog
                    }
                }

                secilenSaatBilgisi = String.format("%02d:%02d", secilenSaat, secilenDakika)
                tvSaat.text = "Seçilen Saat: $secilenSaatBilgisi"

            }, suAn.get(Calendar.HOUR_OF_DAY), suAn.get(Calendar.MINUTE), true).show()
        }

        btnRandevu.setOnClickListener {
            if (secilenTarihBilgisi.isEmpty() || secilenSaatBilgisi.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen hem tarih hem de saat seçin!", Toast.LENGTH_SHORT).show()
            } else {
                randevuKaydet()
            }
        }
    }

    private fun randevuKaydet() {
        Log.d("BackendTest", "Randevu sorgusu başladı -> Tarih: $secilenTarihBilgisi, Saat: $secilenSaatBilgisi")

        db.collection("randevular")
            .whereEqualTo("date", secilenTarihBilgisi)
            .whereEqualTo("time", secilenSaatBilgisi)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    gercekKaydiYap(secilenTarihBilgisi, secilenSaatBilgisi)
                } else {
                    Toast.makeText(requireContext(), "Bu saat dolu!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BackendTest", "Firebase Sorgu Hatası", exception)
                Toast.makeText(requireContext(), "Sorgu Hatası: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
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

        db.collection("randevular").add(yeniRandevu)
            .addOnSuccessListener {
                Log.d("BackendTest", "Kayıt BAŞARILI!")
                Toast.makeText(requireContext(), "Randevu Başarıyla Alındı!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("BackendTest", "Kayıt Yazma Hatası", exception)
                Toast.makeText(requireContext(), "Kayıt Hatası: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}