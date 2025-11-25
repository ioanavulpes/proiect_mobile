# LocalPulse Architecture Documentation

## Overview

LocalPulse follows the **MVVM (Model-View-ViewModel)** architectural pattern with Clean Architecture principles, ensuring separation of concerns and testability.

## Architecture Layers

```
┌─────────────────────────────────────────┐
│           UI Layer (Compose)            │
│  ┌─────────────┐      ┌──────────────┐ │
│  │   Screens   │◄─────┤  ViewModels  │ │
│  └─────────────┘      └──────────────┘ │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Repository Layer               │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │EventRepository│  │FavoritesRepository│
│  └──────────────┘  └─────────────────┘ │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│          Data Layer                     │
│  ┌──────────┐  ┌──────────┐           │
│  │    API   │  │ Firestore│           │
│  └──────────┘  └──────────┘           │
└─────────────────────────────────────────┘
```

## Layer Details

### 1. UI Layer (`ui/`)

**Responsibility**: Render UI and handle user interactions

**Components**:
- **Screens**: Composable functions that define UI structure
- **Navigation**: NavHost configuration for app navigation
- **Theme**: Material Design 3 theming

**Key Files**:
```
ui/
├── auth/
│   ├── LoginScreen.kt         # Email/password login UI
│   ├── RegisterScreen.kt      # Account creation UI
│   └── AuthViewModel.kt       # Auth state management
├── home/
│   └── HomeScreen.kt          # Main feature navigation
├── events/
│   ├── EventsScreen.kt        # Events list with search
│   ├── EventDetailsScreen.kt  # Single event details
│   └── EventsViewModel.kt     # Events state & logic
├── favorites/
│   ├── FavoritesScreen.kt     # Saved events list
│   └── FavoritesViewModel.kt  # Favorites state
├── navigation/
│   └── AppNavigation.kt       # Navigation graph
└── theme/
    ├── Color.kt               # Color palette
    ├── Theme.kt               # Material theme config
    └── Type.kt                # Typography styles
```

**Design Patterns**:
- **Unidirectional Data Flow**: UI observes ViewModel state via StateFlow
- **Single Source of Truth**: ViewModels hold UI state
- **Compose State Management**: Remember and StateFlow for reactive UI

### 2. ViewModel Layer

**Responsibility**: Business logic, state management, and data orchestration

**Pattern**: Each screen has a corresponding ViewModel

**Key ViewModels**:

#### AuthViewModel
- Handles login/register operations
- Manages authentication state
- Communicates with AuthRepository

```kotlin
State: loginState, registerState
Actions: login(), register(), resetState()
```

#### EventsViewModel
- Fetches events from API
- Manages search functionality
- Handles favorite toggling
- Tracks favorite states

```kotlin
State: eventsState, eventDetailsState, favoriteIds
Actions: searchEvents(), getEventDetails(), toggleFavorite()
```

#### FavoritesViewModel
- Loads user favorites from Firestore
- Real-time updates via Firestore listeners
- Handles favorite removal

```kotlin
State: favoritesState
Actions: removeFavorite()
```

**State Management**:
- Uses `StateFlow` for reactive state
- `Resource<T>` sealed class for loading/success/error states

### 3. Repository Layer (`data/repository/`)

**Responsibility**: Abstract data sources and provide clean API to ViewModels

**Repositories**:

#### AuthRepository
```kotlin
Methods:
- login(email, password): Resource<FirebaseUser>
- register(email, password): Resource<FirebaseUser>
- logout()
- isUserLoggedIn(): Boolean
```

**Data Source**: Firebase Authentication

#### EventRepository
```kotlin
Methods:
- searchEvents(city): Resource<List<Event>>
- getEventById(id): Resource<Event>
```

**Data Source**: Eventbrite API via EventbriteApiService

#### FavoritesRepository
```kotlin
Methods:
- addFavorite(event): Resource<Unit>
- removeFavorite(eventId): Resource<Unit>
- isFavorite(eventId): Boolean
- getFavorites(): Flow<Resource<List<Favorite>>>
```

**Data Source**: Firebase Firestore

