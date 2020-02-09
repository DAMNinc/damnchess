/*
 * EvalTools.java
 * Forskellige funktioner der benyttes i evalueringsfunktionerne. 
 */

package ai;

import engine.Board;
import engine.Piece;

public class EvalTools {
	
	/**
	 * Beregner "Taxicabdistance" mellem 2 brikker. Alts� afstanden ved at f�lge felterne og ikke fugle flugt.
	 * @param p1 Brik 1
	 * @param p2 Brik 2
	 * @param b Br�ttet de st�r p�
	 * @return Afstanden mellem dem
	 */
	public static int getTaxicabDistance(Piece p1, Piece p2, Board b){
		return (Math.abs(p1.getFile()-p2.getFile()) + Math.abs(p1.getRank()-p2.getRank()));
	}
	
	/** 
	 * Giver en v�rdi for hvor langt vi er i spillet
	 * @param b Br�ttet
	 * @return V�rdi mellem 1 og 10, hvor 10 er slutning
	 */
	public static int getGameProgress(Board b){
		// skal v�re afh�ngig af resterende tid, antal brikker og forventet antal moves
		
		double progress = 0.0;
		
		// antal brikker
		progress += (double) ( 32 - b.getPieces().size() ) / 32 * 5; // 5 skal v�re 3.3 her, da vi vil have en til ting at vurdere l�ngden p�
		
		final int MOVES = 100;
		
		progress += (double) b.getMoveHistory().size() / MOVES * 5; // 5 skal v�re 3.3 her
				
		
		return (int) Math.round(progress);		
	}

}
