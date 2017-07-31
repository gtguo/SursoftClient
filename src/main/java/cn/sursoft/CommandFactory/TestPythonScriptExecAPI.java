package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import cn.sursoft.logUtil.AdbCmdDebugInfoStream;
import cn.sursoft.util.Adb;

/**
 * Created by gtguo on 7/14/2017.
 */

public class TestPythonScriptExecAPI extends PythonScriptExecAPI {
    public TestPythonScriptExecAPI(String userId,String taskName, String[] serialId,
                               String argsJson,//Json args
                               String scriptPath){
        super(userId,taskName,serialId,argsJson,scriptPath);
    }
    public static void main(String[] args){

        /********************
        Adb adb = new Adb();
        IDevice[] devices = adb.getDevices();
        TestPythonScriptExecAPI t = null;
        if(devices.length>0){
            t = new TestPythonScriptExecAPI(devices[0],
                    "E:\\java\\SursoftClient\\GMonkey3\\GMonekey.py",
                    "E:\\java\\SursoftClient\\GMonkey3\\Monkey.json");
        }
        t.execTestCmd("python");
         *****************/
    }
}
