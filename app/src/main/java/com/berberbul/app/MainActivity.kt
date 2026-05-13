package com.berberbul.app

import android.os.Bundle
import android.view.View
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

        // Gezinme çubuğu ve fragment geçişlerinin (Navigation Graph) birbirine bağlanması
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Sayfa değiştikçe alt menüyü dinamik olarak güncelleyen dinleyici
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // 1. Kapak ve Giriş Ekranları: Alt menü tamamen gizli
                R.id.selectionFragment, R.id.loginFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }

                // 2. Berber (Usta) Sayfaları: Berber menüsünü yükle
                R.id.berberAnaFragment, R.id.BerberProfilFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.clear()
                    // Az önce oluşturduğumuz menu_berber.xml dosyasını bağlıyoruz
                    bottomNavigationView.inflateMenu(R.menu.menu_berber)
                }

                // 3. Müşteri Sayfaları (Diğer tüm durumlar): Orijinal müşteri menüsünü yükle
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.clear()
                    // Müşterinin orijinal alt menü XML dosyasını yüklüyoruz (İsmini kontrol et)
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}