package com.rockchip.remotecontrol.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerManagerUtil
{
  private static final String TAG = "PowerManagerUtil";
  private PowerManager mPowerManager;
  private PowerManager.WakeLock mWakeLock;

  public PowerManagerUtil(Context context)
  {
    this(context, 10);
  }

  public PowerManagerUtil(Context context, int flags) {
    this.mPowerManager = ((PowerManager)context.getSystemService("power"));
    this.mWakeLock = this.mPowerManager.newWakeLock(flags, "PowerManagerUtil");
  }

  public void acquireWakeLock() {
    if (this.mWakeLock != null)
      try {
        if (!this.mWakeLock.isHeld())
          this.mWakeLock.acquire();
        else
          LogUtil.d(this, "error: wake lock is held!");
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  public void releaseWakeLock()
  {
    if (this.mWakeLock != null)
      try {
        if (this.mWakeLock.isHeld()) {
          LogUtil.d(this, "now release wake lock!");
          this.mWakeLock.release();
          this.mWakeLock.setReferenceCounted(false);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
}