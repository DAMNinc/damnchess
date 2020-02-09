package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

import common.Vars;

public class ChessBoardBackground extends JPanel
{

	private ChessBoard chessboard;
	
	private final Font bigFont = new Font("Arial",Font.BOLD, 70);
	public ChessBoardBackground(ChessBoard chessboard)
	{
		this.chessboard = chessboard;
		setLayout(null);
		setPreferredSize(new Dimension(540,600));
		setOpaque(false); // Gennemsigtig
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setFont(new Font("Arial", 0, 14));
		
		// Tegn venstre
		for (int i = 0 ; i <= 7 ; i++)
			g.drawString("" + (8-i), 
						 chessboard.getX()-15, 
						 chessboard.getY() + (chessboard.getHeight()/8)*i + chessboard.getHeight()/16 + 4);
		// Tegn højre
		for (int i = 0 ; i <= 7 ; i++)
			g.drawString("" + (8-i), 
						 chessboard.getWidth() + chessboard.getY() + 8, 
						 chessboard.getY() + (chessboard.getHeight()/8)*i + chessboard.getHeight()/16 + 4);
		
		
		String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
		
	    // Tegn top
		for (int i = 0 ; i <= 7 ; i++)
			g.drawString(letters[i], 
						 chessboard.getX() + (chessboard.getWidth()/8)*i + chessboard.getWidth()/16 - 2, 
						 chessboard.getY() - 8);
		
	    // Tegn bund
		for (int i = 0 ; i <= 7 ; i++)
			g.drawString(letters[i], 
						 chessboard.getX() + (chessboard.getWidth()/8)*i + chessboard.getWidth()/16 - 2, 
						 chessboard.getY() + chessboard.getHeight() + 16);
		
		//g.drawString("Hold \"Ctrl\" for at få vist mulige ryk!", chessboard.getX(), chessboard.getY() + chessboard.getHeight() + 50);
	
//		g.setColor(Color.white);
//		g.fillRect(25, 203, 473, 118);
//		
//		g.setColor(Color.black);
//		g.drawRect(25, 203, 473, 118);
//		
//		g.setFont(bigFont);
//		g.drawString(Vars.APPTITLE, 40, 290);
	}
}
