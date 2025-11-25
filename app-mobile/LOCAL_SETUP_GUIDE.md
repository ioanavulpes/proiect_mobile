# LocalPulse - Quick Setup Guide

This is a quick reference guide for setting up LocalPulse on your local machine.

## 📦 What You Need

1. **Android Studio** (Hedgehog 2023.1.1 or newer)
2. **JDK 17** or higher
3. **Firebase Account** (free)
4. **Ticketmaster Developer Account** (free)

---

## ⚡ Quick Setup (5 Steps)

### Step 1: Open Project in Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `app-mobile` folder
4. Wait for Gradle sync to complete

### Step 2: Copy Configuration Templates

**⚠️ Important - First Time Setup:**

These files contain sensitive API keys and are NOT in version control:

1. **Copy Firebase config:**
   ```bash
   cp app/google-services.json.example app/google-services.json
   ```

2. **Copy strings config:**
   ```bash
   cp app/src/main/res/values/strings.xml.example app/src/main/res/values/strings.xml
   ```

### Step 3: Setup Firebase (10 minutes)

**Create Firebase Project:**
1. Go to https://console.firebase.google.com/
2. Click "+ Add project"
3. Name it "LocalPulse"
4. Continue through setup

**Add Android App:**
1. Click Android icon
2. Package name: `com.localpulse`
3. Download `google-services.json`
4. **Replace** `app/google-services.json` with your downloaded file

**Enable Services:**
1. **Authentication**: 
   - Go to Authentication → Sign-in method
   - Enable "Email/Password"
   - Click Save

2. **Firestore**:
   - Go to Firestore Database
   - Click "Create database"
   - Start in production mode
   - Choose a region
   - Click Enable

3. **Add Security Rules**:
   - Go to Firestore → Rules tab
   - Copy rules from `FIRESTORE_RULES.txt`
   - Paste and click "Publish"

4. **Analytics & Crashlytics**:
   - Already enabled by default
   - No action needed

### Step 4: Get Ticketmaster API Key (5 minutes)

1. Go to https://developer.ticketmaster.com/
2. Sign up for a free account or log in
3. Go to "Apps" → "Create a new app"
4. Fill in:
   - App Name: LocalPulse
   - Description: Android event discovery app
   - URL: http://localhost (or your website)
5. Click "Save"
6. **Copy your Consumer Key** (looks like: `G24a3lt3VY...`)

### Step 5: Add API Key to App

1. Open `app/src/main/res/values/strings.xml`
2. Find this line:
   ```xml
   <string name="ticketmaster_api_key">YOUR_TICKETMASTER_API_KEY_HERE</string>
   ```
3. Replace with your API key:
   ```xml
   <string name="ticketmaster_api_key">G24a3lt3VYuugGL86ZNs7KmmUnZ7Tlpk</string>
   ```
4. Save file

**⚠️ Security Note:** 
- These files are in `.gitignore` - they won't be committed to Git
- Never share your API keys publicly
- Rate limit: 5000 requests/day (free tier)

### Step 6: Build and Run

1. Connect Android device or start emulator (API 26+)
2. Click **Run** button (green play icon) or press `Shift + F10`
3. Wait for build to complete
4. App will launch on your device!

---

## ✅ First Run Checklist

After launching the app:

- [ ] Can you see the login screen?
- [ ] Can you create an account?
- [ ] Can you log in?
- [ ] Can you see the home screen with 4 feature cards?
- [ ] Can you browse events?
- [ ] Can you save an event to favorites?
- [ ] Can you view your favorites?

If all checked ✅ - You're good to go! 🎉

---

## 🐛 Common Issues

### "Plugin not found" error
- **Fix**: Update Android Studio and sync Gradle again

### "401 Unauthorized" when loading events
- **Fix**: Check your Ticketmaster API key in `strings.xml`
- Make sure there are no extra spaces
- Verify API key is valid at https://developer.ticketmaster.com/

### "Authentication failed"
- **Fix**: Check Firebase Authentication is enabled
- Verify `google-services.json` is in `app/` folder
- Check package name matches: `com.localpulse`

### "No events found"
- **Fix**: Try searching for a different city
- Check your internet connection
- Default city is "Bucharest" - try "New York", "London", etc.

### Can't save favorites / No favorites showing
- **Fix**: Check Firestore is enabled
- Verify security rules are published
- Check you're logged in

---

## 🎯 Test Features

Once running, test these features:

1. **Registration**: Create account → Check Firestore Users collection
2. **Login**: Sign in with your account
3. **Events**: Search events in different cities
4. **Favorites**: Save event → Check Firestore Favorites collection
5. **Event Details**: Click event → View details → Open in Ticketmaster

---

## 📱 Test Accounts

You can create test accounts:
- email: `test@example.com`
- password: `test123456`

---

## 🔗 Useful Links

- **Firebase Console**: https://console.firebase.google.com/
- **Ticketmaster Developer Portal**: https://developer.ticketmaster.com/
- **Ticketmaster API Docs**: https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/
- **Full README**: See `README.md` for detailed documentation

---

## 💡 Tips

- Use real email when testing registration (Firebase sends verification emails in production)
- Different cities have different event counts - try major cities
- Favorites sync in real-time across devices
- Pull down to refresh events list
- Token is stored locally - don't share your APK publicly

---

## 🚀 Next Steps

After setup:
1. Explore the codebase structure
2. Try modifying UI colors in `ui/theme/Color.kt`
3. Add more cities to search
4. Customize event card layouts
5. Add event categories/filters

---

**Need Help?** 
- Check the main `README.md` for detailed docs
- Review `FIRESTORE_RULES.txt` for security rules explanation
- Open an issue if you encounter problems

**Happy Coding! 🎉**

