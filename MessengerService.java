package com.android.client;

import android.app.Service;


import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * 服务端
 */
public class MessengerService extends Service {

    public static final int MSG_SUM = 1;
    public static final int MSG_RET = 2;

    public Messenger clientMessenger;

    public Messenger serverMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUM:
                    clientMessenger = msg.replyTo;
                    Message message = Message.obtain(null,MSG_RET);
                    message.arg1 = msg.arg1+msg.arg2;
                    try {
                        clientMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    handleMessage(msg);
            }

        }
    });


    @Override
    public IBinder onBind(Intent intent) {
        return serverMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return true;
    }

    @Override
    public void onRebind(Intent intent) {

    }

}

