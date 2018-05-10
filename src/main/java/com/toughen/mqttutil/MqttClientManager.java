package com.toughen.mqttutil;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.toughen.mqttutil.connectstatus.MqttConnectionStatusReceiver;
import com.toughen.mqttutil.connectstatus.MqttConnectionStatusService;
import com.toughen.mqttutil.constant.MqttConstant;
import com.toughen.mqttutil.constant.MqttConstantParamsEntity;
import com.toughen.mqttutil.enums.MqttConnectStatusEnum;
import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;
import com.toughen.mqttutil.getconnect.GetMqttClientConnect;
import com.toughen.mqttutil.handlers.MqttConnectHandler;
import com.toughen.mqttutil.message.MqttTopicAndMsgValEntity;
import com.toughen.mqttutil.message.tool.GetMessageTool;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * Created by Administrator on 2017/12/26.
 */

public class MqttClientManager {
    private Context context;
    private boolean canRepeatConnect = false;
    private MqttCallbackExtended mqttCallback;
    private MqttConnectionStatusReceiver myMqttConnReceiver;
    private MqttConnectHandler mqttStatusHandler;


    private static MqttClientManager instance;

    private MqttClientManager() {
    }

    public static MqttClientManager getInstance() {
        if (instance == null) synchronized (MqttClientManager.class) {
            if (instance == null) instance = new MqttClientManager();
        }
        return instance;
    }

    /**
     * context1 为application
     * 初始化
     */
    public synchronized void init(Context context1, String clientId, MqttConstantParamsEntity entity) {
        if (entity == null || context1 == null || TextUtils.isEmpty(clientId)) {
            CrashReport.postCatchedException(new NullPointerException());
            throw new NullPointerException();
        } else {
            this.context = context1;
            this.mqttStatusHandler = new MqttConnectHandler();
            initMqttCallBack();
            initMqttConnReceiver();
            initMqttConstant(entity);
            GetMqttClientConnect.getInstance().init(clientId, mqttCallback, mqttStatusHandler, context);
        }
    }

    private synchronized void initMqttConstant(MqttConstantParamsEntity entity) {
        MqttConstant.init(entity);
    }

    /**
     * 监听MQTT 连接状态的广播
     */
    private void initMqttConnReceiver() {
        if (myMqttConnReceiver == null) myMqttConnReceiver = new MqttConnectionStatusReceiver() {
            @Override
            public void toRepeatConnect() {
                restartMqttClientConnect();
            }
        };
    }

    /**
     * 实例化MQTT client的回调
     */
    private void initMqttCallBack() {
        if (mqttCallback == null) mqttCallback = new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                //TODO 连接成功
                Log.e("MqttClientManager", "connectComplete ======" + reconnect);
                if (reconnect || canRepeatConnect) {
                    MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_RECONNECT_SUCCESS);
                } else {
                    canRepeatConnect = true;
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(MqttConnectionStatusService.REPEAT_ACTION);
                    LocalBroadcastManager.getInstance(context).registerReceiver(myMqttConnReceiver, filter);
                    MqttCallBackManager.getInstance().callbackConnectStatus(MqttConnectStatusEnum.STATUS_CONNECT_SUCCESS);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                //TODO 连接已经断开
                Log.e("MqttClientManager", "connection lost======");
                GetMessageTool.clearAllTopic();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //TODO 消息到达
                Log.e("MqttClientManager", topic + "msg arrived===" + message);
                Message msg = Message.obtain();
                MqttTopicAndMsgValEntity entity = new MqttTopicAndMsgValEntity();
                entity.setTopic(topic);
                entity.setMessage(new String(message.getPayload()));
                msg.obj = entity;
                msg.what = 1001;
                mqttHandler.sendMessage(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };
    }

    /**
     * 连接MQTT client
     */
    public synchronized void startMqttClientConnect() {
        GetMqttClientConnect.getInstance().startConnect();
    }

    /**
     * 重连MQTT client
     */
    public synchronized void restartMqttClientConnect() {
        if (canRepeatConnect) {
            GetMqttClientConnect.getInstance().setConnectNum(0);
            GetMqttClientConnect.getInstance().startConnect();
        }
    }

    /**
     * 断开MQTT client 连接
     */
    public synchronized void disconnectMqttClientConnect() {
        GetMqttClientConnect.getInstance().disConnect();
    }

    public synchronized void clearMqttListener() {
        MqttCallBackManager.getInstance().removeAllConnectStatusListenr();
        if (context != null && myMqttConnReceiver != null)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(myMqttConnReceiver);
        mqttCallback = null;
        canRepeatConnect = false;
        myMqttConnReceiver = null;
    }

    /**
     * 是否处于连接状态
     */
    public synchronized boolean isOnLine() {
        if (getMqttClient() == null) return false;
        return getMqttClient().isConnected();
    }

    public MqttAsyncClient getMqttClient() {
        return GetMqttClientConnect.getInstance().getMqttClient();
    }


    /**
     * 消息分发
     */
    private Handler mqttHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (msg) {
                //TODO 消息到达后的操作
                MqttTopicAndMsgValEntity entity = (MqttTopicAndMsgValEntity) msg.obj;
                MqttCallBackManager.getInstance().callbackMessage(entity, MqttMessageSendStatusEnum.STATUS_MSG_ARRIVED);
            }
        }
    };
}
