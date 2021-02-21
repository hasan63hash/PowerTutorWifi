package com.example.wifitry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*
   Bilgi için:
   1) https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/com/android/internal/os/PowerProfile.java
*/

public class PowerProfile {

    private Context context;
    private Class<?> powerProfileClazz;
    private Object powerProInstance;

    private String TAG = "PowerProfile";
    private String ERR_TAG = "PowerProfileErr";

    private boolean error = false;

    @SuppressLint("PrivateApi")
    public PowerProfile(Context context){
        this.context = context;
        try{
            powerProfileClazz = Class.forName("com.android.internal.os.PowerProfile");

            Class[] argTypes = {Context.class};
            Constructor constructor = powerProfileClazz.getDeclaredConstructor(argTypes);

            //Instantiate
            Object[] arguments = {context};
            powerProInstance = constructor.newInstance(arguments);
            getPowerConstants();
        }catch (Exception ex){
            error = true;
            Log.d(ERR_TAG, "PowerProfile oluşturulurken bir hata oluştu. " + ex);
        }
    }

    // Güç Değişkenleri ==================================================
    public Double POWER_NONE, POWER_CPU_IDLE, POWER_CPU_AWAKE, POWER_CPU_ACTIVE, POWER_WIFI_SCAN,
            POWER_WIFI_ON, POWER_WIFI_ACTIVE, POWER_GPS_ON, POWER_BLUETOOTH_ON, POWER_BLUETOOTH_ACTIVE,
            POWER_BLUETOOTH_AT_CMD, POWER_SCREEN_ON, POWER_RADIO_ON, POWER_RADIO_SCANNING,
            POWER_RADIO_ACTIVE, POWER_SCREEN_FULL, POWER_AUDIO, POWER_VIDEO, POWER_CPU_SPEEDS,
            POWER_BATTERY_CAPACITY;

    // Fonksiyonlar ======================================================
    private void getPowerConstants(){
        POWER_NONE = getAveragePower("none");
        POWER_CPU_IDLE = getAveragePower("cpu.idle");
        POWER_CPU_AWAKE = getAveragePower("cpu.awake");
        POWER_CPU_ACTIVE = getAveragePower("cpu.active");
        POWER_WIFI_SCAN = getAveragePower("wifi.scan");
        POWER_WIFI_ON = getAveragePower("wifi.on");
        POWER_WIFI_ACTIVE = getAveragePower("wifi.active");
        POWER_GPS_ON = getAveragePower("gps.on");
        POWER_BLUETOOTH_ON = getAveragePower("bluetooth.on");
        POWER_BLUETOOTH_ACTIVE = getAveragePower("bluetooth.active");
        POWER_BLUETOOTH_AT_CMD = getAveragePower("bluetooth.at");
        POWER_SCREEN_ON = getAveragePower("screen.on");
        POWER_RADIO_ON = getAveragePower("radio.on");
        POWER_RADIO_SCANNING = getAveragePower("radio.scanning");
        POWER_RADIO_ACTIVE = getAveragePower("radio.active");
        POWER_SCREEN_FULL = getAveragePower("screen.full");
        POWER_AUDIO = getAveragePower("dsp.audio");
        POWER_VIDEO = getAveragePower("dsp.video");
        POWER_CPU_SPEEDS = getAveragePower("cpu.speeds");
        POWER_BATTERY_CAPACITY = getAveragePower("battery.capacity");
    }

