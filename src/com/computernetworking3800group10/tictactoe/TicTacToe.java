package com.computernetworking3800group10.tictactoe;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TicTacToe implements Runnable{
	
	public String ip = "localhost";
	private int port = 22222;
	private Scanner input = new Scanner(System.in);
	private JFrame frame;
	private final int WIDTH = 1000;
	private final int HEIGHT = 1000;
	private Thread thread;
	
	private Painter painter;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	private ServerSocket serverSocket;
	
	private BufferedImage board;
	private BufferedImage blueX;
	private BufferedImage redCircle;
	
	private String[] spaces = new String[9];
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
		
		painter = new Painter();
		painter.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		
		if (!connect())
		{
			initializeServer();
		}
		
		frame = new JFrame();
		frame.setTitle("Tic Tac Toe");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
		thread = new Thread(this, "TicTacToe");
		thread.start();
		
	}
	
	@Override
	public void run() {
		while (true)
		{
			tick();
			painter.repaint();
			
			if 	(!circle && !accepted) {
				listenForServerRequest();
			}
		}
		
	}
	
	private void render(Graphics g) {
		g.drawImage(board, 0, 0, null);
		if (unableToCommunicateWithOpponent)
		{
			g.setColor(Color.RED);
			g.setFont(smallerFont);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(unableToCommunicateWithOpponentString);
			g.drawString(unableToCommunicateWithOpponentString, WIDTH/2 - stringWidth/2, HEIGHT/2);
			return;
		}
		
		if (accepted) {
			for (int i =0; i < spaces.length; i++)
			{
				if (spaces[i] == "X")
				{
					g.drawImage(blueX, (i % 3) * lengthOfSpace + 4 * (i%3), (int) (i/3) * lengthOfSpace + 4 * (int) (i/3), null);
				}
				else if (spaces[i] == "O")
				{
					g.drawImage(redCircle, (i % 3) * lengthOfSpace + 4 * (i%3), (int) (i/3) * lengthOfSpace + 4 * (int) (i/3), null);
				}
			}
		}
		
		if (won || enemyWon)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(4));
			g.setColor(Color.BLACK);
			g.drawLine(firstSpot % 3 * lengthOfSpace + 4 * firstSpot % 3 + lengthOfSpace / 2, (int)(firstSpot/3) * lengthOfSpace + 4 * (int) (firstSpot/3) + lengthOfSpace/2,
					secondSpot % 3 * lengthOfSpace + 4 * secondSpot % 3 + lengthOfSpace / 2, (int) (secondSpot/3) * lengthOfSpace + 4 * (int) (secondSpot/3) + lengthOfSpace/2);
			g.setColor(Color.RED);
			g.setFont(largerFont);
			if (won)
			{
				int stringWidth = g2.getFontMetrics().stringWidth(wonString);
				g.drawString(wonString, WIDTH/2 - stringWidth/2, HEIGHT/2);
				
			}
			else if (enemyWon)
			{
				int stringWidth = g2.getFontMetrics().stringWidth(enemyWonString);
				g.drawString(enemyWonString, WIDTH/2 - stringWidth/2, HEIGHT/2);
			}
		}
		else {
			g.setColor(Color.RED);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
			g.drawString(waitingString, WIDTH/2 - stringWidth/2, HEIGHT/2);
		}
	}


	private void tick() {
		if (errors >= 10)
		{
			unableToCommunicateWithOpponent = true;
		}
		if (!turn && !unableToCommunicateWithOpponent)
		{
			try {
				int space = dis.readInt();
				if (circle)
				{
					spaces[space] = "X";
				}
				else
				{
					spaces[space] = "O";
				}
				checkForEnemyWin();
				turn = true;
			} catch (IOException e)
			{
				e.printStackTrace();
				errors++;
			}
		}
	}
	
	
	private void checkForWin() {
		
	}
	
	private void checkForEnemyWin() {
		
	}
	
	private void checkForTie() {
		
	}
	
	private void listenForServerRequest()
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
	
	private boolean connect() {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
		} catch (IOException e){
			System.out.println("Unable to connect to address " + ip + ":" + port + "| Starting a new server.");
			return false;
		}
		System.out.println("Successfully connected to opponent.");
		return true;
		
		
	}
	
	private void initializeServer()
	{
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch(Exception e){
			e.printStackTrace();
		}
		turn = true;
		circle = false;
	}
	
	
	
	private void loadImages() {
		try {
			board = ImageIO.read(getClass().getResourceAsStream("/Board.png"));
			blueX = ImageIO.read(getClass().getResourceAsStream("/BlueX.png"));
			redCircle = ImageIO.read(getClass().getResourceAsStream("/RedCircle.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args)
	{
		System.out.println("Hello world!");
	}
	
	private class Painter extends JPanel implements MouseListener{
		private static final long serialVersionUID = 1L;

		public Painter() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
			addMouseListener(this);
		}
		
		public void painterComponent(Graphics g) {
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		
	}


}
