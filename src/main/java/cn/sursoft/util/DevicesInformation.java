package cn.sursoft.util;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by gtguo on 5/23/2017.
 */

public class DevicesInformation extends SocketClient{
    private static final String TAG = "DevicesInformation ";
    private static final String GET_SCREEN_CMD = "wm size";
    private static final String GET_CPUINFO_CMD = "cat /proc/cpuinfo";
    private static final String GET_RAMINFO_CMD = "cat /proc/meminfo";
    private static final String GET_MACINFO_CMD = "cat /sys/class/net/wlan0/address";

    private String manufacturer;
    private String productName;
    private String deviceName;
    private String model;
    private String sdkVersion;
    private String androidVersion;
    private String serialId;
    private String screenInfo;
    private String MACInfo;
    private String cpuInfo;
    private String RAMInfo;

    //private DevPhoneInfo devPhoneInfo= null;
    //public DevicesInformation(){}

    public DevicesInformation(IDevice iDevice){
        super(iDevice);
        this.manufacturer = iDevice.getProperty("ro.product.manufacturer");
        this.productName = iDevice.getProperty("ro.product.name");
        this.deviceName = iDevice.getProperty("ro.product.device");
        this.model = iDevice.getProperty("ro.product.model");
        this.sdkVersion = iDevice.getProperty("ro.build.version.sdk");
        this.androidVersion = iDevice.getProperty("ro.build.version.release");
        this.serialId = iDevice.getSerialNumber();
        //setPhoneInfo(new SocketClient(iDevice).getDevPhoneInfo());
        //setPhoneInfo(getDevPhoneInfo());
        new ScreenInfoReceiver().execGetScreenCmd(iDevice);
        new CpuInfoReceiver().execGetBatteryInfoCmd(iDevice);
        new RamInfoReceiver().execGetRamInfoCmd(iDevice);
        new MACInfoReceiver().execGetMACInfoCmd(iDevice);
    }

    private class ScreenInfoReceiver extends MultiLineReceiver{
        private static final String DISPLAY_MATCHER = "init=";
        public ScreenInfoReceiver(){
            super();
        }

