package com.toughen.mqttutil.constant;

/**
 * Created by lijianjian on 2018/4/10.
 */

public class MqttConstantParamsEntity {
    private String mqttBroker;
    private String mqttTopic;
    private String mqttGroupid;
    private String username;
    private String password;

    public String getMqttBroker() {
        return mqttBroker;
    }

    public void setMqttBroker(String mqttBroker) {
        this.mqttBroker = mqttBroker;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public String getMqttGroupid() {
        return mqttGroupid;
    }

    public void setMqttGroupid(String mqttGroupid) {
        this.mqttGroupid = mqttGroupid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
