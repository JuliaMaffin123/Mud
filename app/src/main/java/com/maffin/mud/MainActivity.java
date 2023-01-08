package com.maffin.mud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private GameProcessor game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.textView);
        final TextInputEditText inpSend = findViewById(R.id.inpSend);
        final AppCompatImageButton btnSend = findViewById(R.id.btnSend);

        textView.setMovementMethod(new ScrollingMovementMethod());

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String command = inpSend.getText().toString().trim().toLowerCase();
                if (command.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.emptyInput, Toast.LENGTH_LONG).show();
                } else {
                    game.process(textView, command);
                    inpSend.setText("");
                    if (game.isEndGame()) {
                        inpSend.setEnabled(false);
                        btnSend.setEnabled(false);
                    }
                }
            }
        });

        game = new GameProcessor(this);
        startGame();
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
            startGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGame() {
        final TextView textView = findViewById(R.id.textView);
        final TextInputEditText inpSend = findViewById(R.id.inpSend);
        final AppCompatImageButton btnSend = findViewById(R.id.btnSend);

        // Сброс состояния и очистка экрана
        game.startGame(textView);
        // Разблокировка управления
        inpSend.setEnabled(true);
        btnSend.setEnabled(true);
    }
}