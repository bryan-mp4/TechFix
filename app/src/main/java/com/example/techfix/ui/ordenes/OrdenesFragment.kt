package com.example.techfix.ui.ordenes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.techfix.databinding.FragmentOrdenesBinding

/**
 * Pantalla de órdenes de servicio (Placeholder para entregas posteriores).
 */
class OrdenesFragment : Fragment() {

    private var _binding: FragmentOrdenesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdenesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
