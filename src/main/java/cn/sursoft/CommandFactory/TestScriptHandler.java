package cn.sursoft.CommandFactory;
import java.io.File;

/**
 * Created by gtguo on 5/16/2017.
 */

public interface TestScriptHandler {
    public void execTestCmd();
    public File getTestReport();
    public File getTestLog();
}
