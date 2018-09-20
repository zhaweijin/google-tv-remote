package com.rockchip.remotecontrol.common;

import com.rockchip.remotecontrol.util.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class DeviceCache
{
  private static DeviceCache instance;
  private List<DeviceInfo> deviceInfoList = new ArrayList();
  private DeviceChangeListener mDeviceChangeListener;

  public static DeviceCache getInstance()
  {
    if (instance == null)
      instance = new DeviceCache();
    return instance;
  }

  public void add(DeviceInfo deviceInfo)
  {
    synchronized (this.deviceInfoList) {
      this.deviceInfoList.add(deviceInfo);
      if (this.mDeviceChangeListener != null)
        this.mDeviceChangeListener.DeviceAdd(deviceInfo);
    }
  }

  public void remove(DeviceInfo deviceInfo)
  {
    synchronized (this.deviceInfoList) {
      if (isExists(deviceInfo)) {
        this.deviceInfoList.remove(deviceInfo);
        if (this.mDeviceChangeListener != null)
          this.mDeviceChangeListener.DeviceRemove(deviceInfo);
      }
    }
  }

  public void update(DeviceInfo deviceInfo)
  {
    synchronized (this.deviceInfoList) {
      for (DeviceInfo item : this.deviceInfoList)
        if (item.equals(deviceInfo)) {
          if (!item.getDeviceName().equals(deviceInfo.getDeviceName())) {
            item.setDeviceName(deviceInfo.getDeviceName());
            this.mDeviceChangeListener.DeviceUpdate(item);
          }
          item.setTime(deviceInfo.getTime());
        }
    }
  }

  public void clear()
  {
    synchronized (this.deviceInfoList) {
      this.deviceInfoList.clear();
    }
  }

  public List<DeviceInfo> getDeviceInfoList()
  {
    synchronized (this.deviceInfoList) {
      List list = new ArrayList();
      list.addAll(this.deviceInfoList);
      return list;
    }
  }

  public DeviceInfo getDeviceInfoByAddress(String Address)
  {
    synchronized (this.deviceInfoList) {
      for (DeviceInfo item : this.deviceInfoList) {
        if ((item.getDeviceAddress() != null) && (item.getDeviceAddress().equals(Address)))
          return item;
      }
      return null;
    }
  }

  public boolean isExists(DeviceInfo deviceInfo)
  {
    if (deviceInfo == null) return false;
    synchronized (this.deviceInfoList) {
      for (DeviceInfo item : this.deviceInfoList) {
        if (item.getDeviceAddress().equals(deviceInfo.getDeviceAddress())) {
          return true;
        }
      }
      LogUtil.d("New RC device. RemoteHost is " + deviceInfo.getDeviceAddress());
      return false;
    }
  }

  public void setDeviceChangeListener(DeviceChangeListener listener)
  {
    this.mDeviceChangeListener = listener;
  }

  public static abstract interface DeviceChangeListener
  {
    public abstract void DeviceAdd(DeviceInfo paramDeviceInfo);

    public abstract void DeviceRemove(DeviceInfo paramDeviceInfo);

    public abstract void DeviceUpdate(DeviceInfo paramDeviceInfo);
  }
}