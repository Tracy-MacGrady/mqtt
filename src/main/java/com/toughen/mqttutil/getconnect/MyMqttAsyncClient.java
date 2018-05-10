package com.toughen.mqttutil.getconnect;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;

/**
 * Created by Administrator on 2017/12/26.
 */

public class MyMqttAsyncClient extends MqttAsyncClient {
    public MyMqttAsyncClient(String serverURI, String clientId) throws MqttException {
        super(serverURI, clientId);
    }

    public MyMqttAsyncClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        super(serverURI, clientId, persistence);
    }

    public MyMqttAsyncClient(String serverURI, String clientId, MqttClientPersistence persistence, MqttPingSender pingSender) throws MqttException {
        super(serverURI, clientId, persistence, pingSender);
    }

    public ClientComms getComms() {
        return comms;
    }
}
