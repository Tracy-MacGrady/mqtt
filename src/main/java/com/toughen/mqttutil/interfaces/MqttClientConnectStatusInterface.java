package com.toughen.mqttutil.interfaces;

/**
 * Created by 李健健 on 2017/12/1.
 */

public interface MqttClientConnectStatusInterface {

    void mqttClientConnectSuccess();

    void mqttClientRepeatSuccess();

    void mqttClientConnectFailure();

    void mqttClientDisConnectSuccess();

    void mqttClientDisConnectFailure();
}
