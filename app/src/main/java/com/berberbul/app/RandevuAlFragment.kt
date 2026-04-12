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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berberbul.app.data.Berber
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RandevuAlFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var secilenTarihBilgisi = ""

    private lateinit var rvBerberler: RecyclerView
    private lateinit var berberAdapter: BerberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_randevu_al, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arayüz bileşenlerinin tanımlanması
        val randevuButonu = view.findViewById<Button>(R.id.btnRandevuAl)
        val tvTarih = view.findViewById<TextView>(R.id.tvSecilenTarih)
        rvBerberler = view.findViewById(R.id.rvBerberList)

        // RecyclerView yapılandırması
        rvBerberler.layoutManager = LinearLayoutManager(requireContext())

        // Liste için statik test verileri (İlerleyen aşamalarda Firebase'den çekilecektir)
        val testBerberleri = listOf(
            Berber(id = 1, dukkanAdi = "MUZO", adres = "Kalkınma, Trabzon", ortalamaPuan = 4.8f, yorumSayisi = 120, musteriyeUzaklik = 1.2),
            Berber(id = 2, dukkanAdi = "iBoss", adres = "Kalkınma, Trabzon", ortalamaPuan = 4.5f, yorumSayisi = 85, musteriyeUzaklik = 3.5),
            Berber(id = 3, dukkanAdi = "Arzum Erkek Berberi", adres = "Kalkınma, Trabzon", ortalamaPuan = 4.2f, yorumSayisi = 40, musteriyeUzaklik = 0.8)
        )

        // Adapter bağlantısının sağlanması
        berberAdapter = BerberAdapter(testBerberleri)
        rvBerberler.adapter = berberAdapter

        // Tarih seçici (DatePicker) yapılandırması
        tvTarih?.setOnClickListener {
            val takvim = Calendar.getInstance()
            val yil = takvim.get(Calendar.YEAR)
            val ay = takvim.get(Calendar.MONTH)
            val gun = takvim.get(Calendar.DAY_OF_MONTH)

            val dpd = android.app.DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                secilenTarihBilgisi = "$year-${monthOfYear + 1}-$dayOfMonth"
                tvTarih.text = "Seçilen Tarih: $secilenTarihBilgisi"
            }, yil, ay, gun)

            dpd.show()
        }

        // Randevu oluşturma tetikleyicisi
        randevuButonu?.setOnClickListener {
            if (secilenTarihBilgisi.isEmpty()) {
                Toast.makeText(context, "Lütfen önce bir tarih seçin!", Toast.LENGTH_SHORT).show()
            } else {
                randevuKaydet()
            }
        }
    }

    // Firebase üzerinde saat çakışması kontrolü
    private fun randevuKaydet() {
        val secilenTarih = secilenTarihBilgisi
        val secilenSaat = "14:00"

        db.collection("randevular")
            .whereEqualTo("date", secilenTarih)
            .whereEqualTo("time", secilenSaat)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    gercekKaydiYap(secilenTarih, secilenSaat)
                } else {
                    Toast.makeText(context, "Üzgünüz, bu saat dolu! Başka bir saat seçin.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseBackend", "Sorgu hatası", e)
            }
    }


    // Başarılı doğrulamadan sonra veritabanına kayıt işlemi
    private fun gercekKaydiYap(tarih: String, saat: String) {


        val yeniRandevu = hashMapOf(
            "barberName" to "Kuafor Selim",
            "customerName" to "Müşteri Can",
            "date" to tarih,
            "time" to saat,
            "isConfirmed" to false
        )

        db.collection("randevular")
            .add(yeniRandevu)
            .addOnSuccessListener { documentReference ->
                db.collection("randevular").document(documentReference.id)
                    .update("id", documentReference.id)

                Toast.makeText(context, "Randevunuz Başarıyla Oluşturuldu!", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseBackend", "Yeni Randevu Oluştu: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseBackend", "Kayıt hatası", e)
            }
    }
}