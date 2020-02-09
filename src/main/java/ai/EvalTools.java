/*
 * EvalTools.java
 * Forskellige funktioner der benyttes i evalueringsfunktionerne. 
 */

package ai;

import engine.Board;
import engine.Piece;

public class EvalTools {
	
	/**
	 * Beregner "Taxicabdistance" mellem 2 brikker. Altså afstanden ved at følge felterne og ikke fugle flugt.
	 * @param p1 Brik 1
	 * @param p2 Brik 2
	 * @param b Brættet de står på
	 * @return Afstanden mellem dem
	 */
	public static int getTaxicabDistance(Piece p1, Piece p2, Board b){
		return (Math.abs(p1.getFile()-p2.getFile()) + Math.abs(p1.getRank()-p2.getRank()));
	}
	
	/** 
	 * Giver en værdi for hvor langt vi er i spillet
	 * @param b Brættet
	 * @return Værdi mellem 1 og 10, hvor 10 er slutning
	 */
	public static int getGameProgress(Board b){
		// skal være afhængig af resterende tid, antal brikker og forventet antal moves
		
		double progress = 0.0;
		
		// antal brikker
		progress += (double) ( 32 - b.getPieces().size() ) / 32 * 5; // 5 skal være 3.3 her, da vi vil have en til ting at vurdere længden på
		
		final int MOVES = 100;
		
		progress += (double) b.getMoveHistory().size() / MOVES * 5; // 5 skal være 3.3 her
				
		
		return (int) Math.round(progress);		
	}

}
