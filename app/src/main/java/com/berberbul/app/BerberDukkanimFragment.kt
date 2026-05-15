package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BerberDukkanimFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berber_dukkanim, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvDukkanAdi = view.findViewById<TextView>(R.id.tvDukkanAdi)
        val tvTelefon = view.findViewById<TextView>(R.id.tvTelefon)
        val tvAdres = view.findViewById<TextView>(R.id.tvAdres)
        val btnDuzenle = view.findViewById<Button>(R.id.btnDuzenle)


        btnDuzenle.setOnClickListener {
            findNavController().navigate(R.id.action_berberDukkanimFragment_to_berberDukkanDuzenleFragment)
        }

        // Firestore'dan berberin kendi bilgilerini çekme
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("berberler").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvDukkanAdi.text = document.getString("dukkanAdi") ?: "Belirtilmemiş"
                        tvTelefon.text = document.getString("telefon") ?: "Belirtilmemiş"
                        tvAdres.text = document.getString("adres") ?: "Belirtilmemiş"
                    } else {
                        tvDukkanAdi.text = "Henüz dükkan bilgisi girmediniz."
                        tvTelefon.text = "-"
                        tvAdres.text = "-"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Veriler alınamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}