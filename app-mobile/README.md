# LocalPulse Android App

LocalPulse is an Android application built with Kotlin and Jetpack Compose that helps users discover local events using the Eventbrite API. Users can browse events, save favorites, and get personalized recommendations.

## 🏗️ Architecture

- **Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose with Material Design 3
- **Backend**: Firebase (Authentication, Firestore, Analytics, Crashlytics)
- **API Integration**: Eventbrite API via OkHttp
- **Language**: Kotlin

## 🚀 Features

- ✅ **Authentication**: Email/password login and registration via Firebase Auth
- ✅ **Event Discovery**: Browse real events from Eventbrite API with city search
- ✅ **Favorites**: Save events to Firestore and view them later
- ✅ **Event Details**: View complete event information and open in Eventbrite
- 🔜 **Google Maps Integration**: Coming soon
- 🔜 **AI Recommendations**: Coming soon

## 📋 Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK API 26+ (Android 8.0 Oreo)
- Firebase account
- Eventbrite API token

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd app-mobile
```

### 2. Firebase Setup

#### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select existing project
3. Enter project name: **LocalPulse** (or your preferred name)
4. Follow the setup wizard (Analytics optional but recommended)

#### Step 2: Add Android App to Firebase

1. In Firebase Console, click the Android icon to add an Android app
2. Enter the following details:
   - **Package name**: `com.localpulse`
   - **App nickname**: LocalPulse (optional)
   - **Debug signing certificate**: (optional, for testing)
3. Click "Register app"

#### Step 3: Download google-services.json

1. Download the `google-services.json` file
2. **Replace** the placeholder file at `app/google-services.json` with your downloaded file
3. This file contains your Firebase configuration

#### Step 4: Enable Firebase Services

**Enable Firebase Authentication:**
1. In Firebase Console, go to **Authentication** > **Sign-in method**
2. Enable **Email/Password** provider
3. Click Save

**Enable Cloud Firestore:**
1. Go to **Firestore Database** > **Create database**
2. Choose **Start in production mode** (we'll add security rules next)
3. Select your region (closest to your users)
4. Click Enable

**Set up Firestore Security Rules:**
1. Go to **Firestore Database** > **Rules**
2. Replace the rules with the following:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - users can only read/write their own document
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Favorites collection - users can only read/write their own favorites
    match /favorites/{favoriteId} {
      allow read: if request.auth != null && 
                     resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && 
                       request.resource.data.userId == request.auth.uid;
      allow update, delete: if request.auth != null && 
                               resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Click **Publish**

**Enable Analytics and Crashlytics:**
1. Go to **Analytics** > Enable Google Analytics (if not already enabled)
2. Go to **Crashlytics** > Enable Crashlytics
3. No additional configuration needed - SDK will handle it

### 3. Eventbrite API Setup

#### Step 1: Get Your Eventbrite API Token

1. Visit [Eventbrite API Portal](https://www.eventbrite.com/platform/api)
2. Sign in or create an Eventbrite account
3. Navigate to **Account Settings** > **API Keys** (or go directly to [API Keys page](https://www.eventbrite.com/account-settings/apps))
4. Click **"Create API Key"** or **"Create Private Token"**
5. Fill in the required information:
   - **Application Name**: LocalPulse
   - **Application Description**: Event discovery mobile app
6. Accept the Terms of Service
7. Click **"Create Key"**
8. **Copy your Private Token** (it will look like: `ABCDEFGH123456789...`)

#### Step 2: Add Token to the App

1. Open `app/src/main/res/values/strings.xml`
2. Find the line:
   ```xml
   <string name="eventbrite_token">YOUR_EVENTBRITE_PRIVATE_TOKEN_HERE</string>
   ```
3. Replace `YOUR_EVENTBRITE_PRIVATE_TOKEN_HERE` with your actual token:
   ```xml
   <string name="eventbrite_token">ABCDEFGH123456789...</string>
   ```
4. Save the file

⚠️ **Security Note**: The token in `strings.xml` will be embedded in your APK. For production apps:
- Use BuildConfig or environment variables
- Consider using a backend server to proxy API calls
- Never commit tokens to public repositories

### 4. Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator (API 26+)
4. Click the **Run** button or press `Shift + F10`

The app will install and launch on your device!

## 📱 App Structure

```
com.localpulse/
├── data/
│   ├── model/          # Data classes (Event, User, Favorite)
│   ├── network/        # Eventbrite API service
│   └── repository/     # Data repositories
├── ui/
│   ├── auth/          # Login & Register screens
│   ├── home/          # Home screen
│   ├── events/        # Events list & details
│   ├── favorites/     # Favorites screen
│   ├── map/           # Map placeholder
│   ├── recommendations/ # Recommendations placeholder
│   ├── navigation/    # Navigation graph
│   └── theme/         # Material3 theme
└── util/              # Utilities & constants
```

## 🔥 Firestore Database Structure

```
users (collection)
  └── {userId} (document)
      ├── email: string
      └── createdAt: timestamp

