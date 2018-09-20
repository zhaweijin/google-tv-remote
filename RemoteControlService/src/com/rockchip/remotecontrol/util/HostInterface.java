package com.rockchip.remotecontrol.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class HostInterface
{
  public static boolean USE_LOOPBACK_ADDR = false;
  public static boolean USE_ONLY_IPV4_ADDR = true;
  public static boolean USE_ONLY_IPV6_ADDR = false;

  private static String ifAddress = "";
  public static final int IPV4_BITMASK = 1;
  public static final int IPV6_BITMASK = 16;
  public static final int LOCAL_BITMASK = 256;

  public static final void setInterface(String ifaddr)
  {
    ifAddress = ifaddr;
  }

  public static final String getInterface()
  {
    return ifAddress;
  }

  private static final boolean hasAssignedInterface()
  {
    return ifAddress.length() > 0;
  }

  private static final boolean isUsableAddress(InetAddress addr)
  {
    if ((!USE_LOOPBACK_ADDR) && 
      (addr.isLoopbackAddress())) {
      return false;
    }
    if ((USE_ONLY_IPV4_ADDR) && 
      ((addr instanceof Inet6Address))) {
      return false;
    }

    return (!USE_ONLY_IPV6_ADDR) || 
      (!(addr instanceof Inet4Address));
  }

  public static final int getNHostAddresses()
  {
    if (hasAssignedInterface()) {
      return 1;
    }
    int nHostAddrs = 0;
    try {
      Enumeration nis = NetworkInterface.getNetworkInterfaces();
      while (nis.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface)nis.nextElement();
        Enumeration addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
          InetAddress addr = (InetAddress)addrs.nextElement();
          if (!isUsableAddress(addr))
            continue;
          nHostAddrs++;
        }
      }
    }
    catch (Exception localException) {
    }
    return nHostAddrs;
  }

  public static final InetAddress[] getInetAddress(int ipfilter, String[] interfaces)
  {
    Enumeration nis;
    if (interfaces != null) {
      Vector iflist = new Vector();
      for (int i = 0; i < interfaces.length; i++)
      {
        NetworkInterface ni;
        try {
          ni = NetworkInterface.getByName(interfaces[i]);
        }
        catch (SocketException e)
        {
          continue;
        }
        
        if (ni == null) continue; iflist.add(ni);
      }

      nis = iflist.elements();
    } else {
      try {
        nis = NetworkInterface.getNetworkInterfaces();
      }
      catch (SocketException e)
      {
        return null;
      }
    }

    ArrayList addresses = new ArrayList();
    while (nis.hasMoreElements()) {
      NetworkInterface ni = (NetworkInterface)nis.nextElement();
      Enumeration addrs = ni.getInetAddresses();
      while (addrs.hasMoreElements()) {
        InetAddress addr = (InetAddress)addrs.nextElement();
        if (((ipfilter & 0x100) == 0) && (addr.isLoopbackAddress())) {
          continue;
        }
        if (((ipfilter & 0x1) != 0) && ((addr instanceof Inet4Address)))
          addresses.add(addr);
        else if (((ipfilter & 0x10) != 0) && ((addr instanceof InetAddress))) {
          addresses.add(addr);
        }
      }
    }
    return (InetAddress[])addresses.toArray(new InetAddress[0]);
  }

  public static final String getHostAddress(int n)
  {
    if (hasAssignedInterface()) {
      return getInterface();
    }
    int hostAddrCnt = 0;
    try {
      Enumeration nis = NetworkInterface.getNetworkInterfaces();
      while (nis.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface)nis.nextElement();
        Enumeration addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
          InetAddress addr = (InetAddress)addrs.nextElement();
          if (!isUsableAddress(addr))
            continue;
          if (hostAddrCnt < n) {
            hostAddrCnt++;
          }
          else {
            String host = addr.getHostAddress();

            return host;
          }
        }
      }
    } catch (Exception localException) {
    }
    return "";
  }

  public static final InterfaceAddress getInterfaceAddressByAddress(String address)
  {
    try {
      Enumeration nis = NetworkInterface.getNetworkInterfaces();
      while (nis.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface)nis.nextElement();
        List interfaceAddrs = ni.getInterfaceAddresses();
        Iterator it = interfaceAddrs.iterator();
        while (it.hasNext()) {
          InterfaceAddress addr = (InterfaceAddress)it.next();
          if (addr.getAddress().getHostAddress().equals(address))
            return addr;
        }
      }
    }
    catch (Exception localException) {
    }
    return null;
  }

  public static final boolean isIPv6Address(String host)
  {
    try
    {
      InetAddress addr = InetAddress.getByName(host);

      return (addr instanceof Inet6Address);
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  public static final boolean isIPv4Address(String host)
  {
    try {
      InetAddress addr = InetAddress.getByName(host);

      return (addr instanceof Inet4Address);
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  public static final boolean hasIPv4Addresses()
  {
    int addrCnt = getNHostAddresses();
    for (int n = 0; n < addrCnt; n++) {
      String addr = getHostAddress(n);
      if (isIPv4Address(addr))
        return true;
    }
    return false;
  }

  public static final boolean hasIPv6Addresses()
  {
    int addrCnt = getNHostAddresses();
    for (int n = 0; n < addrCnt; n++) {
      String addr = getHostAddress(n);
      if (isIPv6Address(addr))
        return true;
    }
    return false;
  }

  public static final String getIPv4Address()
  {
    int addrCnt = getNHostAddresses();
    for (int n = 0; n < addrCnt; n++) {
      String addr = getHostAddress(n);
      if (isIPv4Address(addr))
        return addr;
    }
    return "";
  }

  public static final String getIPv6Address()
  {
    int addrCnt = getNHostAddresses();
    for (int n = 0; n < addrCnt; n++) {
      String addr = getHostAddress(n);
      if (isIPv6Address(addr))
        return addr;
    }
    return "";
  }

  public static final String getHostURL(String host, int port, String uri)
  {
    String hostAddr = host;
    if (isIPv6Address(host))
      hostAddr = "[" + host + "]";
    return 
      "http://" + 
      hostAddr + 
      ":" + Integer.toString(port) + 
      uri;
  }

  public static String getFirstUsableAddress()
  {
    int nHostAddrs = getNHostAddresses();
    String bindAddress = null;
    if (nHostAddrs > 0) {
      bindAddress = getHostAddress(0);
    }
    if (bindAddress == null) {
      bindAddress = getDefaultLocalhostIPAddress();
    }
    return bindAddress;
  }

  public static String getDefaultLocalhostIPAddress()
  {
    try
    {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostAddress();
    } catch (UnknownHostException localUnknownHostException) {
    }
    return null;
  }

  public static boolean isLocalAddress(String hostAddr)
  {
    int nHostAddrs = getNHostAddresses();
    for (int n = 0; n < nHostAddrs; n++) {
      String bindAddress = getHostAddress(n);
      if ((bindAddress != null) && (bindAddress.equals(hostAddr))) {
        return true;
      }
    }
    return false;
  }

  public static byte[] getFirstNetworkInterfaceHardwareAddress() {
    try {
      Enumeration interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
      for (Object ifacee : Collections.list(interfaceEnumeration)) {
    	  NetworkInterface iface = (NetworkInterface)ifacee;
        if ((!iface.isLoopback()) && (iface.getHardwareAddress() != null)) {
          return iface.getHardwareAddress();
        }
      }
    }
    catch (Exception localException)
    {
    }
    return null;
  }
}