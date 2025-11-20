# Answ-English - English Learning App 🎓

App Android para prática de conversação em inglês com perguntas e respostas.

## 🚀 Quick Start

### Pré-requisitos
- Android Studio Arctic Fox ou superior
- JDK 11 ou 17
- Android SDK 23+ (mínimo)
- Android SDK 36 (target)

### Build & Install

```bash
# Clone o repositório (se aplicável)
git clone [URL]

# Navegar para o diretório
cd Answ-English

# Build
.\gradlew.bat clean assembleDebug

# Instalar no dispositivo conectado
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

## 📝 Documentação

Ver **[copilot.md](./copilot.md)** para:
- Histórico completo de mudanças
- Troubleshooting
- Changelog detalhado
- Instruções de build

## 🔧 Tecnologias

- **Linguagem:** Kotlin
- **UI:** Material Design 3, ViewBinding, DataBinding
- **Database:** SQLite
- **Ads:** Google AdMob (Rewarded, Banner, Interstitial)
- **TTS:** Android TextToSpeech
- **Tradução:** Google ML Kit Translate
- **Câmera:** CameraX
- **Screen Recording:** HBRecorder
- **Coroutines:** Kotlin Coroutines

## 📦 Estrutura do Projeto

```
com.answering
├── activities/          # Activities principais
│   ├── MainActivity     # Tela inicial
│   ├── beginner         # Tela de perguntas
│   ├── SettingsScreen   # Configurações
│   └── AutomaticQuestions
├── funcoes/            # Funções auxiliares
│   ├── DialogShow
│   ├── Translate
│   ├── textToSpeak
│   ├── rewardedAd
│   └── ...
└── dados/              # Dados e database
    ├── Questions
    └── localSqlDatabase
```

## 🐛 Problemas Conhecidos

Ver [copilot.md](./copilot.md) seção "Problemas Conhecidos Restantes"

## 📅 Último Update

**Data:** 2025-11-20  
**Mudanças:** Refatoração completa de namespace (AnsweringAPP → com.answering)

---

Desenvolvido com ❤️ para estudantes de inglês

