package cn.sursoft.util;

import com.android.ddmlib.*;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by gtguo on 8/2/2017.
 */
public abstract class ShellCmdInterface extends MultiLineReceiver {

    private IDevice iDevice;
    public ShellCmdInterface(IDevice i){
        this.iDevice = i;
    }
    public void executeShellCommand(String cmd){
        try {
            iDevice.executeShellCommand(cmd,this,50,SECONDS);
            //iDevice.installRemotePackage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isCancelled(){
        return false;
    }
}
