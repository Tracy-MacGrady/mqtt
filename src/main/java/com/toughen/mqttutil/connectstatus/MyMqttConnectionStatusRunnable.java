package com.toughen.mqttutil.connectstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.toughen.mqttutil.MqttClientManager;

/**
 * Created by 李健健 on 2017/7/7.
 */

public class MyMqttConnectionStatusRunnable implements Runnable {
    private Handler handler;
    private boolean isDo = true;
    private Context context;

    public MyMqttConnectionStatusRunnable(Context context) {
        this.context = context;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setIsDo(boolean isDo) {
        this.isDo = isDo;
        if (handler != null) handler.removeMessages(MqttConnectionStatusService.REPEAT_CODE);
    }

    @Override
    public void run() {
        while (isDo) {
            try {
                Thread.sleep(5000);
                if (checkNet(context)) {
                    if (MqttClientManager.getInstance().getMqttClient() != null && !MqttClientManager.getInstance().getMqttClient().isConnected()) {
                        handler.sendEmptyMessage(MqttConnectionStatusService.REPEAT_CODE);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkNet(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        return false;
    }
}
