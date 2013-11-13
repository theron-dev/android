package org.hailong.framework.net.dns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DNSService implements Runnable {

	
	private MulticastSocket _socket;
	private int _port;
	private boolean _isStarted;
	private Thread _thread;
	
	public DNSService(int port){
		_isStarted = false;
		_port = port;
	}
	
	
	public void start() throws IOException{
		if(!_isStarted){

			_socket = new MulticastSocket(_port);
			
			_socket.setBroadcast(true);
			
			_socket.joinGroup( InetAddress.getByName("255.255.255.255"));
			
			_thread = new Thread(this);
			
			_thread.start();
			
			_isStarted = true;
		}
	}
	
	public void close(){
		
		if(_isStarted){
			
			_socket.close();
			
			_socket = null;
			
			_thread.stop();
			
			_thread = null;
			
			
			
			_isStarted =false;
		}
	}

	
	public boolean isStarted(){
		return _isStarted;
	}


	public void run() {
		
	}
}
