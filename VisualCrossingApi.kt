package meu.tempo.data.api

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import meu.tempo.data.model.RespostaTempo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Timeline API da Visual Crossing.
 * Documentação: https://www.visualcrossing.com/resources/documentation/weather-api/timeline-weather-api/
 */
interface VisualCrossingApi {

    @GET("VisualCrossingWebServices/rest/services/timeline/{local}")
    suspend fun previsao(
        @Path("local") local: String,                    // "lat,lon"
        @Query("key") chave: String,
        @Query("unitGroup") unidades: String = "metric",
        @Query("lang") idioma: String = "pt",
        @Query("include") incluir: String = "current,hours,days",
        @Query("contentType") tipo: String = "json"
    ): RespostaTempo

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true   // a API retorna muitos campos além dos mapeados
            isLenient = true
        }

        fun criar(): VisualCrossingApi {
            val cliente = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://weather.visualcrossing.com/")
                .client(cliente)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(VisualCrossingApi::class.java)
        }
    }
}
