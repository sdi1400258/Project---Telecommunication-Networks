package com.g3.myapp.mqtt;

import com.g3.myapp.internetoptions.ApplicationOptions;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.security.auth.callback.Callback;

public class MqttController implements MqttCallback {
    private  String clientId = "Android";
    private String broker = "tcp://192.168.43.189:1883";

    private MqttClient client;

    public MqttController(int role) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();

            broker = "tcp://" + ApplicationOptions.getEdgeIp() +":" + ApplicationOptions.getEdgePort();
            clientId = "clientid" + role;

            client = new MqttClient(broker, clientId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        client.setCallback(this);
        MqttConnectOptions mqOptions = new MqttConnectOptions();
        mqOptions.setCleanSession(true);
        try {
            client.connect(mqOptions);      //connecting to broker
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToEdgeServer(int role, MqttCallback callback) {
        String topic = "edge_to_android_" + role;

        try {
            client.setCallback(callback);
            client.subscribe(topic); //subscribing to the topic name  test/topic
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishToEdge(String content, int role) {
        String topic = "android_" + role + "_to_edge";

        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);
            client.publish(topic, message);
        } catch (MqttException me) {
            System.out.println("reason" + me.getReasonCode());
            System.out.println("msg" + me.getMessage());
            System.out.println("loc" + me.getLocalizedMessage());
            System.out.println("cause" + me.getCause());
            System.out.println("excep" + me);
            me.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (topic.equals("android_1_to_edge")) {
            String sss = new String(mqttMessage.getPayload());
            System.out.println("message received from android 1:" + sss);
        } else {
            String sss = new String(mqttMessage.getPayload());
            System.out.println("message received from android 2:" + sss);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public void disconnect() {

        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
