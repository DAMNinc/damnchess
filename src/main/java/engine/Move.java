package engine;

import java.io.Serializable;
import java.util.Hashtable;

import common.Datalink;

public class Move implements Serializable
{
	Piece movedPiece;
	Piece takenPiece;
	Piece checkPiece;
	byte from;
	byte to;
	boolean checkedKing;
	boolean checkMatedKing;
	boolean castledLeft;
	boolean castledRight;
	boolean promotion;
	boolean kingWasChecked;
	boolean gameDrawed;
	byte whitePawnPromoteType;
	byte blackPawnPromoteType;
	String moveHistory;
	short fiftyMoveCounter;
	Hashtable<Piece, byte[]> pieceMoves;
	
	public Move(Piece piece, byte to)
	{
		this.to = to;
		this.from = piece.getPos();
		movedPiece = piece;
		
		checkedKing = false;
		checkMatedKing = false;
		castledLeft = false;
		castledRight = false;
		promotion = false;
		kingWasChecked = false;
		gameDrawed = false;
		
		takenPiece = null;
		checkPiece = null;
		
		whitePawnPromoteType = Datalink.whitePawnPromoteType;
		blackPawnPromoteType = Datalink.blackPawnPromoteType;
		
		fiftyMoveCounter = 1;
		
		pieceMoves = new Hashtable<Piece, byte[]>();
	}

	public Piece getMovedPiece()
	{
		return movedPiece;
	}
	
	public void setMoveHistory(String history)
	{
		moveHistory = history; 
	}

	public String getMoveHistory() 
	{
		return moveHistory;
	}

	public boolean isCastledRight() {
		return castledRight;
	}

	public void setCastledRight(boolean castledRight) {
		this.castledRight = castledRight;
	}

	public byte getFrom() {
		return from;
	}

	public Piece getTakenPiece() {
		return takenPiece;
	}

	public byte getTo() {
		return to;
	}

	public byte getBlackPawnPromoteType() {
		return blackPawnPromoteType;
	}

	public byte getWhitePawnPromoteType() {
		return whitePawnPromoteType;
	}

	public boolean isCastledLeft() {
		return castledLeft;
	}

	public void setCastledLeft(boolean castledLeft) {
		this.castledLeft = castledLeft;
	}
	
}
