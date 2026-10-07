package com.example.techfix.data.remote

import com.example.techfix.data.model.ApiResponse
import com.example.techfix.data.model.LoginRequest
import com.example.techfix.data.model.RegisterRequest
import com.example.techfix.data.model.Servicio
import com.example.techfix.data.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz de Retrofit con las llamadas HTTP a la API PHP de TechFix.
 * Se utilizan funciones suspendibles (Coroutines) para peticiones asíncronas no bloqueantes.
 */
interface ApiService {

    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<Usuario>>

    @POST("register.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<Usuario>>

    @GET("get_servicios.php")
    suspend fun getServicios(): Response<ApiResponse<List<Servicio>>>
}
