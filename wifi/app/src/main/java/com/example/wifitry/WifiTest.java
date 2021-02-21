package com.example.wifitry;

import android.graphics.Bitmap;

public class WifiTest extends Thread {

    boolean state;

    public WifiTest(){
        state = true;
    }

    @Override
    public void run() {

        while(true){
            // Resim indir
            try {
                ImageDownloader downloader = new ImageDownloader();
                Bitmap bitmap = downloader.execute("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.facebook.com%2F1453AKINCILARI%2F&psig=AOvVaw2lSnQtSMrzzOCuZGIY-tyN&ust=1610209527383000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJC7u4rgjO4CFQAAAAAdAAAAABAD").get();
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void finish(){
        state = false;
    }
}
