package com.screenShare;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class Server {
	ArrayList clients = new ArrayList();
	ArrayList clientsIn = new ArrayList();
	ServerSocket server;
	Server(){
		Timer t = new Timer();
		try {
			InetAddress addr = InetAddress.getByName("0.0.0.0");
			server = new ServerSocket(17771, 4, addr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		t.schedule(new GetClients(), 0, 1);
	}
	class GetClients extends TimerTask{
		
		@Override
		public void run(){
			while(true){
				try {
					Socket client = server.accept();
					PrintWriter pw = new PrintWriter(client.getOutputStream());
					clients.add(pw);
					InputStreamReader isr = new InputStreamReader(client.getInputStream());
					clientsIn.add(isr);
					getAndSend();
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		public void broadcast(String message){
			Iterator i = clients.iterator();
			while(i.hasNext()){
				try{
					PrintWriter pw = (PrintWriter) i.next();
					pw.println(message);
					pw.flush();
				}catch(Exception e){e.printStackTrace();}
			}
		}
		public void getAndSend(){
			while(true){
				Iterator i = clientsIn.iterator();
				while(i.hasNext()){
					try{
						InputStreamReader pw = (InputStreamReader) i.next();
						BufferedReader br = new BufferedReader(pw);
						broadcast(br.readLine());
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}
	}
}
