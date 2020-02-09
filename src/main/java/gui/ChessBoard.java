package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import common.Datalink;
import common.Vars;
import engine.*;


public class ChessBoard extends JPanel implements MouseListener //, MouseMotionListener
{
	
	 
	private ChessFrame mother;
	private ChessBoardField fields[]; // Indeholder 64 Skakfelter
	private byte markedField; // index p� markeret skakfelt. -1, hvis intet er markeret
	private Datalink datalink; // Link til modellen
	private ArrayList<Byte> legalFields; // Felter der kan rykkes til
	
	public static final byte NONE = -1;
	
	public ChessBoard(Datalink datalink, ChessFrame mother)
	{
		loadChessPieces();
		
		this.mother = mother;
		legalFields = new ArrayList<Byte>();
		this.datalink = datalink;
		fields = new ChessBoardField[64];
		markedField = NONE;
	
		setPreferredSize(new Dimension(474,474));
		setLayout(new GridLayout(8,8));
		setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
		for (byte i = 0 ; i < 64 ; i++)
		{
			ChessBoardField tempPanel = new ChessBoardField(i, this);
			tempPanel.setContent(datalink.getBoardData().getBoard()[small2big(i)]);
			
			// Baggrundsfarve
			if (i % 2 - ((i / 8)+1) % 2 == 0) 
				tempPanel.setBackground(Color.white);
			else 
				tempPanel.setBackground(Color.getHSBColor((float)0.2, (float)0.2, (float)0.4));
			
			fields[i] = tempPanel; 
			
			tempPanel.addMouseListener(this);
			//tempPanel.addMouseMotionListener(this);
		}
		
		for(int i = 7; i >= 0; i--)
			for(int j = 0; j < 8; j++)
				add(fields[(i*8)+j]);
	}
	
	
	private void loadChessPieces()
	{
		Vars.picBPawn = Toolkit.getDefaultToolkit().getImage("gui/graphics/bPawn.png");
	}
	
	/**
	 * Synkroniserer GUI'en br�t med br�ttet i skakmotoren
	 *
	 */
	public void reloadBoard()
	{
		clearLegalFields();
		for(byte i = 0 ; i < fields.length ; i++)
			fields[i].setContent(datalink.getBoardData().getBoard()[small2big(i)]);
	}
	
	public byte small2big(byte index)
	{
		return (byte) ((21 + index) + (index/8)*3-(index/8));
	}
	
	public byte big2small(byte index)
	{
		return (byte) ((index-21)-((index-21)/10)*2);  
	}
	
	/**
	 * Viser lovlige tr�k for en brik i et bestemt skakfelt. Selve beregningen af legalmoves foretages i motoren.
	 * @param pos Index p� det felt der �nskes lovlige tr�k for
	 */
	public void showLegalMoves(byte pos)
	{
		if((markedField == pos || markedField == NONE) && fields[pos].getContent() != 0)
		{	
			repaintFields(legalFields);
			legalFields.clear();
			
			byte legalmoves[] = datalink.getBoardData().getLegalMoves(datalink.getBoardData().getPieceAtPos(small2big(pos)));
			
			for(byte move : legalmoves)		
				legalFields.add(big2small(move));
			
			repaintFields(legalmoves);
		}
	}
	
	/**
	 * Ofte er det kun nogle f� felter der skal repaintes
	 * @param fieldsToRepaint Liste over de felter der skal repaintes
	 */
	private void repaintFields(ArrayList<Byte> fieldsToRepaint)
	{
		for (Byte CBF : fieldsToRepaint)
			fields[CBF].repaint();
	}
	
	/**
	 * Ofte er det kun nogle f� felter der skal repaintes
	 * @param fieldsToRepaint Liste over de felter der skal repaintes
	 */
	private void repaintFields(byte[] fieldsToRepaint)
	{
		for (byte pos : fieldsToRepaint)
			fields[big2small(pos)].repaint();
	}
	
	/**
	 * Repainter hele br�ttet, ved at repainte hvert felt
	 *
	 */
	public void repaintBoard()
	{
		paintImmediately(0, 0, getWidth(), getHeight());
		for(ChessBoardField CBF : fields)
			CBF.paintImmediately(0, 0, CBF.getWidth(), CBF.getHeight());
		paintImmediately(0, 0, getWidth(), getHeight());
	}

	/**
	 * Felter som kan rykkes til slettes og repaintes,
	 * s� de ikke l�ngere indeholder en gr�n ring.
	 */
	public void clearLegalFields()
	{
		ArrayList<Byte> copy = new ArrayList<Byte>();
		
		copy.addAll(legalFields);
		
		legalFields.clear();
		
		for(byte field : copy)
			fields[field].paintImmediately(0, 0, fields[field].getWidth(), fields[field].getHeight());
	}
	
	
	/**********************
	 * GETTERS & SETTERS
	 **********************/

	public ChessBoardField[] getFields() 
	{
		return fields;
	}
	public void setFields(ChessBoardField[] fields) 
	{
		this.fields = fields;
	}
	public byte getMarkedField() 
	{
		return markedField;
	}
	public void setMarkedField(byte markedField) 
	{
		this.markedField = markedField;
	}
	public ArrayList<Byte> getLegalFields() 
	{
		return legalFields;
	}
	public Datalink getDatalink() 
	{
		return datalink;
	}
	
	/**********************
	 * LISTENERS
	 **********************/
	
	/** 
	 * MouseListener - Lytter efter museklik
	 */
	
