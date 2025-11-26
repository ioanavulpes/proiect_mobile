# 📍 Emulator GPS Setup Guide

## Quick Setup (30 seconds)

### Step 1: Start Emulator
Run your app from Android Studio: `Run → Run 'app'`

### Step 2: Open Extended Controls
Click the **`...`** (three dots) button in the emulator sidebar.

**Keyboard shortcut:**
- Windows/Linux: `Ctrl + Shift + E`
- Mac: `Cmd + Shift + E`

### Step 3: Navigate to Location
In the Extended Controls window, select **"Location"** from the left sidebar.

### Step 4: Set GPS Coordinates
Choose one of these methods:

#### Method A: Quick Test Locations

**London, UK:**
```
Latitude:  51.5074
Longitude: -0.1278
```

**București, Romania:**
```
Latitude:  44.4268
Longitude: 26.1025
```

**Paris, France:**
```
Latitude:  48.8566
Longitude: 2.3522
```

**New York, USA:**
```
Latitude:  40.7128
Longitude: -74.0060
```

#### Method B: Search for Location
1. In the Extended Controls window, scroll down to the **"Search"** section
2. Type city name (e.g., "London")
3. Select from results
4. Coordinates auto-fill

### Step 5: Send Location to Emulator
1. Enter or search for coordinates
2. Click **"Send"** button
3. Location is now active!

### Step 6: Verify in App
1. Go back to LocalPulse app
2. Navigate to Map screen
3. Click **"My Location"** button (📍)
4. You should see:
   - Blue dot on map at your set location
   - Green card at top showing coordinates
   - Distance calculations in event InfoWindows

---

## 🔍 Troubleshooting

### Permission Dialog Doesn't Appear

**Solution 1: Uninstall and Reinstall**
```
1. Long-press app icon in emulator
2. Select "Uninstall"
3. Run app again from Android Studio
4. Permission dialog should appear
```

**Solution 2: Reset Permissions via Settings**
```
1. Open Settings in emulator
2. Go to Apps → LocalPulse → Permissions
3. Revoke Location permission
4. Open app again
5. Grant permission when prompted
```

### Location Not Detected

**Check Logcat for errors:**
```
1. Android Studio → Logcat tab (bottom)
2. Filter by: "MapViewModel" or "MapScreen"
3. Look for logs with 📍, ✅, or ❌ icons
```

**Common log messages:**

✅ **Success:**
```
MapViewModel: ✅ Location received: 51.5074, -0.1278
```

❌ **No permission:**
```
MapViewModel: Location permission not granted, cannot get location
```

❌ **No location set:**
```
MapViewModel: ❌ No last known location available
MapViewModel: Please set location in emulator: Extended Controls > Location
```

### Location Shows But Map Doesn't Center

**Solution:** Click the **"My Location"** button (📍) in the top-right of the map.

### Distance Not Showing in InfoWindows

**Cause:** Location not retrieved yet

**Solution:**
1. Ensure location is set in Extended Controls
2. Click **Refresh** button (🔄) in map toolbar
3. Wait 1-2 seconds
4. Tap event marker again

---

## 🎯 Advanced: Simulate Movement

### Walk Simulation
1. Extended Controls → Location
2. **"Routes"** tab (if available)
3. Or manually set multiple points:
   - Set Point A → Send
   - Wait 5 seconds
   - Set Point B → Send
   - Repeat to simulate walking

### GPX/KML Playback
1. Extended Controls → Location
2. Click **"Load GPX/KML"**
3. Select route file
4. Click **"Play"** to simulate movement

---

## 📱 Testing on Real Device

If you have a physical Android phone:

### Enable Developer Options
1. Settings → About Phone
2. Tap "Build Number" 7 times
3. Go back → Developer Options now visible

### Enable USB Debugging
1. Settings → Developer Options
2. Enable "USB Debugging"
3. Connect phone to computer via USB

### Run App
1. Android Studio → Select your device from dropdown
2. Click Run
3. **GPS must be enabled** on your phone!

### Grant Location Permission
1. App will request permission on first map visit
2. Choose **"Allow"** or **"While using the app"**
3. Your real location will be used!

---

## 🔐 Permission Flow in App

1. **First Visit:** Permission dialog appears automatically
2. **Denied:** Red card shows "Grant Permission" button
3. **Granted:** Green card shows current coordinates
4. **Location Retrieved:** Blue dot appears on map
5. **Tap Event:** InfoWindow shows distance from you

---

## 📊 What to Expect

### After Setting Location

**In Logcat:**
```
MapScreen: 🗺️ Map screen loaded. Permission granted: true
MapScreen: ✅ Permission already granted, getting location...
MapViewModel: Starting location request...
MapViewModel: ✅ Location received: 51.5074, -0.1278
MapViewModel:    Accuracy: 20.0m, Provider: fused
```

**In App:**
- 📍 Blue dot on map (your location)
- 🟢 Green card at top: "📍 51.5074, -0.1278"
- 📌 Red markers for events
- Tap marker → InfoWindow shows: "5.2 km away"

---

## 💡 Pro Tips

1. **Set location BEFORE opening map** for instant results
2. **Use London coordinates** for most events (good API coverage)
3. **Check Logcat** for detailed debugging info
4. **Refresh location** (🔄 button) if coordinates seem wrong
5. **Uninstall app** if permission dialog won't appear

---

## 🚨 Common Errors & Fixes

| Error | Fix |
|-------|-----|
| "Location permission not granted" | Grant permission when dialog appears |
| "No last known location available" | Set location in Extended Controls |
| "Location is null" | Wait a few seconds, then refresh |
| Permission dialog never appears | Uninstall app and reinstall |
| Wrong location showing | Set new coordinates in Extended Controls |

---

## ✅ Quick Verification Checklist

- [ ] Emulator running
- [ ] Extended Controls opened
- [ ] Coordinates set (e.g., 51.5074, -0.1278)
- [ ] "Send" button clicked
- [ ] App running
- [ ] Map screen opened
- [ ] Permission granted when prompted
- [ ] Green card shows coordinates
- [ ] Blue dot visible on map
- [ ] Event markers show distance

**All checked?** 🎉 GPS is working!

---

## 📞 Still Having Issues?

Check Logcat and look for these specific log tags:
- `MapScreen`
- `MapViewModel`
- `FusedLocationProviderClient`

The logs will tell you exactly what's happening at each step!

