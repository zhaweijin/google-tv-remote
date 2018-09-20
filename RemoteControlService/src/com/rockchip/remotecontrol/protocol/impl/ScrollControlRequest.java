package com.rockchip.remotecontrol.protocol.impl;

import android.util.Log;
import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.protocol.UDPPacket;
import com.rockchip.remotecontrol.util.DataTypesConvert;

public class ScrollControlRequest extends RemoteControlRequest
{
  private boolean DEBUG = true;
  private int mOrientation;
  private float mOffset;
  private int mTotalLenght;
  private int mAction;

  private void LOG(String msg)
  {
    if (this.DEBUG)
      Log.d("ScrollControlRequest", msg);
  }

  public ScrollControlRequest()
  {
    setControlType(1025);
  }

  public ScrollControlRequest(UDPPacket packet) {
    super(packet);
  }

  protected byte[] encodeData()
  {
    byte[] data = new byte[13];
    data[0] = (byte)this.mOrientation;
    byte[] tmp = DataTypesConvert.changeIntToByte(this.mTotalLenght, 4);
    fillData(data, tmp, 1, 4);
    tmp = DataTypesConvert.floatToByte(this.mOffset);
    fillData(data, tmp, 5, 8);
    tmp = DataTypesConvert.changeIntToByte(this.mAction, 4);
    fillData(data, tmp, 9, 12);
    
    return data;
  }
  

  protected void decodeData(byte[] data)
  {
    this.mOrientation = data[0];
    byte[] tmp = fetchData(data, 1, 4);
    this.mTotalLenght = DataTypesConvert.changeByteToInt(tmp);
    tmp = fetchData(data, 5, 8);
    this.mOffset = DataTypesConvert.byteToFloat(tmp);
    tmp = fetchData(data, 9, 12);
    this.mAction = DataTypesConvert.changeByteToInt(tmp);
  }
  

  public int getOrientation() {
    return this.mOrientation;
  }

  public void setOrientation(int Orientation) {
    this.mOrientation = Orientation;
  }

  public float getOffset() {
    return this.mOffset;
  }

  public void setOffset(float Offset) {
    this.mOffset = Offset;
  }

  public int getTotalLenght() {
    return this.mTotalLenght;
  }

  public void setTotalLenght(int TotalLenght) {
    this.mTotalLenght = TotalLenght;
  }

  public int getAction() {
    return this.mAction;
  }

  public void setAction(int Action) {
    this.mAction = Action;
  }
  
	
}