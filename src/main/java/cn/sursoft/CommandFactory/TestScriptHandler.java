package cn.sursoft.CommandFactory;
import java.io.File;

/**
 * Created by gtguo on 5/16/2017.
 */

public interface TestScriptHandler {
    public void execTestCmd(String cmd);
    public File getTestReport();
    public File getTestLog();
}
