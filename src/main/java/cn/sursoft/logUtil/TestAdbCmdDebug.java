package cn.sursoft.logUtil;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 7/11/2017.
 * 获取adb logcat命令执行的打印信息，用于远程debug手机功能
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
            //adbCmd = new AdbCmdDebugInfoStream(devices[0],"E:\\java\\SursoftClient\\Log");
        }
        adbCmd.registerObserver(t);
        adbCmd.execAdbCmd("logcat");
        //adbCmd.removeObserver(t);
        //adbCmd.isCancelled();
    }
}
