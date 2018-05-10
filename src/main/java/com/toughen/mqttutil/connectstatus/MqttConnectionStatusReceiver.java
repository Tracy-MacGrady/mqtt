package com.toughen.mqttutil.connectstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/12/25.
 */

public class MqttConnectionStatusReceiver extends BroadcastReceiver {
    public void toRepeatConnect() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(MqttConnectionStatusService.REPEAT_ACTION)) {
            toRepeatConnect();
        }
    }
}
