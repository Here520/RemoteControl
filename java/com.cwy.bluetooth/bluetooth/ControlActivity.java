package com.cwy.bluetooth.bluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cwy.bluetooth.bluetooth.utils.BluetoothUtil;
import com.cwy.remotecontrol.R;

public class ControlActivity extends AppCompatActivity
{
    private final int AcceptThread_SUCCESS_CONNECT = 1;
    private final int CONNECT_CLOSEED = 7;
    public final int CONNECT_ERROR = 6;
    private BluetoothSocket ConnectedSocket = null;
    private final int ConnetThread_SUCCESS_CONNECT = 2;
    private final int DATA = 3;
    public final int SEND_ERROR = 5;
    public final int SEND_SUCCESS = 4;
    private AcceptThread acceptThread = null;
    private ArrayAdapter<String> arrayAdapter = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private View callView = null;
    private ConnetThread connetThread = null;
    private String content = null;
    private EditText et_content = null;
    private LinearLayout ll_mode = null;
    private ListView lv_massage = null;
    private List<String> massages = new ArrayList();
    private BluetoothDevice mmdevice = null;
    private View sendView = null;
    private TextView tv_state = null;
    private readThread writeThread = null;
    private Handler handler = new Handler(){
        public void handleMessage(Message paramMessage)
        {
            String[] arrayOfString;
            switch (paramMessage.what)
            {
                case AcceptThread_SUCCESS_CONNECT:
                    Toast.makeText(ControlActivity.this, "连接成功,可以传输数据。", Toast.LENGTH_SHORT).show();
                    ConnectedSocket = ((BluetoothSocket)paramMessage.obj);
                    writeThread = new readThread(ConnectedSocket);
                    writeThread.start();
                    break;
                case ConnetThread_SUCCESS_CONNECT:
                    Toast.makeText(ControlActivity.this, "连接成功,可以传输数据。", Toast.LENGTH_SHORT).show();
                    ConnectedSocket = ((BluetoothSocket)paramMessage.obj);
                    writeThread = new readThread(ConnectedSocket);
                    writeThread.start();
                    break;
                case CONNECT_ERROR:
                    Toast.makeText(ControlActivity.this, "连接失败请重试。", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_CLOSEED:
                    finish();
                    break;
                case DATA:
                    arrayOfString = ((String)paramMessage.obj).split("##");
                    if ("TEL".equals(arrayOfString[0])) {
                        dialTelephone(arrayOfString[1]);
                        Log.i("DATA", "handleMessage: TEL");
                    }
                    else if ("OPEN".equals(arrayOfString[0])) {
                        openNotification();
                        Log.i("DATA", "handleMessage: OPEN");
                    }
                    else if ("CLOSE".equals(arrayOfString[0])) {
                        closeNotification();
                        Log.i("DATA", "handleMessage: CLOSE");
                    }
                    else if ("TEXT".equals(arrayOfString[0])){
                        massages.add(arrayOfString[1]);
                        arrayAdapter.notifyDataSetChanged();
                        Log.i("DATA", "handleMessage: TEXT");
                    }
                    break;
                case SEND_SUCCESS :
                    if ((content != null) && (!TextUtils.isEmpty(content))) {
                        massages.add(content);
                        content = null;
                        arrayAdapter.notifyDataSetChanged();
                        et_content.setText(null);
                    }
                    break;
                case SEND_ERROR:
                    Toast.makeText(ControlActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(paramMessage);
        }
    };


    private void dialTelephone(String paramString)
    {
        BluetoothUtil.mSocket = ConnectedSocket;
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + paramString)));
    }

    public void closeConnect(View paramView)
    {
        if (ConnectedSocket != null)
            new Thread()
            {
                public void run()
                {
                    try
                    {
                       ConnectedSocket.close();
                        if (acceptThread != null)
                        {
                            acceptThread.interrupt();
                            acceptThread = null;
                        }
                        if (connetThread != null)
                        {
                            connetThread.interrupt();
                            connetThread = null;
                        }
                        if (writeThread != null)
                        {
                            writeThread.interrupt();
                            writeThread = null;
                        }
                        Message localMessage = new Message();
                        localMessage.what = CONNECT_CLOSEED;
                        handler.sendMessage(localMessage);
                    }
                    catch (IOException localIOException)
                    {
                        localIOException.printStackTrace();
                    }
                }
            }.start();
    }

