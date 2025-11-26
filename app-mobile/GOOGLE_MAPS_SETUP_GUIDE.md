# 🗺️ Google Maps Setup Guide - LocalPulse

Acest ghid te va ajuta să configurezi Google Maps API pentru feature-ul de hartă din LocalPulse.

---

## 📋 Ce Face Feature-ul de Hartă?

✅ **Funcționalități Implementate:**
- 📍 Preia locația ta curentă (cu permisiune)
- 🗺️ Afișează evenimente pe hartă cu markere
- 📏 Calculează distanța până la fiecare eveniment
- 🚗 Estimează timpul de călătorie cu mașina (60 km/h average)
- 🚶 Estimează timpul de călătorie pe jos (5 km/h average)
- 🎯 Selectează evenimente pe hartă pentru a vedea detalii
- 🧭 Buton "My Location" pentru a centra harta pe locația ta

---

## 🔑 Cum Obții Google Maps API Key

### Pasul 1: Creează Proiect în Google Cloud Console

1. **Accesează Google Cloud Console:**
   ```
   https://console.cloud.google.com/
   ```

2. **Creează un proiect nou:**
   - Click pe dropdown-ul de proiecte (sus, lângă "Google Cloud")
   - Click "NEW PROJECT"
   - Numele proiectului: `LocalPulse` (sau orice nume dorești)
   - Click "CREATE"

---

### Pasul 2: Activează Maps SDK for Android

1. **Navighează la API Library:**
   ```
   https://console.cloud.google.com/apis/library
   ```

2. **Caută și activează:**
   - Caută: **"Maps SDK for Android"**
   - Click pe rezultat
   - Click **"ENABLE"**

3. **(Opțional) Activează și Directions API:**
   - Dacă vrei în viitor să adaugi și rute vizuale pe hartă
   - Caută: **"Directions API"**
   - Click **"ENABLE"**

---

### Pasul 3: Creează API Key

1. **Navighează la Credentials:**
   ```
   https://console.cloud.google.com/apis/credentials
   ```

2. **Creează Credential:**
   - Click **"+ CREATE CREDENTIALS"** (sus)
   - Selectează **"API key"**
   - Se va genera un API key

3. **Copiază API Key-ul:**
   - Vei vedea un pop-up cu key-ul tău
   - Click **"COPY"** și salvează-l temporar

---

### Pasul 4: Restricționează API Key-ul (IMPORTANT!)

**Pentru securitate, restricționează key-ul doar pentru aplicația ta:**

1. **După ce ai copiat key-ul, click pe "EDIT API KEY"** (sau navighează din nou la Credentials și click pe key)

2. **Application restrictions:**
   - Selectează **"Android apps"**
   - Click **"+ Add an item"**

