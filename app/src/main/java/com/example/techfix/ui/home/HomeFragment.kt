package com.example.techfix.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.techfix.R
import com.example.techfix.data.local.SessionManager
import com.example.techfix.databinding.FragmentHomeBinding

/**
 * Pantalla de Inicio / Home con saludo personalizado e información de la empresa.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        val usuario = sessionManager.getUser()
        val nombre = usuario?.nombre ?: "Cliente"
        binding.tvWelcomeName.text = getString(R.string.welcome_message, nombre)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
