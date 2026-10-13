package com.ymmooot.tozanmode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class TozanTileService : TileService() {

    override fun onStartListening() = render()

    override fun onClick() {
        // ロック画面からPINなしでロックなしに切り替えられると、ロックの意味がなくなる
        if (isLocked) unlockAndRun(::toggle) else toggle()
    }

    private fun toggle() {
        if (isActive()) disable() else enable()
        render()
    }

    private fun enable() {
        val editor = prefs().edit()
        ACTIVE_VALUES.forEach { (key, value) ->
            editor.putString(key, Settings.Secure.getString(contentResolver, key))
            Settings.Secure.putString(contentResolver, key, value)
        }
        editor.apply()
    }

    private fun disable() {
        // 元が未設定(null)ならnullを書き戻し、OSの既定値に従わせる
        ACTIVE_VALUES.keys.forEach { key ->
            Settings.Secure.putString(contentResolver, key, prefs().getString(key, null))
        }
    }

    private fun render() {
        val tile = qsTile ?: return
        val granted = checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        val active = isActive()
        tile.state = when {
            !granted -> Tile.STATE_UNAVAILABLE
            active -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        tile.subtitle = getString(
            when {
                !granted -> R.string.tile_no_permission
                active -> R.string.tile_on
                else -> R.string.tile_off
            }
        )
        tile.updateTile()
    }

    private fun isActive() =
        Settings.Secure.getString(contentResolver, LOCK_AFTER_TIMEOUT) == ACTIVE_VALUES[LOCK_AFTER_TIMEOUT]

    private fun prefs() = getSharedPreferences("previous_values", Context.MODE_PRIVATE)

    private companion object {
        // いずれもSettings.Secureの非公開キーなので文字列で直接書く
        const val LOCK_AFTER_TIMEOUT = "lock_screen_lock_after_timeout"

        val ACTIVE_VALUES = mapOf(
            LOCK_AFTER_TIMEOUT to "43200000", // 12時間
            // ロックなしのままポケットの中で画面が点くと誤操作になるので、電源ボタン以外の点灯手段を止める
            "doze_tap_gesture" to "0",
            "doze_pulse_on_double_tap" to "0",
            "double_tap_to_wake" to "0",
            "doze_pulse_on_pick_up" to "0",
            "wake_gesture_enabled" to "0",
        )
    }
}
