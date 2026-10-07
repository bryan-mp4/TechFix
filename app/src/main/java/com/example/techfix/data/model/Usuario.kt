package com.example.techfix.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa a un usuario autenticado en TechFix.
 */
data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("rol") val rol: String, // 'cliente', 'tecnico', 'admin'
    @SerializedName("fecha_registro") val fechaRegistro: String? = null
)
