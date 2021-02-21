package com.example.wifitry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        PowerProfile powerProfile = new PowerProfile(this);
        Wifi wifi = new Wifi(this, powerProfile);
        wifi.start();

        WifiTest test = new WifiTest();
        test.start();
    }

    /////sonradan yapılanlar veri alma ve gonderme socket programlama
    public void gonder(View view){
            String gonderilecekMetin = "Gönderdim";
            Socketprog sp = new Socketprog();
            sp.execute(gonderilecekMetin);
    }

    class Socketprog extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
           Socket socket;
            InputStreamReader isr;
            BufferedReader br;
            PrintWriter prw;
            String sonuc;
            try {
                socket= new Socket("192.168.1.29",6060);
                isr=new InputStreamReader(socket.getInputStream());
                br=new BufferedReader(isr);
                prw=new PrintWriter(socket.getOutputStream());
                prw.println(strings[0]);
                prw.flush();
                sonuc=br.readLine();
                return sonuc;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }


}
