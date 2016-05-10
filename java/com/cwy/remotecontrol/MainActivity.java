package com.cwy.remotecontrol;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cwy.remotecontrol.util.FinalData;
import com.cwy.remotecontrol.util.Utils;

public class MainActivity extends AppCompatActivity {
    private Socket socket = null;
    private EditText ev_ip;
    private TextView tv_state;
    private LinearLayout touchBoard;
    private OutputStream mOutputStream = null;
    private String TAG = "MainActivity";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FinalData.SOCKET_NULL:
                    Toast.makeText(MainActivity.this,"连接错误请重试",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket = Utils.socket;
        touchBoard = (LinearLayout) findViewById(R.id.touchBoard);
    }

    public void leftClick(View view){
        new writeThread(FinalData.LEFTPRESS).start();
    }
    public void rightClick(View view){
        new writeThread(FinalData.RIGHTPRESS).start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = event.getAction();
        switch (i) {
            //按下
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN" + event.getRawX() + ":" + event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE" + event.getRawX() + ":" + event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP" + event.getRawX() + ":" + event.getRawY());
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTION_CANCEL" + event.getRawX() + ":" + event.getRawY());
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private class writeThread extends Thread{
        private String content;
        private byte[] buf;
        public writeThread(String content){
            this.content = content;
        }

        @Override
        public void run() {
            if (socket == null){
                Message msg = new Message();
                msg.what = FinalData.SOCKET_NULL;
                handler.sendMessage(msg);
            }
            try {
                if(mOutputStream == null)
                    mOutputStream = socket.getOutputStream();
                buf = content.getBytes();
                mOutputStream.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
                mOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (socket != null) {
            Log.i(TAG, "onStop: success" + " " + socket.isClosed());
            try {
                socket.close();
                Utils.socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "onDestroy: success");
        super.onDestroy();
    }
}
