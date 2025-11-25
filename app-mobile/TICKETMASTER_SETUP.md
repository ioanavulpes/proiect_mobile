# 🎫 Ticketmaster API Setup Guide

## How to Get Your Ticketmaster API Key

### Step 1: Create Account
1. Go to: **https://developer.ticketmaster.com/**
2. Click **"Get Your API Key"** or **"Sign Up"**
3. Fill in registration:
   - Email address
   - Password
   - First & Last name
4. Verify your email

### Step 2: Create an App
1. After login, go to **"My Apps"** or **"Apps"** tab
2. Click **"Create a new app"** or **"+ Create App"**
3. Fill in the form:
   ```
   App Name: LocalPulse
   Description: Android event discovery application
   App URL: http://localhost
   ```
4. Accept Terms & Conditions
5. Click **"Save"** or **"Create"**

### Step 3: Get Your API Key
1. After creating the app, you'll see app details
2. Find **"Consumer Key"** - this is your API key!
3. Copy the Consumer Key (it looks like: `abc123xyz456...`)

### Step 4: Add to Your App
1. Open: `app/src/main/res/values/strings.xml`
2. Find the line:
   ```xml
   <string name="ticketmaster_api_key">YOUR_TICKETMASTER_API_KEY_HERE</string>
   ```
3. Replace with your actual key:
   ```xml
   <string name="ticketmaster_api_key">abc123xyz456YourActualKey</string>
   ```
4. Save the file

### Step 5: Test
1. **Rebuild** the app in Android Studio
2. **Run** on device/emulator
3. Search for events in any city
4. You should see real events! 🎉

---

## API Details

### Endpoint Used
```
https://app.ticketmaster.com/discovery/v2/events.json
```

### Parameters
- `apikey`: Your Consumer Key
- `city`: City name to search (e.g., "Bucharest", "New York")
- `size`: Number of results (default: 50)

### Rate Limits
- **Free Tier**: 5,000 requests per day
- **Rate**: 5 requests per second

### Example Request
```
GET https://app.ticketmaster.com/discovery/v2/events.json?apikey=YOUR_KEY&city=Bucharest&size=20
```

---

## Troubleshooting

### Error: 401 Unauthorized
- ❌ API key is invalid or missing
- ✅ Check you copied the entire Consumer Key
- ✅ Verify key is in strings.xml correctly

### Error: 429 Too Many Requests
- ❌ Rate limit exceeded
- ✅ Wait a few seconds and try again
- ✅ Free tier: 5000 requests/day max

### No events showing
- ✅ Try different cities: "New York", "London", "Los Angeles"
- ✅ Check Logcat for "TicketmasterAPI" logs
- ✅ Verify API key is correct

### Check API Status
Visit: https://developer.ticketmaster.com/api-explorer/v2/

---

## Testing Your API Key

### Method 1: Browser Test
Open this URL in browser (replace YOUR_KEY):
```
https://app.ticketmaster.com/discovery/v2/events.json?apikey=YOUR_KEY&city=London&size=5
```

If it works, you'll see JSON with events!

### Method 2: In App
1. Build & Run the app
2. Open **"Evenimente"** (Events)
3. Search for "New York" or "London"
4. Should show events within seconds

---

## API Documentation

Full Ticketmaster API docs:
- **Main Docs**: https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/
- **API Explorer**: https://developer.ticketmaster.com/api-explorer/v2/
- **Support**: https://developer.ticketmaster.com/support/

---

## Security Notes

⚠️ **Keep Your API Key Secure:**
- Don't commit it to public repositories
- Don't share it publicly
- For production, use environment variables or secure storage

📝 **Current Storage:**
- Development: `strings.xml` (okay for learning)
- Production: Use BuildConfig or backend proxy

---

## What's Different from Eventbrite?

| Feature | Eventbrite | Ticketmaster |
|---------|-----------|--------------|
| Auth | Bearer Token | API Key in URL |
| Rate Limit | Variable | 5000/day |
| Coverage | More indie events | More concerts/sports |
| Image Quality | Good | Excellent |
| Venue Data | Good | Very detailed |

---

## Need Help?

1. Check Logcat for detailed error messages
2. Visit Ticketmaster Developer Portal
3. Review API Explorer for testing
4. Check app logs with tag "TicketmasterAPI"

---

**Ready to get events! 🎉**

