package cn.sursoft.watcher;

import com.android.ddmlib.IDevice;

/**
 * Created by gtguo on 6/27/2017.
 */

public interface DevicesWatchObserver {
    public void deviceConnectChange(IDevice iDevice,int flag);
}
