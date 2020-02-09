package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import common.Datalink;
import common.Vars;

import engine.Piece;


public class ChessBoardField extends JPanel
{
	public static final int empty = 0;
	private byte content;
	private byte pos;
	private ChessBoard mother;
	
	public ChessBoardField(byte pos, ChessBoard cb)
	{
		this.mother = cb;
		this.pos = pos;
		content = 0;
		setDoubleBuffered(true);
	}

//    public void update ( Graphics g ) {
//        paint(g);
//    }

	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		String filename = "";

		switch(content)
		{
			case empty:
				break;
				
			case Piece.BPAWN:
				filename = "gui/graphics/bPawn.png";
				break;
				
			case Piece.BKNIGHT:
				filename = "gui/graphics/bKnight.png";
				break;
				
			case Piece.BBISHOP:
				filename = "gui/graphics/bBishop.png";
				break;
				
			case Piece.BROOK:
				filename = "gui/graphics/bRook.png";
				break;
				
			case Piece.BQUEEN:
				filename = "gui/graphics/bQueen.png";
				break;
				
			case Piece.BKING:
				filename = "gui/graphics/bKing.png";
				break;
				
			case Piece.WPAWN:
				filename = "gui/graphics/wPawn.png";
				break;
				
			case Piece.WKNIGHT:
				filename = "gui/graphics/wKnight.png";
				break;
				
			case Piece.WBISHOP:
				filename = "gui/graphics/wBishop.png";
				break;
				
			case Piece.WROOK:
				filename = "gui/graphics/wRook.png";
				break;
				
			case Piece.WQUEEN:
				filename = "gui/graphics/wQueen.png";
				break;
				
			case Piece.WKING:
				filename = "gui/graphics/wKing.png";
				break;					
			default:
				break;
		}
		
		// Tegner en brik, hvis der skal!
		if (filename != "")
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Image image = Toolkit.getDefaultToolkit().getImage(classLoader.getResource(filename));
			g.drawImage(image,0,0,getWidth(),getHeight(),this);
		}
		
		// Hvis hvid konge er i skak => orange firkant rundt om
		if(content == Piece.WKING  && Datalink.whiteKingCheck)
		{
			g.setColor(Color.orange);	
			g.drawRect(0,0,getWidth(),getHeight());
			g.drawRect(1,1,getWidth()-2,getHeight()-2);
			g.drawRect(2,2,getWidth()-4,getHeight()-4);
		}
		
		// Hvis sort konge er i skak => orange firkant rundt om
		if(content == Piece.BKING && Datalink.blackKingCheck)
		{
			g.setColor(Color.orange);
			g.drawRect(0,0,getWidth(),getHeight());
			g.drawRect(1,1,getWidth()-2,getHeight()-2);
			g.drawRect(2,2,getWidth()-4,getHeight()-4);
		}
		
		// Hvis denne brik har sat kongen i skak => orange firkant rundt om
		if((Datalink.blackKingCheck || Datalink.whiteKingCheck) && mother.getDatalink().getBoardData().getCheckPiece() != null)
			if (mother.getDatalink().getBoardData().getCheckPiece().getPos() == mother.small2big(pos))
			{
				g.setColor(Color.orange);
				g.drawRect(0,0,getWidth(),getHeight());
				g.drawRect(1,1,getWidth()-2,getHeight()-2);
				g.drawRect(2,2,getWidth()-4,getHeight()-4);
			}	
		
		// Tegner grøn ring, hvis der kan rykkes over i dette felt
		if(mother.getLegalFields().contains(pos))
		{
			g.setColor(Color.green);
			
			if(content != 0)
				g.setColor(Color.blue);
			
			for(int i = 14 ; i < 16; i++)
				g.drawOval(i, i, getWidth()-i*2, getHeight()-i*2);
		}
	
		// Tegner grøn ramme hvis dette felt er markeret
		if (mother.getMarkedField() == pos)
		{
			g.setColor(Color.green);
			
			if (mother.getLegalFields().size() == 0)
				g.setColor(Color.red);
			
			g.drawRect(0,0,getWidth(),getHeight());
			g.drawRect(1,1,getWidth()-2,getHeight()-2);
			g.drawRect(2,2,getWidth()-4,getHeight()-4);
		}
	}
	
	
	/**
	 * GETTERS & SETTERS
	 */
	public byte getPos() {
		return pos;
	}
	
	public byte getContent() {
		return content;
	}

	public void setContent(byte content) {
		this.content = content;
		paintImmediately(0, 0, getWidth(), getHeight());
	}
}
