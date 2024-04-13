package com.computernetworking3800group10.tictactoe;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TicTacToe implements Runnable{
	
	private String ip = "localhost";
	private int port = 22222;
	private Scanner input = new Scanner(System.in);
	private JFrame frame;
	private final int WIDTH = 515;
	private final int HEIGHT = 535;
	private Thread thread;
	
	private SocketManager socketManager = new SocketManager();
	
	private Painter painter;
	
	private BufferedImage board;
	private BufferedImage blueX;
	private BufferedImage redCircle;
	
	private String[] spaces = new String[9];
	private boolean turn = false;
	private boolean circle = true;
	private boolean unableToCommunicateWithOpponent = false;
	private boolean won = false;
	private boolean enemyWon = false;
	private boolean tie = false;
	
	private int lengthOfSpace = 164;
	private int errors = 0;
	
	private int firstSpot = -1;
	private int secondSpot = -1;
	
	private Font font = new Font("Times New Roman", Font.PLAIN, 32);
	
	private String waitingString = "Waiting for another player";
	private String unableToCommunicateWithOpponentString = "Unable to communicate with opponent.";
	private String wonString = "You win!";
	private String enemyWonString = "You lose!";
	private String tieString = "Tie!";
	
	private int[][] wins = new int[][] {
		{0,1,2},{3,4,5},{6,7,8},
		{0,3,6},{1,4,7},{2,5,8},
		{0,4,8},{2,4,6}
	};
	
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
		
		
		
		if (!socketManager.connect(ip, port))
		{
			socketManager.initializeServer(ip, port);
			turn = true;
			circle = false;
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
			
			if 	(!circle && !socketManager.isAccepted()) {
				socketManager.listenForServerRequest();
			}
		}
		
	}
	
	private void render(Graphics g) {
		g.drawImage(board, 0, 0, null);
		if (unableToCommunicateWithOpponent)
		{
			g.setColor(Color.RED);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(unableToCommunicateWithOpponentString);
			g.drawString(unableToCommunicateWithOpponentString, WIDTH/2 - stringWidth/2, HEIGHT/2);
			return;
		}
		if (socketManager.isAccepted()) {
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
			if (won || enemyWon)
			{
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(4));
				g.setColor(Color.BLACK);
				g.drawLine(firstSpot % 3 * lengthOfSpace + 4 * firstSpot % 3 + lengthOfSpace / 2, (int)(firstSpot/3) * lengthOfSpace + 4 * (int) (firstSpot/3) + lengthOfSpace/2,
						secondSpot % 3 * lengthOfSpace + 4 * secondSpot % 3 + lengthOfSpace / 2, (int) (secondSpot/3) * lengthOfSpace + 4 * (int) (secondSpot/3) + lengthOfSpace/2);
				g.setColor(Color.RED);
				g.setFont(font);
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
			if (tie)
			{
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.RED);
				g.setFont(font);
				int stringWidth = g2.getFontMetrics().stringWidth(tieString);
				g.drawString(tieString, WIDTH/2 - stringWidth/2, HEIGHT/2);
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
				int space = socketManager.getDis().readInt();
				if (circle)
				{
					spaces[space] = "X";
				}
				else
				{
					spaces[space] = "O";
				}
				checkForEnemyWin();
				checkForTie();
				turn = true;
			} catch (IOException e)
			{
				e.printStackTrace();
				errors++;
			}
		}
	}
	
	
	private void checkForWin() {
		for (int i =0; i < wins.length; i++)
		{
			if (circle)
			{
				if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O")
				{
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					won = true;
				}
			}
			else
			{
				if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X")
				{
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					won = true;
				}
			}
		}
	}
	
	private void checkForEnemyWin() {
		for (int i =0; i < wins.length; i++)
		{
			if (!circle)
			{
				if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O")
				{
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					enemyWon = true;
				}
			}
			else
			{
				if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X")
				{
					firstSpot = wins[i][0];
					secondSpot = wins[i][2];
					enemyWon = true;
				}
			}
		}
	}
	
	private void checkForTie() {
		if (!(enemyWon || won))
		{
			for (int i = 0; i < spaces.length; i++)
			{
				if (spaces[i] == null)
				{
					return;
				}
				
			}
			tie = true;
		}
	}
	
	private void loadImages() {
		try {
			board = ImageIO.read(getClass().getResourceAsStream("/res/Board.png"));
			blueX = ImageIO.read(getClass().getResourceAsStream("/res/BlueX.png"));
			redCircle = ImageIO.read(getClass().getResourceAsStream("/res/RedCircle.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class Painter extends JPanel implements MouseListener{
		private static final long serialVersionUID = 1L;

		public Painter() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
			addMouseListener(this);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			render(g);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (socketManager.isAccepted()) 
			{
				if (turn && !unableToCommunicateWithOpponent && !won && !enemyWon)
				{
					int x = e.getX() / lengthOfSpace;
					int y = e.getY() / lengthOfSpace;
					y *= 3;
					int position = x+y;
					if (spaces[position] == null)
					{
						if (!circle)
						{
							spaces[position] = "X";
						}
						else
						{
							spaces[position] = "O";
						}
						turn = false;
						repaint();
						Toolkit.getDefaultToolkit().sync();
						try {
							socketManager.getDos().writeInt(position);
							socketManager.getDos().flush();
						} catch (IOException error) {
							errors++;
							error.printStackTrace();
						}
								
						System.out.println("SENT DATA");
						checkForWin();
						checkForTie();
					}
					
					
				}
			}
			
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
