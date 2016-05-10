package com.cwy.remotecontrol.util;

import android.os.Handler;

import java.net.Socket;

/**
 * Created by cwy on 2016/5/5.
 */
public class FinalData {
    private FinalData(){

    }
    public static final int RECEIVE_IP = 1;
    public static final int CONNECT_SUCCESS = 2;
    public static final int CONNECT_ERROR = 3;
    public static final int TCP_PORT = 5658;
    public static final int SOCKET_NULL = 4;
    public static String HostIp = null;
    public static String LEFTPRESS = "LEFTPRESS";
    public static String RIGHTPRESS = "RIGHTPRESS";
    public static final int REQUEST = 5;
}
