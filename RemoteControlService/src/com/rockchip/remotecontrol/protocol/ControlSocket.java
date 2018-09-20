package com.rockchip.remotecontrol.protocol;

import android.os.Process;
import android.util.Log;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ControlSocket extends UDPSocket
  implements Runnable
{
  private Thread responseThread = null;
  private RequestListener mRequestListener;
  private String ThreadNameHead;

  public ControlSocket(String NameHead)
  {
    this.ThreadNameHead = NameHead;
  }

  public ControlSocket(int port, String NameHead) {
    super(port);
    this.ThreadNameHead = NameHead;
  }

  public ControlSocket(String bindAddr, int port, String NameHead) {
    super(bindAddr, port);
    this.ThreadNameHead = NameHead;
  }

  public void setRequestListener(RequestListener requestListener)
  {
    this.mRequestListener = requestListener;
  }

  public void run()
  {
    Process.setThreadPriority(-4);

    Thread thisThread = Thread.currentThread();
    while (this.responseThread == thisThread) {
      Thread.yield();
      UDPPacket packet = receive();
      if (packet == null) return;
      if (this.mRequestListener != null)
        this.mRequestListener.requestRecieved(packet);
    }
  }

  public void start()
  {
    Log.d(this.ThreadNameHead, "responseThread start");
    StringBuffer name = new StringBuffer(this.ThreadNameHead + "/");
    DatagramSocket s = getDatagramSocket();
    InetAddress localAddr = s.getLocalAddress();
    if (localAddr != null) {
      name.append(s.getLocalAddress()).append(':');
      name.append(s.getLocalPort());
    }
    Log.d(this.ThreadNameHead, "thread name:" + name.toString());
    this.responseThread = new Thread(this, name.toString());
    this.responseThread.start();
  }

  public void stop() {
    close();
    this.responseThread = null;
  }

  public boolean post(String addr, int port, RemoteControlRequest request) {
    return post(addr, port, request.encodeMessage());
  }

  public static abstract interface RequestListener
  {
    public abstract void requestRecieved(UDPPacket paramUDPPacket);
  }
}