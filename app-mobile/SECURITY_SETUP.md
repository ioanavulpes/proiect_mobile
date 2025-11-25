# 🔒 Configurare Securitate - API Keys

## ⚠️ IMPORTANT - Citește Înainte de a Începe!

Acest proiect folosește API keys și configurări sensibile care **NU sunt incluse în Git** pentru securitate.

## 📝 Fișiere Care Conțin Informații Sensibile

Următoarele fișiere conțin API keys și **trebuie configurate local**:

### 1. `app/google-services.json` 
**Ce conține:** Configurarea Firebase (Project ID, API Keys)  
**Status:** ❌ NU este în Git (adăugat în `.gitignore`)  
**Template disponibil:** ✅ `app/google-services.json.example`

### 2. `app/src/main/res/values/strings.xml`
**Ce conține:** Ticketmaster API Key  
**Status:** ❌ NU este în Git (adăugat în `.gitignore`)  
**Template disponibil:** ✅ `app/src/main/res/values/strings.xml.example`

---

## 🚀 Pași Configurare (Prima Dată)

### Opțiunea 1: Pe Windows (PowerShell)

```powershell
# Copiază template-urile
Copy-Item app\google-services.json.example app\google-services.json
Copy-Item app\src\main\res\values\strings.xml.example app\src\main\res\values\strings.xml
```

### Opțiunea 2: Pe Linux/Mac (Terminal)

```bash
# Copiază template-urile
cp app/google-services.json.example app/google-services.json
cp app/src/main/res/values/strings.xml.example app/src/main/res/values/strings.xml
```

### Opțiunea 3: Manual

1. Copiază `app/google-services.json.example` → `app/google-services.json`
2. Copiază `app/src/main/res/values/strings.xml.example` → `app/src/main/res/values/strings.xml`

---

## 🔑 Obținere API Keys

### Firebase (google-services.json)

1. Intră pe https://console.firebase.google.com/
2. Creează un proiect nou sau selectează unul existent
3. Adaugă aplicația Android:
   - Package name: `com.localpulse`
4. Descarcă `google-services.json`
5. **Înlocuiește** fișierul `app/google-services.json` cu cel descărcat

### Ticketmaster API Key

1. Intră pe https://developer.ticketmaster.com/
2. Creează un cont sau loghează-te
3. Creează o aplicație nouă:
   - App Name: LocalPulse
   - Description: Android event discovery app
4. Copiază **Consumer Key** (API Key)
5. Deschide `app/src/main/res/values/strings.xml`
6. Înlocuiește `YOUR_TICKETMASTER_API_KEY_HERE` cu cheia ta:

```xml
<string name="ticketmaster_api_key">CHEIA_TA_AICI</string>
```

---

## ✅ Verificare Configurare

După configurare, verifică că:

- [ ] Fișierul `app/google-services.json` există și conține datele tale Firebase
- [ ] Fișierul `app/src/main/res/values/strings.xml` există
- [ ] API key-ul Ticketmaster este setat în `strings.xml`
- [ ] Aplicația se compilează fără erori
- [ ] Te poți autentifica (testează Firebase)
- [ ] Vezi evenimente (testează Ticketmaster API)

---

## 🛡️ Securitate - Best Practices

### ✅ CORECT
- ✅ Ține aceste fișiere doar local
- ✅ Nu le partaja niciodată public
- ✅ Folosește fișierele `.example` ca referință
- ✅ Regenerează API keys dacă sunt compromise

### ❌ GREȘIT
- ❌ Nu face commit la aceste fișiere în Git
- ❌ Nu trimite API keys pe Discord/Slack/Email
- ❌ Nu include API keys în screenshots
- ❌ Nu partaja fișierul `google-services.json`

---

## 🔄 Lucrul în Echipă

### Pentru Dezvoltatori Noi:

1. Clonează repository-ul
2. Urmează pașii din secțiunea "🚀 Pași Configurare"
3. Obține propriile tale API keys
4. Configurează fișierele local
5. Nu face commit la fișierele de configurare!

### Pentru Dezvoltatori Existenți:

Dacă ai deja fișierele configurate:
- **NU trebuie să faci nimic!** 
- Fișierele tale locale rămân neschimbate
- `.gitignore` previne commit-ul accidental

---

## 🆘 Probleme Comune

### "google-services.json not found"
**Soluție:** Copiază template-ul și configurează-l cu datele tale Firebase

### "Invalid API Key" pentru Ticketmaster
**Soluție:** Verifică că:
- API key-ul este corect copiat (fără spații extra)
- API key-ul este activ în contul Ticketmaster
- Nu ai depășit limita de 5000 cereri/zi

### "Authentication failed" Firebase
**Soluție:** Verifică că:
- `google-services.json` conține datele proiectului corect
- Package name este `com.localpulse`
- Authentication cu Email/Password este activat în Firebase Console

---

## 📚 Documentație Completă

Pentru ghid complet de setup, vezi:
- `LOCAL_SETUP_GUIDE.md` - Setup complet pas cu pas
- `README.md` - Documentație generală proiect

---

## ⚡ TL;DR (Rezumat Rapid)

```powershell
# 1. Copiază templates
Copy-Item app\google-services.json.example app\google-services.json
Copy-Item app\src\main\res\values\strings.xml.example app\src\main\res\values\strings.xml

# 2. Obține API keys de pe:
# - Firebase: https://console.firebase.google.com/
# - Ticketmaster: https://developer.ticketmaster.com/

# 3. Configurează fișierele cu API keys-urile tale

# 4. Build & Run! 🚀
```

**Nu uita:** Aceste fișiere sunt în `.gitignore` - nu vor fi commit-ate accidental! ✅

