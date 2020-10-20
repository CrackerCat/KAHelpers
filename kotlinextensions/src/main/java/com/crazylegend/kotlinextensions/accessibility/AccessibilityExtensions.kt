package com.crazylegend.kotlinextensions.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.crazylegend.kotlinextensions.context.accessibilityManager

/**
 * Created by crazy on 10/20/20 to long live and prosper !
 */

/**
 * Based on {@link com.android.settingslib.accessibility.AccessibilityUtils#getEnabledServicesFromSettings(Context,int)}
 * @see <a href="https://github.com/android/platform_frameworks_base/blob/d48e0d44f6676de6fd54fd8a017332edd6a9f096/packages/SettingsLib/src/com/android/settingslib/accessibility/AccessibilityUtils.java#L55">AccessibilityUtils</a>
 */
inline fun <reified T : AccessibilityService> Context.hasAccessibilityPermission(): Boolean {
    val expectedComponentName = ComponentName(this, T::class.java)
    val enabledServicesSetting = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?: return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        val componentNameString = colonSplitter.next()
        val enabledService = ComponentName.unflattenFromString(componentNameString)
        if (enabledService != null && enabledService == expectedComponentName) return true
    }
    return false
}


val Context.isAccessibilityEnabled get() = accessibilityManager?.isEnabled ?: false


inline fun <reified T : AccessibilityService> Context.isAccessibilityServiceRunning(): Boolean {
    val settingsString = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return settingsString != null && settingsString.contains("${packageName}/${T::class.java.name}")
}

fun Context.askForAccessibilityPermissions() = startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))