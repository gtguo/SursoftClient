package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

/**
 * Created by gtguo on 5/16/2017.
 */

public class ParameterAssemble {
    private static final String TAG = "ParameterAssemble ";
    private String userId;
    private String taskName;
    private IDevice[] devices;
    private String scriptPath;
    private String argsJsonPath;

    private File fileReport = null;
    private File fileLog = null;

    public ParameterAssemble(String userId,String taskName,
                             IDevice[] serialId,
                             String argsJsonPath,
                             String scriptPath){
        this.userId = userId;
        this.taskName = taskName;
        this.devices = serialId;
        //this.scriptPath = downloadTestScriptFile(scriptPath);
        //this.argsJsonPath = downloadTestArgsJsonFile(argsJsonPath);
        this.scriptPath = scriptPath;
        this.argsJsonPath = argsJsonPath;
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
/*****************************************
    private String downloadTestScriptFile(String url){
        String localScriptName = "execScript.py";
        downloadFile(url,localScriptName);
        try{
            FileReader reader = new FileReader(localScriptName);
            if(reader.read()<=0){
                System.out.println("The execScript.py is empty!");
                return null;
            }
        }catch (FileNotFoundException e){
            System.out.println("Have no execScript.py file to exec!");
            return null;
        }catch (IOException e){
            System.out.println("Read execScript.py err!");
            return null;
        }
        return localScriptName;
    }

    private String downloadTestArgsJsonFile(String url){
        String localArgsFileName = "argsFile.json";
        downloadFile(url,localArgsFileName);
        try{
            FileReader reader = new FileReader(localArgsFileName);
            if(reader.read()<=0){
                System.out.println("The argsFile.json is empty!");
                return null;
            }
        }catch (FileNotFoundException e){
            System.out.println("Have no argsFile.json");
            return null;
        }catch (IOException e){
            System.out.println("Read argsFile.json err!");
            return null;
        }
        return localArgsFileName;
    }

    private void downloadFile(String urlFile,String fileName){
        try {
            URL url = new URL(urlFile);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();
            DataInputStream in = new DataInputStream(c.getInputStream());
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
            byte[] buffer = new byte[4096];
            int count =0;
            while ((count = in.read(buffer))>0){
                out.write(buffer,0,count);
            }
            out.close();
            in.close();
        }catch (Exception e){
            System.out.println("downloadFile failed:"+urlFile+" "+fileName);
            e.printStackTrace();
        }
    }
 *******************/
    private void generalTestReportLogFile(){
        String userdir = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");

        fileReport = new File(userdir+separator+"Report"
                    +separator+userId
                    +separator+taskName
                    +separator);
        fileReport.mkdirs();

        fileLog = new File(userdir+separator+
                    "Log"+separator
                    +userId+separator+taskName
                    +separator);
        fileLog.mkdirs();
        System.out.println(TAG+"generalTestReportLogFile");
    }

}
