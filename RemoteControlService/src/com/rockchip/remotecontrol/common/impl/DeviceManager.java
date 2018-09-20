package com.rockchip.remotecontrol.common.impl;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.rockchip.remotecontrol.common.DeviceInfo;
import com.rockchip.remotecontrol.common.IDeviceService;
import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.util.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class DeviceManager
{
  private BinderListener mBinderListener;
  private IDeviceService mDeviceService;
  private boolean isConnected;
  private Context mContext;
  private List<onDeviceChangeListener> mDeviceChangeListenerList = new ArrayList();

  private BroadcastReceiver mDeviceChangeBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context context, Intent intent)
    {
      String action = intent.getAction();
      DeviceInfo device = (DeviceInfo)intent.getParcelableExtra("deviceItem");
      LogUtil.d(DeviceManager.this, "Receiver device change broadcast:" + action + 
        " listener count:" + DeviceManager.this.mDeviceChangeListenerList.size());
      if (action.equals("com.rockchip.remotecontrol.action.AddDevice"))
        for (DeviceManager.onDeviceChangeListener listener : DeviceManager.this.mDeviceChangeListenerList)
          listener.DeviceAdd(device);
      else if (action.equals("com.rockchip.remotecontrol.action.RemoveDevice"))
        for (DeviceManager.onDeviceChangeListener listener : DeviceManager.this.mDeviceChangeListenerList)
          listener.DeviceRemove(device);
      else if (action.equals("com.rockchip.remotecontrol.action.UpdateDevice"))
        for (DeviceManager.onDeviceChangeListener listener : DeviceManager.this.mDeviceChangeListenerList)
          listener.DeviceUpdate(device);
    }
  };

  private ServiceConnection mServiceConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName component, IBinder binder) {
      DeviceManager.this.mDeviceService = ((IDeviceService)binder);
      DeviceManager.this.isConnected = true;
      if (DeviceManager.this.mBinderListener != null)
        DeviceManager.this.mBinderListener.onBindCompleted();
    }

    public void onServiceDisconnected(ComponentName name)
    {
      DeviceManager.this.mDeviceService = null;
      DeviceManager.this.isConnected = false;
    }
  };

  public DeviceManager(Context context)
  {
    this.mContext = context;
  }

  public boolean StartManager()
  {
    if (isConnectService()) return true;
    LogUtil.d(this, "start manager");
    RegisterDeviceChangeBroadcastListener();
    Intent i = new Intent(this.mContext, DeviceService.class);
    return this.mContext.bindService(i, this.mServiceConnection, 1);
  }

  public void StopManager()
  {
    if (!isConnectService()) return;
    LogUtil.d(this, "stop manager");
    UnregisterDeviceChangeBroadcastListener();
    this.mContext.unbindService(this.mServiceConnection);
    this.mDeviceService.clearCache();
  }

  public boolean isConnectService()
  {
    return this.isConnected;
  }

  public boolean validateService()
  {
    if (!this.isConnected) {
      LogUtil.e(this, "It hasn't bind to wimo service, please call startManager().");
      return false;
    }
    return true;
  }

  private void RegisterDeviceChangeBroadcastListener()
  {
    IntentFilter intentfilter = new IntentFilter();
    intentfilter.addAction("com.rockchip.remotecontrol.action.AddDevice");
    intentfilter.addAction("com.rockchip.remotecontrol.action.RemoveDevice");
    intentfilter.addAction("com.rockchip.remotecontrol.action.UpdateDevice");
    this.mContext.registerReceiver(this.mDeviceChangeBroadcastReceiver, intentfilter);
  }

  private void UnregisterDeviceChangeBroadcastListener()
  {
    try
    {
      this.mContext.unregisterReceiver(this.mDeviceChangeBroadcastReceiver);
      removeAllListener();
    }
    catch (Exception localException)
    {
    }
  }

  public boolean isDeviceExist(DeviceInfo device)
  {
    if ((device == null) || (device.getDeviceAddress() == null)) {
      return false;
    }
    if (!validateService()) return false;

    this.mDeviceService.setConnectedDevice(device);
    boolean result = false;
    RemoteControlRequest deviceCheckRequest = new RemoteControlRequest();
    deviceCheckRequest.setControlType(1537);
    deviceCheckRequest.setRequestHost(device.getDeviceAddress());
    result = deviceCheckRequest.post(56456);
    deviceCheckRequest.close();

    if (result) {
      try {
        Thread.sleep(1500L);
      }
      catch (Exception localException)
      {
      }
      return this.mDeviceService.IsConnectedDeviceExist();
    }

    return result;
  }

  public void searchDevice(boolean clearCache)
  {
    if (!validateService()) return;
    if (clearCache)
      this.mDeviceService.clearCache();
    this.mDeviceService.searchDevice();
  }

  public void clearDeviceCache()
  {
    if (!validateService()) return;
    this.mDeviceService.clearCache();
  }

  public List<DeviceInfo> getDeviceList()
  {
    if (!validateService()) return new ArrayList();
    return this.mDeviceService.getCurrentList();
  }

  public void setBinderListener(BinderListener listener)
  {
    this.mBinderListener = listener;
  }

  public void addDeviceChangeListener(onDeviceChangeListener listener)
  {
    if (!this.mDeviceChangeListenerList.contains(listener))
      this.mDeviceChangeListenerList.add(listener);
  }

  public void removeDeviceChangeListener(onDeviceChangeListener listener)
  {
    this.mDeviceChangeListenerList.remove(listener);
  }

  public void removeAllListener()
  {
    this.mDeviceChangeListenerList.clear();
  }

  public static abstract interface BinderListener
  {
    public abstract void onBindCompleted();
  }

  public static abstract interface onDeviceChangeListener
  {
    public abstract void DeviceAdd(DeviceInfo paramDeviceInfo);

    public abstract void DeviceRemove(DeviceInfo paramDeviceInfo);

    public abstract void DeviceUpdate(DeviceInfo paramDeviceInfo);
  }
}