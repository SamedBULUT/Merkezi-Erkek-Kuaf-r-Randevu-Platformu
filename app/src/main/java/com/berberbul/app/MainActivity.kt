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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.selectionFragment, R.id.loginFragment, R.id.berberDukkanDuzenleFragment,
                R.id.musteriProfilDuzenleFragment, R.id.berberOnaylananlarFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.berberAnaFragment, R.id.berberDukkanimFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.clear()
                    bottomNavigationView.inflateMenu(R.menu.menu_berber)
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.clear()
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