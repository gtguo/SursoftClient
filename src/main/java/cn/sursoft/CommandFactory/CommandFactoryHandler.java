package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

/**
 * Created by gtguo on 7/24/2017.
 */

public class CommandFactoryHandler extends PythonScriptExecAPI implements TestStatusObserver {
    private TestTypeEnum typeEnum;
    public CommandFactoryHandler(String userId,String taskName,
                                 String[] serialId,
                                String argsJson,//Json args
                                String scriptPath,TestTypeEnum typeEnum){
        super(userId,taskName,serialId,argsJson,scriptPath);
        this.typeEnum = typeEnum;
    }

    //start
    public void startTask(){
        //generate cmd String
        String cmd = null;
        switch (typeEnum){
            case CTS:
                // cts cmd
                break;
            case LOWMEMORY:
                // low memory cmd
                break;
            case MONKEY:
                //monkey cmd
                break;
            case STABILITY:
                //stability cmd
                break;
            case MEMORYLEAK:
                //memory leak
                break;
            default:
                break;
        }
        execTestCmd(cmd);
    }
    //create Thread to do test task
    //class DoTask

    public void TestStatusChanged(String status){}
}
