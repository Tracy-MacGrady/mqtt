package com.toughen.mqttutil.getconnect;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.toughen.mqttutil.constant.MacSignature;
import com.toughen.mqttutil.constant.MqttConstant;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 李健健 on 2017/12/1.
 */

public class GetMqttClientConnect {
    private String clientid;
    private MqttCallbackExtended callBack;
    private Handler handler;
    private Context context;
    private int connectNum = 0;
    private MyMqttAsyncClient mqttClient;
    private MqttConnectOptions connOpts;
    private String sign;
    private static GetMqttClientConnect instance;

    private GetMqttClientConnect() {
    }

    public static GetMqttClientConnect getInstance() {
        if (instance == null) synchronized (GetMqttClientConnect.class) {
            if (instance == null) instance = new GetMqttClientConnect();
        }
        return instance;
    }

    public synchronized void init(String clientid, MqttCallbackExtended callBack, Handler handler, Context context) {
        this.clientid = clientid;
        this.callBack = callBack;
        this.handler = handler;
        this.context = context;
        this.connectNum = 0;
        initMqttAndroidClient();
    }

    private synchronized void initMqttAndroidClient() {
        try {
            if (this.mqttClient != null && this.connOpts != null) return;
            this.sign = MacSignature.macSignature(MqttConstant.MQTT_GROUPID, MqttConstant.MQTT_SECRETKEY);
            String producerClientId = MqttConstant.MQTT_GROUPID + "@@@ClientID_" + clientid;
            MemoryPersistence persistence = new MemoryPersistence();
            this.mqttClient = new MyMqttAsyncClient(MqttConstant.MQTT_BROKER, producerClientId, persistence);
            this.mqttClient.setCallback(callBack);
            this.connOpts = new MqttConnectOptions();
            this.connOpts.setUserName(MqttConstant.MQTT_ACCESSKEY);
            this.connOpts.setServerURIs(new String[]{MqttConstant.MQTT_BROKER});
            this.connOpts.setPassword(sign.toCharArray());
            this.connOpts.setCleanSession(false);
            this.connOpts.setKeepAliveInterval(80);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    public void setConnectNum(int connectNum) {
        this.connectNum = connectNum;
    }

    public synchronized void startConnect() {
        try {
            if (mqttClient == null || connOpts == null) initMqttAndroidClient();
            if (!mqttClient.isConnected())
                getConnect();
            else handler.sendEmptyMessage(102);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    private synchronized void getConnect() {
        try {
            if (mqttClient.isConnected()) {
                handler.sendEmptyMessage(102);
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
                        handler.sendEmptyMessage(101);
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    public void disConnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                if (mqttClient.getComms().isDisconnecting()) return;
                mqttClient.disconnect(context, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("GetMqttClientConnect", "come in the method disconnect onSuccess");
                        mqttClient = null;
                        connOpts = null;
                        handler.sendEmptyMessage(200);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("GetMqttClientConnect", "come in the method disconnect onFailure");
                        mqttClient = null;
                        connOpts = null;
                        handler.sendEmptyMessage(201);
                    }
                });
            } else {
                mqttClient = null;
                connOpts = null;
                if (handler != null) handler.sendEmptyMessage(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mqttClient = null;
            connOpts = null;
            if (handler != null) handler.sendEmptyMessage(201);
        }
    }

    public MqttAsyncClient getMqttClient() {
        return mqttClient;
    }

}
