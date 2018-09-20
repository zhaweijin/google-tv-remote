package com.reveive.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.imt.remote.remotecontrol.KeyStoreManager;

import android.content.Context;
import android.util.Log;

public class Utils {

	private static KeyStoreManager keyStoreManager;
	
	public static InetAddress getLocalAddress() {
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
			Log.e("ee", ex.toString());
		}
		return null;
	}

	
	public static void print(String tag,String value){
		Log.v(tag, value);	
	}
	 
	public static SSLServerSocket getSslSocket(Context context) {
		// Build a new key store based on the key store manager.
		try {
			KeyStoreManager keyStoreManager = getKeyStoreManager(context);
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
			// then generate a new SSLSocket from it.
			SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
			SSLServerSocket sock = (SSLServerSocket) factory.createServerSocket(1234);
			sock.setNeedClientAuth(true);
			sock.setUseClientMode(true);

			return sock;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return null;
	}
	
	
	public static KeyStoreManager getKeyStoreManager(Context context){
		if(keyStoreManager==null)
		  keyStoreManager = new KeyStoreManager(context);
		return keyStoreManager;
	}
	
	
	public static boolean checkCurrentSystemIsRK3188_42(){
		if(android.os.Build.MODEL.equals("rk31sdk") && android.os.Build.VERSION.RELEASE.equals("4.2.2")){
			return true;
		}
		return false;
	}
	
	
	public static String getCurrentSystemName(){
		if(android.os.Build.MODEL.equals("rk31sdk"))
			return "rk3188";
		else
            return "rk3066";
	}
	
}