**Benefits**:
- Single point of truth for data
- Easy to test (can mock repositories)
- Hides implementation details from ViewModels

### 4. Data Layer (`data/`)

#### Models (`data/model/`)

**Event.kt**
```kotlin
data class Event(
    val id: String,
    val name: String,
    val description: String?,
    val url: String,
    val image: String?,
    val startTime: String,
    val venueName: String,
    val venueAddress: String
)
```

**User.kt**
```kotlin
data class User(
    val uid: String,
    val email: String,
    val createdAt: Timestamp?
)
```

**Favorite.kt**
```kotlin
data class Favorite(
    val id: String,
    val userId: String,
    val eventId: String,
    val eventName: String,
    val eventImage: String?,
    val eventUrl: String,
    val timestamp: Timestamp?
)
```

#### Network (`data/network/`)

**EventbriteApiService.kt**
- OkHttp client configuration
- API endpoint definitions
- JSON parsing with Kotlinx Serialization
- Error handling

**EventbriteResponse.kt**
- API response models
- Maps Eventbrite JSON to Kotlin data classes

```kotlin
Methods:
- searchEvents(city): List<Event>
- getEventById(id): Event?
```

### 5. Utilities (`util/`)

**Resource.kt**
```kotlin
sealed class Resource<out T> {
    data class Success<out T>(val data: T)
    data class Error(val message: String)
    object Loading
}
```

**Constants.kt**
- API endpoints
- Navigation routes
- Default values
- Collection names

## Data Flow

### Example: Loading Events

```
1. User opens EventsScreen
   └─> EventsViewModel initialized
       └─> searchEvents("Bucharest") called

2. EventsViewModel.searchEvents()
   ├─> Updates state to Loading
   └─> Calls EventRepository.searchEvents()

3. EventRepository.searchEvents()
   └─> Calls EventbriteApiService.searchEvents()

4. EventbriteApiService.searchEvents()
   ├─> Makes HTTP request to Eventbrite API
   ├─> Parses JSON response
   └─> Returns List<Event>

5. EventRepository
   └─> Wraps result in Resource.Success or Resource.Error

6. EventsViewModel
   └─> Updates eventsState StateFlow

7. EventsScreen
   └─> Observes eventsState
       └─> Recomposes UI with new data
```

### Example: Saving Favorite

```
1. User taps favorite icon on EventCard
   └─> EventsViewModel.toggleFavorite(event) called

2. EventsViewModel.toggleFavorite()
   ├─> Checks if event is already favorite
   └─> Calls FavoritesRepository.addFavorite(event)

3. FavoritesRepository.addFavorite()
   ├─> Creates Favorite object
   ├─> Adds to Firestore "favorites" collection
   └─> Returns Resource<Unit>

4. EventsViewModel
   └─> Updates favoriteIds state

5. EventCard
   └─> Observes favoriteIds
       └─> Updates icon (filled/outline)
```

## Navigation Architecture

**Pattern**: Single-Activity with Jetpack Compose Navigation

**Routes**:
```kotlin
- login          → LoginScreen
- register       → RegisterScreen
- home           → HomeScreen
- events         → EventsScreen
- event_details/{id} → EventDetailsScreen
- favorites      → FavoritesScreen
- map            → MapPlaceholderScreen
- recommendations → RecommendationsPlaceholderScreen
```

**Navigation Flow**:
```
App Start
  ↓
Check Auth → Login/Home
  ↓
Home (4 feature cards)
  ├─> Events → Event Details
  ├─> Favorites
  ├─> Map (placeholder)
  └─> Recommendations (placeholder)
```

## Dependency Management

**Dependency Injection**: Manual dependency injection via ViewModelFactory

```kotlin
// Repository creation
val authRepository = AuthRepository()
val eventRepository = EventRepository(context)
val favoritesRepository = FavoritesRepository()

// ViewModel creation
val viewModel: EventsViewModel = viewModel(
    factory = EventsViewModelFactory(
        eventRepository, 
        favoritesRepository
    )
)
```

**Future Enhancement**: Consider Hilt/Dagger for larger apps

## State Management

### UI State Patterns