favorites (collection)
  └── {autoId} (document)
      ├── userId: string
      ├── eventId: string
      ├── eventName: string
      ├── eventImage: string
      ├── eventUrl: string
      └── timestamp: timestamp
```

## 🌐 API Integration

### Eventbrite API

- **Base URL**: `https://www.eventbriteapi.com/v3/`
- **Endpoint**: `events/search/`
- **Authentication**: Bearer token in Authorization header
- **Parameters**:
  - `location.address`: City name for search
  - `expand`: `venue` (includes venue details)
  - `page_size`: 50 (max events per request)

### Sample Request

```
GET https://www.eventbriteapi.com/v3/events/search/?location.address=Bucharest&expand=venue&page_size=50
Authorization: Bearer YOUR_TOKEN_HERE
```

## 🛠️ Technologies Used

### Core
- Kotlin 1.9.20
- Android SDK 26+ (Target SDK 34)

### UI
- Jetpack Compose BOM 2023.10.01
- Material Design 3
- Compose Navigation 2.7.5
- Coil (Image loading) 2.5.0
- Accompanist SwipeRefresh 0.32.0

### Backend & Storage
- Firebase Auth
- Firebase Firestore
- Firebase Analytics
- Firebase Crashlytics

### Networking
- OkHttp 4.12.0
- Kotlinx Serialization 1.6.0

### Architecture
- Lifecycle ViewModel
- Kotlin Coroutines & Flow
- MVVM Pattern

## 🔐 Security

### Firestore Security Rules
The app uses Firebase Security Rules to ensure users can only access their own data. See the security rules section above.

### API Token Security
- Token is stored in `strings.xml` for development
- ProGuard rules are configured to obfuscate code in release builds
- Consider moving sensitive keys to backend for production

### ProGuard
ProGuard is enabled in release builds to:
- Obfuscate code
- Remove unused code
- Protect Firebase and OkHttp classes

## 🐛 Troubleshooting

### Firebase Authentication Errors
**Error**: "An internal error has occurred"
- **Solution**: Check that Email/Password authentication is enabled in Firebase Console
- Verify `google-services.json` is correct and in the `app/` directory

### Eventbrite API Errors
**Error**: "API Error: 401 Unauthorized"
- **Solution**: Check your API token in `strings.xml`
- Ensure the token is valid and not expired
- Verify you're using a private token, not a public OAuth token

**Error**: "No events found"
- **Solution**: Try a different city name
- Some cities may have limited events
- Check your internet connection

### Build Errors
**Error**: "Plugin [id: 'com.google.gms.google-services'] was not found"
- **Solution**: Ensure you have the latest Android Studio version
- Check your internet connection for Gradle downloads
- Try **File** > **Invalidate Caches and Restart**

### Gradle Sync Issues
- **Solution**: 
  - Update Android Studio to the latest version
  - Check Gradle version compatibility
  - Clean project: **Build** > **Clean Project**
  - Rebuild: **Build** > **Rebuild Project**

## 📸 Screenshots

(Add screenshots of your app here after building)

## 🚀 Future Enhancements

- [ ] Google Maps integration for event locations
- [ ] AI-powered event recommendations
- [ ] Push notifications for upcoming events
- [ ] Event categories and advanced filters
- [ ] Social sharing capabilities
- [ ] Dark mode support (already themed, just needs toggle)
- [ ] Offline support with caching

## 📄 License

This project is for educational purposes. Ensure compliance with Eventbrite API Terms of Service and Firebase Terms when using in production.

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📧 Support

For issues or questions:
- Open an issue in the repository
- Check Eventbrite API documentation: https://www.eventbrite.com/platform/api
- Check Firebase documentation: https://firebase.google.com/docs

---

**Built with ❤️ using Kotlin and Jetpack Compose**

