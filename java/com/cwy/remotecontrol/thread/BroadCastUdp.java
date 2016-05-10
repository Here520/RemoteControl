package com.cwy.remotecontrol.thread;

import android.util.Log;

import com.cwy.remotecontrol.wifi.WiFiInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by cwy on 2016/5/5.
 */
public class BroadCastUdp extends Thread {

    public static final int SERVER_PORT = 5659;
    public static final String BROADCAST_IP = "224.0.0.3";
    private String ip;
    private String TAG = "BroadCastUdp";
    private MulticastSocket ms ;
    public BroadCastUdp(String _ip) {
        ip = _ip;
        Log.i(TAG, "BroadCastUdp: "+ip);
        try {
            ms = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

            DatagramPacket dataPacket = null;
            try {
                ms.setTimeToLive(4);
                //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
                byte[] data = ip.getBytes();
                //224.0.0.1为广播地址
                InetAddress address = InetAddress.getByName(BROADCAST_IP);
                //这个地方可以输出判断该地址是不是广播类型的地址
                System.out.println(address.isMulticastAddress());
                dataPacket = new DatagramPacket(data, data.length, address,
                        SERVER_PORT);
                ms.send(dataPacket);
                ms.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}