	public void mouseClicked(MouseEvent arg0) 
	{		
		if (arg0.getSource().getClass().getSimpleName().equals("ChessBoardField"))
		{	
			ChessBoardField CBF = (ChessBoardField) arg0.getSource();
			
			// Denne lille man�vre er n�dvendig, da Datalink.currentPlayer skifter frem og tilbage n�r AI t�nker
			int currentPlayer = Vars.WHITE;
			if (mother.getLblCurrentPlayer().getText().toString().equals("Sort tr�kker"))
				currentPlayer = Vars.BLACK;
			
			// Klikket er lovligt hvis:
			// 1. Der er tale om sort brik eller et lovligt felt (gr�n ring). Derudover skal det v�re sorts tur
			// 2. Der er tale om hvid brik eller et lovligt felt (gr�n ring). Derudover skal det v�re hvids tur
			// 3. Sammen med 1. og 2. skal det ved "Human vs. AI" spil v�re Humans tur eller ogs� skal det v�re to player spil
			// 4. Spillet m� ikke v�re slut!
			if ((((CBF.getContent() <= 0 || legalFields.contains(CBF.getPos())) && Datalink.currentPlayer == Vars.BLACK) || 
				 ((CBF.getContent() >= 0 || legalFields.contains(CBF.getPos())) && Datalink.currentPlayer == Vars.WHITE)) &&
				 ((currentPlayer != Datalink.aiColor && !Datalink.twoPlayerGame) || (Datalink.twoPlayerGame)) &&
				  Datalink.gameEnded == false)
			{
				
				// Hvis feltet der er trykket p� ikke er det felt der er markeret og
				// det felt man klikker p� indeholder en brik (Alts� flyt markering!)
				if (markedField != CBF.getPos() && CBF.getContent() != 0 && !legalFields.contains(CBF.getPos()))
				{
					byte index = markedField;
					
					markedField = CBF.getPos();
					CBF.repaint();
					if(index != -1)
						fields[index].repaint();
					showLegalMoves(CBF.getPos());
					
				}
				// Hvis det felt man klikker p� allerede er markeret (Fjern markering!)
				else if (markedField == CBF.getPos())
				{
					markedField = NONE;
					CBF.repaint();
					clearLegalFields();
				}	
				// Hvis det felt man klikker p� et felt der kan rykkes til (Alts� flyt brik!)
				else if (legalFields.contains(CBF.getPos()))
				{
					Piece pieceToMove = datalink.getBoardData().getPieceAtPos(small2big((byte)markedField));
					
					// Tjek om "Pawn promotion"
					if ((pieceToMove.getType() == Piece.BPAWN && pieceToMove.getRank() == 2 && Datalink.currentPlayer == Vars.BLACK) ||
						(pieceToMove.getType() == Piece.WPAWN && pieceToMove.getRank() == 7 && Datalink.currentPlayer == Vars.WHITE))
					{
						Promotion pro = new Promotion(mother);
						pro.pack();
						pro.setVisible(true);
					}	
					
					//Slet brik fra gammel felt i gui
					fields[markedField].setContent((byte)0);
					markedField = NONE;
					
					// Flyt brik i model
					byte toPosition = small2big(CBF.getPos());
					datalink.move(pieceToMove, toPosition);
					
					//Flyt brik til nyt felt i gui
					movePieceInGUI(toPosition);
			
//					if (datalink.getBoardData().canUseFiftyMoveRule() && datalink.twoPlayerGame)
//					{
//						String color;
//						if (datalink.currentPlayer == Vars.WHITE)
//							color = "hvid";
//						else 
//							color = "sort";
//						
//						Object[] options = {"Ja", "Nej"};
//						int n = JOptionPane.showOptionDialog(mother,
//						    "Vil " + color + " benytte sig af 50-ryks-regelen? \n Det betyder at spille ender i remi",
//						    "50-ryks-regel",
//						    JOptionPane.YES_NO_OPTION,
//						    JOptionPane.QUESTION_MESSAGE,
//						    null,
//						    options,
//						    options[1]);
//						
//						if (n == 0)
//							datalink.getBoardData().useFiftyMoveRule();
//					}
					
					
					datalink.play();  // n�ste spiller
				}
			}
			else if (Datalink.gameEnded)
			{
				Object[] options = {"Ja", "Nej"};
				int n = JOptionPane.showOptionDialog(mother,
				    "Vil du starte et nyt spil?",
				    Vars.APPTITLE,
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[0]);
				
				if (n == 0)
					datalink.newAIgame();
					
				
			}
		}
	}
	
	public void movePieceInGUI(int toPosition)
	{
		fields[big2small((byte)toPosition)].setContent(datalink.getBoardData().getBoard()[toPosition]);	
	}
	
	
	// Disse bruges ikke, men skal v�re der!
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
	/**
	 * MouseMotionListener - Lytter efter om musen flytter sig
	 */
	
//	public void mouseDragged(MouseEvent arg0) {}
//
//	public void mouseMoved(MouseEvent arg0) {
//		ChessBoardField CBF = (ChessBoardField) arg0.getSource();
//		//System.out.println(CBF.getContent());
//		//System.out.println(chessboard.getDatalink().getBoardData().getBoard()[chessboard.small2big(CBF.getPos())]);
//		
//		if(markedField == NONE && Vars.ctrlPressed && 
//			(((CBF.getContent() < 0 && Datalink.currentPlayer == Vars.BLACK) || 
//			 (CBF.getContent() > 0 && Datalink.currentPlayer == Vars.WHITE)) || Vars.debugMode))
//			showLegalMoves(CBF.getPos());
//		
////		if(markedField == NONE && Vars.ctrlPressed)
////			showMoves(CBF.getPos());
//		
//		if (CBF.getContent() == 0 && markedField == NONE)
//			clearLegalFields();
//	}
}