package com.example.techfix.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un servicio del catálogo de reparación.
 */
data class Servicio(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo_dispositivo") val tipoDispositivo: String, // 'celular', 'pc', 'tablet', 'todos'
    @SerializedName("precio_base") val precioBase: Double,
    @SerializedName("icono") val icono: String,
    @SerializedName("activo") val activo: Int
)
