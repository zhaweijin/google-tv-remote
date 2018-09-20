package com.imt.remote.remotecontrol;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.bouncycastle.jce.provider.JDKDSASigner.noneDSA;

import com.reveive.protocol.PairingServer;
import com.reveive.protocol.TcpServer;
import com.reveive.protocol.UdpServer;
import com.reveive.protocol.Utils;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

public class RemoteControlServices extends Service {

	ServerSocket serverSocket = null;

	private TcpServer tcpServer;
	private UdpServer udpServer;
	private PairingServer pairingServer;

	private String TAG = "RemoteControlServices";

	public static final int START_CONNECT = 0x111;
	public static final int START_PAIR_CONNECT = 0x222;
	public static final int SHOW_VERCIFY_DIALOG = 0x333;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_CONNECT:
				Log.v("aaaaaaaa", tcpServer.getMainThreadState() + "");
				if (!tcpServer.getMainThreadState())
					new Thread(tcpServer).start();
				// new Thread(new TcpServer(TestServices.this,handler)).start();
				break;
			case START_PAIR_CONNECT:
				// handel cancel dialog
				// new Thread(new
				// PairingServer(RemoteControlServices.this,handler)).start();
				new Thread(pairingServer).start();
				break;
			case SHOW_VERCIFY_DIALOG:
				Log.v(TAG, "receive message");
				showVercifyDialog(msg);
				break;
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if (!Utils.getKeyStoreManager(this).hasServerIdentityAlias()) {
			Utils.print(TAG, "init keystore");
			Utils.getKeyStoreManager(this).initializeKeyStore(getUniqueId());
		} else {
			Utils.print(TAG, "keystore exist");
		}

		
		String flag = "";
		try {
			flag = intent.getStringExtra("boot");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		if(flag.equals("start")){
			serviceStart();
		}else if(flag.equals("stop")) {
			serviceStop();
		}else {
			serviceStart();
		}
		
		
		 

		// Message message = new Message();
		// Bundle bundle = new Bundle();
		// bundle.putString("vercify_code", "aaaa");
		// bundle.putString("client_name", "eeeeeeeeeee");
		// message.setData(bundle);
		// message.what = RemoteControlServices.SHOW_VERCIFY_DIALOG;
		// handler.sendMessage(message);
		// showVercifyDialog(message);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		serviceStop();
	}
	
	public void serviceStart(){
		Utils.print(TAG, "services start");
		udpServer = new UdpServer(9101, handler);
		new Thread(udpServer).start();

		tcpServer = new TcpServer(this, handler);
		new Thread(tcpServer).start();

		pairingServer = new PairingServer(this, handler);
	}
	
	public void serviceStop(){
		try {
			Utils.print(TAG, "services stop");
			if(udpServer!=null)
				udpServer.close();
			if(tcpServer!=null)
				tcpServer.close();
			if(pairingServer!=null)
				pairingServer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	private void showVercifyDialog(Message msg) {

		Intent it = new Intent(this, OutputDialog.class);
		it.putExtras(msg.getData());
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(it);
	}

	private String getUniqueId() {
		String id = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
		// null ANDROID_ID is possible on emulator
		return id != null ? id : "emulator";
	}

}
