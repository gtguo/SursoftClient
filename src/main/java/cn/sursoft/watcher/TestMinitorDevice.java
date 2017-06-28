package cn.sursoft.watcher;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 6/27/2017.
 */

public class TestMinitorDevice implements DevicesWatchObserver{
    @Override
    public void deviceConnectChange(IDevice iDevice, int flag){
        System.out.println(iDevice.getSerialNumber()+" FLAG: "+flag);
    }

    public static void main(String[] args){
        Adb adb = new Adb();
        TestMinitorDevice m = new TestMinitorDevice();
        adb.registerObserver(m);
        adb.startMinitorOnClient();
    }
}
