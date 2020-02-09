package engine;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Repræsenterer en brik
 */
public class Piece implements Serializable
{
	public static final byte WPAWN = 1;
	public static final byte WKNIGHT = 2;
	public static final byte WBISHOP = 3;
	public static final byte WROOK = 4;
	public static final byte WQUEEN = 5;
	public static final byte WKING = 6;
	
	public static final byte BPAWN = -1;
	public static final byte BKNIGHT = -2;
	public static final byte BBISHOP = -3;
	public static final byte BROOK = -4;
	public static final byte BQUEEN = -5;
	public static final byte BKING = -6; 
	
	private byte type;
	private byte pos;
	private short numMoves;
	
	// Denne variabel bruges til rykhistorik og sammenligning
	private byte[] legalMoves;
	
	private ArrayList<Piece> enemies;
	
	public Piece (byte type, byte pos)
	{
		this.type = type;
		this.pos = pos;
		legalMoves = new byte[0];
		numMoves = 0;
		enemies = new ArrayList<Piece>();
	}
	
	public String toString(){
		String s = "";
		if(type < 0) s = "Black ";
		else s = "White ";
		switch(type){		
		case WPAWN:
		case BPAWN:
			s = s + "pawn";
			break;
		case WROOK:
		case BROOK:
			s = s + "rook";
			break;
		case WKNIGHT:
		case BKNIGHT:
			s = s + "knight";
			break;
		case WBISHOP:
		case BBISHOP:
			s = s + "bishop";
			break;
		case WQUEEN:
		case BQUEEN:
			s = s+ "queen";
			break;
		case WKING:
		case BKING:
			s = s + "king";
		}
		s = s + " (" + type + ") @ " + pos;
		return s;
	}
	
	/**
	 * Returnerer brikkens type, f.eks. -1 for sort bonde
	 * @return Brikkens type som en byte
	 */
	public byte getType()
	{
		return type;
	}
	
	/**
	 * Returnerer brikkens position, f.eks. 21 for A1
	 * @return En byte med brikkens position 
	 */
	public byte getPos()
	{
		return pos;
	}
	
	/**
	 * Sætter ny position for brikken, f.eks. 21 for A1;
	 * @param pos En byte der er brikkens nye position
	 */
	public void setPos(byte pos)
	{
		this.pos = pos;
	}
	
	
	/**
	 * Returnerer brikkens række fra 1-8 
	 * @return en byte med brikken række
	 */
	public byte getRank()
	{
		return (byte) ((pos-pos%10)/10-1);	
	}
	
	/**
	 * Returnerer brikkens kollonne fra 1-8, hvor 1 = A og 8 = H
	 * @return en byte med brikkens kollone
	 */
	public byte getFile()
	{
		return (byte) (pos%10);
	}

	/**
	 * Ændrer brikkens type. Dette sker kun når en bonde opnår promotion
	 * @param type En byte der er brikkens nye type
	 */
	public void setType(byte type)
	{
		this.type = type;
	}
	
	/**
	 * Indlæser et sæt ryk, som denne brik kan foretage
	 * @param moves de nye ryk for brikken
	 */
	protected void setLegalMoves(byte[] moves)
	{
		legalMoves = moves;
	}
	
	/**
	 * Returnerer resultatet af lovlige træk, denne brik kan foretage
	 * De er muligvis forældede. I så fald gælder de som en slags
	 * træk-historik.
	 * @return Et array med lovlige ryk
	 */
	protected byte[] getLegalMoves()
	{
		return legalMoves;
	}

	/**
	 * Returnerer antallet af træk, som denne brik har foretaget
	 * @return
	 */
	public short getNumMoves()
	{
		return numMoves;
	}

	/**
	 * Forøger antallet af træk med 1
	 */
	public void increaseNumMoves()
	{
		numMoves++;
	}
	
	/**
	 * Formindsker antallet af træk med 1
	 */
	public void decreaseNumMoves()
	{
		numMoves--;
	}
	
	/**
	 * Giver en liste over de brikker, som truer denne brik
	 * @return en liste med modstanderbrik
	 */
	public ArrayList<Piece> getEnemies()
	{
		return enemies;
	}
}