**1. Loading State**
```kotlin
when (state) {
    is Resource.Loading -> CircularProgressIndicator()
    is Resource.Success -> DisplayData(state.data)
    is Resource.Error -> ErrorMessage(state.message)
}
```

**2. Real-time Updates** (Favorites)
```kotlin
// Repository returns Flow
fun getFavorites(): Flow<Resource<List<Favorite>>>

// ViewModel collects Flow
viewModelScope.launch {
    repository.getFavorites().collect { state ->
        _favoritesState.value = state
    }
}
```

## Error Handling

### Levels

**1. Network Layer**
```kotlin
try {
    val response = client.newCall(request).execute()
    if (!response.isSuccessful) {
        throw Exception("API Error: ${response.code}")
    }
} catch (e: Exception) {
    throw Exception("Failed to fetch: ${e.message}")
}
```

**2. Repository Layer**
```kotlin
return try {
    val data = apiService.searchEvents(city)
    Resource.Success(data)
} catch (e: Exception) {
    Resource.Error(e.message ?: "Unknown error")
}
```

**3. ViewModel Layer**
```kotlin
_eventsState.value = Resource.Loading
_eventsState.value = eventRepository.searchEvents(city)
// Automatically propagates Success or Error
```

**4. UI Layer**
```kotlin
when (eventsState) {
    is Resource.Error -> {
        Text((eventsState as Resource.Error).message)
        Button(onClick = { retry() }) { Text("Retry") }
    }
}
```

## Security Considerations

### Firebase Security Rules
- Users can only access their own data
- All operations require authentication
- Server-side validation via Firestore rules

### API Security
- Token stored in strings.xml (dev)
- ProGuard obfuscation in release
- Consider backend proxy for production

## Testing Strategy

### Unit Tests (Future)
- ViewModels (business logic)
- Repositories (data operations)
- Utilities

### Integration Tests (Future)
- Repository + Firebase
- Repository + API

### UI Tests (Future)
- Compose UI testing
- Navigation flows

## Performance Optimizations

### Current
- OkHttp connection pooling
- Coil image caching
- Compose recomposition optimization
- Firestore real-time listeners

### Future Enhancements
- Room database for offline caching
- Paging for large event lists
- Image placeholder/progressive loading
- WorkManager for background sync

## Scalability Considerations

### Current Architecture Supports
- Adding new features (screens + ViewModels)
- Swapping data sources (e.g., different API)
- Multiple authentication providers
- Additional repositories

### Future Growth
- Multi-module architecture
- Feature modules
- Shared business logic modules
- Dependency injection framework

## Technology Stack

### Core
- **Language**: Kotlin 1.9.20
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

### UI
- **Jetpack Compose**: BOM 2023.10.01
- **Material 3**: Latest
- **Navigation**: 2.7.5
- **Coil**: 2.5.0 (Image loading)

### Backend
- **Firebase Auth**: Authentication
- **Cloud Firestore**: Database
- **Firebase Analytics**: Analytics
- **Crashlytics**: Crash reporting

### Network
- **OkHttp**: 4.12.0
- **Kotlinx Serialization**: 1.6.0

### Architecture Components
- **Lifecycle ViewModel**: State management
- **Kotlin Coroutines**: Async operations
- **Flow**: Reactive streams

## Design Patterns Used

1. **MVVM**: Separation of UI and business logic
2. **Repository Pattern**: Abstract data sources
3. **Observer Pattern**: StateFlow for reactive UI
4. **Factory Pattern**: ViewModel factories
5. **Sealed Classes**: Type-safe state representation
6. **Single Source of Truth**: ViewModel holds state

## Code Organization Principles

1. **Feature-based packages**: Code organized by feature
2. **Separation of Concerns**: Each layer has single responsibility
3. **Dependency Rule**: Outer layers depend on inner layers
4. **Reactive Programming**: Flow/StateFlow for data streams
5. **Composability**: Reusable Compose components

---

This architecture provides:
- ✅ Testability
- ✅ Maintainability
- ✅ Scalability
- ✅ Separation of Concerns
- ✅ Clear data flow
- ✅ Easy debugging

