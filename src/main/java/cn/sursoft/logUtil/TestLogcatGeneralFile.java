package cn.sursoft.logUtil;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 7/14/2017.
 */

public class TestLogcatGeneralFile {
    public static void main(String[] args){
        Adb adb = new Adb();
        IDevice[] devices = adb.getDevices();
        AdbCmdDebugInfoStream adbCmd = null;
        if(devices.length>0){
            adbCmd = new AdbCmdDebugInfoStream(devices[0],"E:\\java\\SursoftClient\\Log");
        }
        adbCmd.execAdbCmd("logcat");
        //adbCmd.removeObserver(t);
        //adbCmd.isCancelled();
    }
}
