package cn.sursoft.logUtil;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import org.omg.SendingContext.RunTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtguo on 7/11/2017.
 */

public class AdbCmdDebugInfoStream extends MultiLineReceiver implements TestLogGet{
    private IDevice iDevice = null;
    private List<OutputShell> observers = new ArrayList<OutputShell>();

    public AdbCmdDebugInfoStream(IDevice i){
        //this.cmd = cmd;
        this.iDevice = i;
    }

    public void execAdbCmd(String cmd){
        try {
            iDevice.executeShellCommand(cmd,this);
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

    public void stopAdbCmd(){
        //停止调试命令，如logcat，等于ctrl+C
        try{
            Runtime.getRuntime().exec("python ");
        }catch (IOException e){
            System.out.println("stop adb logcat err!");
            e.printStackTrace();
        }
    }
    @Override
    public void processNewLines(String[] strings) {
        for(String var : strings){
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
