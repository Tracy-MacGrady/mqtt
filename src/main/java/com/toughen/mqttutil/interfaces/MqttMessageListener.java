package com.toughen.mqttutil.interfaces;


import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;

/**
 * Created by 李健健 on 2017/6/22.
 */

public interface MqttMessageListener<T> {
    void parseMsgFromString(MqttMessageSendStatusEnum statusEnum, String msgValue);

    void msgArrived(T msgModelInfo);

    void msgSendSuccess(T msgModelInfo);

    void msgSendFailure(T msgModelInfo);
}
