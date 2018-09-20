package com.rockchip.remotecontrol.common.impl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.rockchip.remotecontrol.common.DeviceCache;
import com.rockchip.remotecontrol.common.DeviceCache.DeviceChangeListener;
import com.rockchip.remotecontrol.common.DeviceCacheWatcher;
import com.rockchip.remotecontrol.common.DeviceInfo;
import com.rockchip.remotecontrol.common.IDeviceService;
import com.rockchip.remotecontrol.protocol.ControlSocket;
import com.rockchip.remotecontrol.protocol.ControlSocket.RequestListener;
import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.protocol.UDPPacket;
import com.rockchip.remotecontrol.protocol.impl.DeviceSearchControlRequest;
import com.rockchip.remotecontrol.util.HostInterface;
import com.rockchip.remotecontrol.util.LogUtil;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceService extends Service
  implements DeviceCache.DeviceChangeListener
{
  private DeviceServiceBinder mServiceBinder;
  private Timer mSearchTimer;
  private DeviceCacheWatcher mDeviceCacheWatcher;
  private ControlSocket mUDPRespone;
  private boolean mIsConnectedDeviceExist;
  private DeviceInfo mConnectedDevice;
  public static final String ACTION_ADD_DEVICE = "com.rockchip.remotecontrol.action.AddDevice";
  public static final String ACTION_REMOVE_DEVICE = "com.rockchip.remotecontrol.action.RemoveDevice";
  public static final String ACTION_UPDATE_DEVICE = "com.rockchip.remotecontrol.action.UpdateDevice";
  public static final String KEY_DEVICE = "deviceItem";

  public void onCreate()
  {
    LogUtil.d(this, "DeviceService Create");
    super.onCreate();
    this.mServiceBinder = new DeviceServiceBinder();
    DeviceCache.getInstance().setDeviceChangeListener(this);
    this.mIsConnectedDeviceExist = false;
    this.mConnectedDevice = new DeviceInfo();
    Start();
  }

  public void onDestroy()
  {
    LogUtil.d(this, "DeviceService Destroy");
    super.onDestroy();
    Stop();
  }

  public void Start() {
    startUDPListener();
    startTimerSearch();
    this.mDeviceCacheWatcher = new DeviceCacheWatcher();
    this.mDeviceCacheWatcher.start();
  }

  public void Stop() {
    this.mDeviceCacheWatcher.stop();
    stopTimerSearch();
    stopUDPListener();
  }

  public IBinder onBind(Intent intent)
  {
    if (this.mServiceBinder == null)
      this.mServiceBinder = new DeviceServiceBinder();
    return this.mServiceBinder;
  }

  private void startTimerSearch()
  {
    this.mSearchTimer = new Timer();
    TimerTask task = new TimerTask() {
      public void run() {
        LogUtil.d(DeviceService.this, "timer search");
        DeviceService.this.search();
      }
    };
    this.mSearchTimer.schedule(task, 500L, 3000L);
  }

  private void stopTimerSearch()
  {
    if (this.mSearchTimer != null)
      this.mSearchTimer.cancel();
  }

  public synchronized void search()
  {
    String localAddr = HostInterface.getHostAddress(0);
    String broadcastAddr = "255.255.255.255";
    InterfaceAddress iaddr = HostInterface.getInterfaceAddressByAddress(localAddr);
    if ((iaddr != null) && (iaddr.getBroadcast() != null)) {
      broadcastAddr = iaddr.getBroadcast().getHostAddress();
    }

    RemoteControlRequest deviceSearchRequest = new RemoteControlRequest();
    deviceSearchRequest.setControlType(1793);
    deviceSearchRequest.setRequestHost(broadcastAddr);
    deviceSearchRequest.post(56456);
    deviceSearchRequest.close();
  }

  private void startUDPListener()
  {
    if (this.mUDPRespone == null) {
      this.mUDPRespone = new ControlSocket(56456, "RC.udpListener");
      this.mUDPRespone.setRequestListener(new ControlSocket.RequestListener()
      {
        public void requestRecieved(UDPPacket packet)
        {
          RemoteControlRequest respone = new RemoteControlRequest(packet);
          if (respone.getCommandType() == 1536) {
            LogUtil.d(DeviceService.this, "Recieved device " + packet.getRemoteAddress() + " check udp");
            synchronized (DeviceService.this.mConnectedDevice) {
              if (DeviceService.this.mConnectedDevice == null) {
                DeviceService.this.mIsConnectedDeviceExist = false;
              }
              else if (packet.getRemoteAddress().equals(DeviceService.this.mConnectedDevice.getDeviceAddress()))
                DeviceService.this.mIsConnectedDeviceExist = true;
              else {
                DeviceService.this.mIsConnectedDeviceExist = false;
              }
            }
          }
          if (respone.getCommandType() == 1792) {
            LogUtil.d(DeviceService.this, "Recieved device discovery udp");
            DeviceSearchControlRequest deviceSearchRespone = new DeviceSearchControlRequest(packet);

            if ((deviceSearchRespone.getDeviceAddress() == null) || 
              (deviceSearchRespone.getDeviceName() == null)) return;
            DeviceInfo device = new DeviceInfo();
            device.setDeviceAddress(deviceSearchRespone.getDeviceAddress());
            device.setDeviceName(deviceSearchRespone.getDeviceName());
            device.setTime(Long.valueOf(System.currentTimeMillis()));
            if (DeviceCache.getInstance().isExists(device))
              DeviceCache.getInstance().update(device);
            else {
              DeviceCache.getInstance().add(device);
            }
          }
        }
      });
    }
    this.mUDPRespone.start();
  }

  public void DeviceAdd(DeviceInfo deviceInfo)
  {
    Intent i = new Intent();
    i.setAction("com.rockchip.remotecontrol.action.AddDevice");
    i.putExtra("deviceItem", deviceInfo);
    sendBroadcast(i);
  }

  private void stopUDPListener()
  {
    if (this.mUDPRespone != null)
      this.mUDPRespone.stop();
  }

  public void DeviceRemove(DeviceInfo deviceInfo)
  {
    Intent i = new Intent();
    i.setAction("com.rockchip.remotecontrol.action.RemoveDevice");
    i.putExtra("deviceItem", deviceInfo);
    sendBroadcast(i);
  }

  public void DeviceUpdate(DeviceInfo deviceInfo)
  {
    Intent i = new Intent();
    i.setAction("com.rockchip.remotecontrol.action.UpdateDevice");
    i.putExtra("deviceItem", deviceInfo);
    sendBroadcast(i);
  }

  private class DeviceServiceBinder extends Binder
    implements IDeviceService
  {
    public DeviceServiceBinder()
    {
    }

    public void searchDevice()
    {
      LogUtil.d(DeviceService.this, "call search");
      DeviceService.this.search();
    }

    public void clearCache()
    {
      DeviceCache.getInstance().clear();
    }

    public List<DeviceInfo> getCurrentList()
    {
      return DeviceCache.getInstance().getDeviceInfoList();
    }

    public void setConnectedDevice(DeviceInfo device)
    {
      synchronized (DeviceService.this.mConnectedDevice) {
        DeviceService.this.mIsConnectedDeviceExist = false;
        DeviceService.this.mConnectedDevice = device;
      }
    }

    public boolean IsConnectedDeviceExist()
    {
      return DeviceService.this.mIsConnectedDeviceExist;
    }
  }
}