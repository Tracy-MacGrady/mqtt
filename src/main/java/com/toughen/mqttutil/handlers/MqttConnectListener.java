package com.toughen.mqttutil.handlers;


import com.toughen.mqttutil.MqttCallBackManager;
import com.toughen.mqttutil.MqttClientManager;
import com.toughen.mqttutil.enums.MqttConnectStatusEnum;

/**
 * Created by lijianjian on 2018/4/10.
 */

public class MqttConnectListener {

    public void handleMessage(MqttConnectStatusEnum what) {
        switch (what) {
            case STATUS_CONNECT_SUCCESS://todo 连接 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_CONNECT_SUCCESS);
                break;
            case STATUS_CONNECT_FAILURE://todo 连接 mqtt连接 失败
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_CONNECT_FAILURE);
                break;
            case STATUS_RECONNECT_SUCCESS://todo 重连 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_RECONNECT_SUCCESS);
                break;
            case STATUS_DISCONNECT_SUCCESS://todo 断开 mqtt连接 成功
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_DISCONNECT_SUCCESS);
                MqttClientManager.getInstance().clearMqttListener();
                break;
            case STATUS_DISCONNECT_FAILURE://todo 断开 mqtt连接 失败
                MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_DISCONNECT_FAILURE);
                MqttClientManager.getInstance().clearMqttListener();
                break;
        }
    }

}
