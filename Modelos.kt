package meu.tempo.data.model

import kotlinx.serialization.Serializable

/**
 * Modelos da resposta da Timeline API da Visual Crossing.
 * Uma única chamada retorna condições atuais, horas e dias —
 * as três abas do app são alimentadas por este mesmo objeto.
 *
 * Todos os campos numéricos são anuláveis por segurança:
 * a API omite campos quando a estação não reporta o dado.
 */
@Serializable
data class RespostaTempo(
    val resolvedAddress: String? = null,
    val timezone: String? = null,
    val description: String? = null,
    val currentConditions: CondicoesAtuais? = null,
    val days: List<Dia> = emptyList()
)

@Serializable
data class CondicoesAtuais(
    val datetime: String? = null,       // "HH:mm:ss"
    val temp: Double? = null,
    val feelslike: Double? = null,
    val humidity: Double? = null,
    val dew: Double? = null,            // ponto de orvalho
    val precip: Double? = null,
    val precipprob: Double? = null,
    val windspeed: Double? = null,
    val windgust: Double? = null,
    val winddir: Double? = null,
    val pressure: Double? = null,
    val visibility: Double? = null,
    val cloudcover: Double? = null,
    val uvindex: Double? = null,
    val solarradiation: Double? = null,
    val conditions: String? = null,
    val icon: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null
)

@Serializable
data class Dia(
    val datetime: String = "",          // "yyyy-MM-dd"
    val tempmax: Double? = null,
    val tempmin: Double? = null,
    val temp: Double? = null,
    val precipprob: Double? = null,
    val precip: Double? = null,
    val conditions: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val humidity: Double? = null,
    val windspeed: Double? = null,
    val uvindex: Double? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val hours: List<Hora> = emptyList()
)

@Serializable
data class Hora(
    val datetime: String = "",          // "HH:mm:ss"
    val temp: Double? = null,
    val feelslike: Double? = null,
    val precipprob: Double? = null,
    val precip: Double? = null,
    val windspeed: Double? = null,
    val humidity: Double? = null,
    val conditions: String? = null,
    val icon: String? = null
)
