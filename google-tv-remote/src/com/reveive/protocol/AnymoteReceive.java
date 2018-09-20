/*
 * Copyright (C) 2009 Google Inc.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reveive.protocol;


import com.google.anymote.Key.Action;
import com.google.anymote.Key.Code;
import com.google.anymote.common.AnymoteFactory;
import com.google.anymote.common.ConnectInfo;
import com.google.anymote.common.ErrorListener;
import com.google.anymote.device.DeviceAdapter;
import com.google.anymote.server.RequestReceiver;
import com.google.anymote.server.ServerAdapter;
import com.google.anymote.server.ServerMessageAdapter;
import com.reveive.protocol.AckManager.Listener;
import com.rockchip.remotecontrol.protocol.impl.MouseControlRequest;
import com.rockchip.remotecontrol.protocol.impl.MouseControlRequest3188_42;
import com.rockchip.remotecontrol.protocol.impl.ScrollControlRequest;
import com.rockchip.remotecontrol.protocol.impl.SoftKeyControlRequest;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * An implementation of the ICommandSender interface which uses the Anymote
 * protocol.
 *
 */
public final class AnymoteReceive {

  private final static String LOG_TAG = "AnymoteSender";

  /**
   * Core service that manages the connection to the server.
   */
  private final Context context;
  private Handler handler;

  /**
   * Receiver for the Anymote protocol.
   */
  private final RequestReceiver requestReceiver;

  /**
   * Error listener for the Anymote protocol.
   */
  private final ErrorListener errorListener;

  /**
   * Sender for the Anymote protocol.
   */
  private ServerAdapter serverAdapter;

  /**
   * The Ack manager.
   */
  private final AckManager ackManager;

  
  private MouseControlRequest mMouseControlRequest;
  private MouseControlRequest3188_42 mouseControlRequest3188_42;
  
  private SoftKeyControlRequest mSoftKeyControlRequest;
  private ScrollControlRequest mScrollControlRequest;
  
  private boolean is42 = false;
  
  private HashMap<String, Integer> keycodeHashMap = new HashMap<String, Integer>();
  private KeycodeMap map;
  public AnymoteReceive(Context context,Handler handler) {
    this.handler = handler;
    this.context = context;
    map = new KeycodeMap();
    
    is42 = Utils.checkCurrentSystemIsRK_42();
    
    map.initKeycodeMap();
    keycodeHashMap = map.getKeycodeMap();
    
    ackManager = new AckManager(new Listener() {
      public void onTimeout() {
        onConnectionError();
        
      }
    }, this);

    requestReceiver = new RequestReceiver() {
		
    	long newtime = 0;
    	long oldtime = 0;
    	Code oldCode;
    	
    	
    	int scrollCount = 0;
    	boolean moveFinish = false;
		int offset;
		boolean actionUp = false;
		int orientation = 0;
		
		@Override
		public void onMouseWheel(int arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "mouse wheel:x="+arg0+"*************"+"mouse wheel:y="+arg1);
			
			
//			oldtime = SystemClock.currentThreadTimeMillis();
		    
			
			/*
			 * 1 x
			 * 0 y
			 */
					
			
			
			if(Math.abs(arg0)>Math.abs(arg1)){
				orientation = 1;
				offset = arg0;
			}else {
				orientation = 0;
				offset = arg1;
			}
			
 
	
			if(!actionUp){
				actionUp=true;
				sendScrollMouseMessageToServer(orientation, offset, 1, MotionEvent.ACTION_DOWN);
			}else {
				sendScrollMouseMessageToServer(orientation, offset, 1, MotionEvent.ACTION_MOVE);
				if(!moveFinish){
					moveFinish=true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(150);
								sendScrollMouseMessageToServer(orientation, offset, 1, MotionEvent.ACTION_UP);
								moveFinish = false;
								actionUp = false;
							} catch (Exception e) {
									// TODO: handle exception
							    e.printStackTrace();
							}
						}
					  }).start();
				}
			}
			
		}
		
		@Override
		public void onMouseEvent(int arg0, int arg1) {
			// TODO Auto-generated method stub
//			Log.v(LOG_TAG, "onMouseEvent");
//			Log.v(LOG_TAG, "onMouseEvent:deltax="+arg0+"-------------"+"onMouseEvent:deltay="+arg1);
			if(is42)
				sendMouseMessageToServer_42(arg0, arg1);
			else {
				sendMouseMessageToServer(arg0, arg1);
			}
			   
		}
		
		@Override
		public void onKeyEvent(Code arg0, Action arg1) {
			// TODO Auto-generated method stub
			boolean isCapsOn = false;
			boolean isLongPress = false;
			Log.v(LOG_TAG, "code="+arg0.getNumber() +"******"+"action="+arg1.getNumber());
			
//			//only check keycode down event,because remote control check up event
//			if(arg1.getNumber()==1){
//				
//				if(oldCode!=null && oldCode==arg0){
//					if(newtime != 0 && oldtime!=0 && oldtime-newtime<=500){
//						isLongPress = true;
//					}
//				}
//				oldCode = arg0;
//				
//				newtime = SystemClock.currentThreadTimeMillis();
//				oldtime = newtime;
//				
//				sendKeyEventMessageToServer(isLongPress, isCapsOn, arg0.getNumber());
//			}
			if(arg1.getNumber()==1){
				if(arg0.getNumber()==272){
					Log.v("22222", "2222");
					sendKeyEventMessageToServer(isLongPress, isCapsOn, -101);
				}else {
					sendKeyEventMessageToServer(isLongPress, isCapsOn, arg0.getNumber());
				}
				
			}
			
            
		}
		
		@Override
		public boolean onFling(String arg0) {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "onFling");
			return false;
		}
		
		@Override
		public void onData(String arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "onData:type="+arg0+"***********"+"onData:value="+arg1);
