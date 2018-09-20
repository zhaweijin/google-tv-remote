package com.rockchip.remotecontrol.common;

import com.rockchip.remotecontrol.util.LogUtil;
import com.rockchip.remotecontrol.util.ThreadCore;
import java.util.List;

public class DeviceCacheWatcher extends ThreadCore
{
  public void run()
  {
    while (isRunnable()) {
      try {
        Thread.sleep(3000L);
      } catch (InterruptedException localInterruptedException) {
      }
      List deviceInfoList = DeviceCache.getInstance().getDeviceInfoList();
      LogUtil.d(this, "device cache size:" + deviceInfoList.size());
      for (Object item1 : deviceInfoList) {
    	  DeviceInfo item = (DeviceInfo)item1;
        LogUtil.d(this, "device:" + item.getDeviceAddress() + " time:" + item.getTime());
        if (checkTimeOut(item)) {
          LogUtil.d(this, item.getDeviceAddress() + " timeout. ");

          DeviceCache.getInstance().remove(item);
        }
      }
    }
  }

  private boolean checkTimeOut(DeviceInfo deviceInfo)
  {
    long nowMills = System.currentTimeMillis() / 1000L * 1000L;
    long prevMills = deviceInfo.getTime().longValue() / 1000L * 1000L;
    return nowMills - prevMills > 30000L;
  }
}