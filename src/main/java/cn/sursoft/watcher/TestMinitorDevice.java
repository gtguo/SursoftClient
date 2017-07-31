package cn.sursoft.watcher;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;
import cn.sursoft.util.SingletonMinitorIDevice;

/**
 * Created by gtguo on 6/27/2017.
 * 演示热插拔通知机制。
 * 实现DevicesWatchObserver接口
 */

public class TestMinitorDevice{

    public static void main(String[] args){
        Adb adb = new Adb();
        adb.getDevices();
        IDevice device = SingletonMinitorIDevice.getInstance().getIDeviceBySerialNum("WSPZLJLR89ONGIIN");
        System.out.println("TestMinitorDevice:"+device.getSerialNumber());
    }
}
