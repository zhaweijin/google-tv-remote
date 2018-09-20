package com.rockchip.remotecontrol.util;

public abstract class ThreadCore
  implements Runnable
{
  private Thread mThreadObject = null;

  public void setThreadObject(Thread obj) {
    this.mThreadObject = obj;
  }

  public Thread getThreadObject() {
    return this.mThreadObject;
  }

  public void start()
  {
    Thread threadObject = getThreadObject();
    if (threadObject == null) {
      threadObject = new Thread(this, "RC.ThreadCore");
      setThreadObject(threadObject);
      threadObject.start();
    }
  }

  public boolean isRunnable()
  {
    return Thread.currentThread() == getThreadObject();
  }

  public void stop()
  {
    Thread threadObject = getThreadObject();
    if (threadObject != null) {
      setThreadObject(null);
      threadObject.interrupt();
    }
  }

  public void restart()
  {
    stop();
    start();
  }

  public void interrupt()
  {
    Thread threadObject = getThreadObject();
    if (threadObject != null)
      threadObject.interrupt();
  }
}