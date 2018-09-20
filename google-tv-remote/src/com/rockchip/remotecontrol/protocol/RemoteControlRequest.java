package com.rockchip.remotecontrol.protocol;

import android.util.Log;
import com.rockchip.remotecontrol.common.DeviceInfo;
import com.rockchip.remotecontrol.util.DataTypesConvert;

public class RemoteControlRequest
{
  private static final String TAG = "RemoteControlRequest";
  private static final boolean DEBUG = true;
  public static final int REMOTE_CONTROL_PORT = 56456;
  public static final int MIN_MSG_LEN = 4;
  private int controlType;
  private int length;
  private byte[] data;
  private UDPSocket uSocket;
  private String requestHost = "";
  private int requestPort = -1;

  protected boolean isBadMsg = false;

  private void LOG(String msg)
  {
    Log.d("RemoteControlRequest", msg);
  }

  public RemoteControlRequest()
  {
  }

  public RemoteControlRequest(byte[] msg)
  {
    decodeMessage(msg);
  }

  public RemoteControlRequest(UDPPacket packet) {
    decodeMessage(packet.getData());
  }

  protected byte[] encodeData()
  {
    return new byte[0];
  }

  protected void decodeData(byte[] data)
  {
  }

  public boolean post(int Port)
  {
    if (this.uSocket == null) {
      this.uSocket = new UDPSocket();
    }
    if (Port > 0)
    {
      this.requestPort = Port;
    }
    return this.uSocket.post(this.requestHost, this.requestPort, encodeMessage());
  }
  

  public boolean close()
  {
    if (this.uSocket != null) {
      this.uSocket.close();
    }
    return true;
  }

  public void setRequestHost(DeviceInfo device) {
    if (device == null) return;
    setRequestHost(device.getDeviceAddress());
    setRequestPort(56456);
  }

  public byte[] encodeMessage()
  {
    int len = 4;
    this.data = encodeData();
    this.length = this.data.length;
    if (this.data != null)
      len += this.data.length;
    byte[] msg = new byte[len];

    byte[] tmp = DataTypesConvert.changeIntToByte(this.controlType, 2);
    msg[0] = tmp[0];
    msg[1] = tmp[1];

    tmp = DataTypesConvert.changeIntToByte(this.length, 2);
    msg[2] = tmp[0];
    msg[3] = tmp[1];

    fillData(msg, this.data, 4, len - 1);
    return msg;
  }
  

  public void decodeMessage(byte[] msg)
  {
    this.isBadMsg = false;
    if ((msg == null) || (msg.length < 4)) {
      LOG("Bad message. Message is null or too short");
      this.isBadMsg = true;
      return;
    }
    this.length = DataTypesConvert.changeByteToInt(msg, 2, 3);
    if (this.length + 4 != msg.length) {
      LOG("Bad message. Message length error. ");
      this.isBadMsg = true;
      return;
    }

    this.controlType = DataTypesConvert.changeByteToInt(msg, 0, 1);

    this.data = fetchData(msg, 4, msg.length - 1);
    if ((this.data == null) || (this.data.length != this.length)) {
      LOG("data error: " + this.data);
      this.isBadMsg = true;
      return;
    }
    decodeData(this.data);
  }
  

  public void fillData(byte[] msg, byte[] data, int start, int end)
  {
    if ((data == null) || (start > end)) return;
    int i = start; for (int j = 0; (i <= end) && (j < data.length); j++) {
      msg[i] = data[j];

      i++;
    }
  }

  public byte[] fetchData(byte[] msg, int start, int end)
  {
    if (start > end) return null;
    byte[] tData = new byte[end - start + 1];
    int i = start; for (int j = 0; i <= end; j++) {
      tData[j] = msg[i];

      i++;
    }

    return tData;
  }

  public int getControlType() {
    return this.controlType;
  }

  public int getCommandType() {
    return this.controlType & 0xFF00;
  }

  public void setControlType(int controlType) {
    this.controlType = controlType;
  }

  public int getLength() {
    if (this.data == null) return 0;
    return this.data.length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }
  public String getStringData() {
    if (this.data == null) return null;
    return new String(this.data);
  }
  public byte[] getData() {
    return this.data;
  }

  public void setRequestHost(String host)
  {
    this.requestHost = host;
  }

  public String getRequestHost() {
    return this.requestHost;
  }

  public void setRequestPort(int host) {
    this.requestPort = host;
  }

  public int getRequestPort() {
    return this.requestPort;
  }
  public boolean isBadData() {
    return this.isBadMsg;
  }
}