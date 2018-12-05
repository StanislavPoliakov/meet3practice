package home.stanislavpoliakov.meet3practice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isServiceStarted = false;
    final String LOG_TAG = "meet3_logs";
    private Button firstButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.i(LOG_TAG, "Main Activity: Created");
        initButtons();
    }

    /**
     * Метод инициализации кнопок Main Activity и добавления к ним обработчика
     */
    private void initButtons() {
        firstButton = findViewById(R.id.first_button);
        firstButton.setText("Запустить сервис");
        Button secondButton = findViewById(R.id.second_button);
        firstButton.setOnClickListener(this);
        secondButton.setOnClickListener(this);
        //Log.d(LOG_TAG, "Main Activity: Initialization successful");
    }

    /**
     * Метод обработки нажатий кнопок.
     * Верхняя (первая) кнопка принудительно запускает и останавливает сервис.
     * Нижняя (вторая) кнопка открывает вторую Activity
     * @param v
     * Мы передаем значение null в качестве параметра метода запроса на Intent от сервиса, потому
     * что не обрабатываем на этой Activity сообщения сервиса, но обрабатываем их на второй Activity
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_button:
                if (!isServiceStarted) {
                    //Запуск сервиса по onStartCommand
                    startService(MyService.newIntent(MainActivity.this, null));
                    isServiceStarted = true;
                    firstButton.setText(R.string.stop_service);
                } else {
                    //Останавливаем сервис
                    stopService(MyService.newIntent(MainActivity.this, null));
                    MyService.isStarted = false;
                    //Log.d(LOG_TAG, "Main Activity: Stopping Service...");
                    isServiceStarted = false;
                    firstButton.setText(R.string.start_service);
                }
                break;
            case R.id.second_button:
                startActivity(SecondActivity.newIntent(MainActivity.this));
                break;
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
