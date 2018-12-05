package home.stanislavpoliakov.meet3practice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isServiceStarted = false;
    final String LOG_TAG = "meet3_logs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "Main Activity: Created");
        initButtons();
    }

    /**
     * Метод инициализации кнопок Main Activity и добавления к ним обработчика
     */
    private void initButtons() {
        Button firstButton = findViewById(R.id.first_button);
        Button secondButton = findViewById(R.id.second_button);
        firstButton.setOnClickListener(this);
        secondButton.setOnClickListener(this);
        Log.d(LOG_TAG, "Main Activity: Initialization successful");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_button:
                if (!isServiceStarted) {
                    //Запуск сервиса по onStartCommand
                    startService(MyService.newIntent(MainActivity.this));
                    isServiceStarted = true;
                } else {
                    //Останавливаем сервис
                    stopService(MyService.newIntent(MainActivity.this));
                    Log.d(LOG_TAG, "Main Activity: Stopping Service...");
                    isServiceStarted = false;
                }
                break;
        }
    }
}
