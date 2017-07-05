package cn.sursoft.logUtil;

import java.io.OutputStream;

/**
 * Created by gtguo on 7/5/2017.
 */

public interface TestLogGet {
    public void registerObserver(OutputShell o);

    public void removeObserver(OutputShell o);

    public void notifyObservers(OutputStream outputStream);
}
