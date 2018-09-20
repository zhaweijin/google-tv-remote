package com.reveive.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Enumeration;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.google.polo.exception.PoloException;
import com.google.polo.pairing.ClientPairingSession;
import com.google.polo.pairing.PairingContext;
import com.google.polo.pairing.PairingListener;
import com.google.polo.pairing.PairingSession;
import com.google.polo.pairing.ServerPairingSession;
import com.google.polo.pairing.message.EncodingOption;
import com.google.polo.ssl.DummySSLServerSocketFactory;
import com.google.polo.wire.PoloWireInterface;
import com.google.polo.wire.WireFormat;
import com.imt.remote.remotecontrol.KeyStoreManager;
import com.imt.remote.remotecontrol.RemoteControlServices;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PairingServer implements Runnable {

	private Context mContext;
    private String LOG_TAG = "PairingServer";
    private String result="";
    
    private String DISMISS_ACTIVITY = "com.dismiss_output_activity";
    private boolean START_RUNNING = true;
    
    private Handler handler;
    private SSLServerSocket server = null;
	public PairingServer(Context mContext,Handler handler) {
		this.mContext = mContext;
		this.handler = handler;
	}

	
	public void run() {
		    START_RUNNING = true;
			server = getsslServerSocket(mContext);
			if(server==null)
				return;
			
			
			Thread th = new Thread(new Runnable() {

				public void run() {
					Utils.print(LOG_TAG, "pairing start...");
					while (START_RUNNING) {
						try {
							Utils.print(LOG_TAG, "pair lisenser...");
							SSLSocket socket = (SSLSocket)server.accept();

							Utils.print(LOG_TAG, "pairing tcp service>>>>>>>>>>>>>>>>>> has connection");
							handleResponse(socket);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			th.start();
		
	}

	
	public void close(){
		try {
			START_RUNNING = false;
			if(server!=null && !server.isClosed())
			   server.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	
	
	public  SSLServerSocket getsslServerSocket(Context context) {
		// Build a new key store based on the key store manager.
		try {
			SSLServerSocketFactory sslServerSocketFactory;
			 
			try {

				KeyStoreManager keyStoreManager = Utils.getKeyStoreManager(context);
				KeyManager[] keyManagers = keyStoreManager.getKeyManagers();
				sslServerSocketFactory = DummySSLServerSocketFactory.fromKeyManagers(keyManagers);
			} catch (GeneralSecurityException e) {
				throw new IllegalStateException("Cannot build socket factory",e);
			}
		    
		     
		   SSLServerSocket socket;
			try {
				socket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(1235);

				socket.setNeedClientAuth(true);
				socket.setUseClientMode(false);
				return socket;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return null;
	}
	
	public InetAddress getLocalAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress;
					}
				}
			}
		} catch (SocketException ex) {
			Utils.print(LOG_TAG, ex.toString());
		}
		return null;
	}
	

	
	private void handleResponse(SSLSocket clntSock) {

		try {

			PairingContext context;
			try {
				context = PairingContext.fromSslSocket(clntSock, true);
			} catch (PoloException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			PoloWireInterface protocol = WireFormat.PROTOCOL_BUFFERS.getWireInterface(context);
			ServerPairingSession pairingSession = new ServerPairingSession(protocol, context, "AnyMote");

			EncodingOption hexEnc = new EncodingOption(EncodingOption.EncodingType.ENCODING_HEXADECIMAL, 4);
			pairingSession.addInputEncoding(hexEnc);
			pairingSession.addOutputEncoding(hexEnc);

			PairingListener listener = new PairingListener() {
				public void onSessionEnded(PairingSession session) {
					Utils.print(LOG_TAG,"onSessionEnded: " + session);
					Utils.print(LOG_TAG,"session result====" + session.hasSucceeded());

				}

				public void onSessionCreated(PairingSession session) {
					Log.d(LOG_TAG, "onSessionCreated: " + session);
				}

				public void onPerformOutputDeviceRole(PairingSession session,
						byte[] gamma) {
					Utils.print(LOG_TAG, "onPerformOutputDeviceRole: " + session+ ", " + session.getEncoder().encodeToString(gamma));
					Utils.print(LOG_TAG, "service output encode>>>"+ session.getEncoder().encodeToString(gamma));
					Utils.print(LOG_TAG, "client name>>>>"+session.getPeerName());
					
					
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("vercify_code", session.getEncoder().encodeToString(gamma));
					bundle.putString("client_name", session.getPeerName());
					message.setData(bundle);
					message.what = RemoteControlServices.SHOW_VERCIFY_DIALOG;
					handler.sendMessage(message);
					
				}

				public void onPerformInputDeviceRole(PairingSession session) {
				}

				public void onLogMessage(LogLevel level, String message) {
					Utils.print(LOG_TAG, "Log: " + message + " (" + level + ")");
				}
			};

			boolean ret = pairingSession.doPair(listener);
			if (ret) {
				Utils.print(LOG_TAG, "Success");
				if (pairingSession.hasSucceeded()) {
					Utils.getKeyStoreManager(mContext).storeCertificate(context.getClientCertificate());
					handler.sendEmptyMessage(RemoteControlServices.START_CONNECT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.print(LOG_TAG, "pairing sucess-------");
			sendCloseDialogBroadcast();
			try {
				server.close();
				START_RUNNING = false;
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
       }
       
	}
	
	public void getIn(SSLSocket socket){  
        BufferedReader in = null;  
        String str = null;  
        try {  
            in = new BufferedReader(new InputStreamReader(  
                            socket.getInputStream()));  
            str = new String(in.readLine().getBytes(),"utf-8");  
            Utils.print("get message", ""+str);
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  

    }  

	public void sendCloseDialogBroadcast(){
		Intent intent = new Intent();
		intent.setAction(DISMISS_ACTIVITY);
		mContext.sendBroadcast(intent);
	}
}
