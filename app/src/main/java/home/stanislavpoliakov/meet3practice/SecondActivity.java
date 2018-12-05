package home.stanislavpoliakov.meet3practice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    final String LOG_TAG = "meet3_logs";
    private TextView messageView;
    private Button unbindService_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i(LOG_TAG, "Second Activity: Created");
        initItems();

        //Привязываем сервис в момент создания Activity
        bindService(MyService.newIntent(SecondActivity.this), serviceConnection, BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "Second Activity: Binding Service...");
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG,"Соединение с сервисом установлено");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Соединение с сервисом потеряно");
        }
    };

    /**
     * Инициализируем текстовое поле для вывода сообщений сервиса и кнопку отвязки
     */
    private void initItems() {
        messageView = findViewById(R.id.messageView);
        unbindService_button = findViewById(R.id.unbind_button);
        unbindService_button.setOnClickListener(this);
        unbindService_button.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        unbindService(serviceConnection);
        unbindService_button.setEnabled(false);
        Log.d(LOG_TAG, "Second Activity: Unbinding Service...");
    }

    /**
     * Создаем и возвращаем Intent второй Activity
     * @param context вызывающей стороны
     * @return новый Intent второй Activity
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SecondActivity.class);
    }
}
