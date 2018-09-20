package com.rockchip.remotecontrol.util;

import android.hardware.SensorEvent;
import android.view.MotionEvent;
import com.rockchip.remotecontrol.common.DeviceInfo;
import com.rockchip.remotecontrol.protocol.impl.GSensorControlRequest;
import com.rockchip.remotecontrol.protocol.impl.MouseControlRequest;
import com.rockchip.remotecontrol.protocol.impl.ScrollControlRequest;
import com.rockchip.remotecontrol.protocol.impl.SoftKeyControlRequest;

public class ResponseControlUtil
{
  private static final int MAX_DELAY_TIME = 45;
  private static final int MAX_TRY_CONNECT_TIME = 3;
  public static final int SCROLL_ORIANTION_V = 0;
  public static final int SCROLL_ORIANTION_H = 1;
  private static SoftKeyControlRequest mSoftKeyControlRequest;
  private static MouseControlRequest mMouseControlRequest;
  private static ScrollControlRequest mScrollControlRequest;
  private static GSensorControlRequest mGSensorControlRequest;
  private static DeviceInfo mConnectedDevice;

  public static void setConnectDevice(DeviceInfo device)
  {
    mConnectedDevice = device;
  }

  public static boolean PostSoftKeyEvent(int keycode, boolean isLongPress, boolean isCapsOn)
  {
    if (mConnectedDevice != null) {
      if (mSoftKeyControlRequest == null) {
        mSoftKeyControlRequest = new SoftKeyControlRequest();
        mSoftKeyControlRequest.setRequestHost(mConnectedDevice.getDeviceAddress());
      }
      mSoftKeyControlRequest.setKeyCode(keycode);
      mSoftKeyControlRequest.setLongPress(isLongPress);
      mSoftKeyControlRequest.setCapsOn(isCapsOn);

      mSoftKeyControlRequest.post(56456);
      return true;
    }
    return false;
  }

  public static boolean PostMotionEvent(MotionEvent event, boolean isTouchMode, int touchzoneW, int touchzoneH)
  {
    if (mConnectedDevice != null) {
      if (mMouseControlRequest == null) {
        mMouseControlRequest = new MouseControlRequest();
        mMouseControlRequest.setRequestHost(mConnectedDevice.getDeviceAddress());
      }

      int count = event.getPointerCount();
      count = count >= 2 ? 2 : count;
      float[] X = new float[count];
      float[] Y = new float[count];
      int[] pointerIds = new int[count];
      for (int i = 0; i < count; i++) {
        X[i] = event.getX(i);
        Y[i] = event.getY(i);
        pointerIds[i] = event.getPointerId(i);
      }
      event.setAction(event.getAction() & 0x1FF);
      mMouseControlRequest.setAbsolute(isTouchMode);
      mMouseControlRequest.setPointerCount(count);
      mMouseControlRequest.setPointerIds(pointerIds);
      mMouseControlRequest.setMouseX(X);
      mMouseControlRequest.setMouseY(Y);
      mMouseControlRequest.setActionCode(event.getAction());
      mMouseControlRequest.setScreenWidth(touchzoneW);
      mMouseControlRequest.setScreenHeight(touchzoneH);

      long t = System.currentTimeMillis();
      mMouseControlRequest.post(56456);
      long usedTime = System.currentTimeMillis() - t;
      if (usedTime < 45L)
        try {
          Thread.sleep(45L - usedTime);
        }
        catch (Exception localException) {
        }
      return true;
    }
    return false;
  }

  public static boolean PostScrollEvent(int orientation, float scrollLenght, int scrollzoneLenght, int action)
  {
    if (mConnectedDevice != null) {
      if (mScrollControlRequest == null) {
        mScrollControlRequest = new ScrollControlRequest();
        mScrollControlRequest.setRequestHost(mConnectedDevice.getDeviceAddress());
      }

      mScrollControlRequest.setOrientation(orientation);
      mScrollControlRequest.setTotalLenght(scrollzoneLenght);
      mScrollControlRequest.setOffset(scrollLenght);
      mScrollControlRequest.setAction(action);

      long t = System.currentTimeMillis();
      mScrollControlRequest.post(56456);
      long usedTime = System.currentTimeMillis() - t;
      if (usedTime < 45L)
        try {
          Thread.sleep(45L - usedTime);
        }
        catch (Exception localException) {
        }
      return true;
    }
    return false;
  }

  public static boolean PostGsensorEvent(SensorEvent event)
  {
    if (mConnectedDevice != null) {
      if (mGSensorControlRequest == null) {
        mGSensorControlRequest = new GSensorControlRequest();
        mGSensorControlRequest.setRequestHost(mConnectedDevice.getDeviceAddress());
      }
      float[] values = event.values;

      mGSensorControlRequest.setGx(values[0]);
      mGSensorControlRequest.setGy(values[1]);
      mGSensorControlRequest.setGz(values[2]);
      mGSensorControlRequest.setAccuracy(event.accuracy);
      mGSensorControlRequest.post(56456);
      return true;
    }
    return false;
  }

  public static boolean DisableGsensorControl()
  {
    if (mConnectedDevice != null) {
      GSensorControlRequest request = new GSensorControlRequest();
      request.setRequestHost(mConnectedDevice.getDeviceAddress());
      request.setControlType(259);
      boolean result = false;
      int i = 0;
      while ((!result) && (i < 3)) {
        i++;
        result = request.post(56456);
      }
      request.close();
      return true;
    }
    return false;
  }

  public static boolean EnableGsensorControl()
  {
    if (mConnectedDevice != null) {
      GSensorControlRequest request = new GSensorControlRequest();
      request.setRequestHost(mConnectedDevice.getDeviceAddress());
      request.setControlType(258);
      boolean result = false;
      int i = 0;
      while ((!result) && (i < 3)) {
        i++;
        result = request.post(56456);
      }
      request.close();
      return true;
    }
    return false;
  }

  public static void ColseControlRequest()
  {
    if (mSoftKeyControlRequest != null) {
      mSoftKeyControlRequest.close();
      mSoftKeyControlRequest = null;
    }

    if (mMouseControlRequest != null) {
      mMouseControlRequest.close();
      mMouseControlRequest = null;
    }

    if (mScrollControlRequest != null) {
      mScrollControlRequest.close();
      mScrollControlRequest = null;
    }

    if (mGSensorControlRequest != null) {
      mGSensorControlRequest.close();
      mGSensorControlRequest = null;
    }
  }
}