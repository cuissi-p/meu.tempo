package meu.tempo.data

import meu.tempo.data.api.VisualCrossingApi
import meu.tempo.data.model.RespostaTempo

/**
 * Repositório: uma única chamada à Timeline API alimenta as três abas.
 * Mantém cache em memória para evitar requisições repetidas
 * (a cota gratuita da Visual Crossing é de 1000 registros/dia).
 */
class RepositorioTempo(
    private val api: VisualCrossingApi = VisualCrossingApi.criar()
) {
    private var cache: RespostaTempo? = null
    private var cacheInstante: Long = 0
    private var cacheLocal: String = ""

    /** Validade do cache: 15 minutos. */
    private val validadeMs = 15 * 60 * 1000L

    suspend fun buscar(latitude: Double, longitude: Double, chave: String, forcar: Boolean = false): RespostaTempo {
        val local = "%.4f,%.4f".format(latitude, longitude)
        val agora = System.currentTimeMillis()

        val emCache = cache
        if (!forcar && emCache != null && cacheLocal == local && agora - cacheInstante < validadeMs) {
            return emCache
        }

        val resposta = api.previsao(local = local, chave = chave)
        cache = resposta
        cacheInstante = agora
        cacheLocal = local
        return resposta
    }
}
