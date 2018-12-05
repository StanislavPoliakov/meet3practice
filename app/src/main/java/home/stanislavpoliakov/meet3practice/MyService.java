package home.stanislavpoliakov.meet3practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    private final String LOG_TAG = "meet3_logs";
    private Thread serviceWorkThread;
    private String serviceResultMessage;
    private boolean isMustWork = false;
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "Service: Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Service: Started by onStartCommand");
        serviceWork();
        //Сервис не будет перезапущен, если будет убит системой
        return START_NOT_STICKY;
    }

    /**
     * Метод, имитирующий работу сервиса. Раз в две секунды выдает time-stamp и записывает его в сообщение
     * для последующией отправки. Метод запущен в отдельном потоке для разгрузки основного потока приложения.
     * Есть проверка выполняет ли сервис работу (isServiceWorking).
     */
    private void serviceWork() {
        /* Запускаем работу только в том случае, если сервис не выполняет работу. Чтобы множественные
          нажатия кнопки "1" в главной Activity не плодили потоки. */
        if ((serviceWorkThread == null) || (!serviceWorkThread.isAlive())) {
            isMustWork = true;
            serviceWorkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //Calendar currentTime = Calendar.getInstance();
                    try {
                        while (isMustWork) {
                            Thread.sleep(2000);
                            serviceResultMessage = String.valueOf(Calendar.getInstance().getTime());
                            Log.d(LOG_TAG, "Service working... Result message: " + serviceResultMessage);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            serviceWorkThread.start();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "Service: Binded successfully");
        serviceWork();
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!serviceWorkThread.isAlive()) isMustWork = false;

        Log.d(LOG_TAG, "Service: Undinded successfully");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isMustWork = false;
        Log.i(LOG_TAG, "Service: Destroyed");
    }

    /**
     * Создаем и возвращаем Intent сервиса
     * @param context вызывающей стороны
     * @return новый Intent сервиса
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, MyService.class);
    }
}
