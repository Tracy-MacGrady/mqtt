package com.toughen.mqttutil.constant;

/**
 * Created by lijianjian on 2018/4/10.
 */

public class MqttConstantParamsEntity {
    private String mqttBroker;
    private String mqttTopic;
    private String mqttGroupid;
    private String mqttAccesskey;
    private String mqttSecretkey;

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

    public String getMqttAccesskey() {
        return mqttAccesskey;
    }

    public void setMqttAccesskey(String mqttAccesskey) {
        this.mqttAccesskey = mqttAccesskey;
    }

    public String getMqttSecretkey() {
        return mqttSecretkey;
    }

    public void setMqttSecretkey(String mqttSecretkey) {
        this.mqttSecretkey = mqttSecretkey;
    }
}
