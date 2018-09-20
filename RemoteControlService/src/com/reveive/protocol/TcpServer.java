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
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertPathValidatorException;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.imt.remote.remotecontrol.KeyStoreManager;
import com.imt.remote.remotecontrol.RemoteControlServices;


import android.R.bool;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class TcpServer implements Runnable {

	private String TAG = "TcpServer";
	private Context context;
	
	private AnymoteReceive receive;
	public enum ConnectionStatus {
		    OK,
		    ERROR_CREATE,
		    ERROR_HANDSHAKE
	}
	
	private ConnectionStatus currentConnectState = ConnectionStatus.OK;

	private boolean SERVER_RUNNING = true;
	private SSLServerSocket server;
	private Handler handler;
	public TcpServer(Context context,Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	public void run() {


		
		try {
			 SERVER_RUNNING = true;
			 currentConnectState = ConnectionStatus.OK;
			 server = getsslServerSocket(context);
             if(server==null)
			    return;
			 Utils.print(TAG, "tcp start...");
			 while (SERVER_RUNNING) {
				try {
					Utils.print(TAG, "tcp Listener...");
					SSLSocket socket = (SSLSocket)server.accept();
					Utils.print(TAG, "tcp service>>>>>>>>>>>>>>>>>>has connect");
							
					handleResponse(socket);
				} catch (Exception e) {
							// TODO: handle exception
					Utils.print(TAG, "main therad exception");
					e.printStackTrace();
				}
			}
			
//			Log.v(TAG, "tcp thread end");

		} catch (Exception e) {
			Utils.print(TAG, "aa");
            e.printStackTrace();
		}
		
		
		
	}

	public void close(){
		try {
			if(!server.isClosed())
			   server.close();
			SERVER_RUNNING = false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	
	
	
	public boolean getMainThreadState(){
		return SERVER_RUNNING;
	}
	
	
	public  SSLServerSocket getsslServerSocket(Context context) {
		// Build a new key store based on the key store manager.
		try {
			KeyStoreManager keyStoreManager = Utils.getKeyStoreManager(context);
			KeyManager[] keyManagers = keyStoreManager.getKeyManagers();
			TrustManager[] trustManagers = keyStoreManager.getTrustManagers();

			if (keyManagers.length == 0) {
				throw new IllegalStateException("No key managers");
			}

			// Create a new SSLContext, using the new KeyManagers and TrustManagers
			// as the sources of keys and trust decisions, respectively.
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);

			// Finally, build a new SSLSocketFactory from the SSLContext, and
			// then generate a new SSLServerSocket from it.
			SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(1234);
			serverSocket.setNeedClientAuth(true);
			serverSocket.setUseClientMode(false);
	

			return serverSocket;
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
			Utils.print("ee", ex.toString());
		}
		return null;
	}
	

	
	private void handleResponse(SSLSocket acceptSocket){

		try {
		    	SocketAddress clientAddress = acceptSocket.getRemoteSocketAddress();  
		    	Utils.print(TAG,"Handling client at " + clientAddress);  
				acceptSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
					
					@Override
					public void handshakeCompleted(HandshakeCompletedEvent event) {
						// TODO Auto-generated method stub
					    Utils.print(TAG, "tcp service hand shake complete");	
					}
				});
		        BufferedReader in = null;  
		        String str = null;  
		        in = new BufferedReader(new InputStreamReader(acceptSocket.getInputStream()));  
                str = new String(in.readLine().getBytes(),"utf-8");  
 
		        Utils.print(TAG, ">>>>>>>>>>>>>>>tcp");

		}catch (SSLException e) {
			// TODO: handle exception
			Utils.print(TAG, "SSL exception");
			currentConnectState = ConnectionStatus.ERROR_HANDSHAKE;
			handler.sendEmptyMessage(RemoteControlServices.START_PAIR_CONNECT);
		}catch (IOException e) {
			// TODO: handle exception
			Utils.print(TAG, "IO exception");
			currentConnectState = ConnectionStatus.ERROR_HANDSHAKE;
			handler.sendEmptyMessage(RemoteControlServices.START_PAIR_CONNECT);
		}catch (Exception e) {
			// TODO: handle exception
			Utils.print(TAG, "exception");
			e.printStackTrace();
			currentConnectState = ConnectionStatus.ERROR_CREATE;
		}finally{
			if(currentConnectState != ConnectionStatus.OK){
				try {
					server.close();
					acceptSocket.close();
					Log.v(TAG, "server socket close");
					SERVER_RUNNING = false;
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}
		
		
		Utils.print(TAG, "server socket state="+server.isClosed());
		if(!server.isClosed())
		{
			Utils.print(TAG, "AnymoteReceive loading......");
			receive = new AnymoteReceive(context,handler);
			
		      if (!receive.setSocket(acceptSocket)) {
		    	  Utils.print(TAG, "Initial message failed");
		        receive.disconnect();
		        try {
		        	acceptSocket.close();
		        } catch (IOException e) {
		        	Utils.print(TAG, "failed to close socket");
		          e.printStackTrace();
		        }
		      }
		}
       
	}
	


	

}
