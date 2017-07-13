package cn.sursoft.logUtil;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 7/11/2017.
 */

public class TestAdbCmdDebug implements OutputShell {
    @Override
    public void getOutputShellLog(String out) {
        System.out.println(out);
    }

    public static void main(String[] args){
        Adb adb = new Adb();
        IDevice[] devices = adb.getDevices();

        TestAdbCmdDebug t = new TestAdbCmdDebug();
        AdbCmdDebugInfoStream adbCmd = null;
        if(devices.length>0){
            adbCmd = new AdbCmdDebugInfoStream(devices[0]);
        }
        adbCmd.registerObserver(t);
        adbCmd.execAdbCmd("logcat");
        //adbCmd.removeObserver(t);
        //adbCmd.isCancelled();
    }
}
