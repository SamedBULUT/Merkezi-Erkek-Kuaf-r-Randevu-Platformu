package com.berberbul.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_login.xml tasarımını ekrana bağlar
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase sınıflarını başlatıyoruz
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // SelectionFragment'tan gelen rolü alıyoruz ("berber" veya "musteri")
        val gelenRol = arguments?.getString("KULLANICI_ROLU") ?: "musteri"

        // XML tasarımındaki öğeleri buluyoruz
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

                        // Kullanıcının e-postasını ve rolünü bir paket (Map) yapıyoruz
                        val userMap = hashMapOf(
                            "email" to email,
                            "role" to gelenRol
                        )

                        // Firebase Firestore'da 'users' koleksiyonuna kaydediyoruz
                        db.collection("users").document(uid!!).set(userMap).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Kayıt Başarılı! Rol: $gelenRol", Toast.LENGTH_SHORT).show()
                            // Not: Burada daha sonra ana sayfaya yönlendirme kodu eklenecek
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
                        // Giriş başarılı olunca veritabanından kişinin rolünü çekiyoruz
                        db.collection("users").document(uid!!).get().addOnSuccessListener { document ->
                            val rol = document.getString("role")
                            Toast.makeText(requireContext(), "Giriş yapıldı. Rolün: $rol", Toast.LENGTH_SHORT).show()
                            // Not: Burada rolüne göre berber veya müşteri ana sayfasına yönlendirme yapılacak
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
}