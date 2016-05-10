package com.cwy.remotecontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cwy.remotecontrol.util.FinalData;
import com.cwy.remotecontrol.util.Utils;
import com.cwy.remotecontrol.wifi.WiFiInfo;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class FoundDevices extends AppCompatActivity {
    private static String TAG = "WifiBroadcastActivity";
    private String address;
    private Button bt_find = null;
    private WiFiInfo wiFiInfo = null;
    private MyHandler myHandler = new MyHandler();
    private ProgressDialog dialog = null;
    private ListView lv_pcInfo = null;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> pcInfo = new ArrayList();
    private List<String> pcIP = new ArrayList();


    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case FinalData.RECEIVE_IP:
                    Utils.broadCastUdp = null;
                    dialog.cancel();
                    String[] pc = ((String)msg.obj).split("##");
                    if (!pcInfo.contains("计算机名称："+"\n"+pc[1])) {
                        pcInfo.add("计算机名称："+"\n"+pc[1]);
                        pcIP.add(pc[0]);
                        arrayAdapter.notifyDataSetChanged();

                    }
                    break;
                case FinalData.CONNECT_SUCCESS:
                    Utils.socket = (Socket) msg.obj;
                    dialog.cancel();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_devices);
        bt_find = (Button) findViewById(R.id.bt_find);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, this.pcInfo);
        lv_pcInfo = (ListView) findViewById(R.id.lv_devices);
        lv_pcInfo.setAdapter(arrayAdapter);

        lv_pcInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog = ProgressDialog.show(FoundDevices.this, "", "正在连接请稍后。。。", false);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                new ConnectThread(pcIP.get(position)).start();
            }
        });
        if (Utils.wiFiInfo == null){
            wiFiInfo = new WiFiInfo(this);
            Utils.wiFiInfo = wiFiInfo;
        }
        else wiFiInfo = Utils.wiFiInfo;

        Utils.mHandler = myHandler;
    }

    @Override
    protected void onRestart() {
        if (Utils.receiveUdp == null)
            Log.i(TAG, "onCreate: "+true);
        else
            Log.i(TAG, "onCreate: "+false);
        Log.i(TAG, "onRestart: success");
        super.onRestart();
    }

    public void findDevices(View view) {
        if (wiFiInfo.isOpen()) {
                dialog = ProgressDialog.show(this, "", "正在搜索请稍后。。。", false);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Utils.broadCastUdp = null;
                }
            });
                dialog.show();
                Utils.startUDP();
            } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请打开WIFI，并与电脑连接同一WIFI。");
            builder.setTitle("提醒");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        bt_find.setText("继续搜索");
    }

    @Override
    protected void onStop() {
        if (Utils.receiveUdp!=null) {
            Log.i(TAG, "onStop: " + Utils.receiveUdp.isAlive());
        }
        if (Utils.broadCastUdp != null){
            Utils.broadCastUdp = null;
        }
        super.onStop();
    }

    private class ConnectThread extends Thread{
        private String ip;
        private Socket socket;
        public ConnectThread(String ip){
            this.ip = ip;
        }
        @Override
        public void run() {
                    try {
                        socket = new Socket(ip, FinalData.TCP_PORT);
                        Message msg1 = new Message();
                        msg1.what = FinalData.CONNECT_SUCCESS;
                        msg1.obj = socket;
                        myHandler.sendMessage(msg1);
                    } catch (Exception e) {
                        Message msg2 = new Message();
                        msg2.what = FinalData.CONNECT_ERROR;
                        myHandler.sendMessage(msg2);
                        e.printStackTrace();
                    }
                }
            }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

