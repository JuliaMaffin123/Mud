package com.maffin.mud;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Управление настройками приложения
 */
public class PreferenceHelper {

    public static SharedPreferences prefs;

    // Имя файла с настройками
    public static final String APP_PREFERENCE_NAME = "app_preference_name";

    /**
     * Устанавливает boolean настройку.
     *
     * @param key   ключ для сохранения boolean значения в shared-preference
     * @param value boolean значение для сохранения в shared-preference по ключу
     */
    public static void putBoolean(Context context, String key, boolean value) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Возвращает boolean знаяение из shared-preference.
     *
     * @param key          ключ для получения boolean значения в shared-preference
     * @param defaultValue boolean значение по умолчанию, которое вернется, если ключ не будет найден в настройках
     * @return boolean     значение из shared-preference по ключу
     */
    public static boolean getBoolean(Context context,String key, boolean defaultValue) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean value = prefs.getBoolean(key, defaultValue);
        return value;
    }

    /**
     * Устанавливает int настройку.
     *
     * @param key   ключ для сохранения int значения в shared-preference
     * @param value int значение для сохранения в shared-preference по ключу
     */
    public static void putInt(Context context, String key, int value) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Возвращает int знаяение из shared-preference.
     *
     * @param key          ключ для получения int значения в shared-preference
     * @param defaultValue int значение по умолчанию, которое вернется, если ключ не будет найден в настройках
     * @return int         значение из shared-preference по ключу
     */
    public static int getInt(Context context,String key, int defaultValue) {
        prefs = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int value = prefs.getInt(key, defaultValue);
        return value;
    }
}
