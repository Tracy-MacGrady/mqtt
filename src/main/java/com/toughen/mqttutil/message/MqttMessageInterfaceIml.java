package com.toughen.mqttutil.message;

import com.alibaba.fastjson.JSONObject;
import com.toughen.mqttutil.enums.MqttMessageSendStatusEnum;
import com.toughen.mqttutil.interfaces.MqttMessageListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/26.
 */

public abstract class MqttMessageInterfaceIml<T> implements MqttMessageListener<T> {

    @Override
    public void parseMsgFromString(MqttMessageSendStatusEnum statusEnum, String msgValue) {
        if (msgValue == null) msgSendFailure(null);
        T t;
        if (getType() == String.class) {
            t = (T) msgValue;
        } else
            t = JSONObject.parseObject(msgValue, getType());
        switch (statusEnum) {
            case STATUS_MSG_ARRIVED:
                msgArrived(t);
                break;
            case STATUS_SEND_SUCCESS:
                msgSendSuccess(t);
                break;
            case STATUS_SEND_FAILURE:
                msgSendFailure(t);
                break;
        }
    }

    /**
     * 获取当前的类型
     *
     * @return
     */
    private Type getType() {
        Type type = String.class;
        Type mySuperClass = this.getClass().getGenericSuperclass();
        if (mySuperClass instanceof ParameterizedType)
            type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }
}
