# AGENTS.md - Answ-English

## Visao Geral

Este repositorio e um app Android nativo em Kotlin para pratica de conversacao em ingles. O app se chama `Answ English`, usa `applicationId` e `namespace` `com.answering`, e organiza a experiencia em perguntas por nivel, exemplos de resposta, leitura por voz, traducao automatica, automacao de perguntas, moedas, anuncios e gravacao de video com camera frontal sobreposta.

O app nao usa Jetpack Compose. A UI atual e baseada em XML, `ViewBinding` e `DataBinding`.

## Regras Criticas

- Sempre preserve o namespace `com.answering`.
- Nunca reintroduza o namespace antigo `AnsweringAPP`.
- Nao remova o plugin `kotlin-kapt`; ele e necessario para o DataBinding gerar as classes de binding.
- Antes de mexer em layout usado por binding, confira se os IDs continuam batendo com o Kotlin.
- Nao trate strings de perguntas como traducoes normais: a maior parte das perguntas e exemplos em ingles esta marcada com `translatable="false"` porque o app usa ML Kit para traduzir em runtime.
- Nao mova ou renomeie activities sem atualizar `AndroidManifest.xml`, layouts com `tools:context`, imports e referencias de binding.
- Nao exponha credenciais de assinatura em mensagens, docs ou logs. O `app/build.gradle` atual tem uma signing config local hardcoded e deve ser tratado como sensivel.

## Stack e Build

- Android Gradle Plugin: `8.13.0`.
- Kotlin Gradle plugin: `2.2.21`.
- `compileSdk`: `36`.
- `targetSdk`: `36`.
- `minSdk`: `23`.
- Java/Kotlin target: JVM 1.8.
- JDK configurado no `gradle.properties`: Microsoft JDK 17 em `C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot`.
- Repositorios: `google()`, `mavenCentral()` e JitPack.
- Build features ativas: `viewBinding`, `dataBinding`, `buildConfig`.
- CameraX deve ficar em versao compativel com pagina de memoria de 16 KB. A versao antiga `1.3.4` empacota `libimage_processing_util_jni.so` com alinhamento ELF de 4 KB e bloqueia updates no Google Play para apps target Android 15+. A versao atual `1.5.2` foi validada com `Alignment: 16384`.

Comandos comuns no PowerShell:

```powershell
.\gradlew.bat clean assembleDebug --stacktrace
.\gradlew.bat installDebug
.\gradlew.bat bundleRelease --stacktrace
adb devices
adb logcat -s com.answering:V AndroidRuntime:E
```

O APK debug sai em `app/build/outputs/apk/debug/app-debug.apk`.
O AAB release sai em `app/build/outputs/bundle/release/app-release.aab`.

Observacao: `settings.gradle` inclui `:nativetemplates`, mas a pasta `nativetemplates` nao existe neste checkout. Verifique isso antes de assumir que esse modulo esta ativo.

## Estrutura Principal

```text
app/src/main/java/com/answering/
  activities/
    MainActivity.kt
    beginner.kt
    settingsScreen.kt
    AutomaticQuestions.kt
  dados/
    Questions.kt
    localSqlDatabase.kt
  funcoes/
    ApiCall.kt
    DailyCoins.kt
    DialogShow.kt
    Instructions.kt
    Translate.kt
    popup.kt
    rewardedAd.kt
    settingsSQL.kt
    showQuestions.kt
    textToSpeak.kt
```

Layouts principais:

- `activity_main.xml`: home e escolha de niveis.
- `beginner.xml`: tela principal de pratica.
- `layout-h360dp/beginner.xml`, `layout-h480dp/beginner.xml`, `layout-h600dp/beginner.xml`: variantes por altura da tela.
- `activity_settings_screen.xml`: configuracao de timers.
- `activity_automatic_questions.xml`: activity placeholder.
- `dialog_info.xml`, `dialog_review.xml`, `popup.xml`: dialogs customizados.

## Fluxo do App

`MainActivity`:

- Esconde a action bar.
- Inicializa o modelo de traducao com `Translate(this).download(this)`.
- Abre/cria o SQLite local por `localSqlDatabase`.
- Chama `initializeRow()` e consulta a linha principal.
- Exibe o primeiro dialog quando `FIRSTACESS` indica primeiro acesso.
- Inicializa AdMob e carrega banner.
- Abre a tela `beginner` com extra `level`: `basic`, `begInterm`, `advanced` ou `allquestions`.
- Abre `SettingsScreen`.
- Compartilha link da Play Store usando `ShareCompat`.

