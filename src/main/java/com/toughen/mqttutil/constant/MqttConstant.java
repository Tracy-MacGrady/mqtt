package com.toughen.mqttutil.constant;

import android.text.TextUtils;

/**
 * Created by 李健健 on 2017/6/21.
 * Mqtt 消息规则相关
 */

public class MqttConstant {
    //mqtt相关参数
    public static String MQTT_BROKER = "";
    public static String MQTT_TOPIC = "";
    public static String MQTT_GROUPID = "";
    public static String MQTT_USERNAME = "";
    public static String MQTT_PASSWORD = "";

    public static boolean hasInit() {
        return TextUtils.isEmpty(MQTT_BROKER) || TextUtils.isEmpty(MQTT_TOPIC) || TextUtils.isEmpty(MQTT_GROUPID) || TextUtils.isEmpty(MQTT_USERNAME) || TextUtils.isEmpty(MQTT_PASSWORD);
    }

    public static void init(MqttConstantParamsEntity entity) {
        if (entity == null) return;
        MQTT_BROKER = entity.getMqttBroker();
        MQTT_TOPIC = entity.getMqttTopic();
        MQTT_GROUPID = entity.getMqttGroupid();
        MQTT_USERNAME = entity.getUsername();
        MQTT_PASSWORD = entity.getPassword();
    }
}