//			ProtocolConstants.DATA_TYPE_STRING
			try {
				if(arg0.equals(ProtocolConstants.DATA_TYPE_STRING)){
					char c = arg1.charAt(0);
					if((c >= 'a' && c <= 'z')){
						sendKeyEventMessageToServer(false, false, keycodeHashMap.get(arg1.toUpperCase()));
					}else if((c >= 'A' && c <= 'Z')){
						sendKeyEventMessageToServer(false, true, keycodeHashMap.get(arg1));
					}else {
						sendKeyEventMessageToServer(false, false, keycodeHashMap.get(arg1));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		@Override
		public void onConnect(ConnectInfo arg0) {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "onConnect");
		}
	};

    errorListener = new ErrorListener() {
      public void onIoError(String message, Throwable exception) {
        Log.d(LOG_TAG, "IoError: " + message, exception);
        onConnectionError();
      }
    };
  }

  
	public boolean check(String data) {
		char c = data.charAt(0);
		if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
			return true;
		} else {
			return false;
		}
	}
  
  /**
   * Sets the socket the sender will use to communicate with the server.
   *
   * @param socket the socket to the server
   */
  public boolean setSocket(Socket socket) {
    if (socket == null) {
      throw new NullPointerException("null socket");
    }
    return instantiateProtocol(socket);
  }

  

  private void sendConnect() {
    ServerAdapter sender = getSender();
    if (sender != null) {
//      Log.v(LOG_TAG,"sender not null>>>>");
//      sender.sendConnect(new ConnectInfo(ProtocolConstants.DEVICE_NAME,
//          getVersionCode()));
    }
  }

  /**
   * Called when an error occurs on the transmission.
   */
  private void onConnectionError() {
    if (disconnect()) {
//      coreService.notifyConnectionFailed();
    	Log.v(LOG_TAG, "nead message connection error");
    }
  }

  /**
   * Instantiates the protocol.
   */
  private boolean instantiateProtocol(Socket socket) {
    disconnect();
    try {
    	serverAdapter =
          AnymoteFactory.getServerAdapter(requestReceiver, socket.getInputStream(), socket.getOutputStream(), errorListener);
    } catch (IOException e) {
      Log.d(LOG_TAG, "Unable to create sender", e);
      serverAdapter = null;
      return false;
    }
    Log.v(LOG_TAG, "send receive start");
//    sendConnect();
//    ackManager.start();
    return true;
  }

  /**
   * Returns the version number as defined in Android manifest
   * {@code versionCode}
   */
  private int getVersionCode() {
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(
          context.getPackageName(),
          0 /* basic info */);
      return info.versionCode;
    } catch (NameNotFoundException e) {
      Log.d(LOG_TAG, "cannot retrieve version number, package name not found");
    }
    return -1;
  }

  public synchronized boolean disconnect() {
    ackManager.cancel();
    if (serverAdapter != null) {
    	serverAdapter = null;
      return true;
    }
    return false;
  }

  private ServerAdapter getSender() {
    return serverAdapter;
  }

  
  public void sendMouseMessageToServer(int deltaX,int deltaY){

      try {   
			boolean isTouchMode = true;

			if (mMouseControlRequest == null){
				mMouseControlRequest = new MouseControlRequest();
			}
			int count = 1;
			count = count >= 2 ? 2 : count;
			float[] X = new float[count];
			float[] Y = new float[count];
			int[] pointerIds = new int[count];
			X[0] = deltaX;
			Y[0] = deltaY;
			pointerIds[0] = 0;

			mMouseControlRequest.setRequestHost("127.0.0.1");
			mMouseControlRequest.setAbsolute(true);
			mMouseControlRequest.setPointerCount(count);
			mMouseControlRequest.setPointerIds(pointerIds);
			mMouseControlRequest.setMouseX(X);
			mMouseControlRequest.setMouseY(Y);
			mMouseControlRequest.setActionCode(MotionEvent.ACTION_MOVE & 0x1FF);
			mMouseControlRequest.setScreenWidth(1);
			mMouseControlRequest.setScreenHeight(1);
			
			mMouseControlRequest.post(56456);
      
      } catch (Exception e) {  
          e.printStackTrace();  
      } 
      
  }
  
  public void sendMouseMessageToServer_42(int deltaX,int deltaY){

      try {   
			boolean isTouchMode = true;

			if (mouseControlRequest3188_42 == null){
				mouseControlRequest3188_42 = new MouseControlRequest3188_42();
			}
			int count = 1;
			count = count >= 2 ? 2 : count;
			float[] X = new float[count];
			float[] Y = new float[count];
			int[] pointerIds = new int[count];
			X[0] = deltaX;
			Y[0] = deltaY;
			pointerIds[0] = 0;

			mouseControlRequest3188_42.setRequestHost("127.0.0.1");
			mouseControlRequest3188_42.setAbsolute(true);
			mouseControlRequest3188_42.setPointerCount(count);
			mouseControlRequest3188_42.setPointerIds(pointerIds);
			mouseControlRequest3188_42.setMouseX(X);
			mouseControlRequest3188_42.setMouseY(Y);
			mouseControlRequest3188_42.setActionCode(MotionEvent.ACTION_MOVE & 0x1FF);
			mouseControlRequest3188_42.setScreenWidth(1);
			mouseControlRequest3188_42.setScreenHeight(1);
			
			mouseControlRequest3188_42.post(56456);
      
      } catch (Exception e) {  
          e.printStackTrace();  
      } 
      
  }
  
  
  public void sendKeyEventMessageToServer(boolean isLongPress,boolean isCapsOn,int mKeyCode){

      try {          	
      	if (mSoftKeyControlRequest == null) {
              mSoftKeyControlRequest = new SoftKeyControlRequest();
        }
      	mSoftKeyControlRequest.setRequestHost("127.0.0.1");
      	mSoftKeyControlRequest.setKeyCode(mKeyCode);
        mSoftKeyControlRequest.setLongPress(isLongPress);
        mSoftKeyControlRequest.setCapsOn(isCapsOn);

        mSoftKeyControlRequest.post(56456);

      } catch (Exception e) {  
          e.printStackTrace();  
      } 
      
  }
  
  
  /*
   * scrollLenght x :delta
   * scrollSecondLength y : delta
   */
  public void sendScrollMouseMessageToServer(int orientation, float scrollLenght,int scrollzoneLenght, int action){
	
	  try {
		  if (mScrollControlRequest == null) {
			  mScrollControlRequest = new ScrollControlRequest();
          }
		
		  mScrollControlRequest.setRequestHost("127.0.0.1");
		  mScrollControlRequest.setOrientation(orientation);
	      mScrollControlRequest.setTotalLenght(scrollzoneLenght);
	      mScrollControlRequest.setOffset(scrollLenght);
	      mScrollControlRequest.setAction(action);

          Log.v("aaaa", "aaaa");
	      mScrollControlRequest.post(56456);
		  
	  } catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	  }
  }

}
