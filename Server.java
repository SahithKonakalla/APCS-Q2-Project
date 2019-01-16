import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server implements Runnable {

	String name;
	final String client;
	final boolean isClient;
	String ip = "localhost";
	int port = 5454;
	
	ArrayList<PrintWriter> PW;
	ArrayList<BufferedReader> BR;

	ServerSocket ss;
	Socket s;

	PrintWriter pw;
	BufferedReader br;


	class ServerThread extends Thread{

		Socket soc;
		BufferedReader br1;
		public ServerThread(Socket s1){
			soc = s1;
			try {
				br1 = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot read from client");
			}

		}

		public void run() {
			String msg = null;
			
			while(true){
				try {
					msg = br1.readLine();
					if(msg != null){
						if(msg.charAt(0) == 'p'){
							System.out.println("forward: " + msg.substring(1));
						}
						else if(msg.charAt(0) == 'l'){
							System.out.println("lateral: " + msg.substring(1));
						}
					}
					for(int i=0; i<PW.size(); i++){
						PW.get(i).println(msg);
						PW.get(i).flush();
					}
				} catch (IOException e) {

				} finally{
					if(soc != null){
						try {
							soc.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Cannot end connection");
						}
					}
				}
			}
		}

	}


	public Server(String client, String name){
		this.name = name;
		this.client = client;
		if(client.equals("client")) isClient = true;
		else isClient = false;

		if(!isClient){
			PW = new ArrayList<PrintWriter>();
			BR = new ArrayList<BufferedReader>();
			try {
				ss = new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to receive connection from client");			
			}

		}
		else{
			try{
				s = new Socket(ip, port);
				pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			}catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to connect client to server");
			}
		}
	}	

	public synchronized void send(String message){
		if(isClient){
			pw.println(message);
			pw.flush();
		}
		else{
			System.out.println(PW.size());
			for(int i=0; i<PW.size(); i++){
				PW.get(i).println(message);
				PW.get(i).flush();
			}
		}
	}

	public void receive(){
		String msg = null;
		while(true){
			try {
				msg = br.readLine();
				if(msg != null){
					if(msg.charAt(0) == 'p'){
						System.out.println("forward: " + msg.substring(1));
					}
					else if(msg.charAt(0) == 'l'){
						System.out.println("lateral: " + msg.substring(1));
					}
				}
			} catch (IOException e) {
				System.out.println("Cannot read from server");
			} finally{
				if(s == null){
					try {
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Client connection has dropped");	
					}
				}
			}
		}
	}

	public void run() {
		if(isClient){
			receive();
		}
		else{
			while(true){
				try{
					System.out.println("LLLLLLLL");
					s = ss.accept();
					pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
					br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					System.out.println("HIII");
					PW.add(pw);
					BR.add(br);
					Thread t = new Thread(new ServerThread(s));
					t.start();
				} catch (Exception e){
					System.out.println("Cannot create a connection between client.");
				}
			}

		}
	}



}



















