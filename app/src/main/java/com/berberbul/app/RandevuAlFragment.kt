package com.berberbul.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RandevuAlFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    // YENİ: Seçilen tarihi saklamak için değişken
    private var secilenTarihBilgisi = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevu_al, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val randevuButonu = view.findViewById<Button>(R.id.btnRandevuAl)
        // YENİ: XML'e eklediğimiz tarih yazısını buluyoruz
        val tvTarih = view.findViewById<TextView>(R.id.tvSecilenTarih)

        // YENİ: Tarih yazısına tıklandığında takvim açılsın
        tvTarih?.setOnClickListener {
            val takvim = Calendar.getInstance()
            val yil = takvim.get(Calendar.YEAR)
            val ay = takvim.get(Calendar.MONTH)
            val gun = takvim.get(Calendar.DAY_OF_MONTH)

            val dpd = android.app.DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                // Seçilen tarihi formatlıyoruz (Örn: 2026-3-31)
                secilenTarihBilgisi = "$year-${monthOfYear + 1}-$dayOfMonth"
                tvTarih.text = "Seçilen Tarih: $secilenTarihBilgisi"
            }, yil, ay, gun)

            dpd.show()
        }

        randevuButonu?.setOnClickListener {
            // YENİ: Tarih seçilip seçilmediğini kontrol ediyoruz
            if (secilenTarihBilgisi.isEmpty()) {
                Toast.makeText(context, "Lütfen önce bir tarih seçin!", Toast.LENGTH_SHORT).show()
            } else {
                randevuKaydet()
            }
        }
    }

    private fun randevuKaydet() {
        // GÜNCELLENDİ: Artık sabit tarih yerine yukarıdaki değişkeni kullanıyoruz
        val secilenTarih = secilenTarihBilgisi
        val secilenSaat = "14:00"

        db.collection("randevular")
            .whereEqualTo("date", secilenTarih)
            .whereEqualTo("time", secilenSaat)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // SAAT BOŞ: Kaydı başlat
                    gercekKaydiYap(secilenTarih, secilenSaat)
                } else {
                    // SAAT DOLU: Uyarı ver
                    Toast.makeText(
                        context,
                        "Üzgünüz, bu saat dolu! Başka bir saat seçin.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseBackend", "Sorgu hatası", e)
            }
    }

    private fun gercekKaydiYap(tarih: String, saat: String) {
        val yeniRandevu = Randevu(
            barberName = "Kuafor Selim",
            customerName = "Müşteri Can",
            date = tarih,
            time = saat,
            isConfirmed = false
        )

        db.collection("randevular")
            .add(yeniRandevu)
            .addOnSuccessListener { documentReference ->
                db.collection("randevular").document(documentReference.id)
                    .update("id", documentReference.id)

                Toast.makeText(context, "Randevunuz Başarıyla Oluşturuldu!", Toast.LENGTH_SHORT)
                    .show()
                Log.d("FirebaseBackend", "Yeni Randevu Oluştu: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseBackend", "Kayıt hatası", e)
            }
    }
}