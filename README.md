# meu.tempo

App de previsão do tempo para Android (Kotlin + Jetpack Compose, Material 3),
alimentado pela Timeline API da Visual Crossing e pela localização do aparelho.

## Funcionalidades

- **Agora** — condições atuais completas: temperatura, sensação, umidade,
  ponto de orvalho, pressão, vento (velocidade, direção, rajadas),
  visibilidade, nebulosidade, UV, precipitação, nascer/pôr do sol.
- **Hoje** — o dia inteiro em intervalos de 3 horas.
- **Próximos dias** — máximas, mínimas e probabilidade de chuva para 10 dias.
- **Configurações** — campo ocultável (mascarado) para a chave da API,
  persistida em DataStore no armazenamento interno do app.

## Arquitetura

- MVVM com `StateFlow`; uma única chamada à Timeline API alimenta as três abas.
- Cache em memória de 15 min para preservar a cota gratuita (1000 registros/dia).
- Localização via `FusedLocationProviderClient` com apenas
  `ACCESS_COARSE_LOCATION` (precisão de bairro é suficiente para clima).
- `targetSdk 36` (Android 16); tema Material 3 com dynamic color,
  que no OneUI 8.5 acompanha a paleta do sistema.

## Como compilar

1. Abra a pasta no **Android Studio** (Ladybug ou mais recente).
2. Aguarde a sincronização do Gradle (baixa as dependências na primeira vez).
3. Conecte o aparelho com depuração USB ativa e rode (`Shift+F10`).

Ou por linha de comando, com o Android SDK configurado:

```bash
./gradlew assembleDebug
# APK em app/build/outputs/apk/debug/app-debug.apk
```

## Primeiro uso

1. Crie uma conta gratuita em https://www.visualcrossing.com/weather-api
   e copie sua chave.
2. Abra o app → ⚙️ Configurações → cole a chave → "Salvar e atualizar".
3. Conceda a permissão de localização aproximada quando solicitada.

## Pontos de atenção (código não compilado neste ambiente)

- Versões de dependências podem exigir ajuste conforme o Android Studio
  sugerir na sincronização (AGP/Kotlin/Compose BOM evoluem rápido).
- Erros de compilação, se houver, tendem a ser pontuais (imports,
  assinaturas de API do Compose). Um ciclo no Claude Code resolve rápido.

## Evoluções sugeridas

- Cache persistente (Room ou JSON bruto em DataStore) para uso offline.
- Ícones vetoriais no lugar dos emojis provisórios.
- Widget de tela inicial e atualização periódica via WorkManager.
- Busca manual de cidade como alternativa à localização.
