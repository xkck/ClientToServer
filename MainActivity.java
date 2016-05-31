package com.android.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final int MSG_SUM = 1;
    public static final int MSG_RET = 2;

    public EditText num1;
    public EditText num2;
    public EditText sum;
    public Button btn;

    //客户端信使
    public Messenger clientMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_RET:
                    sum.setText(msg.arg1+"");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    });

    //服务端信使
    public Messenger serverMessenger;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serverMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMessenger = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        sum = (EditText) findViewById(R.id.sum);
        btn = (Button) findViewById(R.id.calculate);

        //绑定服务
        /*Intent intent = new Intent();
        intent.setAction("com.server.messenger_service");
        intent.setPackage("com.android.client");*/
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        btn.setOnClickListener(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View v) {

        if(num1.getText().toString().equals("") || num2.getText().toString().equals("") ){
            Toast.makeText(this,"请输入数值",Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = Message.obtain(null,MSG_SUM,Integer.parseInt(num1.getText().toString()),Integer.parseInt(num2.getText().toString()));
        message.replyTo = clientMessenger;

        try {
            serverMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
