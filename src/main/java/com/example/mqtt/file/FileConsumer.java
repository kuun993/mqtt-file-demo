package com.example.mqtt.file;

import com.alibaba.fastjson2.JSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author waani
 * @date 2023/4/3 09:15
 */
public class FileConsumer extends MqttConstant {


    private static Map<String, List<FileData>> map = new HashMap<>() ;



    public static void main(String[] args) {


        try {
            MqttClient client = new MqttClient(MQTT_BROKER_HOST, "FileConsumer", new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            options.setUserName(MQTT_USER);
            options.setPassword(MQTT_PASSWORD.toCharArray());

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                }
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("topic:"+topic);

                    FileData fileData = null ;
                    try {
                        fileData = JSON.parseObject(new String(message.getPayload()), FileData.class);
                        fileData.setBytes(Base64.getDecoder().decode(fileData.getContent()));
                    }catch (Exception e){
                        System.out.println("格式转换失败...");
                    }

                    String key = fileData.getFileId();
                    long size = fileData.getSize() ;
                    if(!map.containsKey(key)){
                       List<FileData> fileDataList = new ArrayList<>() ;
                       map.put(key, fileDataList) ;
                    }
                    List<FileData> fileDataList = map.get(key) ;
                    fileDataList.add(fileData) ;
                    if(fileDataList.size() == size){
                        // 生成文件
                        System.out.println("生成文件...");
                        String fileName = fileData.getFileName() ;
                        fileDataList = fileDataList.stream().sorted(Comparator.comparing(FileData::getSort)).collect(Collectors.toList());
                        File file = new File("/Users/waani/temp/mqtt/consumer/" + fileName) ;
                        try(FileOutputStream outputStream = new FileOutputStream(file, true)){
                            for (FileData data : fileDataList) {
                                byte[] bytes = data.getBytes() ;
                                outputStream.write(bytes);
                            }
                        }catch (IOException ioException){
                            ioException.printStackTrace();
                        }

                    }else{
                        System.out.println("继续接收文件块...");
                    }

                }
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });
            client.connect(options);
            //订阅消息
            client.subscribe(MQTT_TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
