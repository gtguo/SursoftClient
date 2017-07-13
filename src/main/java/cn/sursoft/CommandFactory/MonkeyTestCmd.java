package cn.sursoft.CommandFactory;

import com.android.ddmlib.IDevice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        StringBuilder sb = new StringBuilder();
        //ExecThread[] t = null;
        ArrayList<ExecThread> t = new ArrayList<>();
        for(int i =0;i<d.length;i++){

            String s = d[i].getSerialNumber();
            sb.append(s);
            sb.append(" ");

        }
        System.out.println(sb.toString());
        ExecThread temp = new ExecThread(sb.toString());
        new Thread(temp).start();
    }
    @Override
    public File getTestReport(){
        return getParameter().getFileReport();
    }
    @Override
    public File getTestLog(){
        return getParameter().getFileLog();
    }

    class ExecThread implements Runnable{
        private String serialIdList = null;
        public ExecThread(String Id){
            this.serialIdList = Id;
        }
        @Override
        public void run(){
            try{
                System.out.println("python "+
                        getParameter().getScriptPath()+
                        " --Device "+
                        serialIdList);
                Process process = Runtime.getRuntime().exec("python "+
                        getParameter().getScriptPath()+
                        " --Device "+
                        serialIdList);

                //System.out.println(getShellOut(process));
                int exitVal = process.waitFor();
                System.out.println("Process exitValue: " +exitVal);
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        private String getShellOut(Process p) throws IOException{

            StringBuilder sb = new StringBuilder();
            BufferedInputStream in = null;
            BufferedReader br = null;

            try {

                in = new BufferedInputStream(p.getInputStream());
                br = new BufferedReader(new InputStreamReader(in));
                String s = null;

                while ((s = br.readLine()) != null ) {
                    // 追加换行符
                    sb.append("\r\n");
                    sb.append(s);
                }

            } catch (IOException e) {
                throw e;
            } finally {
                br.close();
                in.close();
            }

            return sb.toString();
        }
    }
}
