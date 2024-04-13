package com.computernetworking3800group10.tictactoe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {
	
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ServerSocket serverSocket;
	
	private boolean accepted = false;
	
	public boolean connect(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
		} catch (IOException e){
			System.out.println("Unable to connect to address " + ip + ":" + port + " | Starting a new server.");
			return false;
		}
		System.out.println("Successfully connected to opponent.");
		return true;
		
	}
	
	public void listenForServerRequest()
	{
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
			System.out.println("Opponent found. Starting Tic Tac Toe match." );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initializeServer(String ip, int port)
	{
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public DataOutputStream getDos() {
		return dos;
	}
	
	public DataInputStream getDis() {
		return dis;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
}
