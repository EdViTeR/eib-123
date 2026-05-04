package com.example.pingpong;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private PingPongView pingPongView;
    private Button backButton;
    private Handler timerHandler = new Handler();
    private boolean isRunning = false;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                pingPongView.update();
                timerHandler.postDelayed(this, 16);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Скрываем заголовок
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pingPongView = findViewById(R.id.pingPongView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSimulation();
                finish(); // Возврат на главный экран
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSimulation();
    }

    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            timerHandler.post(timerRunnable);
        }
    }

    private void stopSimulation() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }
}
