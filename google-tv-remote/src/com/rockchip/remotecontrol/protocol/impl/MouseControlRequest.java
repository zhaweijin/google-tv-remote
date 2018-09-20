package com.rockchip.remotecontrol.protocol.impl;

import android.util.Log;
import com.rockchip.remotecontrol.protocol.RemoteControlRequest;
import com.rockchip.remotecontrol.protocol.UDPPacket;
import com.rockchip.remotecontrol.util.DataTypesConvert;

public class MouseControlRequest extends RemoteControlRequest
{
  private boolean DEBUG = false;
  private boolean isAbsolute;
  private int pointCount;
  private int[] pointerIds;
  private float[] mouseX;
  private float[] mouseY;
  private int actionCode;
  private int screenWidth;
  private int screenHeight;

  private void LOG(String msg)
  {
    if (this.DEBUG)
      Log.d("MouseControlRequest", msg);
  }

  public MouseControlRequest()
  {
    setControlType(513);
  }

  public MouseControlRequest(UDPPacket packet) {
    super(packet);
  }

 
	
  //3066
  protected byte[] encodeData()
  {
    byte[] data = new byte[15 + this.pointCount * 10];
    data[0] = isAbsolute ? ((byte)1) : (byte)0;
    byte[] tmp = DataTypesConvert.changeIntToByte(this.pointCount, 2);
    fillData(data, tmp, 1, 2);
    for (int i = 0; i < this.pointCount; i++) {
      tmp = DataTypesConvert.changeIntToByte(this.pointerIds[i], 2);
      fillData(data, tmp, 3 + 10 * i, 4 + 10 * i);
      tmp = DataTypesConvert.floatToByte(this.mouseX[i]);
      fillData(data, tmp, 5 + 10 * i, 8 + 10 * i);

      tmp = DataTypesConvert.floatToByte(this.mouseY[i]);
      fillData(data, tmp, 9 + 10 * i, 12 + 10 * i);
    }

    int newIndex = 3 + 10 * this.pointCount;
    tmp = DataTypesConvert.changeIntToByte(this.actionCode, 4);
    fillData(data, tmp, newIndex, newIndex + 3);

    newIndex += 4;
    tmp = DataTypesConvert.changeIntToByte(this.screenWidth, 4);
    fillData(data, tmp, newIndex, newIndex + 3);

    newIndex += 4;
    tmp = DataTypesConvert.changeIntToByte(this.screenHeight, 4);
    fillData(data, tmp, newIndex, newIndex + 3);
    return data;
  }

  //3066
  protected void decodeData(byte[] data)
  {
    this.isAbsolute = (data[0] == 1);
    byte[] tmp = fetchData(data, 1, 2);
    this.pointCount = DataTypesConvert.changeByteToInt(tmp);
    this.mouseX = new float[this.pointCount];
    this.mouseY = new float[this.pointCount];
    this.pointerIds = new int[this.pointCount];
    for (int i = 0; i < this.pointCount; i++)
    {
      this.pointerIds[i] = DataTypesConvert.changeByteToInt(data, 3 + 10 * i, 4 + 10 * i);
      tmp = fetchData(data, 5 + 10 * i, 8 + 10 * i);
      this.mouseX[i] = DataTypesConvert.byteToFloat(tmp);
      tmp = fetchData(data, 9 + 10 * i, 12 + 10 * i);
      this.mouseY[i] = DataTypesConvert.byteToFloat(tmp);
    }
    int newIndex = 3 + 10 * this.pointCount;
    this.actionCode = DataTypesConvert.changeByteToInt(data, newIndex, newIndex + 3);
    newIndex += 4;
    this.screenWidth = DataTypesConvert.changeByteToInt(data, newIndex, newIndex + 3);
    newIndex += 4;
    this.screenHeight = DataTypesConvert.changeByteToInt(data, newIndex, newIndex + 3);
    super.decodeData(data);
  }
  
  

  public int getPointerCount() {
    return this.pointCount;
  }

  public void setPointerCount(int count) {
    this.pointCount = count;
  }

  public int[] getPointerIds() {
    return this.pointerIds;
  }

  public void setPointerIds(int[] pointerIds) {
    this.pointerIds = pointerIds;
  }

  public float[] getMouseX() {
    return this.mouseX;
  }

  public void setMouseX(float[] mouseX) {
    this.mouseX = mouseX;
  }

  public int getActionCode() {
    return this.actionCode;
  }

  public void setActionCode(int actionCode) {
    this.actionCode = actionCode;
  }

  public float[] getMouseY() {
    return this.mouseY;
  }

  public void setMouseY(float[] mouseY) {
    this.mouseY = mouseY;
  }

  public boolean isAbsolute() {
    return this.isAbsolute;
  }

  public void setAbsolute(boolean isAbsolute) {
    this.isAbsolute = isAbsolute;
  }

  public int getScreenWidth() {
    return this.screenWidth;
  }

  public void setScreenWidth(int screenWidth) {
    this.screenWidth = screenWidth;
  }

  public int getScreenHeight() {
    return this.screenHeight;
  }

  public void setScreenHeight(int screenHeight) {
    this.screenHeight = screenHeight;
  }
}