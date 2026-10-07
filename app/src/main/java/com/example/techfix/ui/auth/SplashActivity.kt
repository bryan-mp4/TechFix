package com.example.techfix.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.techfix.MainActivity
import com.example.techfix.data.local.SessionManager
import com.example.techfix.databinding.ActivitySplashBinding

/**
 * Pantalla inicial de carga (Splash).
 * Verifica si existe una sesión activa y redirige a la pantalla correspondiente.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Breve animación/retardo de 1.5 segundos antes de redirigir
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 1500)
    }

    private fun checkSession() {
        val nextActivity = if (sessionManager.isLoggedIn()) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }

        val intent = Intent(this, nextActivity)
        startActivity(intent)
        finish() // Cierra el Splash para que no se pueda regresar con el botón atrás
    }
}