    /*
        Toplam CPU küme sayısını döner,
        Hata değeri: 0
    */
    public int getNumCpuClusters() {
        int cpuClustersNum = 0;
        if(error){ // Hata var ise
            return 0;
        }

        try {
            Method getNumCpuClusters = powerProfileClazz.getMethod("getNumCpuClusters", null);
            cpuClustersNum = (int) getNumCpuClusters.invoke(powerProInstance, null);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getNumCpuClusters() 'da hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "CpuClustersNum: " + cpuClustersNum);
        return cpuClustersNum;
    }

    /*
        İstenilen cpu kümesindeki çekirdek sayısını döner,
        Hata değeri: 0
    */
    public int getNumCoresInCpuCluster(int cluster) {
        int numCoresInCpuCluster = 0;
        if (error){ // Hata varsa
            return 0;
        }

        try {
            Class[] argTypes = {int.class};
            Object[] args = {cluster};
            Method getNumCoresInCpuCluster = powerProfileClazz.getMethod("getNumCoresInCpuCluster", argTypes);
            numCoresInCpuCluster = (int) getNumCoresInCpuCluster.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getNumCoresInCpuCluster() 'da hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "NumberOfCoresInCpuCluster-" + cluster + " : " + numCoresInCpuCluster);
        return numCoresInCpuCluster;
    }

    /*
        İstenilen CPU kümesinde kaç farklı hız eşiği olduğunu döner,
        Hata değeri: 0,
        1 dönerse sadece 1 hız eşiği var demektir.
    */
    public int getNumSpeedStepsInCpuCluster(int cluster) {
        int numSpeedStepsInCpuCluster = 0;
        if(error){ // Hata var ise
            return 0;
        }

        try{
            Class[] argTypes = {int.class};
            Object[] args = {cluster};
            Method getNumSpeedStepsInCpuCluster = powerProfileClazz.getMethod("getNumSpeedStepsInCpuCluster", argTypes);
            numSpeedStepsInCpuCluster = (int) getNumSpeedStepsInCpuCluster.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getNumSpeedStepsInCpuCluster() 'da hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "NumberofSpeedStepsInCpuCluster-" + cluster + " " + numSpeedStepsInCpuCluster);
        return numSpeedStepsInCpuCluster;
    }

    /*
        Hafıza bant genişliği kovalarının sayısını döner,
        Hata değeri: 0
    */
    public int getNumElements(String key) {
        int numElements = 0;
        if(error){
            return 0;
        }

        try{
            Class[] argTypes = {String.class};
            Object[] args = {key};
            Method getNumElements = powerProfileClazz.getMethod("getNumElements", argTypes);
            numElements = (int) getNumElements.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(TAG, "getNumElements() 'de hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "NumElements: key=" + key + " için " + numElements);
        return numElements;
    }

    /*
        Alt sistem tarafından tüketilen ortalama akımı mAmp biriminde döndürür,
        Eğer kayıtlı bir veri yok ise default değerini döndürür,
        @param type : alt sistem tipi
        @param defaultValue: Kayıtlı veri yoksa döndürülecek değer
        Hata değeri: 0
    */
    public double getAveragePowerOrDefault(String type, double defaultValue) {
        double averagePower = 0;
        if(error){
            return 0;
        }

        try{
            Class[] argTypes = {String.class, double.class};
            Object[] args = {type, defaultValue};
            Method getAveragePowerOrDefault = powerProfileClazz.getMethod("getAveragePowerOrDefault", argTypes);
            averagePower = (double) getAveragePowerOrDefault.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getAveragePowerOrDefault() 'da hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "Type: " + type + " AveragePower: " + averagePower + " mAmp");
        return averagePower;
    }

    /*
        Alt sistem tarafından tüketilen ortalama akımı mAmp biriminde döndürür,
        @param type : alt sistem tipi,
        Hata değeri: 0
    */
    public double getAveragePower(String type) {
        double averagePower = 0;
        if(error){
            return 0;
        }

        try{
            Class[] argTypes = {String.class};
            Object[] args = {type};
            Method getAveragePower = powerProfileClazz.getMethod("getAveragePower", argTypes);
            averagePower = (double) getAveragePower.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getAveragePower() 'da hata oluştu. " + ex);
            return 0;
        }

        Log.d(TAG, "Type: " + type + " AveragePower: " + averagePower + " mAmp");
        return averagePower;
        /*
        double averagePower = getAveragePowerOrDefault(type, 0);
        return averagePower;
        */

    }

    /*
        Alt sistem için istenen seviyedeki ortalama akımı mAmp türünden döner,
        @param type: alt sistem tipi
        @param level: Alt sistemin çalıştığı enerji seviyesi, Örneğin; Telefon ağının sinyal gücü, 0 ile 4 arasında ( En fazla 4 diş var ise),
        1'den fazla seviye için veri yok ise seviye görmezden gelinir,
        Hata değeri: 0
    */
    public double getAveragePower(String type, int level) {
        double averagePower = 0;
        if (error){
            return 0;
        }

        try{
            Class[] argTypes = {String.class, int.class};
            Object[] args = {type, level};
            Method getAveragePower = powerProfileClazz.getMethod("getAveragePower", argTypes);
            averagePower = (double) getAveragePower.invoke(powerProInstance, args);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getAveragePower() 'da sorun çıktı. " + ex);
            return 0;
        }

        Log.d(TAG, "AveragePower: " + averagePower + " mAmp");
        return averagePower;
    }



    /*
        Batarya kapasitesini mAmp cinsinden döner, ulaşamazsa 0 döner
        Hata değeri: 0
    */
    public double getBatteryCapacity() {
        double batteryCapacity = 0;
        if(error){ // Hata var ise
            return 0;
        }

        try{
            Method getBatteryCapacity = powerProfileClazz.getMethod("getBatteryCapacity", null);
            batteryCapacity = (double) getBatteryCapacity.invoke(powerProInstance, null);
        }catch (Exception ex){
            Log.d(ERR_TAG, "getBatteryCapacity() 'de hata çıktı. " + ex);
            return 0;
        }

        Log.d(TAG, "BatteryCapacity: " + batteryCapacity);
        return batteryCapacity;
    }

    /*
       PowerProfile sınıfındaki fonksiyonların isimlerini konsola yazar
    */
    private void getAvailableFunctions(){
        for( Method method : powerProfileClazz.getMethods()){
            Log.d(TAG, "" + method.getName());
        }
    }
}

