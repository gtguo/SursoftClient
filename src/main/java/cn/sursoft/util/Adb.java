package cn.sursoft.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import cn.sursoft.watcher.DevicesWatchObserver;
import cn.sursoft.watcher.DevicesWatcher;

public class Adb implements DevicesWatcher{

    private static final String TAG = "Sursoft ADB ";
    private AndroidDebugBridge mAndroidDebugBridge = null;
    private String adbPath = null;
    private String adbPlatformTools = "platform-tools";
    public static boolean hasInitAdb = false;

    private ArrayList<IDevice> devicelist = null;
    private NewDeviceListener listener = null;
    private List<DevicesWatchObserver> observers = new ArrayList<DevicesWatchObserver>();
    private SingletonMinitorIDevice singletonMinitorIDevice = null;

    public Adb(){
        devicelist = new ArrayList<>();
        listener = new NewDeviceListener();
        init();
        singletonMinitorIDevice = SingletonMinitorIDevice.getInstance();
        this.registerObserver(singletonMinitorIDevice);
        this.startMinitorOnClient();
       // getDevices();
    }

    public ArrayList<IDevice> getDevicelist(){
        return this.devicelist;
    }
    private void getUbuntuAdbPath(){

    }
    /**
     * 获取系统adb路径
     * @return
     */
    private void getWindowsADBPath(){
        if (adbPath == null){
            adbPath = System.getProperty("user.dir");
            System.out.println(TAG + adbPath);
            if(adbPath != null){
                adbPath += File.separator + adbPlatformTools;
                System.out.println(adbPath);
            }else {
                return ;
            }
        }
        adbPath += File.separator + "adb";
        System.out.println(adbPath);
        //return adbPath;
    }

    private String getADBPath(){
        String osName = System.getProperties().getProperty("os.name");
        if(osName.indexOf("Windows") >=0){
            getWindowsADBPath();
        }else{
            getUbuntuAdbPath();
        }
        return adbPath;
        //return "/Users/xiaobaogang/Library/Android/sdk/platform-tools/adb";
    }
    /**
     * 初始化adb连接
     * @return
     */
    private boolean init() {
        boolean success = false;
        if (!hasInitAdb){
            String adbPath = getADBPath();
            if (adbPath != null) {
                com.android.ddmlib.AndroidDebugBridge.init(false);
                mAndroidDebugBridge = AndroidDebugBridge.createBridge(adbPath, true);
                if (mAndroidDebugBridge != null) {
                    success = true;
                    hasInitAdb = true;
                }
                // 延时处理adb获取设备信息
                if (success) {
                    int loopCount = 0;
                    while (mAndroidDebugBridge.hasInitialDeviceList() == false) {
                        try {
                            Thread.sleep(100);
                            loopCount++;
                        } catch (InterruptedException e) {
                        }
                        if (loopCount > 100) {
                            success = false;
                            break;
                        }
                    }
                }
            }
        }

        return success;
    }

    // 获取连接的设备列表
    public IDevice[] getDevices() {
        IDevice[] devices = null;
        if (mAndroidDebugBridge != null) {
            devices = mAndroidDebugBridge.getDevices();
            for(int i=0;i<devices.length;i++){
                devicelist.add(devices[i]);
                singletonMinitorIDevice.getDevInfo().put(devices[i].getSerialNumber(),devices[i]);
                //devInfo.put(devices[i].getSerialNumber(),devices[i]);
            }
        }
        return devices;
    }



    public void stopAndroidDebugBridge(){
        AndroidDebugBridge.disconnectBridge();
    }

    public void startMinitorOnClient(){
        AndroidDebugBridge.addDeviceChangeListener(listener);
        listener.startDeviceMinitor();
    }
    public void stopMinitorOnClient(){
        listener.stopDeviceMinitor();
        AndroidDebugBridge.removeDeviceChangeListener(listener);
    }
    @Override
    public void registerObserver(DevicesWatchObserver o){
        observers.add(o);
    }
    @Override
    public void removeObserver(DevicesWatchObserver o){
        int index = observers.indexOf(o);
        if (index != -1) {
            observers.remove(o);
        }
    }
    @Override
    public void notifyObservers(IDevice iDevice,int flag){
        for (DevicesWatchObserver observer : observers) {
            observer.deviceConnectChange(iDevice,flag);
        }
    }

    private class NewDeviceListener implements AndroidDebugBridge.IDeviceChangeListener{
        private IDevice mDevice;
        //private String mSerial;
        private DeviceMinitor minitor;

        public NewDeviceListener(){
            minitor = new DeviceMinitor();
        }

        @Override
        public void deviceChanged(IDevice device,int changeMask){
            //IDevice.
            System.out.println("changeMask deviceChanged:"+changeMask);
        }

        @Override
        public void deviceConnected(IDevice device){
            System.out.println("deviceConnected");
            /***************
             if(mSerial == null){
             setDevice(device);
             }else if(mSerial.equals((device.getSerialNumber()))){
             setDevice(device);
             }
             ********/
            setDevice(device);
            notifyObservers(device,1);
        }

        @Override
        public void deviceDisconnected(IDevice device){
            System.out.println("deviceDisconnected");
            devicelist.remove(device);
            notifyObservers(device,0);
        }

        private synchronized void setDevice(IDevice device){
            System.out.println("setDevice");
            mDevice = device;
            devicelist.add(device);
            notify();
        }

        public void waitForDevice(){
            System.out.println("waitForDevice");
            synchronized (this){
                if(mDevice ==null){
                    try{
                        //wait(waitTime);
                        wait();
                    }catch (InterruptedException e){
                        System.out.println("Waiting for device interrupted");
                    }
                }
            }
            mDevice = null;
        }


        public void startDeviceMinitor(){
            System.out.println("startDeviceMinitor");
            minitor.start();
        }

        public void stopDeviceMinitor(){
            minitor.flag = true;
            minitor.interrupt();
        }
        class DeviceMinitor extends Thread{
            private boolean flag = false;
            @Override
            public void interrupt(){
                super.interrupt();
            }

            @Override
            public void run(){
                while (!flag){
                    //minitor
                    waitForDevice();
                }
            }
        }
    }

}