    protected void closeNotification()
    {
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).cancel();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.ble_activity_control);
        ll_mode = ((LinearLayout)findViewById(R.id.ll_mode));
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, this.massages);
        sendINFO(null);
        ConnectedSocket = BluetoothUtil.mSocket;
        if (ConnectedSocket != null) {
            writeThread = new readThread(ConnectedSocket);
        }
        massages.add("聊天记录：");
        arrayAdapter.notifyDataSetChanged();
    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    protected void onResume()
    {
        super.onResume();
    }

    protected void onStart()
    {
        String str = getIntent().getStringExtra("bluetoothAddress");
        if ((str != null) && (!TextUtils.isEmpty(str)))
        {
            this.mmdevice = this.bluetoothAdapter.getRemoteDevice(str);
            Toast.makeText(this, this.mmdevice.getName() + str, Toast.LENGTH_SHORT).show();
        }
        else {
            this.acceptThread = new AcceptThread();
            this.acceptThread.start();
            Toast.makeText(this, "服务器已开启,等待客服端连接。。。", Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    protected void onStop()
    {
        if (ConnectedSocket != null)
        {
            new Thread() {
                public void run() {
                    try {
                       ConnectedSocket.close();
                        if (acceptThread != null) {
                           acceptThread.interrupt();
                           acceptThread = null;
                        }
                        if (connetThread != null) {
                           connetThread.interrupt();
                           connetThread = null;
                        }
                        if (writeThread != null) {
                           writeThread.interrupt();
                           writeThread = null;
                        }
                        return;
                    } catch (IOException localIOException) {
                        localIOException.printStackTrace();
                    }
                }
            }.start();
        }
        super.onStop();
    }
    protected void openNotification()
    {
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{1000L, 2000L, 3000L, 4000L }, 0);
    }
    public void sendINFO(View view){
        ll_mode.removeAllViews();
        View message = getLayoutInflater().inflate(R.layout.message,null);
        ll_mode.addView(message);
        lv_massage = ((ListView)message.findViewById(R.id.lv_massage));
        lv_massage.setAdapter(arrayAdapter);
        et_content = ((EditText)message.findViewById(R.id.et_content));
        ((Button)message.findViewById(R.id.bt_send)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                content = ("TEXT##" + et_content.getText().toString());
                if (TextUtils.isEmpty(content))
                {
                    Toast.makeText(ControlActivity.this, "输入内容为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    new writeThread(ConnectedSocket,content.getBytes("utf-8")).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void sendNotfication(View paramView)
    {
        ll_mode.removeAllViews();
        sendView = getLayoutInflater().inflate(R.layout.send, null);
        ll_mode.addView(sendView);
        Button localButton = (Button)sendView.findViewById(R.id.bt_open);
        ((Button)this.sendView.findViewById(R.id.bt_close)).setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramView)
            {
                try
                {
                    new writeThread(ConnectedSocket, "CLOSE##".getBytes("utf-8")).start();
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
            }
        });
        localButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramView)
            {
                try
                {
                    new writeThread(ConnectedSocket, "OPEN##".getBytes("utf-8")).start();
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                }
            }
        });
    }

    public void sendTelephone(View paramView)
    {
        ll_mode.removeAllViews();
        callView = getLayoutInflater().inflate(R.layout.call, null);
        ll_mode.addView(callView);
        ((Button)this.callView.findViewById(R.id.bt_call)).setOnClickListener(new OnClickListener()
        {
            public void onClick(View paramView)
            {
                String str1 = ((EditText)callView.findViewById(R.id.et_phone)).getText().toString().trim();
                String str2 = "TEL##" + str1;
                if (TextUtils.isEmpty(str2))
                {
                    Toast.makeText(ControlActivity.this, "非法输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                try
                {
                    new writeThread(ConnectedSocket, str2.getBytes("utf-8")).start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startConnect(View paramView)
    {
        if (this.mmdevice == null)
        {
            Toast.makeText(this, "等待客服端连接。。。", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ConnectedSocket == null)
        {
            connetThread = new ConnetThread(mmdevice);
            connetThread.start();
            return;
        }
        Toast.makeText(this, "已连接，请断开连接后重新选取。", Toast.LENGTH_SHORT).show();
    }

    private class AcceptThread extends Thread
    {
        private final UUID PANUServiceClass_UUID = UUID.fromString("00001115-0000-1000-8000-00805F9B34FB");
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread()
        {

            BluetoothServerSocket localBluetoothServerSocket2 = null;
            try {
                localBluetoothServerSocket2 = bluetoothAdapter.listenUsingRfcommWithServiceRecord("CWY", this.PANUServiceClass_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = localBluetoothServerSocket2;
        }

        private void cancel()
        {
            try
            {
                mmServerSocket.close();
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
            }
        }

        // ERROR //
        public void run() {
            BluetoothSocket socketTemp = null;
            while (true) {
                try {
                    socketTemp = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = CONNECT_ERROR;
                    handler.sendMessage(msg);
                    break;
                }
                if (socketTemp != null){
                    Message msg = new Message();
                    msg.what = AcceptThread_SUCCESS_CONNECT;
                    msg.obj = socketTemp;
                    handler.sendMessage(msg);
                    break;
                }
            }
        }
    }

    private class ConnetThread extends Thread
    {
        private final UUID PANUServiceClass_UUID = UUID.fromString("00001115-0000-1000-8000-00805F9B34FB");
        private final BluetoothDevice mDevice;
        private final BluetoothSocket mSocket;

        public ConnetThread(BluetoothDevice arg2)
        {
            BluetoothSocket mSocketTemp = null;
            mDevice = arg2;
            try
            {
                mSocketTemp = mDevice.createRfcommSocketToServiceRecord(this.PANUServiceClass_UUID);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            mSocket = mSocketTemp;
        }

        private void cancel()
        {
            try
            {
                mSocket.close();
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
            }
        }

        public void run()
        {
            try
            {
                mSocket.connect();
                Message localMessage2 = new Message();
                localMessage2.what = ConnetThread_SUCCESS_CONNECT;
                localMessage2.obj = mSocket;
                handler.sendMessage(localMessage2);

            }
            catch (IOException localIOException1)
            {
                Message localMessage1 = new Message();
                localMessage1.what = CONNECT_ERROR;
                handler.sendMessage(localMessage1);
                localIOException1.printStackTrace();
                try
                {
                    this.mSocket.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private class readThread extends Thread
    {
        private final BluetoothSocket mySocket;
        public readThread(BluetoothSocket arg2)
        {
            mySocket = arg2;
        }


        public void run()
        {
            Log.i("readThread", "run: start");
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = mySocket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
                        byte[] buf_data = new byte[bytes];
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        Log.i("readThread", "run: "+bytes);
                        String s = new String(buf_data,"utf-8");
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = DATA;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    try {
                        mmInStream.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private class writeThread extends Thread
    {
        private final byte[] bytes;
        private final BluetoothSocket mySocket;
        private final OutputStream os;

        public writeThread(BluetoothSocket paramArrayOfByte, byte[] arg3)
        {
            OutputStream localOutputStream2 = null;
            mySocket = paramArrayOfByte;
            bytes = arg3;
            try
            {
                localOutputStream2 = mySocket.getOutputStream();

            }
            catch (IOException localIOException)
            {
                localOutputStream2 = null;
                localIOException.printStackTrace();
            }
            Log.i("ControlActivity", "writeThread: os");
            os = localOutputStream2;
        }

        public void run()
        {
            try
            {
                os.write(bytes);
                os.flush();
                Message localMessage2 = new Message();
                localMessage2.what = SEND_SUCCESS;
                handler.sendMessage(localMessage2);
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
                Message localMessage1 = new Message();
                localMessage1.what = SEND_ERROR;
                handler.sendMessage(localMessage1);
            }
        }
    }
}
