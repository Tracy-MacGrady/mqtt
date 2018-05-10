package com.toughen.mqttutil.message;

import android.text.TextUtils;

/**
 * Created by lijianjian on 2018/4/10.
 */

public class MqttTopicAndMsgValEntity {
    private String topic;//订阅的Topic  去除 / 后的数据
    private String message;//消息内容

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (TextUtils.isEmpty(topic)) this.topic = "";
        this.topic = topic.replace("/", "");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
