package cn.sursoft.watcher;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 6/27/2017.
 * 演示热插拔通知机制。
 * 实现DevicesWatchObserver接口
 */

public class TestMinitorDevice implements DevicesWatchObserver{
    @Override
    public void deviceConnectChange(IDevice iDevice, int flag){
        //热插拔后会回调此方法，参数iDevice表示热插拔设备句柄，flag：0/拔出，1/插入
        System.out.println(iDevice.getSerialNumber()+" FLAG: "+flag);
    }

    public static void main(String[] args){
        Adb adb = new Adb();
        TestMinitorDevice m = new TestMinitorDevice();
        adb.registerObserver(m);//把自己注册到监听列表
        adb.startMinitorOnClient();//开始监听
    }
}