3. **Adaugă Package Name și SHA-1:**
   
   **Package name:**
   ```
   com.localpulse
   ```

   **SHA-1 Certificate Fingerprint:**
   
   Pentru a obține SHA-1:
   
   **Opțiunea A: Debug Keystore (Pentru Development)**
   ```bash
   # Windows (PowerShell):
   keytool -list -v -keystore "C:\Users\<YourUsername>\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   
   # macOS/Linux:
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   
   **Caută linia:**
   ```
   SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
   ```
   
   **Opțiunea B: Prin Android Studio (Mai Ușor)**
   1. Deschide Android Studio
   2. Click pe **Gradle** tab (dreapta)
   3. Navighează: `app` → `Tasks` → `android` → `signingReport`
   4. Double-click pe `signingReport`
   5. În panoul de jos, vezi SHA1 pentru **Variant: debug**

4. **API restrictions:**
   - Selectează **"Restrict key"**
   - Bifează doar:
     - ✅ **Maps SDK for Android**
     - ✅ **Directions API** (dacă l-ai activat)

5. **Click "SAVE"**

---

### Pasul 5: Adaugă API Key în Aplicație

1. **Deschide fișierul:**
   ```
   app/src/main/res/values/strings.xml
   ```

2. **Găsește linia:**
   ```xml
   <string name="google_maps_api_key">YOUR_GOOGLE_MAPS_API_KEY_HERE</string>
   ```

3. **Înlocuiește cu API Key-ul tău:**
   ```xml
   <string name="google_maps_api_key">AIzaSyAbCdEfGhIjKlMnOpQrStUvWxYz1234567</string>
   ```
   **(Aceasta este doar un exemplu - folosește key-ul tău real!)**

---

## 🚀 Testare

### Pasul 1: Rebuild Aplicația

```bash
Build → Clean Project
Build → Rebuild Project
```

### Pasul 2: Run pe Emulator sau Device

```bash
Run → Run 'app'
```

### Pasul 3: Testează Feature-ul

1. **Login în aplicație**
2. **Click pe "Harta"** din Home Screen
3. **Acceptă permisiunea de locație** când ți se cere
4. **Verifică:**
   - ✅ Se afișează harta Google Maps
   - ✅ Vezi markere pentru evenimente
   - ✅ Locația ta este afișată (punct albastru)
   - ✅ Click pe un marker pentru a vedea timpul de călătorie
   - ✅ Informațiile de călătorie sunt corecte

---

## 🛠️ Troubleshooting

### Problema 1: Harta este gri (nu se încarcă)

**Cauză:** API Key invalid sau neactivat corect.

**Soluție:**
1. Verifică că ai copiat corect API key-ul în `strings.xml`
2. Verifică că ai activat **"Maps SDK for Android"** în Google Cloud Console
3. Verifică că ai adăugat corect SHA-1 fingerprint
4. **Rebuild** aplicația după orice modificare

**Check rapid:**
```
Logcat → Filter: "Google Maps"
→ Caută erori legate de autentificare
```

---

### Problema 2: "Location permission required"

**Cauză:** Permisiunea de locație nu este acordată.

**Soluție:**
1. În aplicație, click pe butonul **"Activează Locația"**
2. Sau: Settings → Apps → LocalPulse → Permissions → Location → "Allow while using the app"

---

### Problema 3: Nu se afișează evenimente pe hartă

**Cauză:** Evenimentele nu au coordonate geografice (de la Ticketmaster API).

**Verificare:**
- Schimbă orașul în **"New York"** sau **"London"**
- Ticketmaster are coordonate mai bune pentru orașe mari internaționale
- București și alte orașe din România pot să nu aibă coordonate complete

**Soluție:**
- Folosește orașe cu acoperire bună în Ticketmaster
- Verifică în Logcat dacă evenimentele au `latitude` și `longitude`

---

### Problema 4: Timpul de călătorie nu apare

**Cauză:** Locația curentă nu este disponibilă.

**Soluție:**
1. Verifică că permisiunea de locație este acordată
2. Click pe butonul **"My Location"** (icon GPS în toolbar)
3. Dacă ești pe **emulator:**
   - În Android Studio: **More Actions** (...) → **Location**
   - Setează o locație manuală (ex: London: 51.5074, -0.1278)

---

## 📊 Cum Funcționează Calculul de Timp

### Algoritm:

1. **Distanța:** Calculată folosind **Haversine formula**
   - Ține cont de curbura Pământului
   - Rezultat: distanță în km în linie dreaptă ("as the crow flies")

2. **Timpul cu mașina:**
   ```
   Viteză medie: 60 km/h (oraș + șosea)
   Timp = Distanță / 60 × 60 minute
   ```

3. **Timpul pe jos:**
   ```
   Viteză medie: 5 km/h
   Timp = Distanță / 5 × 60 minute
   ```

### Nota Importantă:

⚠️ **Acestea sunt estimări!** 
- Calculul este în **linie dreaptă**, nu pe drumuri reale
- Nu ține cont de trafic, semafoare, sau obstacole
- Pentru rute reale, ai nevoie de **Google Directions API** (implementare viitoare)

---

## 🎯 Feature-uri Viitoare (Opțional)

Dacă vrei să extinzi feature-ul:

### 1. **Rute Reale pe Hartă** 🛣️
- Activează **Directions API** în Google Cloud
- Desenează polyline cu ruta pe hartă
- Estimare mai precisă a timpului (include trafic)

### 2. **Clustering pentru Multe Evenimente** 📍
- Dacă ai 100+ evenimente, grupează marker-ele apropiate
- Folosește `maps-compose` clustering

### 3. **Filter Evenimente pe Hartă** 🔍
- Adaugă UI pentru a filtra după categorie
- Afișează doar evenimentele filtrate

### 4. **Salvare Locații Favorite** ⭐
- Permite utilizatorilor să salveze "Home" și "Work"
- Calculează automat timpul de la aceste locații

---

## 💰 Cost și Limite

### Free Tier (Suficient pentru Development):

**Maps SDK for Android:**
- **FREE** pentru 25,000 map loads/zi
- LocalPulse folosește ~1 load per session
- **Concluzie:** Foarte probabil vei rămâne în free tier

**Directions API** (dacă îl activezi):
- **FREE** pentru 40,000 requests/lună
- Apoi: $5 per 1,000 requests

### Recomandare:
- Setează un **billing limit** în Google Cloud Console
- Activează **billing alerts** la $5, $10, etc.
- Pentru un app personal/demo, vei rămâne pe free tier

---

## 📝 Checklist Final

Înainte de a considera feature-ul complet:

- [ ] Google Maps API Key obținut și adăugat în `strings.xml`
- [ ] Maps SDK for Android activat în Google Cloud Console
- [ ] API Key restricționat (Package name + SHA-1)
- [ ] Aplicația rebuild-uită după modificări
- [ ] Permisiune de locație acordată în aplicație
- [ ] Harta se încarcă corect (nu este gri)
- [ ] Marker-ele pentru evenimente sunt vizibile
- [ ] Locația curentă este afișată (punct albastru)
- [ ] Click pe marker afișează timpul de călătorie
- [ ] Distanța și timpii sunt calculate corect
- [ ] Butonul "My Location" funcționează

---

## 🆘 Dacă Încă Ai Probleme

1. **Check Logcat:**
   ```
   Filter: "MapViewModel" sau "Google Maps"
   → Caută erori roșii
   ```

2. **Verifică API Key:**
   ```
   Google Cloud Console → Credentials → Your API Key
   → Check "API restrictions" și "Application restrictions"
   ```

3. **Test SHA-1:**
   ```bash
   # Rulează din nou signingReport
   gradle signingReport
   
   # Compară SHA-1 cu cel din Google Cloud Console
   ```

4. **Clean & Rebuild:**
   ```
   Build → Clean Project
   Build → Rebuild Project
   Invalidate Caches / Restart
   ```

---

## 📞 Resurse Utile

- **Google Maps Documentation:**  
  https://developers.google.com/maps/documentation/android-sdk

- **Maps Compose Documentation:**  
  https://developers.google.com/maps/documentation/android-sdk/maps-compose

- **Get API Key Guide:**  
  https://developers.google.com/maps/documentation/android-sdk/get-api-key

- **Pricing Calculator:**  
  https://mapsplatform.google.com/pricing/

---

## ✅ Gata!

Acum ai un feature complet de hartă cu:
- ✅ Vizualizare evenimente pe Google Maps
- ✅ Locația ta curentă
- ✅ Calcul distanță și timp de călătorie
- ✅ UI interactiv și user-friendly

**Bucură-te de aplicație! 🎉**

