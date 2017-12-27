package com.allens.servicecliet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.allens.servicedemo.Service.IServer;

public class MainActivity extends AppCompatActivity {


    //创建ServiceConnection的匿名类
    private ServiceConnection connection = new ServiceConnection() {

        //重写onServiceConnected()方法和onServiceDisconnected()方法
        //在Activity与Service建立关联和解除关联的时候调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("TAG", "onServiceDisconnected");
        }

        //在Activity与Service建立关联时调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("TAG", "onServiceConnected");
            //IService.Stub.asInterface()方法获取服务器端返回的IBinder对象
            //将IBinder对象传换成了mAIDL_Service接口对象
            IServer iServer = IServer.Stub.asInterface(service);
            try {
                //通过该对象调用在IService.aidl文件中定义的接口方法,从而实现跨进程通信
                iServer.onServer();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick");
                //通过Intent指定服务端的服务名称和所在包，与远程Service进行绑定
                //参数与服务器端的action要一致,即"服务器包名.aidl接口文件名"
                Intent intent = new Intent("com.allens.servicedemo.IServer");

                //Android5.0后无法只通过隐式Intent绑定远程Service
                //需要通过setPackage()方法指定包名
                intent.setPackage("com.allens.servicedemo");

                //绑定服务,传入intent和ServiceConnection对象
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        });
    }
}
