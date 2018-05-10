package com.toughen.mqttutil.message.tool;


import com.toughen.mqttutil.MqttClientManager;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李健健 on 2017/4/27.
 */

public class GetMessageTool {
    private static List<String> subscribeTopicList = new ArrayList<>();

    public static void subscribeTopic(int qos, String... topics) {
        try {
            for (int i = 0; i < topics.length; i++) {
                if (!subscribeTopicList.contains(topics[i]))
                    subscribeTopicList.add(topics[i]);
                String topicFilter = topics[i];
                MqttAsyncClient client = MqttClientManager.getInstance().getMqttClient();
                if (client != null && client.isConnected()) {
                    client.subscribe(topicFilter, qos);
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void unsubscribeTopic(String... topics) {
        try {
            if (subscribeTopicList.size() <= 0) return;
            for (int i = 0; i < topics.length; i++) {
                subscribeTopicList.remove(topics[i]);
            }
            MqttAsyncClient client = MqttClientManager.getInstance().getMqttClient();
            if (client != null && client.isConnected()) {
                client.unsubscribe(topics);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllTopic() {
        subscribeTopicList.clear();
    }
}
