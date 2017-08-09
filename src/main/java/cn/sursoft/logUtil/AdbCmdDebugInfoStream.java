package cn.sursoft.logUtil;

import cn.sursoft.util.SingletonMinitorIDevice;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtguo on 7/11/2017.
 */

public class AdbCmdDebugInfoStream extends MultiLineReceiver implements TestLogGet{
    private IDevice iDevice = null;
    private List<OutputShell> observers = new ArrayList<OutputShell>();
    private String saveLogFilePath = null;
    private String serialNum;

    public AdbCmdDebugInfoStream(String serialNum,String saveLogFilePath){
        //this.cmd = cmd;
        this.serialNum = serialNum;
        this.iDevice = SingletonMinitorIDevice.getInstance().getIDeviceBySerialNum(serialNum);
        this.saveLogFilePath = saveLogFilePath;
    }

    public void execAdbCmd(String cmd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    iDevice.executeShellCommand(cmd,AdbCmdDebugInfoStream.this);
                } catch (TimeoutException e) {
                    System.out.println("adb cmd TimeoutException!");
                    e.printStackTrace();
                } catch (AdbCommandRejectedException e) {
                    System.out.println("adb cmd AdbCommandRejectedException!");
                    e.printStackTrace();
                } catch (ShellCommandUnresponsiveException e) {
                    System.out.println("adb cmd ShellCommandUnresponsiveException!");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("adb cmd IOException!");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getSerialNum(){
        return this.serialNum;
    }
    public void stopAdbLogcatCmd(){
        //停止调试命令，如logcat，等于ctrl+C
        //stopLogcat.py
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = System.getProperty("user.dir");
                path += File.separator + "script";
                path += File.separator + "stopLogcat.py";
                System.out.print("stopAdbLogcatCmd script:"+path);
                try {
                    Runtime.getRuntime().exec("python "+path+" -d "+getSerialNum());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void processNewLines(String[] strings) {
        for(String var : strings){
            //保存到saveLogFilePath 目录下log.txt
            if(saveLogFilePath == null){
                System.out.println("Error:saveLogFilePath is null! ");
            }else {
                try {
                    //保存到saveLogFilePath/log.txt
                    String log = saveLogFilePath+ File.separator +"log.txt";
                    FileOutputStream outputStream = new FileOutputStream(log,true);
                    byte[] tempBytes = var.getBytes();
                    outputStream.write(tempBytes);
                    outputStream.write("\r\n".getBytes());
                    System.out.println("write log.txt...");
                }catch (FileNotFoundException e){
                    System.out.println("saveLogFilePath/log.txt not found!");
                }catch (IOException e){
                    System.out.println(" write saveLogFilePath/log.txt IO ERR!");
                }
            }
            //实时log信息反馈
            notifyObservers(var);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public void registerObserver(OutputShell o){
        observers.add(o);
    }

    public void removeObserver(OutputShell o){
        int index = observers.indexOf(o);
        if (index != -1) {
            observers.remove(o);
        }
    }

    public void notifyObservers(String outputStream){
        for (OutputShell observer : observers) {
            observer.getOutputShellLog(outputStream);
        }
    }
}
