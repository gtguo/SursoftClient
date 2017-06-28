package cn.sursoft.watcher;

import com.android.ddmlib.IDevice;

/**
 * Created by gtguo on 6/27/2017.
 */

public interface DevicesWatcher {
    public void registerObserver(DevicesWatchObserver o);

    public void removeObserver(DevicesWatchObserver o);

    public void notifyObservers(IDevice iDevice,int flag);
}
