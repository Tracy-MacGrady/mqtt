package com.toughen.mqttutil.connectstatus;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Administrator on 2017/12/25.
 */

public class MqttConnectionStatusService extends Service {
    public static int REPEAT_CODE = 1000;
    public static String REPEAT_ACTION = "REPEAT_ACTION";
    private Thread thread;
    private MyMqttConnectionStatusRunnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (runnable == null) runnable = new MyMqttConnectionStatusRunnable(this);
        if (thread == null) thread = new Thread(runnable);
        if (runnable.getHandler() == null) runnable.setHandler(handler);
        runnable.setIsDo(true);
        thread.start();
        return new MyBinder(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REPEAT_CODE) {
                LocalBroadcastManager.getInstance(MqttConnectionStatusService.this).sendBroadcast(new Intent(REPEAT_ACTION));
            }
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        runnable.setIsDo(false);
        thread = null;
        runnable = null;
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        MqttConnectionStatusService service;

        public MyBinder(MqttConnectionStatusService service) {
            this.service = service;
        }

        public MqttConnectionStatusService getService() {
            return service;
        }
    }
}
