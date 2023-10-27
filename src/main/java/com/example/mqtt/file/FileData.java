package com.example.mqtt.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author waani
 * @date 2023/4/3 09:44
 */
@Getter
@Setter
@AllArgsConstructor
public class FileData implements Serializable {

    /**
     * 文件唯一标识
     * file md5
     */
    private String fileId ;

    /**
     * 文件名：hello.png
     */
    private String fileName ;

    /**
     * 文件块：Base64加密字节数组
     * 每块最大值为 1m
     * ' 设置为1m的原因：大部分文件在 1m以内，只需要发送一条消息即可
     */
    private String content ;

    /**
     * 文件块排序
     */
    private long sort ;

    /**
     * 总块数
     * 文件最大值10m，即最大10块
     */
    private long size ;


    private byte[] bytes ;

}
