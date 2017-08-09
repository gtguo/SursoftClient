package cn.sursoft.logUtil;

import com.android.ddmlib.IDevice;

import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 7/14/2017.
 */

public class TestLogcatGeneralFile implements OutputShell {
    public static void main(String[] args) {
        Adb adb = new Adb();
        IDevice[] devices = adb.getDevices();
        AdbCmdDebugInfoStream adbCmd = null;
            TestLogcatGeneralFile t = new TestLogcatGeneralFile();
        if(devices.length>0){
            adbCmd = new AdbCmdDebugInfoStream(devices[0].getSerialNumber(),null);
        }
        adbCmd.registerObserver(t);
        adbCmd.execAdbCmd("logcat");
        try{
            Thread.sleep(5*1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        adbCmd.stopAdbLogcatCmd();
        //adbCmd.removeObserver(t);
        //adbCmd.isCancelled();
    }
    @Override
    public void getOutputShellLog(String out){
        System.out.print(out);
    }
}
