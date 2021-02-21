package com.example.wifitry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;

public class Wifi extends Thread{
    private static final String TAG = "Wifi";
    Context context;
    PowerProfile powerProfile;
    WifiState wifiState;
    int uid;

    double megabitToByte = 125000; // Megabitten byte'a dönüştürmek için çarpan
    long lastTotalBytes = 0; // En son kontrolden kalan totalByte değeri
    long lastTotalPackets = 0;
    double recurTime = 1; // Kaç saniyede bir kontrol ediyoruz,
    // Eğer bunu değiştirirsek paket durumları da değişir çünkü saniyede 15 paket diye kural var
    double totalEnergyUsage = 0;
    long lastTime = 0;


    public Wifi(Context context, PowerProfile powerProfile){
        this.context = context;
        this.powerProfile = powerProfile;
        uid = android.os.Process.myUid(); // Uygulamamızın uid'si

        long receiveBytes = TrafficStats.getUidRxBytes(uid); // İndirilen byte'lar
        long transmitBytes = TrafficStats.getUidTxBytes(uid); // Gönderilen byte'lar
        lastTotalBytes = receiveBytes + transmitBytes;

        wifiState = new WifiState();
    }

    public void update(){
        double energyUsage = 0;
        // Wifi açık mı kontrolü
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean isWifiOn = wifiManager.isWifiEnabled();
       BatteryManager bm = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int yuzde = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d(TAG, " max Batarya kapasitesi : " + powerProfile.POWER_BATTERY_CAPACITY + " mAmp ");
        Log.d(TAG, "Batarya yüzdesi: " + yuzde + " ");
        if(isWifiOn == false){ // Wifi kapalı ise 0 döndür
            energyUsage = 0;
            Log.d(TAG, "WIFI_OFF enerji/saniye: " + energyUsage + " mAmp");
        }
        else{ // Wifi açık ise

            // Bir ağa bağlı mı?
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = mWifi.isConnected();

            if(isConnected == false){ // Ağa bağlı değil ozaman WIFI_SCAN modunda onu döndür
                energyUsage = (powerProfile.POWER_WIFI_SCAN / 3600) * recurTime;
                Log.d(TAG, "WIFI_SCAN energy/saniye: " + energyUsage + " mAmp");
            }
            else{ // Bağlı, güç tüketimine karar ver

                long receiveBytes = TrafficStats.getUidRxBytes(uid); // İndirilen byte'lar
                long transmitBytes = TrafficStats.getUidTxBytes(uid); // Gönderilen byte'lar
                // Log.d(TAG, "Receive: " + receiveBytes + " Transmit: " + transmitBytes);

                double linkSpeed = wifiManager.getConnectionInfo().getLinkSpeed(); // Bağlantı hızını al
                Log.d(TAG, "LinkSpeed: " + linkSpeed + " mbps");

                double bytePerSecond = linkSpeed * megabitToByte; // Bu bağlantı hızına göre 1 saniyede kaç byte indiriyor
                long totalBytes = receiveBytes + transmitBytes; // Toplam byte değerini hesapla
                long deltaBytes = totalBytes - lastTotalBytes; // Bir önceki kontrol ile şuan arasındaki byte farkını al

                if(deltaBytes <= 0){ // Birşey indirmiyor
                    energyUsage = (powerProfile.POWER_WIFI_ON / 3600) * recurTime;
                    Log.d(TAG, "WIFI_ON enerji/saniye: " + energyUsage + " mAmp");
                }
                else{ // Yüksek güç tüketimi, birşey indiriyor
                    energyUsage = (powerProfile.POWER_WIFI_ACTIVE / 3600) * (totalBytes/bytePerSecond);
                    Log.d(TAG, "WIFI_ACTIVE enerji/saniye: " + energyUsage + " mAmp");
                }
                lastTotalBytes = totalBytes; // lastTotalBytes'ı güncelle
            }
        }

        totalEnergyUsage += energyUsage;
        Log.d(TAG, "TotalEnergyUsage: " + totalEnergyUsage + " mAmp");
    }

    @Override
    public void run() {
        while(true){
            try{
                update3();
                Thread.sleep((long)recurTime * 1000);
            }catch (Exception ex){

            }
        }
    }

