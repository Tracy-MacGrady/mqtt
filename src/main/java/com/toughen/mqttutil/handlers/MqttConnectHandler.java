package com.toughen.mqttutil.handlers;


import android.os.Handler;
import android.os.Message;

import com.toughen.mqttutil.MqttCallBackManager;
import com.toughen.mqttutil.MqttClientManager;
import com.toughen.mqttutil.enums.MqttConnectStatusEnum;

/**
 * Created by lijianjian on 2018/4/10.
 */

public class MqttConnectHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 100://todo 连接 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_CONNECT_SUCCESS);
                break;
            case 102://todo 重连 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_RECONNECT_SUCCESS);
                break;
            case 101://todo 连接 mqtt连接 失败
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_CONNECT_FAILURE);
                break;
            case 200://todo 断开 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_DISCONNECT_SUCCESS);
                MqttClientManager.getInstance().clearMqttListener();
                break;
            case 201://todo 断开 mqtt连接 失败
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_DISCONNECT_FAILURE);
                MqttClientManager.getInstance().clearMqttListener();
                break;
        }
    }
}
