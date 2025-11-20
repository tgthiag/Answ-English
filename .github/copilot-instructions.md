# Copilot Instructions - Answ-English Project

## Contexto do Projeto
Este é um app Android para aprendizado de inglês com:
- Perguntas e respostas interativas
- Text-to-Speech (TTS)
- Gravação de vídeo
- Sistema de moedas e recompensas (AdMob)
- Tradução automática (Google ML Kit)

## Namespace Padrão
**SEMPRE USE:** `com.answering`

**NUNCA USE:** `AnsweringAPP` (namespace antigo removido em 2025-11-20)

## Estrutura de Pacotes
```
com.answering
├── activities/     # Activities principais
├── funcoes/        # Funções auxiliares
└── dados/          # Database e dados
```

## Build Configuration
- **Kotlin:** 1.9+
- **compileSdk:** 36
- **minSdk:** 23
- **targetSdk:** 36
- **ViewBinding:** Enabled
- **DataBinding:** Enabled
- **Plugin crítico:** `kotlin-kapt` (necessário para DataBinding)

## Convenções de Código

### Naming
- Activities: PascalCase (ex: `MainActivity`, `beginner`)
- Functions: camelCase (ex: `loadCoins`, `showAd`)
- Constantes: UPPER_SNAKE_CASE (ex: `TABLE_NAME`, `COINS`)

### Imports
Sempre use imports qualificados:
```kotlin
import com.answering.R
import com.answering.databinding.ActivityMainBinding
import com.answering.funcoes.DialogShow
```

### ViewBinding
Preferir ViewBinding sobre findViewById:
```kotlin
// BOM:
binding.btShare.setOnClickListener { ... }

// EVITAR:
findViewById<Button>(R.id.btShare).setOnClickListener { ... }
```

## Comandos Úteis

### Build
```bash
.\gradlew.bat clean assembleDebug --stacktrace
.\gradlew.bat installDebug  # instalar no dispositivo
```

### Debug
```bash
adb devices
adb logcat -s com.answering:V AndroidRuntime:E
```

## Warnings Conhecidos (Não Críticos)
- APIs deprecated em `beginner.kt` (Android 14+)
- GlobalScope em `textToSpeak.kt` (pode usar CoroutineScope)
- startActivityForResult deprecated (pode usar Activity Result API)

## Documentação
Ver **[copilot.md](../copilot.md)** para changelog completo e troubleshooting.

## Status Atual (2025-11-20)
- ✅ Build funcionando (BUILD SUCCESSFUL)
- ✅ R e ViewBinding gerados
- ✅ APK pronto para instalação
- ⏳ Aguardando testes no dispositivo

---
**Atualizado:** 2025-11-20

