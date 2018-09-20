package com.reveive.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.imt.remote.remotecontrol.RemoteControlServices;

import android.os.Handler;
import android.util.Log;



public class UdpServer implements Runnable {  
    private int port;  
    
    private boolean RUNNING_THREAD = true;
    private String TAG = "UdpServer";
    private Handler handler;
    
    private DatagramSocket server;
    
    public UdpServer(int port,Handler handler) {  
        this.port = port;  
        this.handler = handler;
    }  
  
    public void run() {  
        try {  
            // listening at port for udp request  
            server = new DatagramSocket(new InetSocketAddress(port));  
            byte[] bs = new byte[4];  
            ByteBuffer bbuf = null;  
            DatagramPacket data = new DatagramPacket(bs, bs.length);  
//            server.setSoTimeout(1000 * 10); // set timeout  
            while (RUNNING_THREAD) { // 一直监听  
                server.receive(data);  
                
                String strRecv = new String(data.getData(), 0, data.getLength()) + " from "  
                + data.getAddress().getHostAddress() + ":" + data.getPort();  

                Utils.print(TAG, ""+strRecv);
                
                
                try {
    				handlePacket(data);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  

    public void close(){
    	try {
			RUNNING_THREAD = false;
			if(!server.isClosed())
			   server.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
 
    
	private void handlePacket(DatagramPacket packet) throws IOException,
			InterruptedException {

		String DESIRED_SERVICE = "_anymote._tcp";

		DatagramSocket outSocket = new DatagramSocket();

		StringBuilder builder = new StringBuilder();
		builder.append(DESIRED_SERVICE + " ");

		//release for 3188 4.2 
		
		//Ucontrol
		//rk3066
		//3188
		builder.append(Utils.getCurrentSystemName() + " ");
		builder.append("1234" + " ");
		byte[] b = builder.toString().getBytes();

		DatagramPacket p = new DatagramPacket(b, b.length);
		p.setAddress(packet.getAddress());
        p.setPort(packet.getPort());
		outSocket.send(p);
		
		Utils.print(TAG, "service respose from "+packet.getAddress());
		
		handler.sendEmptyMessage(RemoteControlServices.START_CONNECT);

	}
     
}  
