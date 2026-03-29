package com.berberbul.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class RandevuAlFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevu_al, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val randevuButonu = view.findViewById<Button>(R.id.btnRandevuAl)


        randevuButonu?.setOnClickListener {
            randevuKaydet()
        }
    }


    private fun randevuKaydet() {
        val secilenTarih = "2026-03-30"
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