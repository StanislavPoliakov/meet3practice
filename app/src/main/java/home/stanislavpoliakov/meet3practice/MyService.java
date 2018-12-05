package home.stanislavpoliakov.meet3practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final String LOG_TAG = "meet3_logs";
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
        return START_NOT_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
