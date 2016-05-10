package com.cwy.remotecontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cwy.bluetooth.bluetooth.*;
import com.cwy.remotecontrol.util.FinalData;
import com.cwy.remotecontrol.util.Utils;

public class RemoteControlMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_remote_control_main);
    }

    public void blueToothMode(View view){
        Intent intent = new Intent(getApplicationContext(), com.cwy.bluetooth.bluetooth.MainActivity.class);
        startActivity(intent);
    }
    public void mifiMode(View view){
        Intent intent = new Intent(getApplicationContext(),FoundDevices.class);
        startActivityForResult(intent, FinalData.REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FinalData.REQUEST){
            Utils.receiveUdp.stopReceiveUdp();
            Utils.receiveUdp = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
