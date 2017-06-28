package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import java.io.File;
import java.util.GregorianCalendar;

/**
 * Created by gtguo on 5/16/2017.
 */

public class ParameterAssemble {
    private static final String TAG = "ParameterAssemble ";
    private String userId;
    private String taskName;
    private GregorianCalendar calendar;
    private IDevice[] devices;
    private String scriptPath;

    private File fileReport = null;
    private File fileLog = null;

    public ParameterAssemble(String userId,String taskName,
                             GregorianCalendar calendar,
                             IDevice[] serialId,
                             String scriptPath){
        this.userId = userId;
        this.taskName = taskName;
        this.calendar = calendar;
        this.devices = serialId;
        this.scriptPath = scriptPath;
        generalTestReportLogFile();
    }

    public String getScriptPath(){
        return this.scriptPath;
    }
    public File getFileReport(){return this.fileReport;}
    public File getFileLog(){return this.fileLog;}
    public IDevice[] getSerialId(){
        return this.devices;
    }

    private void generalTestReportLogFile(){
        String userdir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");

        fileReport = new File(userdir+separator+"Report"
                    +separator+userId
                    +separator+taskName
                    +separator+calendar.get(GregorianCalendar.YEAR)
                    +(calendar.get(GregorianCalendar.MONTH)+1)
                    +calendar.get(GregorianCalendar.DAY_OF_MONTH)
                    +calendar.get(GregorianCalendar.HOUR)
                    +calendar.get(GregorianCalendar.MINUTE));
        fileReport.mkdirs();

        fileLog = new File(userdir+separator+
                    "Log"+separator
                    +userId+separator+taskName
                    +separator+calendar.get(GregorianCalendar.YEAR)
                    +(calendar.get(GregorianCalendar.MONTH)+1)
                    +calendar.get(GregorianCalendar.DAY_OF_MONTH)
                    +calendar.get(GregorianCalendar.HOUR)
                    +calendar.get(GregorianCalendar.MINUTE));
        fileLog.mkdirs();
        System.out.println(TAG+"generalTestReportLogFile");
    }

}
