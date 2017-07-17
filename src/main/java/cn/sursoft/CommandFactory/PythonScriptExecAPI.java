package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gtguo on 7/14/2017.
 */

public abstract class PythonScriptExecAPI implements TestScriptHandler,TestStatusObserver {
    private IDevice iDevice = null;
    private String scriptFilePath = null;
    private String parameterJsonFilePath = null;

    public PythonScriptExecAPI(IDevice iDevice,String scriptFilePath,String parameterJsonFilePath){
        this.iDevice = iDevice;
        this.scriptFilePath = scriptFilePath;
        this.parameterJsonFilePath = parameterJsonFilePath;
    }
    @Override
    public void execTestCmd(){
        ProcessBuilder pb = new ProcessBuilder("python", scriptFilePath,
                        "--Device",iDevice.getSerialNumber(),"--Path",parameterJsonFilePath);
        //pb.directory(new File(SHELL_FILE_DIR));
        System.out.println(pb.command());
        int runningStatus = 0;
        String s = null;
        try {
            Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                // LOG.error(s);
                CharSequence keyword = "FINISH";
                System.out.println(s);
                if(s.contains(keyword))
                {
                    //adb logcat stop,and notice the platform
                }
            }
            while ((s = stdError.readLine()) != null) {
                // LOG.error(s);
            }
            try {
                runningStatus = p.waitFor();
            } catch (InterruptedException e) {
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public File getTestReport(){return null;}
    @Override
    public File getTestLog(){return null;}
    @Override
    public void TestStatusChanged(String status){}
}
