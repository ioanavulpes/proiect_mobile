# 🔍 Advanced Search Features Guide

## Overview

LocalPulse acum are un sistem avansat de căutare evenimente similar cu Ticketmaster, cu filtre multiple pentru o experiență mai bună!

---

## ✨ Noi Funcționalități

### 1. **LOCATION Filter** 📍
- Dropdown cu orașe populare
- Include orașe din România: Bucharest, Cluj-Napoca, Timisoara, Iasi, Brasov
- Include orașe internaționale: London, New York, Los Angeles, Chicago, Paris
- Opțiune "All Cities" pentru căutare globală

### 2. **CATEGORY Filter** 🎭
- **Music** 🎵 - Concerte, festivaluri muzicale
- **Sports** ⚽ - Evenimente sportive, meciuri
- **Arts & Theatre** 🎨 - Teatru, expoziții, artă
- **Film** 🎬 - Premiere, festivaluri de film
- **Family** 👨‍👩‍👧 - Evenimente pentru familie
- **All Categories** - Toate tipurile de evenimente

### 3. **SEARCH Input** 🔎
- Caută după:
  - Nume artist (ex: "Coldplay", "Ed Sheeran")
  - Nume eveniment (ex: "Jazz Festival")
  - Nume locație/venue (ex: "Arena Națională")

---

## 🎨 UI Design

### Advanced Search Bar
```
┌─────────────────────────────────────────────────┐
│  LOCATION          │  CATEGORY                   │
│  Bucharest    ▼    │  Music             ▼        │
├─────────────────────────────────────────────────┤
│  🔍 Artist, Event or Venue          │  Search   │
└─────────────────────────────────────────────────┘
```

### Caracteristici:
- ✅ Design modern card-based
- ✅ Dropdowns interactive
- ✅ Icons pentru fiecare categorie
- ✅ Butoane cu corners rotunjite
- ✅ Culori Material Design 3

---

## 📊 Cum Funcționează

### Flow-ul de Căutare

```
User selectează filtre
    ↓
Apasă "Search"
    ↓
ViewModel primește parametrii
    ↓
Repository construiește query
    ↓
API Service adaugă parametri la URL
    ↓
Ticketmaster API returnează evenimente filtrate
    ↓
UI afișează rezultatele
```

### Exemplu URL API:
```
https://app.ticketmaster.com/discovery/v2/events.json
  ?apikey=YOUR_KEY
  &city=Bucharest
  &classificationName=music
  &keyword=jazz
  &size=50
```

---

## 🎯 Exemple de Utilizare

### Exemplu 1: Concerte în București
```
Location: Bucharest
Category: Music
Search: (gol)
→ Rezultat: Toate concertele din București
```

### Exemplu 2: Evenimente sportive în Cluj
```
Location: Cluj-Napoca
Category: Sports
Search: (gol)
→ Rezultat: Evenimente sportive din Cluj
```

### Exemplu 3: Caută artist specific
```
Location: All Cities
Category: All Categories
Search: Coldplay
→ Rezultat: Toate concertele Coldplay global
```

### Exemplu 4: Teatru în București
```
Location: Bucharest
Category: Arts & Theatre
Search: (gol)
→ Rezultat: Spectacole de teatru în București
```

---

## 🔧 Parametri API Ticketmaster

| Parametru UI | Parametru API | Descriere |
|--------------|---------------|-----------|
| Location | `city` | Filtrează după oraș |
| Category | `classificationName` | Tip eveniment (music, sports, arts, film, family) |
| Search | `keyword` | Caută în nume artist/eveniment/venue |
| - | `size` | Număr rezultate (default: 50) |

---

## 💡 Tips & Tricks

### Pentru Rezultate Mai Bune:

1. **Cautare Largă → Îngustă**
   - Start: All Cities + All Categories
   - Apoi: Specific city + category

2. **Folosește Keywords**
   - Nume complet artist: "Ed Sheeran"
   - Genre: "jazz", "rock", "opera"
   - Venue: "Arena", "Stadium"

3. **Combină Filtre**
   - City + Category = rezultate locale specifice
   - Category + Keyword = caută artist în genul specificat

4. **Clear Filters**
   - Selectează "All Cities" și "All Categories" pentru reset

---

## 🎨 Categorii de Evenimente

### Music 🎵
- Concerte
- Festivaluri
- Live performances
- DJ events

### Sports ⚽
- Fotbal
- Baschet
- Tenis
- Evenimente sportive internaționale

### Arts & Theatre 🎨
- Spectacole de teatru
- Opere
- Ballet
- Expoziții

### Film 🎬
- Premiere
- Festivaluri de film
- Proiecții speciale

### Family 👨‍👩‍👧
- Evenimente pentru copii
- Circuri
- Show-uri interactive
- Parcuri tematice

---

## 🚀 Funcționalități Viitoare (Posibile)

- [ ] Date Range Filter (Start Date → End Date)
- [ ] Price Range Filter
- [ ] Sort Options (Date, Price, Popularity)
- [ ] Save Search Preferences
- [ ] Recent Searches History
- [ ] Auto-complete pentru orașe
- [ ] Map View pentru evenimente

---

## 🐛 Troubleshooting

### Nu apar rezultate?
- ✅ Verifică conexiunea internet
- ✅ Încearcă un oraș mai mare (ex: London, New York)
- ✅ Schimbă categoria în "All Categories"
- ✅ Verifică API key-ul Ticketmaster

### Prea multe rezultate?
- ✅ Adaugă keyword specific
- ✅ Selectează categorie specifică
- ✅ Selectează oraș specific

### Rezultate neașteptate?
- ✅ Clear toate filtrele
- ✅ Reîncarcă cu "All Cities" + "All Categories"

---

## 📝 Cod Tehnic

### Fișiere Modificate:
1. `Constants.kt` - Categorii și orașe
2. `SearchFilters.kt` - Model de date (nou)
3. `TicketmasterApiService.kt` - Parametri API
4. `EventRepository.kt` - Pass-through parametri
5. `EventsViewModel.kt` - Gestionare filtre
6. `AdvancedSearchBar.kt` - UI component (nou)
7. `EventsScreen.kt` - Integrare search bar

### API Mapping:
```kotlin
UI Filter → API Parameter
─────────────────────────
Location  → &city=Bucharest
Category  → &classificationName=music
Keyword   → &keyword=coldplay
```

---

## 🎉 Enjoy!

Acum ai o interfață profesională de căutare evenimente, similară cu platformele mari internationale! 

**Happy Event Hunting! 🎫**

