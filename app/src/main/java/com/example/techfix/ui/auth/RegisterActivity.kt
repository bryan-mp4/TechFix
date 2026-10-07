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
import com.example.techfix.data.model.RegisterRequest
import com.example.techfix.data.remote.RetrofitClient
import com.example.techfix.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

/**
 * Pantalla de Registro de nuevo Usuario.
 * Valida los datos ingresados y los envía al endpoint register.php del backend.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            attemptRegister()
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Regresa a la LoginActivity previa
        }
    }

    private fun attemptRegister() {
        val nombre = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // 1. Validaciones del formulario
        if (nombre.isEmpty()) {
            binding.tilNombre.error = getString(R.string.error_field_required)
            return
        } else {
            binding.tilNombre.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_field_required)
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return
        } else {
            binding.tilEmail.error = null
        }

        if (telefono.isEmpty()) {
            binding.tilTelefono.error = getString(R.string.error_field_required)
            return
        } else {
            binding.tilTelefono.error = null
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

        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            return
        } else {
            binding.tilConfirmPassword.error = null
        }

        // 2. Enviar datos al servidor PHP con Coroutines
        showLoading(true)

        lifecycleScope.launch {
            try {
                val request = RegisterRequest(nombre, email, password, telefono)
                val response = RetrofitClient.apiService.register(request)
                showLoading(false)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                        // Guardar sesión e ir directamente a la pantalla principal
                        sessionManager.saveUserSession(apiResponse.data)
                        Toast.makeText(
                            this@RegisterActivity,
                            "¡Cuenta registrada con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = apiResponse?.message ?: "No se pudo completar el registro"
                        Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMsg = if (response.code() == 409) {
                        "El correo ya se encuentra registrado. Intenta con otro correo o inicia sesión."
                    } else {
                        "Error al registrar (${response.code()})"
                    }
                    Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.error_network),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.etNombre.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etTelefono.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.tvLoginLink.isEnabled = !isLoading
    }
}
