package com.cwy.bluetooth.bluetooth;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cwy.remotecontrol.R;

import java.util.ArrayList;
import java.util.List;

public class FoundDevices extends AppCompatActivity
        implements OnItemClickListener, OnClickListener
{
    private ArrayAdapter<String> arrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private List<String> bluetoothInfo = new ArrayList();
    private Button bt_search;
    private DevicesReceiver devicesReceiver = new DevicesReceiver();
    private boolean hasRegister = false;
    private ListView lv_found;
    private String selectedBluetoothAddress;

    private void setView()
    {
        this.lv_found = ((ListView)findViewById(R.id.lv_foundDevices));
        this.bt_search = ((Button)findViewById(R.id.bt_search));
        this.arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, this.bluetoothInfo);
        this.lv_found.setAdapter(this.arrayAdapter);
        this.lv_found.setOnItemClickListener(this);
        this.bt_search.setOnClickListener(this);
    }

    public void onClick(View paramView)
    {
        if ((bluetoothAdapter != null) && (bluetoothAdapter.isDiscovering()))
        {
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(this, "正在停止搜索...", Toast.LENGTH_SHORT).show();
            bt_search.setText("开始搜索");
            return;
        }
        if ((bluetoothAdapter != null) &&(!bluetoothAdapter.isDiscovering())) {
            bluetoothAdapter.startDiscovery();
            Toast.makeText(this, "开始搜索请耐心等待...", Toast.LENGTH_SHORT).show();
            bt_search.setText("停止搜索");
        }
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.ble_activity_found_devices);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setView();
    }

    protected void onDestroy()
    {
        if ((this.bluetoothAdapter != null) && (this.bluetoothAdapter.isDiscovering()))
            this.bluetoothAdapter.cancelDiscovery();
        if (this.hasRegister)
        {
            this.hasRegister = false;
            unregisterReceiver(this.devicesReceiver);
        }
        super.onDestroy();
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        String str = (String)this.bluetoothInfo.toArray()[paramInt];
        this.selectedBluetoothAddress = str.substring(-17 + str.length());
        Intent localIntent = new Intent();
        localIntent.putExtra("selectedBluetoothAddress", this.selectedBluetoothAddress);
        setResult(10, localIntent);
        finish();
    }

    protected void onStart()
    {
        if (!this.hasRegister)
        {
            this.hasRegister = true;
            IntentFilter localIntentFilter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(this.devicesReceiver, localIntentFilter1);
        }
        super.onStart();
    }

    private class DevicesReceiver extends BroadcastReceiver
    {
        private DevicesReceiver()
        {
        }

        public void onReceive(Context paramContext, Intent paramIntent)
        {
            String str1 = paramIntent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(str1))
            {
                BluetoothDevice localBluetoothDevice = paramIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str2 = localBluetoothDevice.getName();
                String str3 = localBluetoothDevice.getAddress();
                FoundDevices.this.bluetoothInfo.add(str2 + '\n' + str3);
                FoundDevices.this.arrayAdapter.notifyDataSetChanged();
            }
        }
    }
}