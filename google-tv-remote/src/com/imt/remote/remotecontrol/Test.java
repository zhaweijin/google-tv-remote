package com.imt.remote.remotecontrol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.google.anymote.Key.Code;
 
 
 
import com.rockchip.remotecontrol.protocol.impl.MouseControlRequest;
import com.rockchip.remotecontrol.protocol.impl.SoftKeyControlRequest;
import com.rockchip.remotecontrol.util.DataTypesConvert;
import com.rockchip.remotecontrol.util.ResponseControlUtil;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Test extends Activity{

	//MOUSE COMMAND
	public static final int TYPE_MOUSE_COMMAND = 0x0200;
	//MOUSE
	public static final int TYPE_MOUSE = 0x0201;
	
	//SOFTKEY COMMAND
	public static final int TYPE_SOFTKEY_COMMAND = 0x0300;
	//SOFTKEY
	public static final int TYPE_SOFTKEY = 0x0301;
	
	//SCROLL COMMAND
	public static final int TYPE_SCROLL_COMMAND = 0x0400;
	//SCROLL
	public static final int TYPE_SCROLL = 0x0401;
	
	
//	public static final int SELF_TYPE = 0x1110;
//	
//	public static final int TEST = 123;
	
	private MouseControlRequest mMouseControlRequest;
	private SoftKeyControlRequest mSoftKeyControlRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		
		
		Button button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			int deltaX = 2;
			int deltaY = 2;
			int count = 1;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				 sendKeyEventMessageToServer(false,false,KeyEvent.KEYCODE_VOLUME_UP);
//				 new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//							while(true){
//								sendMouseMessageToServer(deltaX, deltaY);
//								Thread.sleep(5000);
//								deltaX = deltaX + 3;
//								deltaY = deltaY + 3;
//								count++;
//								if(count==10)
//									break;
//							}
//						} catch (Exception e) {
//							// TODO: handle exception
//							e.printStackTrace();
//						}
//					}
//				}).start();
				
				String aa = "b";
				
				char   c   =   aa.charAt(0);   
				int   i   =(int)c;   
				Log.v("value", i+"");
			}
		});
		
		Button button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		button2.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				if (!ResponseControlUtil.PostMotionEvent(event, false, v.getWidth(), v.getHeight())){
//					Log.v("aaa", "bbbb");
//				}
				Log.v("aa", event.getPointerCount()+"");
				Log.v("bb", event.getPointerId(0)+"");
				

				
				
				return false;
				
			}
		});
		
		
		 
	}
	
	
	
    
}
