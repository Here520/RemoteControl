package com.cwy.remotecontrol.thread;

import android.os.Message;
import android.util.Log;

import com.cwy.remotecontrol.Exception.MyException;
import com.cwy.remotecontrol.util.FinalData;
import com.cwy.remotecontrol.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by cwy on 2016/5/5.
 */
public class ReceiveUdp extends Thread {
    private static final String TAG = "ReceiveUdp";
    private static final int CLIENT_PORT = 5657;
    private DatagramSocket udpSocket;
    private String udpResult;
    private static final int MAX_DATA_PACKET_LENGTH = 100;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private boolean start = true;

    @Override
    public void run() {
        DatagramPacket packetReceive = new DatagramPacket(buffer,
                buffer.length);
        try {
            udpSocket = new DatagramSocket(CLIENT_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (start) {
            try {
                udpSocket.receive(packetReceive);
                udpResult = new String(packetReceive.getData(),
                        packetReceive.getOffset(), packetReceive.getLength());
                String ip = packetReceive.getAddress().getHostAddress();
                Log.i(TAG, "run: " + "hrq------Ip地址：" + ip + "-----返回消息-----" + udpResult);
                Message msg = new Message();
                msg.what = FinalData.RECEIVE_IP;
                msg.obj = udpResult;
                Utils.mHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        udpSocket.close();
    }
    public void stopReceiveUdp(){
        start = false;
    }
}
