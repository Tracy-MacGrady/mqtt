package com.toughen.mqttutil;


import com.toughen.mqttutil.constant.MqttConstant;
import com.toughen.mqttutil.enums.MqttConnectStatusEnum;
import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;
import com.toughen.mqttutil.interfaces.MqttClientConnectStatusInterface;
import com.toughen.mqttutil.interfaces.MqttMessageInterface;
import com.toughen.mqttutil.message.MqttTopicAndMsgValEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lijianjian on 2018/4/9.
 * MQTT 相关回调管理类（连接状态、消息状态）
 */

public class MqttCallBackManager {
    private HashMap<String, List<MqttMessageInterface>> messageListenerMap;
    private List<MqttClientConnectStatusInterface> connectStatusListeners;
    private static volatile MqttCallBackManager instance;

    private MqttCallBackManager() {
    }

    public static MqttCallBackManager getInstance() {
        if (instance == null) synchronized (MqttCallBackManager.class) {
            if (instance == null) instance = new MqttCallBackManager();
        }
        return instance;
    }

    public synchronized void addConnectStatusListener(MqttClientConnectStatusInterface listener) {
        if (connectStatusListeners == null) connectStatusListeners = new ArrayList<>();
        connectStatusListeners.add(listener);
    }

    public synchronized void removeConnectStatusListener(MqttClientConnectStatusInterface listener) {
        if (connectStatusListeners == null) return;
        connectStatusListeners.remove(listener);
    }

    public synchronized void addMessageListener(String secondTopic, MqttMessageInterface listener) {
        String key = MqttConstant.MQTT_TOPIC + secondTopic;
        if (messageListenerMap == null) messageListenerMap = new HashMap<>();
        List<MqttMessageInterface> list = messageListenerMap.get(key);
        if (list == null) list = new ArrayList<>();
        list.add(listener);
        messageListenerMap.put(key, list);
    }

    public synchronized void removeMessageListener(String secondTopic, MqttMessageInterface listener) {
        if (messageListenerMap == null) return;
        String key = MqttConstant.MQTT_TOPIC + secondTopic;
        List<MqttMessageInterface> list = messageListenerMap.get(key);
        if (list == null) return;
        list.remove(listener);
        if (list.size() == 0) messageListenerMap.remove(secondTopic);
        else messageListenerMap.put(key, list);
    }

    public synchronized void callbackMessage(MqttTopicAndMsgValEntity entity, MqttMessageSendStatusEnum msgStatusEnum) {
        if (messageListenerMap == null || messageListenerMap.size() == 0 || entity == null) return;
        List<MqttMessageInterface> list = messageListenerMap.get(entity.getTopic());
        if (list == null || list.size() == 0) return;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).parseMsgFromString(msgStatusEnum, entity.getMessage());
        }
    }

    public synchronized void callbackConnectStatus(MqttConnectStatusEnum status) {
        if (connectStatusListeners == null || connectStatusListeners.size() == 0) return;
        for (int i = 0; i < connectStatusListeners.size(); i++)
            switch (status) {
                case STATUS_CONNECT_SUCCESS:
                    connectStatusListeners.get(i).mqttClientConnectSuccess();
                    break;
                case STATUS_RECONNECT_SUCCESS:
                    connectStatusListeners.get(i).mqttClientRepeatSuccess();
                    break;
                case STATUS_DISCONNECT_SUCCESS:
                    connectStatusListeners.get(i).mqttClientDisConnectSuccess();
                    break;
                case STATUS_CONNECT_FAILURE:
                    connectStatusListeners.get(i).mqttClientConnectFailure();
                    break;
                case STATUS_RECONNECT_FAILURE:
                    break;
                case STATUS_DISCONNECT_FAILURE:
                    connectStatusListeners.get(i).mqttClientDisConnectFailure();
                    break;
            }
    }

    public void removeAllConnectStatusListenr() {
        if (connectStatusListeners != null) connectStatusListeners.clear();
    }
}
