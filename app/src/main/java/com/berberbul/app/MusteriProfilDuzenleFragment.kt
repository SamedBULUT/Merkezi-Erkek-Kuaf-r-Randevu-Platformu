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

class MusteriProfilDuzenleFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_musteri_profil_duzenle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etAdSoyad = view.findViewById<TextInputEditText>(R.id.etAdSoyad)
        val etTelefon = view.findViewById<TextInputEditText>(R.id.etTelefon)
        val btnKaydet = view.findViewById<Button>(R.id.btnKaydet)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        etAdSoyad.setText(document.getString("adSoyad"))
                        etTelefon.setText(document.getString("telefon"))
                    }
                }
        }

        btnKaydet.setOnClickListener {
            val adSoyad = etAdSoyad.text.toString().trim()
            val telefon = etTelefon.text.toString().trim()

            if (adSoyad.isNotEmpty() && telefon.isNotEmpty()) {
                val updateMap = mapOf(
                    "adSoyad" to adSoyad,
                    "telefon" to telefon
                )

                if (uid != null) {
                    db.collection("users").document(uid).update(updateMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profil güncellendi!", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}