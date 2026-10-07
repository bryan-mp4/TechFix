package com.example.techfix.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.techfix.data.model.Usuario
import com.google.gson.Gson

/**
 * Gestor de Sesión local utilizando SharedPreferences.
 * Permite guardar, verificar y recuperar la sesión del usuario persistida en el dispositivo.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "techfix_session_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_DATA = "user_data"
    }

    /**
     * Guarda la sesión del usuario convirtiendo el objeto Usuario a JSON.
     */
    fun saveUserSession(usuario: Usuario) {
        val userJson = gson.toJson(usuario)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_DATA, userJson)
            .apply()
    }

    /**
     * Indica si hay un usuario con sesión activa en la app.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Recupera el objeto Usuario almacenado en la sesión.
     */
    fun getUser(): Usuario? {
        val userJson = prefs.getString(KEY_USER_DATA, null) ?: return null
        return try {
            gson.fromJson(userJson, Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina los datos guardados para cerrar la sesión actual.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
