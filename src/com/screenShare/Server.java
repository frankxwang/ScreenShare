package com.screenShare;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;


public class Server {
	ArrayList<PrintWriter> clients = new ArrayList<PrintWriter>();
	ArrayList<InputStreamReader> clientsIn = new ArrayList<InputStreamReader>();
	ServerSocket server;
	Server(){
		Timer t = new Timer();
		try {
			InetAddress addr = InetAddress.getByName("0.0.0.0");
			server = new ServerSocket(17771, 4, addr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		t.schedule(new GetClients(), 0, 0);
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
		public void broadcast(BufferedImage img){
			Iterator<PrintWriter> i = clients.iterator();
			while(i.hasNext()){
				try{
					PrintWriter pw = (PrintWriter) i.next();
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					ImageIO.write(img, "png", baos);
					pw.write(new String(baos.toByteArray()));
					pw.flush();
				}catch(Exception e){e.printStackTrace();}
			}
		}
		public void getAndSend(){
			while(true){
				Iterator<InputStreamReader> i = clientsIn.iterator();
				while(i.hasNext()){
					try{
						InputStreamReader pw = (InputStreamReader) i.next();
						BufferedReader br = new BufferedReader(pw);
						byte[] b = br.readLine().getBytes();
						InputStream in = new ByteArrayInputStream(b);
						broadcast(ImageIO.read(in));
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}
	}
}