        public void execGetScreenCmd(IDevice iDevice){
            try {
                iDevice.executeShellCommand(GET_SCREEN_CMD,this);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void processNewLines(String[] var){
            for(String line: var){
                /****************
                if(line.contains(DISPLAY_MATCHER)){
                   // System.out.println(.toString());
                    String[] s = line.split(" ");
                    setScreenInfo(s[0].substring(DISPLAY_MATCHER.length()));
                    System.out.println(getScreenInfo());
                }
                 **************/
                String[] s = line.split(": ");
                if(s.length ==2 ) {
                    setScreenInfo(s[1]);
                    System.out.println(TAG+getScreenInfo());
                }
            }
        }

        public boolean isCancelled(){
            return false;
        }
    }

    private class MACInfoReceiver extends MultiLineReceiver{
        //private static final String DISPLAY_MATCHER = "init=";
        public MACInfoReceiver(){
            super();
        }

        public void execGetMACInfoCmd(IDevice iDevice){
            try {
                iDevice.executeShellCommand(GET_MACINFO_CMD,this);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void processNewLines(String[] var){
            //HashMap<String,String> tmpBatteryInfo = new HashMap<>();
            for(String line: var){
                if(line.length()==17) {
                    setMacInfo(line);
                    System.out.println(TAG+"MAC:"+getMacInfo());
                }
                return;
            }
            //String[] strs = line.split(":\\s");
            //tmpBatteryInfo.put(strs[0].replace(" ","").toString(), strs[1].toString());
        }

        public boolean isCancelled(){
            return false;
        }
    }

    private class RamInfoReceiver extends MultiLineReceiver{
        //private static final String DISPLAY_MATCHER = "init=";
        public RamInfoReceiver(){
            super();
        }

        public void execGetRamInfoCmd(IDevice iDevice){
            try {
                iDevice.executeShellCommand(GET_RAMINFO_CMD,this);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void processNewLines(String[] var){
            //HashMap<String,String> tmpBatteryInfo = new HashMap<>();
            for(String line: var){
                if(line.contains("MemTotal")) {
                    String[] s = line.split(": ");
                    String s1 = s[s.length - 1];
                    //setRAMInfo();
                    String[] s2 = s1.split("       ");//xxx kb
                    String[] s3 = s2[1].split(" ");
                    //System.out.println(s3[0]); s3[0]:XXXkb
                    //float l = Integer.valueOf(s3[0]) / 1024;
                    //float f = l / 1024;
                    //setRAMInfo(String.valueOf(Math.round(f)) + "GB");
                    setRAMInfo(s3[0]);
                    System.out.println(TAG+getRAMInfo());
                }
                return;
            }
                //String[] strs = line.split(":\\s");
                //tmpBatteryInfo.put(strs[0].replace(" ","").toString(), strs[1].toString());
        }

        public boolean isCancelled(){
            return false;
        }
    }

    private class CpuInfoReceiver extends MultiLineReceiver{
        //private static final String DISPLAY_MATCHER = "init=";
        public CpuInfoReceiver(){
            super();
        }

        public void execGetBatteryInfoCmd(IDevice iDevice){
            try {
                iDevice.executeShellCommand(GET_CPUINFO_CMD,this);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void processNewLines(String[] var){
            //HashMap<String,String> tmpBatteryInfo = new HashMap<>();
            for(String line: var){
              //  System.out.println(line);
                if(line.startsWith("Hardware")){
                    String[] s = line.split(": ");
                    if(s.length ==2 ) {
                        setCpuInfo(s[1]);
                        System.out.println(TAG+getCpuInfo());
                    }
                }
                //String[] strs = line.split(":\\s");
                //tmpBatteryInfo.put(strs[0].replace(" ","").toString(), strs[1].toString());
            }
        }

        public boolean isCancelled(){
            return false;
        }
    }
/****************************
    private class BatteryInfoReceiver extends MultiLineReceiver{
        //private static final String DISPLAY_MATCHER = "init=";
        public BatteryInfoReceiver(){
            super();
        }

        public void execGetBatteryInfoCmd(IDevice iDevice){
            try {
                iDevice.executeShellCommand(GET_BATTERY_CMD,this);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AdbCommandRejectedException e) {
                e.printStackTrace();
            } catch (ShellCommandUnresponsiveException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void processNewLines(String[] var){
            HashMap<String,String> tmpBatteryInfo = new HashMap<>();
            for(String line: var){
                System.out.println(line);
                String[] strs = line.split(":\\s");
                tmpBatteryInfo.put(strs[0].replace(" ","").toString(), strs[1].toString());
            }
            batteryInfo = new BatteryInfo(tmpBatteryInfo);
        }

        public boolean isCancelled(){
            return false;
        }
    }

    class BatteryInfo{
        private String status ;
        private String health ;
        //private String source ;
        private int level ;
        private int scale ;
        private double temperature ;
        private double voltage ;

        public BatteryInfo(HashMap<String,String> batteryStatus){
            this.status = batteryStatus.get("status");
            this.health = batteryStatus.get("health");
            this.level = Integer.valueOf(batteryStatus.get("level"));
            this.scale = Integer.valueOf(batteryStatus.get("scale"));
            this.temperature = (Double.valueOf(batteryStatus.get("temperature")))/10.0;
            this.voltage = (Double.valueOf(batteryStatus.get("voltage")))/1000.0;
        }

        public String getBatteryStatus(){
            return this.status;
        }
        public void setStatus(String status){this.status = status;}

        public String getBatteryHealth(){
            return this.health;
        }
        public void setHealth(String health){this.health = health;}

        public int getBatteryLevel(){
            return this.level;
        }
        public void setLevel(int level){this.level = level;}

        public int getBatteryScale(){
            return this.scale;
        }
        public void setScale(int scale){this.scale = scale;}

        public double getBatteryTemperature(){
            return this.temperature;
        }
        public void setTemperature(double temperature){this.temperature = temperature;}

        public double getBatteryVoltage(){
            return this.voltage;
        }
        public void setVoltage(double voltage){this.voltage = voltage;}
    }

 ******************************/

    private void setScreenInfo(String lcd){
    this.screenInfo = lcd;
}

    private void setCpuInfo(String cpu){
        this.cpuInfo = cpu;
    }

    private void setMacInfo(String mac){
        this.MACInfo = mac;
    }

    private void setRAMInfo(String ram){
        this.RAMInfo = ram;
    }

    public String getMacInfo(){return this.MACInfo;}
    public String getRAMInfo(){return this.RAMInfo;}

    public String getCpuInfo(){return this.cpuInfo;}
    public String getProductName(){
        return this.productName ;
    }
    public String getDeviceName(){
        return this.deviceName ;
    }
    public String getModel(){
        return this.model ;
    }
    public String getSdkVersion(){return this.sdkVersion ;}
    public String getAndroidVersion(){
        return this.androidVersion ;
    }
    public String getSerialId(){return this.serialId;}
    public String getScreenInfo(){return this.screenInfo;}
    public String getManufacturer(){return this.manufacturer;}
}
