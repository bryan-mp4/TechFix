package com.example.techfix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.techfix.databinding.ActivityMainBinding
import com.example.techfix.ui.home.HomeFragment
import com.example.techfix.ui.ordenes.OrdenesFragment
import com.example.techfix.ui.perfil.PerfilFragment
import com.example.techfix.ui.servicios.ServiciosFragment

/**
 * Pantalla Principal con BottomNavigationView.
 * Gestiona la navegación entre Inicio, Servicios, Mis Órdenes y Perfil.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Fragment por defecto
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_servicios -> {
                    replaceFragment(ServiciosFragment())
                    true
                }
                R.id.navigation_ordenes -> {
                    replaceFragment(OrdenesFragment())
                    true
                }
                R.id.navigation_perfil -> {
                    replaceFragment(PerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
