package cn.sursoft.CommandFactory;

import java.io.File;

/**
 * Created by gtguo on 5/17/2017.
 */

public class CTSTestCmd implements TestScriptHandler {
    private static final String TAG = "CTSTestCmd ";
    private ParameterAssemble parameter = null;

    public CTSTestCmd(ParameterAssemble parameterAssemble){
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
