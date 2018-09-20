package com.rockchip.remotecontrol.protocol;

import android.util.Log;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPSocket
{
  private DatagramSocket udpSock = null;
  private String localAddr = "";

  public UDPSocket() {
    open();
  }

  public UDPSocket(String bindAddr, int bindPort) {
    open(bindAddr, bindPort);
  }

  public UDPSocket(int bindPort) {
    open(bindPort);
  }

  protected void finalize() {
    close();
  }

  public void setLocalAddress(String addr) {
    this.localAddr = addr;
  }

  public DatagramSocket getDatagramSocket() {
    return this.udpSock;
  }

  public DatagramSocket getUDPSocket() {
    return this.udpSock;
  }

  public String getLocalAddress() {
    if ((this.localAddr != null) && (this.localAddr.length() > 0))
      return this.localAddr;
    if ((this.udpSock != null) && (this.udpSock.getLocalAddress() != null)) {
      return this.udpSock.getLocalAddress().getHostAddress();
    }
    return "";
  }

  public boolean open()
  {
    close();
    try {
      this.udpSock = new DatagramSocket();
    }
    catch (Exception e) {
      Log.e("UDPSocket", "USocket Open Error:" + e);
      return false;
    }
    return true;
  }

  public boolean open(String bindAddr, int bindPort)
  {
    close();
    try
    {
      InetSocketAddress bindSock = new InetSocketAddress(bindPort);
      this.udpSock = new DatagramSocket(null);
      this.udpSock.setReuseAddress(true);
      this.udpSock.bind(bindSock);
    } catch (Exception e) {
      Log.e("UDPSocket", "USocket Open Error:" + e);
      return false;
    }
    setLocalAddress(bindAddr);
    return true;
  }

  public boolean open(int bindPort)
  {
    close();
    try {
      InetSocketAddress bindSock = new InetSocketAddress(bindPort);
      this.udpSock = new DatagramSocket(null);
      this.udpSock.setReuseAddress(true);
      this.udpSock.bind(bindSock);
    } catch (Exception e) {
      Log.e("UDPSocket", "USocket Open Error:" + e);
      return false;
    }
    return true;
  }

  public boolean close()
  {
    if (this.udpSock == null)
      return true;
    try {
      this.udpSock.close();
      this.udpSock = null;
    }
    catch (Exception e) {
      Log.e("UDPSocket", "USocket Close Error:" + e);
      return false;
    }

    return true;
  }

  public boolean post(String addr, int port, String msg)
  {
    return post(addr, port, msg.getBytes());
  }

  public boolean post(String addr, int port, byte[] data) {
    try {
      InetAddress inetAddr = InetAddress.getByName(addr);
      DatagramPacket dgmPacket = new DatagramPacket(data, data.length, inetAddr, port);
      this.udpSock.send(dgmPacket);
    }
    catch (Exception e) {
      Log.e("UDPSocket", "USocket Post Data Error:" + e);
      return false;
    }
    return true;
  }

  public UDPPacket receive()
  {
    byte[] ssdvRecvBuf = new byte[512];
    UDPPacket recvPacket = new UDPPacket(ssdvRecvBuf, ssdvRecvBuf.length);
    recvPacket.setLocalAddress(getLocalAddress());
    try {
      this.udpSock.receive(recvPacket.getDatagramPacket());
      recvPacket.setTimeStamp(System.currentTimeMillis());
    }
    catch (SocketException e) {
      return null;
    } catch (Exception e) {
      Log.e("UDPSocket", "USocket Receive Data Error:" + e);
      return null;
    }
    return recvPacket;
  }
}