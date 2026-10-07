package com.example.techfix.ui.servicios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.techfix.R
import com.example.techfix.data.remote.RetrofitClient
import com.example.techfix.databinding.FragmentServiciosBinding
import kotlinx.coroutines.launch

/**
 * Pantalla del Catálogo de Servicios.
 * Muestra un RecyclerView cargado vía Retrofit desde MySQL con SwipeRefresh y gestión de estados UI.
 */
class ServiciosFragment : Fragment() {

    private var _binding: FragmentServiciosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ServicioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        loadServicios()
    }

    private fun setupRecyclerView() {
        adapter = ServicioAdapter(emptyList())
        binding.rvServicios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvServicios.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            loadServicios(isSwipeToRefresh = true)
        }

        binding.btnRetry.setOnClickListener {
            loadServicios()
        }
    }

    private fun loadServicios(isSwipeToRefresh: Boolean = false) {
        if (!isSwipeToRefresh) {
            showState(State.LOADING)
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getServicios()
                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val servicios = apiResponse?.data ?: emptyList()

                    if (servicios.isNotEmpty()) {
                        adapter.updateData(servicios)
                        showState(State.SUCCESS)
                    } else {
                        showState(State.EMPTY)
                    }
                } else {
                    showState(State.ERROR, "Error al cargar servicios (${response.code()})")
                }
            } catch (e: Exception) {
                binding.swipeRefresh.isRefreshing = false
                showState(State.ERROR, getString(R.string.error_network))
            }
        }
    }

    private enum class State {
        LOADING, SUCCESS, EMPTY, ERROR
    }

    private fun showState(state: State, errorMessage: String? = null) {
        binding.progressBar.visibility = if (state == State.LOADING) View.VISIBLE else View.GONE
        binding.swipeRefresh.visibility = if (state == State.SUCCESS) View.VISIBLE else View.GONE
        binding.tvEmptyState.visibility = if (state == State.EMPTY) View.VISIBLE else View.GONE
        binding.layoutError.visibility = if (state == State.ERROR) View.VISIBLE else View.GONE

        if (state == State.ERROR && errorMessage != null) {
            binding.tvErrorMessage.text = errorMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
