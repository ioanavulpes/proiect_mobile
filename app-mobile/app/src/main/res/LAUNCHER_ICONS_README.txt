LAUNCHER ICONS SETUP
====================

The app currently uses default Android launcher icons. To create custom launcher icons:

OPTION 1: Use Android Studio Image Asset Studio (Recommended)
--------------------------------------------------------------
1. Right-click on 'res' folder in Android Studio
2. Select New → Image Asset
3. Choose Icon Type: "Launcher Icons (Adaptive and Legacy)"
4. Select your icon image or use clipart
5. Customize colors and shape
6. Click Next → Finish

This will automatically generate all required icon sizes:
- mipmap-mdpi/
- mipmap-hdpi/
- mipmap-xhdpi/
- mipmap-xxhdpi/
- mipmap-xxxhdpi/

OPTION 2: Use Online Icon Generator
------------------------------------
1. Visit: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
2. Upload your icon design
3. Download the generated zip file
4. Extract and copy all mipmap folders to app/src/main/res/

OPTION 3: Manual Creation
--------------------------
Create icons in the following sizes:

mipmap-mdpi/ic_launcher.png          → 48x48 px
mipmap-hdpi/ic_launcher.png          → 72x72 px
mipmap-xhdpi/ic_launcher.png         → 96x96 px
mipmap-xxhdpi/ic_launcher.png        → 144x144 px
mipmap-xxxhdpi/ic_launcher.png       → 192x192 px

For adaptive icons (Android 8.0+):
mipmap-mdpi/ic_launcher_foreground.png     → 108x108 px (foreground layer)
mipmap-hdpi/ic_launcher_foreground.png     → 162x162 px
mipmap-xhdpi/ic_launcher_foreground.png    → 216x216 px
mipmap-xxhdpi/ic_launcher_foreground.png   → 324x324 px
mipmap-xxxhdpi/ic_launcher_foreground.png  → 432x432 px

CURRENT SETUP
-------------
The app currently has:
- Adaptive icon XML configuration (mipmap-anydpi-v26/)
- Vector drawable foreground icon (drawable/ic_launcher_foreground.xml)
- Background color defined in values/ic_launcher_background.xml

The foreground icon uses a calendar/event icon which fits the app's purpose.
The background is the app's primary color (#6366F1 - purple/blue).

DESIGN SUGGESTIONS
------------------
For LocalPulse, consider icons that represent:
- Calendar/event symbols
- Location pins
- Pulse/wave symbols
- Community/people icons
- Combination of calendar + location pin

Colors:
- Primary: #6366F1 (Indigo/Purple)
- Accent: #8B5CF6 (Light Purple)

Remember to use simple, recognizable designs that work well at small sizes!

