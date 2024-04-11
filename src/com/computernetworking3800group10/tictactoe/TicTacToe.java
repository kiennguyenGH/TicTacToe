package com.computernetworking3800group10.tictactoe;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

public class TicTacToe implements Runnable{
	
	public String ip = "localhost";
	private int port = 22222;
	private Scanner input = new Scanner(System.in);
	private JFrame frame;
	private final int WIDTH = 506;
	private final int HEIGHT = 527;
	private Thread thread;
	
	private Painter painter;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private ServerSocket serverSocket;
	
	private BufferedImage board;
	private BufferedImage blueX;
	private BufferedImage redCircle;
	
	private String[][] spaces = new String[3][3];
	
	
 	
	
	public TicTacToe()
	{
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args)
	{
		System.out.println("Hello world!");
	}
	
	public class Painter {
		
	}


}
