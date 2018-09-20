package com.rockchip.remotecontrol.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DeviceInfo
  implements Parcelable
{
  private String mDeviceName;
  private String mDeviceAddress;
  private Long mTime;
  private int mDeviceIconId;
  public static final Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator()
  {
    public DeviceInfo createFromParcel(Parcel source) {
      DeviceInfo item = new DeviceInfo();
      item.mDeviceName = source.readString();
      item.mDeviceAddress = source.readString();
      item.mTime = Long.valueOf(source.readLong());
      item.mDeviceIconId = source.readInt();
      return item;
    }

    public DeviceInfo[] newArray(int size)
    {
      return new DeviceInfo[size];
    }
  };

  public DeviceInfo()
  {
    this.mTime = Long.valueOf(System.currentTimeMillis());
  }

  public DeviceInfo(DeviceInfo info) {
    this.mDeviceName = info.mDeviceName;
    this.mDeviceAddress = info.mDeviceAddress;
    this.mDeviceIconId = info.mDeviceIconId;
    this.mTime = info.mTime;
  }

  public void setDeviceInfo(DeviceInfo info) {
    this.mDeviceName = info.mDeviceName;
    this.mDeviceAddress = info.mDeviceAddress;
    this.mDeviceIconId = info.mDeviceIconId;
    this.mTime = info.mTime;
  }

  public String getDeviceName()
  {
    return this.mDeviceName;
  }

  public void setDeviceName(String DeviceName)
  {
    this.mDeviceName = DeviceName;
  }

  public String getDeviceAddress()
  {
    return this.mDeviceAddress;
  }

  public void setDeviceAddress(String DeviceAddress)
  {
    this.mDeviceAddress = DeviceAddress;
  }

  public Long getTime()
  {
    return this.mTime;
  }

  public void setTime(Long Time)
  {
    this.mTime = Time;
  }

  public int getDeviceIconId()
  {
    return this.mDeviceIconId;
  }

  public void setDeviceIconId(int DeviceIconId)
  {
    this.mDeviceIconId = DeviceIconId;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DeviceInfo other = (DeviceInfo)obj;
    if (this.mDeviceAddress == null) {
      if (other.getDeviceAddress() != null)
        return false;
    } else if (!this.mDeviceAddress.equals(other.getDeviceAddress()))
      return false;
    return true;
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeString(this.mDeviceName);
    dest.writeString(this.mDeviceAddress);
    dest.writeLong(this.mTime.longValue());
    dest.writeInt(this.mDeviceIconId);
  }
}