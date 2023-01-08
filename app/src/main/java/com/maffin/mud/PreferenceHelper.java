package com.maffin.mud;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    public static SharedPreferences prefs;

    // Preference name for app.
    public static final String APP_PREFERENCE_NAME = "app_preference_name";

    /**
     * Set boolean value to shared-preference.
     *
     * @param key   Key for store boolean value to shared-preference.
     * @param value Boolean value to be stored in shared-preference for given key.
     */
    public static void putBoolean(Context context, String key, boolean value) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Get boolean value from shared-preference.
     *
     * @param key          Key for getting boolean value from shared-preference.
     * @param defaultValue Default boolean value that is returned if given key is not found in
     *                     preference.
     * @return string      Boolean value from shared-preference for given key.
     */
    public static boolean getBoolean(Context context,String key, boolean defaultValue) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean value = prefs.getBoolean(key, defaultValue);
        return value;
    }

    /**
     * Set boolean value to shared-preference.
     *
     * @param key   Key for store boolean value to shared-preference.
     * @param value Int value to be stored in shared-preference for given key.
     */
    public static void putInt(Context context, String key, int value) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Get int value from shared-preference.
     *
     * @param key          Key for getting boolean value from shared-preference.
     * @param defaultValue Default int value that is returned if given key is not found in
     *                     preference.
     * @return string      Int value from shared-preference for given key.
     */
    public static int getInt(Context context,String key, int defaultValue) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int value = prefs.getInt(key, defaultValue);
        return value;
    }
}
