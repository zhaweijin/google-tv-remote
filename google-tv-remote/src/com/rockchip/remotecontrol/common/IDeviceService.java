package com.rockchip.remotecontrol.common;

import java.util.List;

public abstract interface IDeviceService
{
  public abstract void searchDevice();

  public abstract void clearCache();

  public abstract List<DeviceInfo> getCurrentList();

  public abstract void setConnectedDevice(DeviceInfo paramDeviceInfo);

  public abstract boolean IsConnectedDeviceExist();
}