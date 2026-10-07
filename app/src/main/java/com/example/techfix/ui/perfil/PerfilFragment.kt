package com.example.techfix.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.techfix.R
import com.example.techfix.data.local.SessionManager
import com.example.techfix.databinding.FragmentPerfilBinding
import com.example.techfix.ui.auth.LoginActivity

/**
 * Pantalla de Perfil de Usuario con opción de Cerrar Sesión.
 */
class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupUserInfo()
        setupListeners()
    }

    private fun setupUserInfo() {
        val usuario = sessionManager.getUser()
        if (usuario != null) {
            binding.tvNombreUsuario.text = usuario.nombre
            binding.tvEmailUsuario.text = getString(R.string.perfil_email_label, usuario.email)
            binding.tvTelefonoUsuario.text = getString(R.string.perfil_telefono_label, usuario.telefono)
            binding.tvRolUsuario.text = "ROL: ${usuario.rol.uppercase()}"
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            // Eliminar sesión guardada localmente
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

            // Redirigir a LoginActivity y limpiar pila de actividades
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
