package com.computernetworking3800group10.tictactoe;
import java.awt.Font;
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
	private boolean turn = false;
	private boolean circle = true;
	private boolean accepted = false;
	private boolean unableToCommunicateWithOpponent = false;
	private boolean won = false;
	private boolean enemyWon = false;
	
	private int lengthOfSpace = 328;
	private int errors = 0;
	
	private int firstSpot = -1;
	private int secondSpot = -1;
	
	private Font font = new Font("Times New Roman", Font.PLAIN, 32);
	private Font smallerFont = new Font("Times New Roman", Font.PLAIN, 20);
	private Font largerFont = new Font("Times New Roman", Font.PLAIN, 50);
	
	private String waitingString = "Waiting for another player";
	private String unableToCommunicateWithOpponentString = "Unable to communicate with opponent.";
	private String wonString = "You win!";
	private String enemyWonString = "You lose!";
	
	
	public TicTacToe()
	{
		System.out.println("Enter the IP Address: ");
		ip = input.nextLine();
		System.out.println("Enter the port: ");
		port = input.nextInt();
		while (port < 1 || port > 65535) {
			System.out.println("Invalid port. Input another port: ");
			port = input.nextInt();
		}
		
		loadImages();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	private void loadImages() {
		
	}

	public static void main(String[] args)
	{
		System.out.println("Hello world!");
	}
	
	public class Painter {
		
	}


}
