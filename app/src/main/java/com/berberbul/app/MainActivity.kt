package com.berberbul.app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Müşteri Arayüzü: Alt gezinme çubuğu (Bottom Navigation) ve fragment geçişlerinin (Navigation Graph) birbirine bağlanması
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        randevuOlustur()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun randevuOlustur() {
        val yeniRandevu = Randevu(
            barberName = "Kuafor Selim",
            customerName = "Müşteri Can",
            date = "2026-03-15",
            time = "10:30",
            isConfirmed = false
        )

        db.collection("randevular")
            .add(yeniRandevu)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseBackend", "Randevu ID: ${documentReference.id}")
                db.collection("randevular").document(documentReference.id)
                    .update("id", documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseBackend", "Hata olustu!", e)
            }
    }
}