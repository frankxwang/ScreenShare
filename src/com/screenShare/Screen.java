package com.screenShare;

import javax.imageio.ImageIO;
import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.Timer;

public class Screen{
	JFrame frame;
	Panel panel;
	Socket chatSoc;
	Screen(){
		frame = new JFrame();
		panel = new Panel();
		panel.setSize(new Dimension(400, 400));
		panel.setBackground(Color.green);
		frame.setPreferredSize(new Dimension(500, 500));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		try{
			chatSoc = new Socket("0.0.0.0", 4055);
		}catch(Exception e){}
		Timer t = new Timer();
		t.schedule(new Update(), 0, 1);
		t.schedule(new SendGet(), 0, 100);
	}
	class Update extends TimerTask{
		@Override
		public void run() {
			(panel).repaint();
		}
	}
	BufferedImage screen;
	BufferedImage otherScreen;
	class Panel extends JPanel{
		protected void paintComponent(Graphics g){
			Robot r = null;
			try {
				r = new Robot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screen = r.createScreenCapture(new Rectangle(screenSize));
//			screen = (BufferedImage) screen.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
			g.drawImage(screen, 0, 0, (int)(this.getHeight()*screenSize.getWidth()/screenSize.getHeight()), this.getHeight(), null);
		}
	}
	class SendGet extends TimerTask{

		@Override
		public void run() {
			Runnable r = new GetImg();
			Thread t = new Thread(r);
			t.start();
			Runnable r2 = new Send();
			Thread t2 = new Thread(r2);
			t2.start();
		}
		
	}
	class GetImg implements Runnable{
		public synchronized void run() {
			try {
				InputStreamReader isr = new InputStreamReader(chatSoc.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				byte[] b = br.readLine().getBytes();
				InputStream in = new ByteArrayInputStream(b);
				otherScreen = ImageIO.read(in);
			} catch (Exception e) {
//				e.printStackTrace();
				}
		}
	}
	
	class Send implements Runnable{
		public synchronized void run() {
			try {
				PrintWriter pw = new PrintWriter(chatSoc.getOutputStream());
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				ImageIO.write(screen, "jpg", baos);
				pw.write(new String(baos.toByteArray()));
				pw.flush();
				baos.close();
			} catch (Exception e){
				e.printStackTrace();}
		}
	}
}
