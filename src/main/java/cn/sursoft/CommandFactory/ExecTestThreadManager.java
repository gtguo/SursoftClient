package cn.sursoft.CommandFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gtguo on 7/3/2017.
 */

public class ExecTestThreadManager {
    private static int MAX_THREAD_NUMBER = 8;
    private ExecutorService threadPool;

    public ExecTestThreadManager(){
        threadPool = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
    }

    public ExecutorService getThreadPool(){
        return this.threadPool;
    }

}
