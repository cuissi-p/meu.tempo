package meu.tempo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias")

/**
 * Armazena a chave da API da Visual Crossing.
 *
 * Nota: DataStore grava em texto claro no armazenamento interno do app
 * (protegido pelo sandbox do Android). Para um app pessoal é suficiente;
 * se quiser criptografia em repouso, o caminho é envelopar o valor com
 * uma chave do Android Keystore antes de gravar.
 */
class PreferenciasUsuario(private val contexto: Context) {

    private val chaveApiKey = stringPreferencesKey("chave_api")

    val chaveApi: Flow<String> = contexto.dataStore.data.map { prefs ->
        prefs[chaveApiKey] ?: ""
    }

    suspend fun salvarChaveApi(valor: String) {
        contexto.dataStore.edit { prefs ->
            prefs[chaveApiKey] = valor.trim()
        }
    }
}
