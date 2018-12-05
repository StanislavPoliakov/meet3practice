package home.stanislavpoliakov.meet3practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
    static boolean isStarted, isBinded;

    //Описываем все возможные варианты сообщений, которые сервис будет посылать в Activity.
    //Если бы нам необходимо было реализовать двустороннее общение, то необходимо было бы
    //описать варианты и в Activity, равно как и создать в сервисе обработчик входящих сообщений.
    //Стоит также отметить, что подключений к сервису может быть несколько. В таком случае необходимо
    //регистрировать клиентов, чтобы знать кому отвечать (reply to).
    static final int MSG_SERVICE_STARTEDUNBINDED = 1;
    static final int MSG_SERVICE_STARTEDBINDED = 2;
    static final int MSG_SERVICE_ONLYBINDED = 3;

    //Мы не регистрируем тут приемник сообщений, потому что сервис ничего не принимает на вход, а только
    //отдает сообщения. Ниже (в onBind) мы увидим, что мы можем вернуть null, потому что не регистрируем
    //приемник сообщений в сервисе, а в Activity setConnection игнорируем передаваемый IBinder
    //final Messenger mMessenger = new Messenger(new Handler());

    //Регистрируем только получателя, которого получили в методе запроса на Intent
    private static Messenger mClient;


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i(LOG_TAG, "Service: Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Service: Started by onStartCommand");
        serviceWork();
        isStarted = true;
        //Сервис не будет перезапущен, если будет убит системой
        return START_NOT_STICKY;
    }

    /**
     * Метод, имитирующий работу сервиса. Раз в секунду выдает time-stamp и записывает его в сообщение
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
                            Thread.sleep(1000);
                            serviceResultMessage = String.valueOf(Calendar.getInstance().getTime());
                            sendMessage(serviceResultMessage);
                            //Log.d(LOG_TAG, "Service working... Result message: " + serviceResultMessage);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            serviceWorkThread.start();
        }
    }

    private void sendMessage(String message) {
        if (SecondActivity.isActive) {
            try {
                //Поскольку String в сообщении можно передавать только через Bundle
                //создаем его
                Bundle bundle = new Bundle();
                bundle.putString("result", message); //result - это ключ сообщения
                int status = 0;
                if (isStarted && isBinded) status = MSG_SERVICE_STARTEDBINDED;
                else if (isStarted) status = MSG_SERVICE_STARTEDUNBINDED;
                else if (isBinded) status = MSG_SERVICE_ONLYBINDED;
                Message msg = Message.obtain(null, status);
                msg.setData(bundle);
                mClient.send(msg);
                //Log.d(LOG_TAG, "Сообщение отправлено - " + m);
            } catch (RemoteException ex) {

            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "Service: Binded successfully");
        serviceWork();
        isBinded = true;
        //return mMessenger.getBinder();
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!serviceWorkThread.isAlive()) isMustWork = false;
        isBinded = false;
        Log.d(LOG_TAG, "Service: Undinded successfully");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
        isBinded = false;
        isMustWork = false;
        if (SecondActivity.isActive) startActivity(MainActivity.newIntent(MyService.this).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        Log.d(LOG_TAG, "Service: Destroyed");
    }

    /**
     * Создаем и возвращаем Intent сервиса
     * @param context вызывающей стороны
     * @return новый Intent сервиса
     */
    public static Intent newIntent(Context context, Messenger activitySide) {
        mClient = activitySide;
        return new Intent(context, MyService.class);
    }
}
