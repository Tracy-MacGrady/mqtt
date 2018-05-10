package com.toughen.mqttutil.enums;

/**
 * Created by lijianjian on 2018/4/9.
 */

public enum MqttConnectStatusEnum {
    STATUS_CONNECT_SUCCESS("CONNECT_S"),
    STATUS_RECONNECT_SUCCESS("RECONNECT_S"),
    STATUS_DISCONNECT_SUCCESS("DISCONNECT_S"),
    STATUS_CONNECT_FAILURE("CONNECT_F"),
    STATUS_RECONNECT_FAILURE("RECONNECT_F"),
    STATUS_DISCONNECT_FAILURE("DISCONNECT_F");

    MqttConnectStatusEnum(String value) {
        setName(value);
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
