# tozan-mode

A Quick Settings tile for Android that keeps the phone unlocked while hiking ("tozan" is Japanese for mountain climbing). Tap it at the trailhead, tap it again when you are back.

Your PIN, fingerprints and wallet cards stay untouched. The tile only changes a handful of system settings and puts them back afterwards.

## What it does

While the tile is on:

- The phone does not lock when the screen turns off. The delay between screen-off and lock (`lock_screen_lock_after_timeout`) is raised to 12 hours.
- Only the power button wakes the screen. Tap to check, double-tap to check, double-tap to wake, lift to check and the wake gesture are turned off, so the unlocked phone does not wake up in your pocket.

Turning the tile off restores every setting to the value it had when the tile was turned on. The previous values are read from the device, not hard-coded.

Turning the tile on from the lock screen asks for your PIN or fingerprint first.

## Requirements

- Android 10 or later. Developed and tested on a Pixel 9a running Android 17.
- A computer with `adb`, the Android SDK and JDK 17 to build and install.

## Install

```sh
./gradlew assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
adb shell pm grant com.ymmooot.tozanmode android.permission.WRITE_SECURE_SETTINGS
adb shell cmd statusbar add-tile com.ymmooot.tozanmode/.TozanTileService
```

No prebuilt APK is provided. The release build is signed with your local debug key, so an APK built on another machine cannot be installed over it without uninstalling first.

`WRITE_SECURE_SETTINGS` cannot be granted from the device itself, so the `pm grant` step is required. Without it the tile is greyed out. The permission survives reboots and reinstalls with `adb install -r`, but not an uninstall.

The last command adds the tile to Quick Settings. You can also add it by hand from the Quick Settings editor, where it appears as Tozan Mode (登山モード when the device language is Japanese).

The app has no launcher icon. It shows up only as a tile and under Settings → Apps.

## Things to know

- **Power button instantly locks.** If this option is enabled in the screen lock settings, pressing the power button still locks the phone immediately. Turn it off if you put the phone to sleep with the power button.
- **Reboot.** Android always asks for the PIN once after a restart.
- **No automatic turn-off.** The mode stays on until you tap the tile again.
- **Changes made while the mode is on are overwritten.** Turning the tile off writes back the values saved when it was turned on.
- **Do not uninstall while the mode is on.** The saved values are lost and the settings stay in their hiking state.
- **Security.** While the mode is on, anyone who picks up the phone can use it.
- **Other devices.** The wake settings are the ones a Pixel uses. Phones from other manufacturers may keep waking on tap or lift.

## License

[MIT](LICENSE)
