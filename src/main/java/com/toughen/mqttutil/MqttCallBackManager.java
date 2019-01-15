package com.toughen.mqttutil;


import com.toughen.mqttutil.enums.MqttConnectStatusEnum;
import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;
import com.toughen.mqttutil.interfaces.MqttClientConnectStatusListener;
import com.toughen.mqttutil.interfaces.MqttMessageListener;
import com.toughen.mqttutil.message.MqttTopicAndMsgValEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lijianjian on 2018/4/9.
 * MQTT 相关回调管理类（连接状态、消息状态）
 */

public class MqttCallBackManager {
    //MQTT消息观察者容器 （用来存储所有的消息监听）
    private HashMap<String, List<MqttMessageListener>> messageListenerMap;
    //MQTT连接状态的观察者容器（用来存储所有的连接状态的监听）
    private List<MqttClientConnectStatusListener> connectStatusListeners;
    //
    private static volatile MqttCallBackManager instance;

    private MqttCallBackManager() {
    }

    public static MqttCallBackManager getInstance() {
        if (instance == null) synchronized (MqttCallBackManager.class) {
            if (instance == null) instance = new MqttCallBackManager();
        }
        return instance;
    }
    /**
     * listener MQTT 连接状态的观察者
     */
    public synchronized void addConnectStatusListener(MqttClientConnectStatusListener listener) {
        if (connectStatusListeners == null) connectStatusListeners = new ArrayList<>();
        connectStatusListeners.add(listener);
    }

    public synchronized void removeConnectStatusListener(MqttClientConnectStatusListener listener) {
        if (connectStatusListeners == null) return;
        connectStatusListeners.remove(listener);
    }

    /**
     * @param topic    所需要监听的消息主题
     * @param listener 消息监听
     */
    public synchronized void addMessageListener(String topic, MqttMessageListener listener) {
        topic=topic.replace("/","");
        if (messageListenerMap == null) messageListenerMap = new HashMap<>();
        List<MqttMessageListener> list = messageListenerMap.get(topic);
        if (list == null) list = new ArrayList<>();
        list.add(listener);
        messageListenerMap.put(topic, list);
    }

    public synchronized void removeMessageListener(String topic, MqttMessageListener listener) {
        topic=topic.replace("/","");
        if (messageListenerMap == null) return;
        List<MqttMessageListener> list = messageListenerMap.get(topic);
        if (list == null) return;
        list.remove(listener);
        if (list.size() == 0) messageListenerMap.remove(topic);
        else messageListenerMap.put(topic, list);
    }

    /**
     * @param entity
     * @param msgStatusEnum 消息状态
     *                      STATUS_SEND_SUCCESS,//消息发送成功
     *                      STATUS_SEND_FAILURE,//消息发送失败
     *                      STATUS_MSG_ARRIVED;//消息接受成功
     */
    public synchronized void callbackMessage(MqttTopicAndMsgValEntity entity, MqttMessageSendStatusEnum msgStatusEnum) {
        if (messageListenerMap == null || messageListenerMap.size() == 0 || entity == null) return;
        List<MqttMessageListener> list = messageListenerMap.get(entity.getTopic());
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
