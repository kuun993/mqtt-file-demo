package com.example.mqtt.file;

import com.alibaba.fastjson.JSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

/**
 * 文件发送方
 * @author waani
 * @date 2023/4/3 09:14
 */
public class FileProducer extends MqttConstant {


    private MqttTopic topic = null ;


    public FileProducer() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        options.setUserName(MQTT_USER);
        options.setPassword(MQTT_PASSWORD.toCharArray());
        MqttClient mqttClient = new MqttClient(MQTT_BROKER_HOST, "FileProducer", new MemoryPersistence());
        mqttClient.connect(options);
        topic = mqttClient.getTopic(MQTT_TOPIC);
    }


    private void send(FileData fileData) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setQos(1);
        message.setRetained(false);
        message.setPayload(JSON.toJSONString(fileData).getBytes());
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("发送成功！");
    }



    public void run() {

        int length = 1024 * 1024;

        File file = new File("/Users/waani/temp/mqtt/producer/male.png") ;

        long size = (file.length() / length) + 1 ;
        long sort = 1 ;

        // or file md5
        String key = UUID.randomUUID().toString() ;

        String name = "male2.png" ;
        try(FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[length] ;
            while (inputStream.read(bytes)>0){
                output.write(bytes);
                FileData fileData =
                        new FileData(key, name, Base64.getEncoder().encodeToString(bytes), sort, size,null) ;
                send(fileData);
                sort++ ;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws MqttException {
        FileProducer fileServer = new FileProducer() ;
        fileServer.run();
    }

}
