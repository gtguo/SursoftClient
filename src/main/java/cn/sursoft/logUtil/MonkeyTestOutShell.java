package cn.sursoft.logUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtguo on 7/3/2017.
 */

public class MonkeyTestOutShell implements TestLogGet {
    private static final String logFile = "log.txt";
    private List<OutputShell> observers = new ArrayList<OutputShell>();

    public FileInputStream getOutputShellLog(){
        try {
            FileInputStream in = new FileInputStream(logFile);
            if(null !=in){
                return in;
            }else {
                System.out.println("in == null! ");
            }
        }catch (FileNotFoundException e){
            System.out.println("log.txt not found!");
            e.printStackTrace();
        }
        return null;
    }

    public void registerObserver(OutputShell o){
        observers.add(o);
    }

    public void removeObserver(OutputShell o){
        int index = observers.indexOf(o);
        if (index != -1) {
            observers.remove(o);
        }
    }

    public void notifyObservers(String outputStream){
        for (OutputShell observer : observers) {
            observer.getOutputShellLog(outputStream);
        }
    }
}
