package com.toughen.mqttutil.getconnect;

import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.toughen.mqttutil.constant.MqttConstant;
import com.toughen.mqttutil.enums.MqttConnectStatusEnum;
import com.toughen.mqttutil.handlers.MqttConnectListener;
import com.toughen.mqttutil.interfaces.MqttClientConnectStatusListener;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * Created by 李健健 on 2017/12/1.
 */

public class GetMqttClientConnect {
    private String clientid;
    private MqttCallbackExtended callBack;
    private Context context;
    private int connectNum = 0;
    private MyMqttAsyncClient mqttClient;
    private MqttConnectOptions connOpts;
    private MqttConnectListener mqttConnectListener;
    private static volatile GetMqttClientConnect instance;

    private GetMqttClientConnect() {
    }

    public static GetMqttClientConnect getInstance() {
        if (instance == null) synchronized (GetMqttClientConnect.class) {
            if (instance == null) instance = new GetMqttClientConnect();
        }
        return instance;
    }

    public synchronized void init(String clientid, MqttCallbackExtended callBack, MqttConnectListener listener, Context context) {
        this.clientid = clientid;
        this.callBack = callBack;
        this.mqttConnectListener = listener;
        this.context = context;
        this.connectNum = 0;
        initMqttAndroidClient();
    }

    private void initMqttAndroidClient() {
        try {
            if (this.mqttClient != null && this.connOpts != null) return;
            String producerClientId = MqttConstant.MQTT_GROUPID + "@@@ClientID_" + clientid;
            MemoryPersistence persistence = new MemoryPersistence();
            this.mqttClient = new MyMqttAsyncClient(MqttConstant.MQTT_BROKER, producerClientId, persistence);
            this.mqttClient.setCallback(callBack);
            this.connOpts = new MqttConnectOptions();
            this.connOpts.setUserName(MqttConstant.MQTT_USERNAME);
            this.connOpts.setServerURIs(new String[]{MqttConstant.MQTT_BROKER});
            this.connOpts.setPassword(MqttConstant.MQTT_PASSWORD.toCharArray());
            this.connOpts.setCleanSession(false);
            this.connOpts.setKeepAliveInterval(80);
            this.connOpts.setAutomaticReconnect(true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public synchronized void startConnect() {
        try {
            if (mqttClient == null || connOpts == null) initMqttAndroidClient();
            if (!mqttClient.isConnected())
                getConnect();
            else
                mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_RECONNECT_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    private void getConnect() {
        try {
            if (mqttClient.isConnected()) {
                mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_RECONNECT_SUCCESS);
                return;
            }
            if (mqttClient.getComms().isConnecting() || mqttClient.getComms().isDisconnecting())
                return;
            mqttClient.connect(connOpts, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.e("GetMqttClientConnect", "come in the method onSuccess");
                    connectNum = 0;
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e("GetMqttClientConnect", "come in the method onFailure this is connectNum" + connectNum);
                    if (connectNum < 5) {
                        connectNum++;
                        startConnect();
                    } else {
                        CrashReport.postCatchedException(throwable);
                        mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_CONNECT_FAILURE);
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    public synchronized void disConnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                if (mqttClient.getComms().isDisconnecting()) return;
                mqttClient.disconnect(context, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("GetMqttClientConnect", "come in the method disconnect onSuccess");
                        mqttClient = null;
                        connOpts = null;
//                        mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_DISCONNECT_SUCCESS);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("GetMqttClientConnect", "come in the method disconnect onFailure");
                        mqttClient = null;
                        connOpts = null;
                        mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_DISCONNECT_FAILURE);
                    }
                });
            } else {
                mqttClient = null;
                connOpts = null;
                mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_DISCONNECT_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mqttClient = null;
            connOpts = null;
            mqttConnectListener.handleMessage(MqttConnectStatusEnum.STATUS_DISCONNECT_FAILURE);
        }
    }

    public MqttAsyncClient getMqttClient() {
        return mqttClient;
    }

    public synchronized void reConnect() {
        try {
            mqttClient.reconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
