package com.example.techfix.data.model

import com.google.gson.annotations.SerializedName

/**
 * Objeto DTO enviado en el cuerpo de la petición de Inicio de Sesión.
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * Objeto DTO enviado en el cuerpo de la petición de Registro de Usuario.
 */
data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String
)
