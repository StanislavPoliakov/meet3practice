package home.stanislavpoliakov.meet3practice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    final String LOG_TAG = "meet3_logs";
    private TextView messageView, serviceStatusView;
    private Button unbindService_button;
    static boolean isActive;

    /* Определяем приёмник сообщений и вешаем на него обработчик входящих сообщений IncomingHandler, в котором
       мы переодпределим метод handleMessage для того, чтобы правильно обрабатывать те сообщений, которые к нам приходят.
       Примечательно то, что нам нет необходимости определять источник этих сообщений!!! Мы, как я понимаю,
       обрабатываем все, что к нам может прийти. То есть мы не создаем пару источник-приемник, а вешаем
       обработчик входящих сообщений, из какого бы источник они не пришли. */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Это внутренний класс обработки входящих сообщений. Отправитель этих сообщений - Service.
     * Мы парсим входящее сообщение, чтобы узнать текущее состояние сервиса и вывести для пользователя
     * результаты в TextView
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                // STARTED и UNBINDED - результат работы сервиса, который запущен принудетельно,
                    //но отвязан от второй Activity
                case MyService.MSG_SERVICE_STARTEDUNBINDED:
                    serviceStatusView.setText(R.string.started_unbinded);
                    messageView.setText(msg.getData().getString("result"));
                    break;

                    // STARTED и BINDED - результат работы сервиса, который запущен принудительно,
                    // и имеет привязку ко второй Activity
                case MyService.MSG_SERVICE_STARTEDBINDED:
                    serviceStatusView.setText(R.string.started_binded);
                    messageView.setText(msg.getData().getString("result"));
                    break;

                    // ONLY BINDED - результат работы сервиса, который не запускался (или был остановлен)
                    // принудительно, но сохраняет привязку ко второй Activity
                case MyService.MSG_SERVICE_ONLYBINDED:
                    serviceStatusView.setText(R.string.only_binded);
                    messageView.setText(msg.getData().getString("result"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //Log.i(LOG_TAG, "Second Activity: Created");
        initItems();

        //Привязываем сервис в момент создания Activity. Текущий приёмник сообщений (mMessenger)
        //передаем в сервис, чтобы он знал, кому слать сообщения!
        bindService(MyService.newIntent(SecondActivity.this, mMessenger), serviceConnection, BIND_AUTO_CREATE);
        //Log.d(LOG_TAG, "Second Activity: Binding Service...");
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
        serviceStatusView = findViewById(R.id.serviceStatusView);
        unbindService_button = findViewById(R.id.unbind_button);
        unbindService_button.setOnClickListener(this);
        unbindService_button.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        unbindService(serviceConnection);
        unbindService_button.setEnabled(false);
        //Log.d(LOG_TAG, "Second Activity: Unbinding Service...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
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