    // Update1'den farkı state'değişikleri yapılırken paket sayılarının dikkate alınması
    public void update2(){
        double energyUsage = 0;
        // Wifi açık mı kontrolü
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean isWifiOn = wifiManager.isWifiEnabled();
      BatteryManager bm = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int yuzde = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d(TAG, " max Batarya kapasitesi : " + powerProfile.POWER_BATTERY_CAPACITY + " mAmp ");
        Log.d(TAG, "Batarya yüzdesi: " + yuzde + " ");
        if(isWifiOn == false){ // Wifi kapalı ise 0 döndür
            energyUsage = 0;
            Log.d(TAG, "WIFI_OFF enerji/saniye: " + energyUsage + " mAmp");
        }
        else{ // Wifi açık ise

            // Bir ağa bağlı mı?
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = mWifi.isConnected();

            if(isConnected == false){ // Ağa bağlı değil ozaman WIFI_SCAN modunda onu döndür
                energyUsage = (powerProfile.POWER_WIFI_SCAN / 3600) * recurTime;
                Log.d(TAG, "WIFI_SCAN energy/saniye: " + energyUsage + " mAmp");
            }
            else{ // Bağlı, güç tüketimine karar ver
                long receivePackets = TrafficStats.getUidRxPackets(uid);
                long transmitPackets = TrafficStats.getUidTxPackets(uid);
                //Log.d(TAG, "Receive: " + receivePackets + " packets Transmit: " + transmitPackets + " packets");

                long totalPackets = receivePackets + transmitPackets;
                if(lastTotalPackets == 0){
                    lastTotalPackets = totalPackets;
                }
                long deltaPackets = totalPackets - lastTotalPackets; // Bir önceki kontrol ile şuan arasındaki paket farkını al
                Log.d(TAG, "DeltaPackets: " + deltaPackets);

                if(deltaPackets <= 15){ // Düşük güç durumu
                    energyUsage = (powerProfile.POWER_WIFI_ON / 3600) * recurTime;
                    Log.d(TAG, "WIFI_ON enerji/saniye: " + energyUsage + " mAmp");
                }
                else{ // Yüksek güç tüketimi, saniyede 15'ten fazla paket
                    energyUsage = (powerProfile.POWER_WIFI_ACTIVE / 3600) * recurTime; // Son recurtime kadar saniye boyunca bu state'de idi
                    Log.d(TAG, "WIFI_ACTIVE enerji/saniye: " + energyUsage + " mAmp");
                }
                lastTotalPackets = totalPackets; // lastTotalPackets'ı güncelle
            }
        }

        totalEnergyUsage += energyUsage;
        Log.d(TAG, "TotalEnergyUsage: " + totalEnergyUsage + " mAmp");
    }

    /*
        Bu fonksiyonda wifiState'i tutuyoruz. Son kontrolden bu yana geçen süreyi dinamik olarak hesaplıyoruz.
        Yani diğer fonksiyonlara göre daha tutarlı. State değişiklikleri indirilen ve gönderilen paket sayılarına göre
        yapılıyor.
    */
    public void update3(){
        double energyUsage = 0;
        // Wifi açık mı kontrolü
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean isWifiOn = wifiManager.isWifiEnabled();
        BatteryManager bm = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int yuzde = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d(TAG, " max Batarya kapasitesi : " + powerProfile.POWER_BATTERY_CAPACITY + " mAmp ");
        Log.d(TAG, "Batarya yüzdesi: " + yuzde + " ");
        if(isWifiOn == false){ // Wifi kapalı ise
            wifiState.setState(wifiState.WIFI_OFF);
        }
        else{ // Wifi açık ise

            // Bir ağa bağlı mı?
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = mWifi.isConnected();

            if(isConnected == false){ // Ağa bağlı değil ozaman WIFI_SCAN modunda
                wifiState.setState(wifiState.WIFI_SCAN);
            }
            else{ // Bağlı
                long receivePackets = TrafficStats.getUidRxPackets(uid);
                long transmitPackets = TrafficStats.getUidTxPackets(uid);
                // Log.d(TAG, "Receive: " + receivePackets + " packets Transmit: " + transmitPackets + " packets");

                long totalPackets = receivePackets + transmitPackets;
                if(lastTotalPackets == 0){
                    lastTotalPackets = totalPackets;
                }
                long deltaPackets = totalPackets - lastTotalPackets; // Bir önceki kontrol ile şuan arasındaki paket farkını al

                if(deltaPackets <= 15){ // Düşük güç durumu
                    wifiState.setState(wifiState.WIFI_ON);
                }
                else{ // Yüksek güç tüketimi, saniyede 15'ten fazla paket
                    wifiState.setState(wifiState.WIFI_ACTIVE);
                }
                lastTotalPackets = totalPackets; // lastTotalPackets'ı güncelle
            }
        }

        // Şuan kontrolleri yaptık hangi state'de olduğumuzu biliyoruz.
        int state = wifiState.getState();
        double time; // Saniye cinsinden son iterasyondan şu ana kadar geçen süre
        if(lastTime == 0){
            time = recurTime;
            lastTime = System.currentTimeMillis();
        }
        else{
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastTime;
            time = (double) deltaTime / 1000; // Milisaniye farkını saniyeye çevirerek geçen zamanı bul
            lastTime = currentTime;
        }
        // Log.d(TAG, "Son iterasyondan bu yana geçen saniye: " + time);

        if(state == wifiState.WIFI_OFF){
            energyUsage = 0;
            Log.d(TAG, "WIFI_OFF enerji/saniye: " + energyUsage + " mAmp");
        }
        else if(state == wifiState.WIFI_SCAN){
            energyUsage = (powerProfile.POWER_WIFI_SCAN / 3600) * time;
            Log.d(TAG, "WIFI_SCAN energy/saniye: " + energyUsage + " mAmp");
        }
        else if(state == wifiState.WIFI_ON){
            energyUsage = (powerProfile.POWER_WIFI_ON / 3600) * time;
            Log.d(TAG, "WIFI_ON enerji/saniye: " + energyUsage + " mAmp");
        }
        else if(state == wifiState.WIFI_ACTIVE){
            energyUsage = (powerProfile.POWER_WIFI_ACTIVE / 3600) * time; // Son recurtime kadar saniye boyunca bu state'de idi
            Log.d(TAG, "WIFI_ACTIVE enerji/saniye: " + energyUsage + " mAmp");
        }

        totalEnergyUsage += energyUsage;
        Log.d(TAG, "TotalEnergyUsage: " + totalEnergyUsage + " mAmp");
    }
}
