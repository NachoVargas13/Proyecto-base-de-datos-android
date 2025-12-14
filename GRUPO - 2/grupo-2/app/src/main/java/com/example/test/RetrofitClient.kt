package com.example.test

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// 1. El Modelo
data class EstudianteRequest(
    @SerializedName("codigo_estudiante") val codigoEstudiante: String,
    val nombres: String,
    val edad: Int,
    val carrera: String,
    val activo: Boolean
)

// 2. La Interfaz
interface ApiService {
    @POST("api/estudiantes")
    suspend fun registrarEstudiante(@Body request: EstudianteRequest): retrofit2.Response<Void>
}

// 3. El Cliente (Singleton)
object RetrofitClient {
    private const val BASE_URL = "https://mi-api-mongo.vercel.app/" // Tu URL de Vercel

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}