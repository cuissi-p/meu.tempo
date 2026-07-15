package meu.tempo

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import meu.tempo.ui.EstadoUi
import meu.tempo.ui.TempoViewModel
import meu.tempo.ui.screens.AbaAgora
import meu.tempo.ui.screens.AbaHoje
import meu.tempo.ui.screens.AbaProximosDias
import meu.tempo.ui.screens.TelaConfiguracoes
import meu.tempo.ui.theme.MeuTempoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeuTempoTheme {
                RaizApp()
            }
        }
    }
}

@Composable
fun RaizApp(viewModel: TempoViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsState()
    val chaveApi by viewModel.chaveApi.collectAsState()
    var mostrarConfig by rememberSaveable { mutableStateOf(false) }

    // Solicita a permissão de localização aproximada e recarrega ao obter resposta
    val pedirPermissao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.atualizar()
    }

    // Carga inicial: dispara o fluxo (o próprio ViewModel decide o estado)
    LaunchedEffect(Unit) {
        if (viewModel.temPermissaoLocalizacao()) {
            viewModel.atualizar()
        } else {
            pedirPermissao.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    if (mostrarConfig) {
        BackHandler { mostrarConfig = false }
        TelaConfiguracoes(
            chaveAtual = chaveApi,
            aoSalvar = { nova ->
                viewModel.salvarChave(nova)
                mostrarConfig = false
            },
            aoVoltar = { mostrarConfig = false }
        )
    } else {
        TelaPrincipal(
            estado = estado,
            aoAtualizar = { viewModel.atualizar(forcar = true) },
            aoAbrirConfig = { mostrarConfig = true },
            aoPedirPermissao = {
                pedirPermissao.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPrincipal(
    estado: EstadoUi,
    aoAtualizar: () -> Unit,
    aoAbrirConfig: () -> Unit,
    aoPedirPermissao: () -> Unit
) {
    val titulosAbas = listOf(
        stringResource(R.string.aba_agora),
        stringResource(R.string.aba_hoje),
        stringResource(R.string.aba_proximos)
    )
    val estadoPager = rememberPagerState { titulosAbas.size }
    val escopo = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = aoAtualizar) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.atualizar)
                        )
                    }
                    IconButton(onClick = aoAbrirConfig) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.configuracoes)
                        )
                    }
                }
            )
        }
    ) { recuo ->
        Column(modifier = Modifier.padding(recuo)) {
            when (estado) {
                is EstadoUi.Sucesso -> {
                    PrimaryTabRow(selectedTabIndex = estadoPager.currentPage) {
                        titulosAbas.forEachIndexed { indice, titulo ->
                            Tab(
                                selected = estadoPager.currentPage == indice,
                                onClick = {
                                    escopo.launch { estadoPager.animateScrollToPage(indice) }
                                },
                                text = { Text(titulo) }
                            )
                        }
                    }
                    HorizontalPager(
                        state = estadoPager,
                        modifier = Modifier.fillMaxSize()
                    ) { pagina ->
                        when (pagina) {
                            0 -> AbaAgora(estado.dados)
                            1 -> AbaHoje(estado.dados)
                            else -> AbaProximosDias(estado.dados)
                        }
                    }
                }

                is EstadoUi.Carregando -> Centralizado {
                    CircularProgressIndicator()
                }

                is EstadoUi.SemChave -> Centralizado {
                    Text(
                        "Configure a chave da API da Visual Crossing para começar.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = aoAbrirConfig) {
                        Text("Abrir configurações")
                    }
                }

                is EstadoUi.SemPermissao -> Centralizado {
                    Text(
                        "O app usa a localização aproximada do aparelho para " +
                            "buscar a previsão do seu local.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = aoPedirPermissao) {
                        Text("Permitir localização")
                    }
                }

                is EstadoUi.Erro -> Centralizado {
                    Text(
                        estado.mensagem,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = aoAtualizar) {
                        Text("Tentar novamente")
                    }
                }
            }
        }
    }
}

@Composable
private fun Centralizado(conteudo: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            conteudo()
        }
    }
}
