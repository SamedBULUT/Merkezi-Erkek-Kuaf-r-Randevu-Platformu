package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BerberDukkanDuzenleFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_dukkan_duzenle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etDukkanAdi = view.findViewById<TextInputEditText>(R.id.etDukkanAdi)
        val etTelefon = view.findViewById<TextInputEditText>(R.id.etTelefon)
        val etAdres = view.findViewById<TextInputEditText>(R.id.etAdres)
        val btnKaydet = view.findViewById<Button>(R.id.btnKaydet)

        val uid = auth.currentUser?.uid

        // Sayfa açıldığında mevcut verileri EditText'lerin içine doldur
        if (uid != null) {
            db.collection("berberler").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etDukkanAdi.setText(document.getString("dukkanAdi"))
                        etTelefon.setText(document.getString("telefon"))
                        etAdres.setText(document.getString("adres"))
                    }
                }
        }

        // Kaydet butonuna basıldığında
        btnKaydet.setOnClickListener {
            val dukkanAdi = etDukkanAdi.text.toString().trim()
            val telefon = etTelefon.text.toString().trim()
            val adres = etAdres.text.toString().trim()

            if (dukkanAdi.isNotEmpty() && telefon.isNotEmpty() && adres.isNotEmpty()) {
                // Hizmetleri kaldırdık, sadece bu 3 alan var
                val berberMap = hashMapOf(
                    "dukkanAdi" to dukkanAdi,
                    "telefon" to telefon,
                    "adres" to adres,
                    "berberUid" to uid // Müşteri tarafında kolay çekmek için UID'yi içine de yazıyoruz
                )

                if (uid != null) {
                    db.collection("berberler").document(uid).set(berberMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Bilgiler başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                            // İşlem bitince bir önceki sayfaya (Dükkanım) otomatik geri dön
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Kayıt Hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}