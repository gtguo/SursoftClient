package cn.sursoft.util;

import com.android.ddmlib.IDevice;

import java.util.HashMap;

import cn.sursoft.watcher.DevicesWatchObserver;

/**
 * Created by gtguo on 7/26/2017.
 */

public class SingletonMinitorIDevice implements DevicesWatchObserver {
    private static SingletonMinitorIDevice instance;
    private SingletonMinitorIDevice(){}
    public static SingletonMinitorIDevice getInstance(){
        if(instance == null){
            instance = new SingletonMinitorIDevice();
        }
        return instance;
    }

    private HashMap<String, IDevice> devInfo = new HashMap<>();

    public IDevice getIDeviceBySerialNum(String serialNum){
        return this.devInfo.get(serialNum);
    }

    public HashMap<String, IDevice> getDevInfo(){return this.devInfo;}

    @Override
    public void deviceConnectChange(IDevice iDevice, int flag){
        //热插拔后会回调此方法，参数iDevice表示热插拔设备句柄，flag：0/拔出，1/插入
        System.out.println("SingletonMinitorIDevice:"+iDevice.getSerialNumber()+" FLAG: "+flag);
        if(flag == 1){
            devInfo.put(iDevice.getSerialNumber(),iDevice);
        }else {
            devInfo.remove(iDevice.getSerialNumber());
        }
    }

}
