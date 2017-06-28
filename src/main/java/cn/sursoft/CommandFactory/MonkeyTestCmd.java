package cn.sursoft.CommandFactory;

import java.io.File;

/**
 * Created by gtguo on 5/17/2017.
 */

public class MonkeyTestCmd implements TestScriptHandler {
    private static final String TAG = "MonkeyTestCmd ";

    private ParameterAssemble parameter = null;

    public MonkeyTestCmd(ParameterAssemble parameterAssemble){
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
