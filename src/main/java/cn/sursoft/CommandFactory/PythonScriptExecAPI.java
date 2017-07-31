package cn.sursoft.CommandFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gtguo on 7/14/2017.
 */

public abstract class PythonScriptExecAPI extends ParameterAssemble implements TestScriptHandler {

    public PythonScriptExecAPI(String userId,String taskName,
                               String[] serialId,
                               String argsJson,//Json args
                               String scriptPath){
        super(userId,taskName,serialId,argsJson,scriptPath);
    }
    @Override
    public void execTestCmd(String cmd){
        ProcessBuilder pb = new ProcessBuilder(cmd, getScriptPath(),getArgsJson());
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
    public File getTestReport(){return getFileReport();}
    @Override
    public File getTestLog(){return getFileLog();}
}
