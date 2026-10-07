package com.example.techfix.data.model

import com.google.gson.annotations.SerializedName

/**
 * Envoltorio genérico para estandarizar las respuestas JSON de la API REST PHP.
 * Mapea la estructura: {"success": bool, "message": string, "data": T}
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null
)
