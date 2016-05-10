package com.cwy.remotecontrol.util;

import android.os.Handler;
import android.util.Log;

import com.cwy.remotecontrol.thread.BroadCastUdp;
import com.cwy.remotecontrol.thread.ReceiveUdp;
import com.cwy.remotecontrol.wifi.WiFiInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

/**
 * Created by cwy on 2016/5/5.
 */
public class Utils {

    static private String LOG_TAG = "Utils";
    public static  Handler mHandler = null;
    public static  ReceiveUdp receiveUdp = null;
    public static  BroadCastUdp broadCastUdp = null;
    public static Socket socket = null;
    public static WiFiInfo wiFiInfo = null;
    private Utils(){

    }
    static public void startUDP(){
        if (receiveUdp == null){
            receiveUdp= new ReceiveUdp();
            receiveUdp.start();
        }
        if (broadCastUdp == null){
            broadCastUdp = new BroadCastUdp(getIP());
            broadCastUdp.start();
        }


    }
    public static String getIP(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        Log.i(LOG_TAG, inetAddress.getHostAddress().toString());
                        if (inetAddress.getHostAddress().toString().length()<=15)
                            return inetAddress.getHostAddress().toString();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
