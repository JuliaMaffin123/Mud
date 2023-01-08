package com.maffin.mud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private GameProcessor game;
    private boolean mute = false;

    private static final String APP_PREFERENCES_MUTE = "mute";
    private static final String APP_PREFERENCES_MUSIC = "music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Подключим к активности разметку
        setContentView(R.layout.activity_main);
        // Отключаем ночную тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Получим ссылки на элементы разметки
        final TextView textView = findViewById(R.id.textView);
        final TextInputEditText inpSend = findViewById(R.id.inpSend);
        final AppCompatImageButton btnSend = findViewById(R.id.btnSend);

        // Включаем скролинг в textView
        textView.setMovementMethod(new ScrollingMovementMethod());
        // Навесим обработчик на кнопку отправки команд
        btnSend.setOnClickListener(v -> {
            String command = inpSend.getText().toString().trim().toLowerCase();
            if (command.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.emptyInput, Toast.LENGTH_LONG).show();
            } else {
                // Обработаем команду
                game.process(textView, command);
                // Очистим поле ввода
                inpSend.setText("");
                // Если игра закончилась, заблокируем элементы управления
                if (game.isEndGame()) {
                    inpSend.setEnabled(false);
                    btnSend.setEnabled(false);
                }
            }
        });
        // Запускаем игру
        game = new GameProcessor(getApplicationContext());
        startGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Сохраняем настройку
        PreferenceHelper.putBoolean(getApplicationContext(), APP_PREFERENCES_MUTE, mute);
        PreferenceHelper.putInt(getApplicationContext(), APP_PREFERENCES_MUSIC, game.getMusic());
        // Отключаем звук принудительно, не зависимо от настройки, т.к. приложение не активно
        game.mute(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Вычитываем настройку
        mute = PreferenceHelper.getBoolean(getApplicationContext(), APP_PREFERENCES_MUTE, false);
        int music = PreferenceHelper.getInt(getApplicationContext(), APP_PREFERENCES_MUSIC, R.raw.forest);
        // Активируем музыку, если надо
        game.setMusic(music);
        game.mute(mute);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceHelper.putInt(getApplicationContext(), APP_PREFERENCES_MUSIC, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_start) {
            // Новая игра
            startGame();
            // Активируем музыку, если надо
            game.mute(mute);
            return true;
        } else if (id == R.id.action_mute) {
            // Вкл/выкл звук
            mute = !mute;
            // Активируем музыку, если надо
            game.mute(mute);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startGame() {
        final TextView textView = findViewById(R.id.textView);
        final TextInputEditText inpSend = findViewById(R.id.inpSend);
        final AppCompatImageButton btnSend = findViewById(R.id.btnSend);

        // Разблокировка управления
        inpSend.setEnabled(true);
        btnSend.setEnabled(true);
        // Сброс состояния и очистка экрана
        game.startGame(textView);
    }
}