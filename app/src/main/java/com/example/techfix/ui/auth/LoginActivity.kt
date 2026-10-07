package com.example.techfix.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.techfix.MainActivity
import com.example.techfix.R
import com.example.techfix.data.local.SessionManager
import com.example.techfix.data.model.LoginRequest
import com.example.techfix.data.remote.RetrofitClient
import com.example.techfix.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

/**
 * Pantalla de Inicio de Sesión.
 * Solicita correo y contraseña, valida las entradas y realiza la petición al backend PHP.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Validaciones del formulario
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_field_required)
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_field_required)
            return
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_short_password)
            return
        } else {
            binding.tilPassword.error = null
        }

        // 2. Ejecutar petición HTTP asíncrona con Coroutines
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                showLoading(false)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                        // Guardar la sesión localmente
                        sessionManager.saveUserSession(apiResponse.data)
                        Toast.makeText(
                            this@LoginActivity,
                            "¡Bienvenido, ${apiResponse.data.nombre}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a MainActivity limpiando el historial de pantallas de auth
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = apiResponse?.message ?: "Error al iniciar sesión"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMsg = if (response.code() == 401) {
                        "Credenciales incorrectas. Verifica tu correo y contraseña."
                    } else {
                        "Error en el servidor (${response.code()})"
                    }
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error_network),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.tvRegisterLink.isEnabled = !isLoading
    }
}
