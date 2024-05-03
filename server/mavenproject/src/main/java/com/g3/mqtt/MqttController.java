package com.g3.mqtt;

import com.g3.database.DatabaseController;
import com.g3.heatmap.Heatmap;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttController implements MqttCallback {
    private final String clientId = "Edge";
    private final String broker = "tcp://localhost:1883";
    public static Heatmap heatmap = null;

    private MqttClient client;

    private DatabaseController databaseController = new DatabaseController();

    public MqttController() {
        try {
            client = new MqttClient(broker, clientId);
            databaseController.connect();

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

    public void subscribeToAndroid1() {
        String topic = "android_1_to_edge";
        try {
            client.subscribe(topic); //subscribing to the topic name  test/topic
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToAndroid2() {
        String topic = "android_2_to_edge";
        try {
            client.subscribe(topic); //subscribing to the topic name  test/topic

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishToAndroid1(String content) {
        String topic = "edge_to_android_1";

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);
            sampleClient.publish(topic, message);
            sampleClient.disconnect();
        } catch (MqttException me) {
            System.out.println("reason" + me.getReasonCode());
            System.out.println("msg" + me.getMessage());
            System.out.println("loc" + me.getLocalizedMessage());
            System.out.println("cause" + me.getCause());
            System.out.println("excep" + me);
            me.printStackTrace();
        }
    }

    public void publishToAndroid2(String content) {
        String topic = "edge_to_android_2";

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);
            sampleClient.publish(topic, message);
            sampleClient.disconnect();
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

    public void printResult(String car) {
        databaseController.stats(car);
    }

    @Override

    // 20.00,26,23.759716,37.971674,105.698069,5.893475,85.62663442096351,42.813316

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (topic.equals("android_1_to_edge")) {
            String sss = new String(mqttMessage.getPayload());
            System.out.println("message received from android 1:" + sss);

            if (sss.equals("new_database")) {
                databaseController.remove(26);
                return;
            }

            if (sss.equals("end_transmission")) {
                printResult("car26");
                return;
            }
        } else {
            String sss = new String(mqttMessage.getPayload());
            System.out.println("message received from android 2:" + sss);

            if (sss.equals("new_database")) {
                databaseController.remove(27);
                return;
            }
            if (sss.equals("end_transmission")) {
                printResult("car27");
                return;
            }
        }


        String sss = new String(mqttMessage.getPayload());
        String[] fields = sss.split(",");


        String t = fields[0].substring(0, fields[0].indexOf('.'));
        long timestep = Long.parseLong(t);
        String device_id = fields[1];
        double real_long = Double.parseDouble(fields[2]);
        double real_lat = Double.parseDouble(fields[3]);
        double real_angle = Double.parseDouble(fields[4]);
        double real_speed = Double.parseDouble(fields[5]);

        double real_lat_radians = Math.toRadians(real_lat);
        double real_long_radians = Math.toRadians(real_long);
        double real_angle_radians = Math.toRadians(real_angle);

        double predicted_lat;
        double predicted_long;

        double R = 6.371 * 10E6;
        double T = 1;
        double delta = T * real_speed / R;

        predicted_lat = Math.asin(Math.sin(real_lat_radians) * Math.cos(delta) +
                Math.cos(real_lat_radians) * Math.sin(delta) * Math.cos(real_angle_radians));
        predicted_long = real_long_radians + Math.atan2(Math.sin(real_angle_radians) * Math.sin(delta) * Math.cos(real_lat_radians),
                Math.cos(delta) - Math.sin(real_lat_radians) * Math.sin(predicted_lat));

        double predicted_lat_degrees = Math.toDegrees(predicted_lat);
        double predicted_long_degrees = Math.toDegrees(predicted_long);

        double real_lat_degress = real_lat;
        double real_long_degress = real_long;


        if (heatmap != null) {
            double real_rssi = 0;
            double real_throughput = 0;

            double predicted_rssi = 0;
            double predicted_throughput = 0;

            double lon;
            double lat;

            boolean real_found = false;
            boolean pred_found = false;

            lon = real_long_degress;
            lat = real_lat_degress;

            int i, j;

            if (lat > 37.9668800 && lat <= 37.9673150) {
                i = 4;
//                System.out.println("i=4");
            } else if (lat > 37.9673150 && lat <= 37.9677500) {
                i = 3;
//                System.out.println("i=3");
            } else if (lat > 37.9677500 && lat <= 37.9681850) {
                i = 2;
//                System.out.println("i=2");
            } else if (lat > 37.9681850 && lat <= 37.9686200) {
                i = 1;
//                System.out.println("i=1");
            } else {
//                System.out.println("latitude out of map");
                i = -1;
            }

            if (lon > 23.7647600 && lon < 23.7658230) {
                j = 1;
//                System.out.println("j=1");
            } else if (lon > 23.7658230 && lon <= 23.7668860) {
                j = 2;
//                System.out.println("j=2");
            } else if (lon > 23.7668860 && lon <= 23.7679490) {
                j = 3;
//                System.out.println("j=3");
            } else if (lon > 23.7679490 && lon <= 23.7690120) {
                j = 4;
//                System.out.println("j=4");
            } else if (lon > 23.7690120 && lon <= 23.7700750) {
                j = 5;
//                System.out.println("j=5");
            } else if (lon > 23.7700750 && lon <= 23.7711380) {
                j = 6;
//                System.out.println("j=6");
            } else if (lon > 23.7711380 && lon <= 23.7722010) {
                j = 7;
//                System.out.println("j=7");
            } else if (lon > 23.7722010 && lon <= 23.7732640) {
                j = 8;
//                System.out.println("j=8");
            } else if (lon > 23.7732640 && lon <= 23.7743270) {
                j = 9;
//                System.out.println("j=9");
            } else if (lon > 23.7743270 && lon <= 23.7753900) {
                j = 10;
//                System.out.println("j=10");
            } else {
                System.out.println("longtitude out of map");
                j = -1;
            }

            i--;
            j--;

//            System.out.println("i=" + i + ", " + j);


            if (i >= 0 && j >= 0) {
                real_found = true;
                real_rssi = heatmap.rssi_values[i][j];
                real_throughput = heatmap.throughput_values[i][j];
            }


            lon = predicted_long_degrees;
            lat = predicted_lat_degrees;

            if (lat > 37.9668800 && lat <= 37.9673150) {
                i = 4;
//                System.out.println("i=4");
            } else if (lat > 37.9673150 && lat <= 37.9677500) {
                i = 3;
//                System.out.println("i=3");
            } else if (lat > 37.9677500 && lat <= 37.9681850) {
                i = 2;
//                System.out.println("i=2");
            } else if (lat > 37.9681850 && lat <= 37.9686200) {
                i = 1;
//                System.out.println("i=1");
            } else {
//                System.out.println("latitude out of map");
                i = -1;
            }

            if (lon > 23.7647600 && lon < 23.7658230) {
                j = 1;
//                System.out.println("j=1");
            } else if (lon > 23.7658230 && lon <= 23.7668860) {
                j = 2;
//                System.out.println("j=2");
            } else if (lon > 23.7668860 && lon <= 23.7679490) {
                j = 3;
//                System.out.println("j=3");
            } else if (lon > 23.7679490 && lon <= 23.7690120) {
                j = 4;
//                System.out.println("j=4");
            } else if (lon > 23.7690120 && lon <= 23.7700750) {
                j = 5;
//                System.out.println("j=5");
            } else if (lon > 23.7700750 && lon <= 23.7711380) {
                j = 6;
//                System.out.println("j=6");
            } else if (lon > 23.7711380 && lon <= 23.7722010) {
                j = 7;
//                System.out.println("j=7");
            } else if (lon > 23.7722010 && lon <= 23.7732640) {
                j = 8;
//                System.out.println("j=8");
            } else if (lon > 23.7732640 && lon <= 23.7743270) {
                j = 9;
//                System.out.println("j=9");
            } else if (lon > 23.7743270 && lon <= 23.7753900) {
                j = 10;
//                System.out.println("j=10");
            } else {
//                System.out.println("longtitude out of map");
                j = -1;
            }

            i--;
            j--;

//            System.out.println("i=" + i + ", " + j);

            if (i >= 0 && j >= 0) {
                pred_found = true;
                predicted_rssi = heatmap.rssi_values[i][j];
                predicted_throughput = heatmap.throughput_values[i][j];
            }

            if (real_found && pred_found) {
                // denormalization
                real_rssi = real_rssi * 80 + 20;
                predicted_rssi = predicted_rssi * 80 + 20;
                //gauss
                real_throughput = real_throughput * 50 + 10;
                predicted_throughput = predicted_throughput * 50 + 10;

                databaseController.insert(timestep, device_id, real_lat, real_long, predicted_lat_degrees, predicted_long_degrees, real_rssi, real_throughput, predicted_rssi, predicted_throughput);
            } else {
                System.out.println("location out of map, sample skipped");
            }

            String topic_android;
            if (topic.equals("android_1_to_edge")) {
                topic_android = "edge_to_android_1";
            } else {
                topic_android = "edge_to_android_2";
            }

            String message_content = String.format("%f,%f,%f,%f,%f,%f,%f,%f",
                    real_lat,
                    real_long,
                    predicted_lat_degrees,
                    predicted_long_degrees,
                    real_rssi,
                    real_throughput,
                    predicted_rssi,
                    predicted_throughput
            );

            MqttMessage m = new MqttMessage(message_content.getBytes());

            client.publish(topic_android, m);
        } else {
            System.out.println(" **** WARNING ****  Sample skipped. Please load heatmap ");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
