package com.berberbul.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class BerberProfilFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var secilenTarihBilgisi = ""
    private var secilenSaatBilgisi = ""
    private var musteriAdSoyad = "Bilinmeyen Müşteri"
    private var berberUid = ""
    private var dukkanAdi = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        berberUid = arguments?.getString("SECILEN_BERBER_UID") ?: ""
        dukkanAdi = arguments?.getString("SECILEN_BERBER_AD") ?: ""

        val tvDukkanAdi = view.findViewById<TextView>(R.id.tvVitrindekiDukkanAdi)
        val tvTelefon = view.findViewById<TextView>(R.id.tvVitrindekiTelefon)
        val tvAdres = view.findViewById<TextView>(R.id.tvVitrindekiAdres)
        val btnTarihSec = view.findViewById<Button>(R.id.btnTarihSec)
        val btnSaatSec = view.findViewById<Button>(R.id.btnSaatSec)
        val btnRandevuAl = view.findViewById<Button>(R.id.btnRandevuAl)

        tvDukkanAdi.text = dukkanAdi

        if (berberUid.isNotEmpty()) {
            db.collection("berberler").document(berberUid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvTelefon.text = document.getString("telefon") ?: "Telefon Belirtilmemiş"
                        tvAdres.text = document.getString("adres") ?: "Adres Belirtilmemiş"
                    }
                }
        }

        val musteriUid = auth.currentUser?.uid
        if (musteriUid != null) {
            db.collection("users").document(musteriUid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        musteriAdSoyad = document.getString("adSoyad") ?: "Bilinmeyen Müşteri"
                    }
                }
        }

        btnTarihSec.setOnClickListener {
            val takvim = Calendar.getInstance()
            val dpd = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    secilenTarihBilgisi = "$year-${monthOfYear + 1}-$dayOfMonth"
                    btnTarihSec.text = secilenTarihBilgisi
                },
                takvim.get(Calendar.YEAR),
                takvim.get(Calendar.MONTH),
                takvim.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        btnSaatSec.setOnClickListener {
            val takvim = Calendar.getInstance()
            val tpd = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    secilenSaatBilgisi = String.format("%02d:%02d", hourOfDay, minute)
                    btnSaatSec.text = secilenSaatBilgisi
                },
                takvim.get(Calendar.HOUR_OF_DAY),
                takvim.get(Calendar.MINUTE),
                true
            )
            tpd.show()
        }

        btnRandevuAl.setOnClickListener {
            if (secilenTarihBilgisi.isEmpty() || secilenSaatBilgisi.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tarih ve saat seçin!", Toast.LENGTH_SHORT).show()
            } else {
                randevuKaydet()
            }
        }
    }

    private fun randevuKaydet() {
        db.collection("randevular")
            .whereEqualTo("berberId", berberUid)
            .whereEqualTo("date", secilenTarihBilgisi)
            .whereEqualTo("time", secilenSaatBilgisi)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    gercekKaydiYap()
                } else {
                    Toast.makeText(context, "Üzgünüz, bu saat dolu!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun gercekKaydiYap() {
        val musteriUid = auth.currentUser?.uid ?: ""

        val yeniRandevu = hashMapOf(
            "barberName" to dukkanAdi,
            "berberId" to berberUid,
            "customerName" to musteriAdSoyad,
            "musteriId" to musteriUid,
            "date" to secilenTarihBilgisi,
            "time" to secilenSaatBilgisi,
            "status" to "Bekliyor"
        )

        db.collection("randevular")
            .add(yeniRandevu)
            .addOnSuccessListener { documentReference ->
                db.collection("randevular").document(documentReference.id)
                    .update("id", documentReference.id)

                Toast.makeText(context, "Randevu Talebi Gönderildi!", Toast.LENGTH_SHORT).show()
            }
    }
}