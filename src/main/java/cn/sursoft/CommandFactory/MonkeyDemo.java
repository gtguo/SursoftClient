package cn.sursoft.CommandFactory;

/**
 * Created by gtguo on 7/6/2017.
 */

import java.util.GregorianCalendar;

import cn.sursoft.util.Adb;

public class MonkeyDemo {
    public static void main(String[] args){
        Adb adb = new Adb();
        ParameterAssemble param = new ParameterAssemble("gtguo",
                "monkey",
                adb.getDevices(),
                "E:\\java\\SursoftClient\\Gmonekey\\Monkey.json",
                "E:\\java\\SursoftClient\\Gmonekey\\GMonekey.py");
        adb.stopAndroidDebugBridge();
        MonkeyTestCmd m = new MonkeyTestCmd(param);
        m.execTestCmd();
    }
}
