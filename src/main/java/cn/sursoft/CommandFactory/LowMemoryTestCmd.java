package cn.sursoft.CommandFactory;

import java.io.File;

/**
 * Created by gtguo on 5/17/2017.
 */

public class LowMemoryTestCmd implements TestScriptHandler {
    private static final String TAG = "LowMemoryTestCmd ";
    private ParameterAssemble parameter = null;

    public LowMemoryTestCmd(ParameterAssemble parameterAssemble){
        this.parameter = parameterAssemble;
    }

    public ParameterAssemble getParameter(){
        return this.parameter;
    }
    @Override
    public void execTestCmd(){
        System.out.println(TAG);
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
}
