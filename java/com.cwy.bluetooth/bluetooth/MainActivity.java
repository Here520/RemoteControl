package com.cwy.bluetooth.bluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.cwy.remotecontrol.R;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler = new Handler()
    {
        public void handleMessage(Message paramMessage)
        {
            super.handleMessage(paramMessage);
        }
    };
    private String selectedBluetoothAddress = null;

    private boolean stateBluetooth()
    {
        if (this.bluetoothAdapter == null)
        {
            Toast.makeText(this, "此设备无蓝牙！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!this.bluetoothAdapter.isEnabled())
        {
            Toast.makeText(this, "蓝牙未开启。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void findDevices(View paramView)
    {
        if (stateBluetooth())
            startActivityForResult(new Intent(this, FoundDevices.class), 10);
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        if (paramInt2 == 0)
            Toast.makeText(this, "请开启蓝牙", Toast.LENGTH_SHORT).show();
        if (paramInt2 == 10) {
            this.selectedBluetoothAddress = paramIntent.getStringExtra("selectedBluetoothAddress").trim();
            Toast.makeText(this, "已选中设备，可以进行远程控制。", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.ble_activity_main);
    }


    protected void onResume()
    {
        super.onResume();
    }

    public void openBluetooth(View paramView)
    {
        if (this.bluetoothAdapter == null)
        {
            Toast.makeText(this, "此设备无蓝牙！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!this.bluetoothAdapter.isEnabled())
        {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"), 1);
            return;
        }
        Toast.makeText(this, "蓝牙已开启！", Toast.LENGTH_SHORT).show();
    }

    public void remoteControl(View paramView)
    {
        if (stateBluetooth())
        {
            Intent localIntent = new Intent(this, ControlActivity.class);
            localIntent.putExtra("bluetoothAddress", selectedBluetoothAddress);
            startActivityForResult(localIntent, 20);
        }
    }
}