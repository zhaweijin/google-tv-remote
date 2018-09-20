package com.rockchip.remotecontrol.protocol.impl;

import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.protocol.UDPPacket;
import com.rockchip.remotecontrol.util.DataTypesConvert;

public class GSensorControlRequest extends RemoteControlRequest
{
  private float gx;
  private float gy;
  private float gz;
  private int accuracy;

  public GSensorControlRequest()
  {
    setControlType(257);
  }

  public GSensorControlRequest(UDPPacket packet) {
    super(packet);
  }

  protected byte[] encodeData()
  {
    byte[] data = new byte[16];
    byte[] tmp = DataTypesConvert.floatToByte(this.gx);
    fillData(data, tmp, 0, 3);
    tmp = DataTypesConvert.floatToByte(this.gy);
    fillData(data, tmp, 4, 7);
    tmp = DataTypesConvert.floatToByte(this.gz);
    fillData(data, tmp, 8, 11);
    tmp = DataTypesConvert.changeIntToByte(this.accuracy, 4);
    fillData(data, tmp, 12, 15);
    return data;
  }

  protected void decodeData(byte[] data)
  {
    byte[] tmp = fetchData(data, 0, 3);
    this.gx = DataTypesConvert.byteToFloat(tmp);
    tmp = fetchData(data, 4, 7);
    this.gy = DataTypesConvert.byteToFloat(tmp);
    tmp = fetchData(data, 8, 11);
    this.gz = DataTypesConvert.byteToFloat(tmp);
    this.accuracy = DataTypesConvert.changeByteToInt(data, 12, 15);
  }

  public float getGx() {
    return this.gx;
  }

  public void setGx(float gx) {
    this.gx = gx;
  }

  public float getGy() {
    return this.gy;
  }

  public void setGy(float gy) {
    this.gy = gy;
  }

  public float getGz() {
    return this.gz;
  }

  public void setGz(float gz) {
    this.gz = gz;
  }

  public int getAccuracy() {
    return this.accuracy;
  }

  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }
}