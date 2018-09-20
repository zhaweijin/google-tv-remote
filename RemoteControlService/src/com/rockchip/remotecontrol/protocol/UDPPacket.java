package com.rockchip.remotecontrol.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPPacket
{
  private DatagramPacket dgmPacket = null;
  private String localAddr = "";
  private long timeStamp;
  private byte[] packetBytes = null;

  protected UDPPacket() {
  }

  public UDPPacket(byte[] buf, int length) {
    this.dgmPacket = new DatagramPacket(buf, length);
  }

  public void setPacket(UDPPacket packet) {
    this.dgmPacket = packet.getDatagramPacket();
    this.localAddr = packet.getLocalAddress();
    this.timeStamp = packet.getTimeStamp();
    this.packetBytes = packet.getData();
  }

  public DatagramPacket getDatagramPacket() {
    return this.dgmPacket;
  }

  public byte[] getData() {
    if (this.packetBytes != null)
      return this.packetBytes;
    DatagramPacket packet = getDatagramPacket();
    int packetLen = packet.getLength();
    this.packetBytes = new byte[packetLen];
    System.arraycopy(packet.getData(), 0, this.packetBytes, 0, packetLen);
    return this.packetBytes;
  }

  public void setLocalAddress(String addr)
  {
    this.localAddr = addr;
  }

  public String getLocalAddress() {
    return this.localAddr;
  }

  public void setTimeStamp(long value) {
    this.timeStamp = value;
  }

  public long getTimeStamp() {
    return this.timeStamp;
  }

  public InetAddress getRemoteInetAddress() {
    return getDatagramPacket().getAddress();
  }

  public String getRemoteAddress() {
    return getDatagramPacket().getAddress().getHostAddress();
  }

  public int getRemotePort() {
    return getDatagramPacket().getPort();
  }

  public String toString() {
    return new String(getData());
  }
}