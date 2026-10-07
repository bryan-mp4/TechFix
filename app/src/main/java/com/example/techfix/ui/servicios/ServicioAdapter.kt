package com.example.techfix.ui.servicios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.techfix.data.model.Servicio
import com.example.techfix.databinding.ItemServicioBinding
import java.text.NumberFormat
import java.util.Locale

/**
 * RecyclerView Adapter para mostrar las tarjetas del catálogo de servicios.
 */
class ServicioAdapter(
    private var servicios: List<Servicio>
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    private val copFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    class ServicioViewHolder(val binding: ItemServicioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val binding = ItemServicioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]
        with(holder.binding) {
            tvNombreServicio.text = servicio.nombre
            tvDescripcionServicio.text = servicio.descripcion
            tvTipoDispositivo.text = servicio.tipoDispositivo.uppercase()
            tvPrecioServicio.text = "${copFormat.format(servicio.precioBase)} COP"
        }
    }

    override fun getItemCount(): Int = servicios.size

    fun updateData(newServicios: List<Servicio>) {
        this.servicios = newServicios
        notifyDataSetChanged()
    }
}
