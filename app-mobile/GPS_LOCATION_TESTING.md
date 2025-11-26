# 🧪 GPS Location Feature - Testing Guide

## Pre-Testing Setup

### 1. Clean Build
```
Build → Clean Project
Build → Rebuild Project
```

### 2. Uninstall Previous App Version
**In Emulator:**
- Long-press LocalPulse app icon
- Select "Uninstall"
- Confirm

**Why?** This resets all permissions to ensure permission dialog appears.

### 3. Set GPS Location in Emulator
```
1. Click "..." (Extended Controls)
2. Location tab
3. Set: 51.5074, -0.1278 (London)
4. Click "Send"
```

### 4. Run Fresh Install
```
Run → Run 'app'
```

---

## 🧪 Test Cases

### Test 1: Permission Dialog Appears ✅

**Steps:**
1. Launch app (fresh install)
2. Navigate to Map screen (bottom nav)

**Expected Result:**
- Permission dialog appears immediately
- Shows: "Allow LocalPulse to access this device's location?"
- Options: "While using the app", "Only this time", "Don't allow"

**Logcat Should Show:**
```
MapScreen: 🗺️ Map screen loaded. Permission granted: false
MapScreen: 📋 Requesting location permissions...
```

**✅ Pass Criteria:** Dialog appears within 1 second of opening map

---

### Test 2: Grant Permission ✅

**Steps:**
1. When dialog appears, tap **"While using the app"**

**Expected Result:**
- Dialog closes
- Red permission card disappears
- Green location card appears at top
- Shows: "📍 51.5074, -0.1278"
- Blue dot appears on map

**Logcat Should Show:**
```
MapScreen: 📍 Permission result - Fine: true, Coarse: true
MapViewModel: Setting location permission: true
MapViewModel: Starting location request...
MapViewModel: ✅ Location received: 51.5074, -0.1278
MapViewModel:    Accuracy: 20.0m, Provider: fused
```

**✅ Pass Criteria:** Location retrieved within 3 seconds

---

### Test 3: Deny Permission ❌→ Re-request ✅

**Steps:**
1. Fresh install (uninstall first)
2. Open map
3. Tap **"Don't allow"** on permission dialog

**Expected Result:**
- Large red card appears at top
- Title: "Location Permission Required"
- Message: Permission explanation
- Button: "ENABLE LOCATION" with LocationOn icon

**Logcat Should Show:**
```
MapScreen: ⚠️ Location permission denied by user
MapViewModel: Location permission not granted, cannot get location
```

**Steps to Re-request:**
1. Tap "ENABLE LOCATION" button on red card

**Expected Result:**
- Permission dialog appears again
- User can grant permission

**✅ Pass Criteria:** Can request permission again from UI

---

### Test 4: Location Refresh Button 🔄

**Steps:**
1. Grant permission
2. Wait for location to load
3. In Extended Controls, change location to Paris: 48.8566, 2.3522
4. Click "Send"
5. In app, tap **Refresh** button (🔄) in toolbar

**Expected Result:**
- Green location card updates to new coordinates
- Shows: "📍 48.8566, 2.3522"
- Blue dot moves on map

**Logcat Should Show:**
```
MapScreen: 🔄 Refresh location button clicked
MapViewModel: Starting location request...
MapViewModel: ✅ Location received: 48.8566, 2.3522
```

**✅ Pass Criteria:** Location updates within 2 seconds

---

### Test 5: My Location Button 📍

**Steps:**
1. Ensure location is loaded
2. Pan/zoom map away from user location
3. Tap **My Location** button (📍) in toolbar

**Expected Result:**
- Map centers on blue dot (user location)
- Zoom level adjusts to show user clearly

**Logcat Should Show:**
```
MapScreen: 📍 My location button clicked
MapScreen: Centering on user: 51.5074, -0.1278
```

**✅ Pass Criteria:** Map centers on user within 1 second

---

### Test 6: Distance Calculation in InfoWindow 📏

**Steps:**
1. Ensure location is loaded (London: 51.5074, -0.1278)
2. Search for "London" events (or they load by default)
3. Tap any event marker on map

**Expected Result:**
- InfoWindow appears above marker
- Shows:
  - Event name
  - Venue name
  - Venue address
  - **"5.2 km away"** (or similar distance)
  - "Tap to view details" (blue text)

**Logcat Should Show:**
```
(No specific logs for distance calc, happens silently)
```

**✅ Pass Criteria:** Distance appears and is reasonable (0-50 km for London events)

---

### Test 7: InfoWindow Click Opens Browser 🌐

