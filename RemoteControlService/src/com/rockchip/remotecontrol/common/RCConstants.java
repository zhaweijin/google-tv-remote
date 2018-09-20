package com.rockchip.remotecontrol.common;

public class RCConstants
{
  public static final String VERSION_SIGN = "RKXX";
  public static final int VERSION_CODE = 32;
  public static final int RECV_MESSAGE_BUFSIZE = 1024;
  public static final int MIN_MESSAGE_SIZE = 23;
  public static final int MAX_MESSAGE_SIZE = 2048;
  public static final int CONNECT_TIME_OUT = 10000;
  public static final int READ_TIME_OUT = 4000;
  public static final int WIMO_UDP_RX_PORT = 48689;
  public static final int WIMO_UDP_TX_PORT = 48689;
  public static final int WIMO_TCP_RX_PORT = 48789;
  public static final int WIMO_TCP_TX_PORT = 48799;
  public static final String RC_UDP_ADDR = "255.255.255.255";
  public static final int SERVER_RETRY_COUNT = 10;
  public static final int HEART_CYCLE = 3000;
  public static final int HEART_CHECK_CYCLE = 3000;
  public static final int HEART_TIME_OUT = 30000;
}