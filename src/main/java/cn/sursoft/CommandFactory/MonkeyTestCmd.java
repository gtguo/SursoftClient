package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by gtguo on 5/17/2017.
 */

public class MonkeyTestCmd implements TestScriptHandler {
    private static final String TAG = "MonkeyTestCmd ";

    private ParameterAssemble parameter = null;

    public MonkeyTestCmd(ParameterAssemble parameterAssemble){
        this.parameter = parameterAssemble;
        //load stript and args file
        //FileInputStream f = new FileInputStream();
    }

    public ParameterAssemble getParameter(){
        return this.parameter;
    }

    @Override
    public void execTestCmd(){
        System.out.println(TAG);
        ExecTestThreadManager threadManager = new ExecTestThreadManager();
        IDevice[] d = getParameter().getSerialId();
        for(int i =0;i<d.length;i++){
            threadManager.getThreadPool().submit(new execThread(d[i].getSerialNumber()));
        }
        threadManager.getThreadPool().shutdown();
        //Process process = Runtime.getRuntime().exec(getParameter().getScriptPath());
        return;
    }
    @Override
    public File getTestReport(){
        return getParameter().getFileReport();
    }
    @Override
    public File getTestLog(){
        return getParameter().getFileLog();
    }

    class execThread implements Runnable{
        private String serialId = null;
        public execThread(String Id){
            this.serialId = Id;
        }
        @Override
        public void run(){
            try{
                Process process = Runtime.getRuntime().exec(getParameter().getScriptPath());
                process.getOutputStream();
                int exitVal = process.waitFor();
                System.out.println("Process exitValue: " + exitVal);
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