**Steps:**
1. Tap event marker to show InfoWindow
2. Tap anywhere on the InfoWindow

**Expected Result:**
- Browser opens
- Shows Ticketmaster event page
- URL starts with: https://www.ticketmaster...

**✅ Pass Criteria:** Browser opens with correct URL

---

### Test 8: City Search Updates Distance 🔍

**Steps:**
1. Location set to London
2. Events loaded for London
3. Tap marker → Note distance (e.g., "2.5 km away")
4. Tap **Search** (🔍) button
5. Search for "Paris"
6. Wait for Paris events to load
7. Tap any Paris event marker

**Expected Result:**
- InfoWindow shows much larger distance
- Example: "342 km away"

**Why?** You're still in London, but viewing Paris events.

**✅ Pass Criteria:** Distance updates correctly for new city

---

### Test 9: Fallback to Last Known Location 🔄

**Steps:**
1. Grant permission
2. Location loads successfully
3. Close emulator (don't stop)
4. Reopen emulator
5. Open app → Map

**Expected Result:**
- Location loads immediately from cache
- Uses "last known location"

**Logcat Should Show:**
```
MapViewModel: Location is null, trying last known location...
MapViewModel: ✅ Last known location: 51.5074, -0.1278
```

**✅ Pass Criteria:** Location loads within 1 second

---

### Test 10: No GPS Set in Emulator ⚠️

**Steps:**
1. Fresh emulator (no GPS set)
2. Grant permission
3. Open map

**Expected Result:**
- Green card appears but might show default coordinates
- OR: No location retrieved

**Logcat Should Show:**
```
MapViewModel: ❌ No last known location available
MapViewModel: Please set location in emulator: Extended Controls > Location
```

**✅ Pass Criteria:** Clear error message in logs

---

## 📊 Test Results Matrix

| Test | Expected | Actual | Pass/Fail |
|------|----------|--------|-----------|
| Permission dialog appears | ✅ | | |
| Grant permission works | ✅ | | |
| Deny → Re-request works | ✅ | | |
| Refresh button updates location | ✅ | | |
| My Location button centers map | ✅ | | |
| Distance shows in InfoWindow | ✅ | | |
| InfoWindow click opens browser | ✅ | | |
| City search updates distance | ✅ | | |
| Fallback to last location | ✅ | | |
| No GPS error message | ✅ | | |

---

## 🐛 Debugging Tips

### Location Not Loading

**Check Logcat filter:** `MapViewModel`

**Look for:**
- ✅ = Success
- ❌ = Error
- ⚠️ = Warning

**Common fixes:**
1. Set GPS in Extended Controls
2. Click Refresh button
3. Grant permission if red card shows
4. Restart emulator

### Permission Dialog Not Appearing

**Solution:**
```
1. Uninstall app completely
2. Clean and rebuild project
3. Fresh install
4. Dialog should appear
```

### Wrong Coordinates Showing

**Check:**
- Extended Controls → Location tab
- Verify coordinates were "Sent"
- Click Refresh in app

### Distance Not Showing

**Requirements:**
- Permission granted ✅
- Location loaded ✅
- Event has valid coordinates ✅

**If all above true but no distance:**
- Check event.latitude/longitude in Logcat
- Some events may lack GPS data

---

## 📞 What to Report if Bugs Found

1. **Exact steps to reproduce**
2. **Logcat output** (filter: MapViewModel, MapScreen)
3. **Expected vs Actual result**
4. **Emulator or real device?**
5. **GPS coordinates set** (if emulator)
6. **Screenshot** of error

---

## ✅ Final Verification

**Everything working if:**
- [ ] Permission dialog appears on first visit
- [ ] Can grant/deny and re-request permission
- [ ] Location loads and shows in green card
- [ ] Blue dot appears on map
- [ ] Distance shows in event InfoWindows
- [ ] Refresh button updates location
- [ ] My Location button centers map
- [ ] Clicking InfoWindow opens browser
- [ ] Clear error messages in Logcat
- [ ] No app crashes

**All checked?** 🎉 GPS feature is fully functional!

---

## 🚀 Next Steps After Testing

1. Test on real Android device (not just emulator)
2. Test with GPS disabled on device
3. Test with airplane mode
4. Test permission revocation during use
5. Test battery drain (leave map open 10 minutes)

---

## 📝 Notes

- Emulator GPS is **simulated**, not real
- Real devices use actual GPS satellites
- Accuracy on emulator: ~20m
- Accuracy on real device: 5-50m depending on conditions
- FusedLocationProviderClient uses WiFi + GPS + Cell towers

