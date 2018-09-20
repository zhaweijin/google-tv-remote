package com.rockchip.remotecontrol.util;

import android.util.Log;

public class LogUtil
{
  public static boolean ENABLED = true;

  public static void d(String msg) {
    if (ENABLED)
      Log.d("eHomeMediaCenter[DEBUG]", msg);
  }

  public static void d(Object obj, String msg)
  {
    if (ENABLED)
      Log.d(obj.getClass().getSimpleName(), msg);
  }

  public static void i(Object obj, String msg)
  {
    if (ENABLED)
      Log.i(obj.getClass().getSimpleName(), msg);
  }

  public static void v(Object obj, String msg)
  {
    if (ENABLED)
      Log.v(obj.getClass().getSimpleName(), msg);
  }

  public static void e(Object obj, String msg)
  {
    Log.e(obj.getClass().getSimpleName(), msg);
  }
  public static void e(Object obj, String msg, Exception ex) {
    Log.e(obj.getClass().getSimpleName(), msg, ex);
  }
  public static void e(String msg, Exception ex) {
    Log.e("eHomeMediaCenter[ERROR]", msg, ex);
  }

  public static void w(Object obj, String msg)
  {
    if (ENABLED)
      Log.w(obj.getClass().getSimpleName(), msg);
  }
}