package com.toughen.mqttutil.message.tool;

import android.content.Context;
import android.util.Log;

import com.toughen.mqttutil.MqttCallBackManager;
import com.toughen.mqttutil.MqttClientManager;
import com.toughen.mqttutil.constant.MqttConstant;
import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;
import com.toughen.mqttutil.message.MqttTopicAndMsgValEntity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;

/**
 * Created by 李健健 on 2017/4/27.
 */

public class SendMessageTool {
    private static volatile SendMessageTool instance;

    private SendMessageTool() {
    }

    public static SendMessageTool getInstance() {
        if (instance == null) synchronized (SendMessageTool.class) {
            if (instance == null) instance = new SendMessageTool();
        }
        return instance;
    }

    public void sendToTopic(Context context, final String messageValue, int qos, String... secondTopics) {
        try {
            MqttMessage message = new MqttMessage(messageValue.getBytes());
            message.setQos(qos);
            Log.e("sendToTopic", " pushed at " + new Date() + " " + messageValue);
            /**
             *消息发送到某个主题Topic，所有订阅这个Topic的设备都能收到这个消息。
             * 遵循MQTT的发布订阅规范，Topic也可以是多级Topic。此处设置了发送到二级topic
             */
            if (secondTopics == null || secondTopics.length == 0) {
                String topic = MqttConstant.MQTT_TOPIC;
                msgPublish(context, messageValue, message, topic);
            } else for (int i = 0; i < secondTopics.length; i++) {
                String topic = MqttConstant.MQTT_TOPIC + "/" + secondTopics[i];
                msgPublish(context, messageValue, message, topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void sendToClient(Context context, String messageValue, String clientId, int qos) {
        try {
            String consumerClientId = MqttConstant.MQTT_GROUPID + "@@@ClientID_" + clientId;
            MqttMessage message = new MqttMessage(messageValue.getBytes());
            message.setQos(qos);
            Log.e("sendToClient", " pushed at " + new Date() + " " + messageValue);
            /**
             * 如果发送P2P消息，二级Topic必须是“p2p”,三级topic是目标的ClientID
             * 此处设置的三级topic需要是接收方的ClientID
             */
            String topic = MqttConstant.MQTT_TOPIC + "/p2p/" + consumerClientId;
            msgPublish(context, messageValue, message, topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void msgPublish(Context context, String messageValue, MqttMessage message, String topic) throws MqttException {
        if (!MqttClientManager.getInstance().isOnLine()) {
            MqttClientManager.getInstance().restartMqttClientConnect();
            MqttTopicAndMsgValEntity entity = new MqttTopicAndMsgValEntity();
            entity.setTopic(topic);
            entity.setMessage(messageValue);
            MqttCallBackManager.getInstance().callbackMessage(entity, MqttMessageSendStatusEnum.STATUS_SEND_FAILURE);
        } else if (context != null) {
            MqttClientManager.getInstance().getMqttClient().publish(topic, message, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        MqttTopicAndMsgValEntity entity;
                        if (asyncActionToken == null) entity = null;
                        else {
                            String msg = new String(asyncActionToken.getResponse().getPayload());
                            entity = new MqttTopicAndMsgValEntity();
                            entity.setTopic(asyncActionToken.getTopics()[0]);
                            if (asyncActionToken == null || asyncActionToken.getTopics() == null || asyncActionToken.getTopics().length <= 0)
                                entity.setTopic("");
                            else entity.setMessage(msg);
                        }
                        MqttCallBackManager.getInstance().callbackMessage(entity, MqttMessageSendStatusEnum.STATUS_SEND_SUCCESS);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    try {
                        MqttTopicAndMsgValEntity entity;
                        if (asyncActionToken == null) entity = null;
                        else {
                            String msg = new String(asyncActionToken.getResponse().getPayload());
                            entity = new MqttTopicAndMsgValEntity();
                            entity.setTopic(asyncActionToken.getTopics()[0]);
                            if (asyncActionToken == null || asyncActionToken.getTopics() == null || asyncActionToken.getTopics().length <= 0)
                                entity.setTopic("");
                            else entity.setMessage(msg);
                        }
                        MqttCallBackManager.getInstance().callbackMessage(entity, MqttMessageSendStatusEnum.STATUS_SEND_FAILURE);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else
            MqttClientManager.getInstance().getMqttClient().publish(topic, message);
    }
}