`beginner`:

- E a tela central de pratica.
- Inicializa CameraX com camera frontal.
- Carrega banco local, moedas, dias de uso, instrucoes, anuncios e rewarded ad.
- Seleciona o conjunto de perguntas conforme o extra `level`.
- Garante timer minimo de 5 segundos para automacao.
- Chama `textToSpeak.TTS(...)` para navegacao, TTS, traducao e automacao.
- Usa `HBRecorder` + `MediaProjection` para gravar a tela.
- Usa CameraX para mostrar preview frontal quando a gravacao esta ativa.
- Ao concluir gravacao, atualiza galeria, consome moeda e tenta abrir dialog de review quando as condicoes atuais forem atendidas.
- Compartilha o video gerado via `Intent.ACTION_SEND` e `FileProvider`.

`SettingsScreen`:

- Carrega os timers atuais do SQLite com `settingsSQL().recoverTimer`.
- Atualiza os tempos de cada nivel quando todos os campos estao preenchidos.
- Tambem carrega banner AdMob.

`AutomaticQuestions`:

- Hoje so infla `ActivityAutomaticQuestionsBinding`; nao ha regra de negocio implementada nela.

## Banco Local e Moedas

O banco local usa `SQLiteOpenHelper` em `localSqlDatabase.kt`.

- Database/table name: `answering`.
- Versao atual: `5`.
- Tabela principal: tambem chamada `answering`.
- O app trabalha como se existisse uma unica linha principal com `ID = 1`.
- `initializeRow()` insere a linha inicial com 20 moedas e timers de 9 segundos.

Colunas importantes:

- `COINS`: saldo de moedas.
- `FIRSTACESS`: controla dialogs iniciais/review.
- `TM_BASIC`, `TM_INTERM`, `TM_ADVANC`, `TM_BEG_INTERM`, `TM_ALLQUESTIONS`: timers da automacao por nivel.
- `DIA_ATUAL`: ultimo dia contabilizado para recompensa diaria.
- `DIAS_USO`: quantidade de dias de uso.

Particularidade importante: varias partes do codigo ainda acessam colunas por indice (`cursor.getString(2)`, `cursor.getString(4)`, `cursor.getString(10)`, `cursor.getString(11)`). Portanto, a ordem das colunas no schema e comportamento real do app. Se mudar o schema, atualize esses acessos ou migre para `getColumnIndexOrThrow`.

Regras atuais de moedas:

- Nova linha inicial: 20 moedas.
- Recompensa diaria: +2 moedas quando muda o dia.
- Automacao de perguntas: custa 1 moeda ao iniciar.
- Gravacao de video: custa 1 moeda ao concluir a gravacao.
- Rewarded ad: +3 moedas quando o usuario ganha a recompensa.

## Perguntas e Niveis

`Questions.kt` monta arrays a partir de `strings.xml`.

- `Basic`: perguntas basicas com exemplos.
- `Intermediate`: perguntas intermediarias com exemplos.
- `Advanced`: perguntas avancadas, geralmente com hint `****`.
- `begInterm`: `Basic + Intermediate`.
- `allLevels`: `Basic + Intermediate + Advanced`.

Nem toda string de pergunta declarada em `strings.xml` esta necessariamente ativa nos arrays. Ao adicionar, remover ou renumerar perguntas, altere tanto os recursos de string quanto os arrays em `Questions.kt`.

Na tela de pratica, as perguntas sao embaralhadas com `shuffle()`.

## TTS e Automacao

O Text-to-Speech usa `Locale.US` e uma variavel global `mTTS` definida em `showQuestions.kt`.

`Question().click(...)`:

- Atualiza texto da pergunta.
- Le a pergunta em voz alta.
- Mostra ou oculta exemplo conforme `checkDicas`.

`textToSpeak.TTS(...)`:

- Configura botoes de avancar/voltar.
- Configura leitura manual da pergunta e do exemplo.
- Traduz pergunta e hint quando a pergunta muda.
- Controla a automacao em loop usando coroutine com delay configuravel.
- Consome 1 moeda ao iniciar a automacao.

Risco conhecido: `textToSpeak.kt` usa `GlobalScope` e controle local de `Job`; se for refatorar, prefira escopo ligado ao lifecycle da activity.

## Traducao

`Translate.kt` usa Google ML Kit Translate:

- Fonte: ingles.
- Destino: idioma detectado por `TelephonyManager.networkCountryIso` e `Locale.getDefault()`.
- Casos especiais: Brasil para `pt`, India para `hi`, Paquistao para `ur` quando o idioma do aparelho esta em ingles.
- `downloadModelIfNeeded` e chamado na home.
- Para idiomas sem strings locais completas, textos de UI podem ser traduzidos em runtime.

