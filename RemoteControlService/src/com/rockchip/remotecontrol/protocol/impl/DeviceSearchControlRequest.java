package com.rockchip.remotecontrol.protocol.impl;

import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.protocol.UDPPacket;
import java.nio.charset.Charset;

public class DeviceSearchControlRequest extends RemoteControlRequest
{
  private String mDeviceAddress;
  private String mDeviceName;

  public DeviceSearchControlRequest()
  {
    setControlType(1793);
  }

  public DeviceSearchControlRequest(UDPPacket packet) {
    super(packet);
  }

  protected byte[] encodeData()
  {
    String msg = this.mDeviceAddress + "@#@" + this.mDeviceName;
    byte[] data = msg.getBytes(Charset.defaultCharset());
    return data;
  }

  protected void decodeData(byte[] data)
  {
    String msg = new String(data, Charset.defaultCharset());
    String[] split = msg.split("@#@");
    if ((split != null) && (split.length == 2)) {
      this.mDeviceAddress = split[0];
      this.mDeviceName = split[1];
    }
  }

  public String getDeviceAddress() {
    return this.mDeviceAddress;
  }

  public void setDeviceAddress(String deviceAddress) {
    this.mDeviceAddress = deviceAddress;
  }

  public String getDeviceName() {
    return this.mDeviceName;
  }

  public void setDeviceName(String DeviceName) {
    this.mDeviceName = DeviceName;
  }
}