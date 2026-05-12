package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val gelenRol = arguments?.getString("KULLANICI_ROLU") ?: "musteri"

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnGirisYap = view.findViewById<Button>(R.id.btnGirisYap)
        val btnKayitOl = view.findViewById<Button>(R.id.btnKayitOl)

        // KAYIT OL BUTONU
        btnKayitOl.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val userMap = hashMapOf(
                            "email" to email,
                            "role" to gelenRol
                        )

                        db.collection("users").document(uid!!).set(userMap).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                            sayfayaYonlendir(gelenRol) // Kayıt sonrası yönlendir
                        }.addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Veritabanı Hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Kayıt Hatası: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }

        // GİRİŞ YAP BUTONU
        btnGirisYap.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        db.collection("users").document(uid!!).get().addOnSuccessListener { document ->
                            val rol = document.getString("role")
                            Toast.makeText(requireContext(), "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                            sayfayaYonlendir(rol) // Giriş sonrası yönlendir
                        }
                    } else {
                        Toast.makeText(requireContext(), "Hatalı Giriş: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen e-posta ve şifrenizi girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sayfayaYonlendir(rol: String?) {
        try {
            if (rol == "berber") {
                // nav_graph'taki ok isminle aynı olmalı
                findNavController().navigate(R.id.action_loginFragment_to_RandevuAlFragment)
            } else {
                // nav_graph'taki ok isminle aynı olmalı (Harita sayfası)
                findNavController().navigate(R.id.action_loginFragment_to_profilFragment)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Navigasyon Hatası: Okları kontrol et!", Toast.LENGTH_SHORT).show()
        }
    }
}