Arquivos de strings locais existentes:

- `res/values/strings.xml`
- `res/values-pt/strings.xml`
- `res/values-es/strings.xml`
- `res/values-de/strings.xml`
- `res/values-hi/strings.xml`

## Anuncios e Review

O app usa Google AdMob:

- Banner em `MainActivity`, `beginner` e `SettingsScreen`.
- Rewarded ad em `rewardedAd.kt`.
- Interstitial existe em `beginner.kt`, mas a chamada de carregamento esta comentada no fluxo principal.

IDs reais de anuncio existem no codigo e nos recursos. Antes de trocar IDs por teste ou producao, confirme o objetivo da alteracao.

O review in-app usa Play Core Review em `ApiCall.kt` por meio da classe `inAppReview`.

## Gravacao, Camera e Compartilhamento

`beginner.kt` combina:

- `HBRecorder` para gravacao de tela.
- `MediaProjectionManager` para pedir permissao de captura de tela.
- CameraX para preview da camera frontal.
- `FileProvider` com authority `com.answering.fileprovider`.
- Permissoes de camera, audio e storage.

O manifest declara:

- `INTERNET`
- `WAKE_LOCK`
- `WRITE_EXTERNAL_STORAGE`
- `WRITE_INTERNAL_STORAGE`
- `RECORD_AUDIO`
- `CAMERA`
- `android.hardware.camera.any`

O app usa `requestLegacyExternalStorage="true"`, entao cuidado ao mexer no armazenamento para Android 10+.

Particularidades:

- O metodo `setOutputPath()` existe, mas o fluxo atual de start nao chama esse metodo antes de `startScreenRecording`.
- Em Android N+, o compartilhamento de video usa `FileProvider.getUriForFile`.
- A gravacao tenta compartilhar o arquivo depois que a galeria e atualizada.

## UI e Recursos

O app tem estilo visual customizado com drawables XML e imagens em `res/drawable`.

Recursos importantes:

- `logo.webp`, `img.webp`, `img_1.webp`, `download.webp`.
- Drawables de fundo: `bg_home_surface.xml`, `bg_hero_card.xml`, `bg_accent_pill.xml`, `container_components.xml`, `container_txt.xml`.
- Icones/acoes: `ic_arrow_right.xml`, `ic_help24.xml`, `ic_info.xml`, `ic_notification.xml`, `ic_videocam.xml`, `share.xml`, `playhint.xml`.

Ao alterar layout da tela `beginner`, confira tambem as variantes `layout-h360dp`, `layout-h480dp` e `layout-h600dp`.

## Riscos Conhecidos

- `HBRecorderOnPause()` e `HBRecorderOnResume()` ainda chamam `TODO("Not yet implemented")`; se esses callbacks forem acionados, podem causar crash.
- Ha APIs deprecated em `beginner.kt`, incluindo `startActivityForResult` e flags antigas de system UI/storage.
- O banco depende de uma linha inicial e de indices de colunas.
- `initializeRow()` e chamado na home; como o ID e fixo, insercoes repetidas podem falhar silenciosamente dependendo do estado do banco.
- `settings.gradle` referencia `:nativetemplates`, mas a pasta nao esta presente neste checkout.
- Nao fazer downgrade do CameraX para `1.3.x`: isso reintroduz a violacao de 16 KB page size da Play Console em `libimage_processing_util_jni.so`.
- Existem nomes historicos/mistos em ingles e portugues e uma activity chamada `beginner` em minusculo. Preserve isso em alteracoes pequenas para evitar refatoracao desnecessaria.

## Procedimento Recomendado para Agentes

1. Leia `README.md`, `copilot.md` e este arquivo antes de alterar codigo.
2. Rode `git status --short --branch` antes de editar e separe mudancas suas de mudancas ja existentes.
3. Para mudancas em Kotlin/XML, rode pelo menos `.\gradlew.bat assembleDebug --stacktrace` quando viavel.
4. Para mudancas de release, use `.\gradlew.bat bundleRelease --stacktrace`.
5. Para bugs em dispositivo, priorize `adb logcat -s com.answering:V AndroidRuntime:E`.
6. Mantenha mudancas pequenas e alinhadas ao padrao atual: Activities + helpers em `funcoes` + SQLite simples em `dados`.
7. Nao converta arquitetura para Compose, Room, MVVM ou outro padrao grande sem pedido explicito.
