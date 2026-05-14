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

class ProfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvAdSoyad = view.findViewById<TextView>(R.id.tvAdSoyad)
        val tvTelefon = view.findViewById<TextView>(R.id.tvTelefon)
        val btnDuzenle = view.findViewById<Button>(R.id.btnDuzenle)

        btnDuzenle.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_musteriProfilDuzenleFragment)
        }

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvAdSoyad.text = document.getString("adSoyad") ?: "Belirtilmemiş"
                        tvTelefon.text = document.getString("telefon") ?: "Belirtilmemiş"
                    } else {
                        tvAdSoyad.text = "Bilgi yok"
                        tvTelefon.text = "Bilgi yok"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}