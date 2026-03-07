# 🔮 AI Kundli Generator — Full Project Guide

## Project Structure

```
AIKundliGenerator/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/aikundli/
│           ├── KundliApplication.kt          ← App class, AdMob init
│           ├── MainActivity.kt
│           ├── model/Models.kt               ← Data classes
│           ├── network/
│           │   ├── ApiService.kt             ← Retrofit interfaces
│           │   └── RetrofitClient.kt         ← Singletons
│           ├── repository/KundliRepository.kt← Data layer
│           ├── viewmodel/KundliViewModel.kt  ← MVVM ViewModels
│           ├── util/
│           │   ├── Database.kt               ← Room DB
│           │   └── PdfGenerator.kt           ← PDF creation & share
│           └── ui/
│               ├── theme/Theme.kt            ← Colors, typography
│               ├── navigation/KundliNavGraph.kt
│               ├── components/
│               │   ├── AdComponents.kt       ← AdMob Banner + Rewarded
│               │   └── SharedComponents.kt   ← GlassCard, GradientButton
│               └── screens/
│                   ├── SplashScreen.kt       ← Animated cosmic splash
│                   ├── HomeScreen.kt         ← Dashboard with cards
│                   ├── GenerateKundliScreen.kt
│                   ├── KundliResultScreen.kt ← Chart + planets + AI reading
│                   ├── MatchKundliScreen.kt  ← Compatibility form + result
│                   ├── DailyHoroscopeScreen.kt
│                   └── OtherScreens.kt       ← Saved, Settings, Premium, Legal
├── backend/
│   ├── main.py          ← FastAPI server
│   └── requirements.txt
├── app/build.gradle.kts
└── build.gradle.kts
```

---

## 🚀 Quick Start

### 1. Android Setup

**Prerequisites**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

**Step-by-step**
```bash
# Clone / open the project in Android Studio
# Sync Gradle — all dependencies will download automatically

# Add your keys to local.properties (never commit to git!):
CEREBRAS_API_KEY=your_cerebras_key_here
BASE_URL=https://your-backend-domain.com/
ADMOB_APP_ID=ca-app-pub-XXXXXXXX~XXXXXXXXXX
```

Update `app/build.gradle.kts` buildConfigField entries or read from local.properties.

**Replace AdMob IDs**
| Location | Test ID (safe for dev) | Replace with |
|---|---|---|
| Banner | `ca-app-pub-3940256099942544/6300978111` | Your real banner ID |
| Rewarded | `ca-app-pub-3940256099942544/5224354917` | Your real rewarded ID |
| App ID in Manifest | `@string/admob_app_id` | Your real app ID |

---

### 2. Backend Setup

```bash
cd backend
python -m venv venv
source venv/bin/activate        # Windows: venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

**Deploy to production (recommended: Railway / Render / AWS)**
```bash
# Dockerfile (simple)
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
```

---

### 3. Cerebras AI Integration

```kotlin
// In KundliRepository.kt — already wired up
// The AI call sends planetary positions and receives JSON horoscope
val request = CerebrasRequest(
    model    = "gpt-oss-120b",
    messages = listOf(
        ChatMessage("system", "You are an expert Vedic astrologer..."),
        ChatMessage("user", "Ascendant: Aries, Sun in Leo House 5...")
    )
)
```

Get your API key: https://cloud.cerebras.ai

---

## 🏗️ Build APK / AAB

### Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release AAB (for Play Store)
```bash
# 1. Generate keystore (one time)
keytool -genkey -v -keystore kundli-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias kundli

# 2. Add to app/build.gradle.kts signingConfigs
signingConfigs {
    create("release") {
        storeFile     = file("kundli-release-key.jks")
        storePassword = "YOUR_STORE_PASSWORD"
        keyAlias      = "kundli"
        keyPassword   = "YOUR_KEY_PASSWORD"
    }
}

# 3. Build
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

---

## 📲 Google Play Upload Checklist

### Before Upload
- [ ] Replace ALL test AdMob IDs with real IDs
- [ ] Set `isDebuggable = false` in release build
- [ ] Remove test device IDs from `KundliApplication.kt`
- [ ] Update `BASE_URL` to production HTTPS endpoint
- [ ] Add `google-services.json` for Firebase (if using Analytics)

### Play Console Steps
1. Go to https://play.google.com/console
2. Create new app → Select "App" → Android
3. Fill store listing (title, description, screenshots)
4. Upload AAB to Internal Testing track
5. Complete Content Rating questionnaire (Entertainment/Astrology)
6. Fill Data Safety form:
   - Data collected: Name, Date of Birth, Location (for chart)
   - Purpose: App functionality
   - Shared with third parties: AdMob (advertising)
7. Set up Pricing: Free with In-App Purchases
8. Configure In-App Products:
   - `premium_basic_monthly` — ₹99/month subscription
   - `premium_pro_monthly` — ₹199/month subscription
9. Submit for review

### Required Assets
- App icon: 512×512 PNG
- Feature graphic: 1024×500 PNG
- Screenshots: min 2 phone screenshots
- Privacy Policy URL: Must be hosted publicly

---

## 💡 Rewarded Ad Flow (unlock feature)

```kotlin
// In any screen — show rewarded ad before generating Kundli
RewardedAdManager.loadAd(context)

Button(onClick = {
    if (isPremiumUser) {
        // direct access
        viewModel.generateKundli(request)
    } else {
        RewardedAdManager.showAd(
            context       = context,
            onRewarded    = { viewModel.generateKundli(request) },
            onDismissed   = { /* ad closed without reward */ },
            onNotAvailable = {
                // Fallback: show message or let user try later
                showSnackbar("Ad not available, try again shortly")
            }
        )
    }
})
```

---

## 🔐 Security Best Practices

1. **Never** commit API keys to git — use `local.properties` or CI secrets
2. All backend calls use HTTPS (`usesCleartextTraffic="false"` in Manifest)
3. Input is validated before API calls (null checks, type coercion)
4. ProGuard enabled in release — obfuscates code
5. Retrofit has 30s connect / 60s read timeouts to prevent hangs

---

## 📈 Scaling to 100k Users

| Component | Recommendation |
|---|---|
| Backend | Deploy on AWS/GCP with auto-scaling |
| Caching | Add Redis for daily horoscope responses |
| DB | PostgreSQL for saved reports (replace SQLite on backend) |
| CDN | Serve chart images from S3 + CloudFront |
| Rate limiting | Add FastAPI rate limiter per IP |
| Monitoring | Sentry (Android + Python) |

---

## 🐛 Common Issues

**Gradle sync fails**
→ File > Invalidate Caches, then sync again

**AdMob shows no ads**
→ Ensure you're using test IDs in debug, real IDs in release

**API 500 errors**
→ Check backend logs; pyephem may need specific date format

**PDF not sharing**
→ Ensure FileProvider is correctly configured in Manifest

---

*Built with ❤️ using Kotlin + Jetpack Compose + FastAPI + Cerebras AI*
