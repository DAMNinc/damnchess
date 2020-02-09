/*
 * EvalFunction.java
 * Evalueringsfunktioner for alle brikker
 */
package ai;

import java.util.ArrayList;

import common.Datalink;
import common.Vars;

import engine.*;

public class EvalFunction {

	public static int evalPawn(Piece pawn, Board board)
	{
		int points = Vars.pawnValue;
		byte position = pawn.getPos();

		// Tjekker nummer 1, om en bonde er isoleret. Variable straf 12 - 20
		int surroundings[] = new int[] { -11, -10, -9, -1, 1, 9, 10, 11 };
		int penalty[] = new int[] { 12, 14, 16, 20, 20, 16, 14, 12 };

		boolean isolated = true;

		for (int i = 0; i < surroundings.length; i++) {
			if (board.getBoard()[position - surroundings[i]] == pawn.getType()) {
				isolated = false;
				break;
			}
		}

		if (isolated)
			points -= penalty[pawn.getFile() - 1]; // skal lige rettes

		// Tjekker nummer 2, om bønder er på samme file. Ikke isolerede får
		// 12 point i straf.
		surroundings = new int[] { -10, 10 };

		for (int i = 0; i < surroundings.length; i++) {
			if (board.getBoard()[position - surroundings[i]] == pawn.getType()) {
				points -= 12;
				break;
			}
		}

		// Tjekker nummer 3, om en pawn er en backward pawn. Straf 6p
		if (pawn.getType() == Piece.WPAWN)
			surroundings = new int[] { -9, -11, -1, 1 };

		else if (pawn.getType() == Piece.BPAWN)
			surroundings = new int[] { 9, 11, -1, 1 };

		boolean backwardpawn = true;

		for (int i = 0; i < surroundings.length; i++) {
			if (board.getBoard()[position - surroundings[i]] == pawn.getType()) {
				backwardpawn = false;
				break;
			}
		}

		if (backwardpawn) {
			points -= 6;

			// Tjekker nummer 4, om en backwardpawn har en halvåben file til den
			// ene eller anden side. Straf 4p.
			boolean leftSideIsOpen = true;
			boolean rightSideIsOpen = true;
			// 20+10+file
			// Nederste og øverste felt behøver vi ikke kigge på. Nederst kommer
			// ens bonde aldrig. Øverst bliver ens bonde promoted
			if (pawn.getType() == Piece.WPAWN) {
				for (int i = 1; i < 7; i++) {
					if (board.getBoard()[20 + pawn.getFile() - 1 + i * 10] == 100) {
						leftSideIsOpen = false;
						break;
					}

					if (board.getBoard()[20 + pawn.getFile() - 1 + i * 10] == pawn.getType()) {
						leftSideIsOpen = false;
					}
				}
				for (int i = 1; i < 7; i++) {
					if (board.getBoard()[20 + pawn.getFile() + 1 + i * 10] == 100) {
						rightSideIsOpen = false;
						break;
					}

					if (board.getBoard()[20 + pawn.getFile() + 1 + i * 10] == pawn.getType()) {
						rightSideIsOpen = false;
					}
				}
			}
			else if (pawn.getType() == Piece.BPAWN) {
				for (int i = 1; i < 7; i++) {
					if (board.getBoard()[80 + pawn.getFile() + 1 - i * 10] == 100) {
						leftSideIsOpen = false;
						break;
					}

					if (board.getBoard()[80 + pawn.getFile() + 1 - i * 10] == pawn.getType()) {
						leftSideIsOpen = false;
					}
				}
				for (int i = 1; i < 7; i++) {
					if (board.getBoard()[80 + pawn.getFile() - 1 - i * 10] == 100) {
						rightSideIsOpen = false;
						break;
					}

					if (board.getBoard()[80 + pawn.getFile() - 1 - i * 10] == pawn.getType()) {
						rightSideIsOpen = false;
					}
				}
			}
			if (leftSideIsOpen || rightSideIsOpen)
				points -= 4;
		}

		// Tjekker nummer 5, bønder der rykker frem får bonus
		int[] filePoint = { 1, 2, 3, 4, 4, 3, 2, 1 };

		// (pawn.getRank()-2), sørger for at man ikke får point når man står i
		// startpositionen
		// filePoint[pawn.getFile()] - antal point varierer ud fra den file man
		// står på
		// getGameProgress(board) / 10 - hvor langt er vi i spillet.
		// Ganges med hvor man står på boardet

		if (pawn.getType() == Piece.WPAWN)
			points += (double) ((pawn.getRank() - 2) * filePoint[pawn.getFile() - 1] + (double) ((double) getGameProgress(board)) / 10 * (double) ((pawn.getRank() - 2) * filePoint[pawn.getFile() - 1]));

		else if (pawn.getType() == Piece.BPAWN)
			points += (double) ((7 - pawn.getRank()) * filePoint[pawn.getFile() - 1] + (double) ((double) getGameProgress(board)) / 10 * (double) ((7 - pawn.getRank()) * filePoint[pawn.getFile() - 1]));

		// Tjekker nummer 6, bønder på e/d får 10p straf, hvis de er på rank 2.
		// Yderligere 15p hvis blocked
		if (pawn.getType() == Piece.WPAWN) {
			if (pawn.getRank() == 2 && (pawn.getFile() == 4 || pawn.getFile() == 5)) {
				points -= 10;
				if ((board.getBoard()[Board.D3] != 0 && pawn.getFile() == 4) || (board.getBoard()[Board.E3] != 0 && pawn.getFile() == 5))
					points -= 15;
			}
		}

		else if (pawn.getType() == Piece.BPAWN) {
			if (pawn.getRank() == 7 && (pawn.getFile() == 4 || pawn.getFile() == 5)) {
				points -= 10;
				if ((board.getBoard()[Board.D6] != 0 && pawn.getFile() == 4) || (board.getBoard()[Board.E6] != 0 && pawn.getFile() == 5))
					points -= 15;
			}
		}

		// Tjekker nummer 7, 10p bonus hvis man befinder sig inden for 2 ryk fra
		// kongen
		// pawn.getType()*6[0] returnerer spillerens egen konge.
		try {
			if (getTaxicabDistance(pawn, board.getPiecesOfType((byte) (pawn.getType() * 6)).get(0), board) <= 2)
				points += 10;
		} catch (Exception e) {
		}

		// Tjekker nummer 8, "Passed" bønder får bonus point for hver rank de
		// rykker frem
		// (som funktion af spillet fremskred), og om modstanderen blokker eller
		// kan angribe
		// et eller flere felter frem, eller om modstanderen konge er i nærheden
		// af bonden.
		// Denne bonus går fra 15 point til blokkeret bonde på rank 2, til 300
		// point til en unblocked
		// pawn der ikke kan stoppes fra queening.
		boolean isPassed = true;

		// tjekker om vejen foran bonden er ikke ledige
		if (pawn.getType() == Piece.WPAWN && isPassed) {
			for (int i = pawn.getPos() + 10; i <= 98; i += 10) {
				if (board.getBoard()[i] != 0) {
					isPassed = false;
					break;
				}
			}
		}
		else if (pawn.getType() == Piece.BPAWN && isPassed) {
			for (int i = pawn.getPos() - 10; i >= 21; i -= 10) {
				if (board.getBoard()[i] != 0) {
					isPassed = false;
					break;
				}
			}
		}

		// kigger alle brikker igennem
		if (isPassed) {
			for (Piece p : board.getPieces()) {

				// hvis brikken er mine egen -> continue
				if ((p.getType() > 0 && pawn.getType() > 0) || (p.getType() < 0 && pawn.getType() < 0))
					continue;

				// tjekker om nogen af modstanderens brikker kan tage felter
				// foran bonden
				byte[] moves = board.getLegalMoves(p);
				for (byte move : moves) {
					if (pawn.getType() == Piece.WPAWN && move % 10 == pawn.getFile() % 10 && move > pawn.getPos()) {
						isPassed = false;
						break;
					}
					else if (pawn.getType() == Piece.BPAWN && move % 10 == pawn.getFile() % 10 && move < pawn.getPos()) {
						isPassed = false;
						break;
					}
				}
				if (!isPassed)
					break;
			}
		}

		// hvis bonden er passed gives der point
		if (pawn.getType() == Piece.WPAWN && isPassed) {
			points += ((double) ((double) getGameProgress(board) / (double) 20) + (double) ((pawn.getRank() - 2)) / 12) * (double) 300;
		}

		if (pawn.getType() == Piece.BPAWN && isPassed) {
			points += ((double) ((double) getGameProgress(board) / (double) 20) + (double) ((7 - pawn.getRank())) / 12) * (double) 300;
		}

		return points;
	}

	public static int evalKnight(Piece knight, Board board)
	{
		int points = Vars.knightValue;

		// Nr. 1: Bonus for afstand til midten. 0 i hjørner, 30 i midten.
		int[][] distanceFromCenterBonus = { { 0, 5, 10, 15, 15, 10, 5, 0 }, { 5, 10, 15, 20, 20, 15, 10, 5 }, { 10, 15, 20, 25, 25, 20, 15, 10 }, { 15, 20, 25, 30, 30, 25, 20, 15 }, { 15, 20, 25, 30, 30, 25, 20, 15 }, { 10, 15, 20, 25, 25, 20, 15, 10 }, { 5, 10, 15, 20, 20, 15, 10, 5 }, { 0, 5, 10, 15, 15, 10, 5, 0 } };

		// finder brikkens position udfra file og rank
		int[] position = { knight.getFile(), knight.getRank() };
		points += distanceFromCenterBonus[position[0] - 1][position[1] - 1];

		// Nr. 2: Bonus for at være inden for 2 ryk fra fjenden. 4p i slutningen
		// positioner der skal tjekkes, relativt til brikken
		int[] positionsToCheck = { -20, -11, -10, -9, -2, -1, 1, 2, 9, 10, 11, 20 };
		for (int pos : positionsToCheck) {
			int field = board.getBoard()[knight.getPos() + pos];
			if (field < 0 && knight.getType() > 0 || field > 0 && knight.getType() < 0)
				points += (double) (getGameProgress(board) / 10) * 4;
		}

		// Nr 3: Straffes med 1p pr. felt for afstanden til hver konge.
		try {
			points -= getTaxicabDistance(knight, board.getPiecesOfType((byte) (Piece.WKNIGHT)).get(0), board);
			points -= getTaxicabDistance(knight, board.getPiecesOfType((byte) (Piece.BKNIGHT)).get(0), board);
		} catch (Exception e) {
		}

		// Nr 4: Undersøger om knight er truet af fjendens pawns. Hvis ikke
		// gives bonus på 8p
		// den er truet, hvis der er en pawn op til 3 felter foran den.
		boolean inDanger = false;
		int[] positions = null;
		int enemyPawn = 0;
		if (knight.getType() == Piece.WKNIGHT) {
			positions = new int[] { 9, 11, 19, 21, 29, 31 };
			enemyPawn = Piece.BPAWN;
		}
		else {// Piece.BKNIGHT
			positions = new int[] { -9, -11, -19, -21, -29, -31 };
			enemyPawn = Piece.WPAWN;
		}

		for (int pos : positions) {
			if (knight.getPos() + pos > 0 && knight.getPos() + pos < 120 && board.getBoard()[knight.getPos() + pos] == enemyPawn) {
				inDanger = true;
				break;
			}
		}

		if (!inDanger) {
			points += (double) (getGameProgress(board) / 10) * 8;
		}

		return points;
	}

	public static int evalBishop(Piece bishop, Board board)
	{
		int points = Vars.bishopValue;
		
		int left = board.getPieces().size();
		points += 32 - left;

		// Nr. x: Bonus for afstand til midten. 0 i hjørner, 30 i midten.
		int[][] distanceFromCenterBonus = { { 14, 14, 14, 14, 14, 14, 14, 14 }, { 14, 17, 17, 17, 17, 17, 17, 14 }, { 14, 17, 20, 20, 20, 20, 17, 14 }, { 14, 17, 20, 22, 22, 20, 17, 14 }, { 14, 17, 20, 22, 22, 20, 17, 14 }, { 14, 17, 20, 20, 20, 20, 17, 14 }, { 14, 17, 17, 17, 17, 17, 17, 14 }, { 14, 14, 14, 14, 14, 14, 14, 14 } };

		// finder brikkens position udfra file og rank
		int[] position = { bishop.getFile(), bishop.getRank() };
		points += distanceFromCenterBonus[position[0] - 1][position[1] - 1];

		// giver 5 point for hvert felt den kan angribe, der ligger op at
		// modstanderens konge
		byte kingMoves[] = { -11, -10, -9, -1, 1, 9, 10, 11 };

		byte kingPos;

		if (bishop.getType() == Piece.WBISHOP)
			kingPos = board.getPiecesOfType(Piece.BKING).get(0).getPos();
		else
			kingPos = board.getPiecesOfType(Piece.WKING).get(0).getPos();

		byte[] bishopMoves = board.getLegalMoves(bishop);

		for (int i = 0; i < kingMoves.length; i++) {
			for (byte b : bishopMoves) {
				if (kingPos - kingMoves[i] == b)
					points += 5;
			}
		}

		// giver point alt efter hvor mange felter løberen kan tage
		int moves = board.getLegalMoves(bishop).length;

		if (moves == 0)
			points -= 4;
		else if (moves == 1)
			points -= 2;
		else if (moves <= 12) {
			points += ((double) (moves - 2) / (double) 10 * 18);
		}
		else {
			points += 18;
		}

		return points;
	}

	public static int evalRook(Piece rook, Board board)
	{
		int points = Vars.rookValue;

		// giver point alt efter hvor mange felter tårnet kan tage
		int moves = board.getLegalMoves(rook).length;

		if (moves <= 12) {
			points += ((double) moves / (double) 12 * 20);
		}
		else {
			points += 20;
		}

		// tjekker om der er bønder på tårnets file
		int file = rook.getFile();

		boolean foundEnemyPawn = false;
		boolean foundOwnPawn = false;

		if (rook.getType() == Piece.BROOK) {
			for (int i = 20 + file; i <= 98; i += 10) {
				Piece temp = board.getPieceAtPos((byte) i);
				if (temp != null && temp.getType() == Piece.WPAWN)
					foundEnemyPawn = true;
				if (temp != null && temp.getType() == Piece.BPAWN)
					foundOwnPawn = true;

			}
		}
		else if (rook.getType() == Piece.WROOK) {
			for (int i = 20 + file; i <= 98; i += 10) {
				Piece temp = board.getPieceAtPos((byte) i);
				if (temp != null && temp.getType() == Piece.BPAWN)
					foundEnemyPawn = true;
				if (temp != null && temp.getType() == Piece.WPAWN)
					foundOwnPawn = true;
			}
		}

		if (!foundOwnPawn)
			points += 10;
		if (!foundEnemyPawn)
			points += 4;

		// Straffes udfra taxicabafstand til fjendens konge. 10p pr. felt
		byte enemyKingType = (rook.getType() == Piece.WROOK ? Piece.BKING : Piece.WKING);
		Piece enemyKing = board.getPiecesOfType(enemyKingType).get(0);

		points -= 5 * getTaxicabDistance(rook, enemyKing, board);

		return points;
	}

	public static int evalQueen(Piece queen, Board board)
	{
		int points = Vars.queenValue;

		// Straffes udfra taxicabafstand til fjendens konge. 10p pr. felt
		try {
			byte enemyKingType = (queen.getType() == Piece.WQUEEN ? Piece.BKING : Piece.WKING);
			Piece enemyKing = board.getPiecesOfType(enemyKingType).get(0);

			points -= 10 * getTaxicabDistance(queen, enemyKing, board);
		} catch (Exception e) {
		}

		return points;
	}

	public static int evalKing(Piece king, Board board)
	{
		int points = Vars.kingValue;

		byte myKing = king.getType();

		byte enemyKnight = (myKing == Piece.WKING ? Piece.BKNIGHT : Piece.WKNIGHT);
		byte enemyQueen = (myKing == Piece.WKING ? Piece.BQUEEN : Piece.WQUEEN);
		byte enemyRook = (myKing == Piece.WKING ? Piece.BROOK : Piece.WROOK);
		byte enemyBishop = (myKing == Piece.WKING ? Piece.BBISHOP : Piece.WBISHOP);

		// Nr. 1: Kongen straffes for at være tæt på midten i starte af spillet,
		// og belønnes for samme i slutningen
		// Går fra -24 til +36. Dette gælder ikke hvis fjenden kun har
		// bønder+konge

		// Hvis alle disse arraylists er tomme, er der kun konge og bønder
		// tilbage
		if (board.getPiecesOfType(enemyKnight).isEmpty() && board.getPiecesOfType(enemyQueen).isEmpty() && board.getPiecesOfType(enemyRook).isEmpty() && board.getPiecesOfType(enemyBishop).isEmpty()) {
			int[][] boardValues = { { 0, 1, 2, 3, 3, 2, 1, 0 }, { 1, 2, 3, 4, 4, 3, 2, 1 }, { 2, 3, 4, 5, 5, 4, 3, 2 }, { 3, 4, 5, 6, 6, 5, 4, 3 }, { 3, 4, 5, 6, 6, 5, 4, 3 }, { 2, 3, 4, 5, 5, 4, 3, 2 }, { 1, 2, 3, 4, 4, 3, 2, 1 }, { 0, 1, 2, 3, 3, 2, 1, 0 } };

			int[] position = { king.getFile(), king.getRank() };

			double gameprogress = (double) (getGameProgress(board) / 10);

			// Hvis vi er i starten af spillet
			if (gameprogress <= 0.5) {
				points += boardValues[position[0]][position[1]] * -4;
			}
			else { // slutningen
				points += boardValues[position[0]][position[1]] * 6;
			}
		}

		if (myKing == Piece.WKING) {
			if (!Datalink.whiteCanCastleLeft && !Datalink.whiteCanCastleRight) {
				
				if (Datalink.whiteHasCastled) {
					points += 25 / getGameProgress(board);
					
				}
				else {
					points -= 50 / getGameProgress(board); 
				}
			}
		}
		else {
			if (!Datalink.blackCanCastleLeft && !Datalink.blackCanCastleRight) {
				if (Datalink.blackHasCastled) {
					points += 25 / getGameProgress(board);
				}
				else {
					points -= 50 / getGameProgress(board); 
				}
			}
		}

		return points;
	}

	public static int eval(Board b)
	{
		try {
			int points = 0;
			if (Datalink.blackKingCheck)
				points += -Vars.checkValue;
			else if (Datalink.whiteKingCheck)
				points += Vars.checkValue;

			return evalColor(Vars.BLACK, b) - evalColor(Vars.WHITE, b) + points;
		} catch (Exception e) {
			return 0;
		}
	}

	private static int evalColor(byte color, Board b)
	{
		ArrayList<Piece> myPieces = b.getPiecesOfColor(color);
		int points = 0;

		for (Piece p : myPieces) {
			switch (p.getType()) {
			case Piece.BPAWN:
			case Piece.WPAWN:
				points += evalPawn(p, b);
				break;
			case Piece.BBISHOP:
			case Piece.WBISHOP:
				points += evalBishop(p, b);
				break;
			case Piece.BKNIGHT:
			case Piece.WKNIGHT:
				points += evalKnight(p, b);
				break;
			case Piece.BROOK:
			case Piece.WROOK:
				points += evalRook(p, b);
				break;
			case Piece.BQUEEN:
			case Piece.WQUEEN:
				points += evalQueen(p, b);
				break;
			case Piece.BKING:
			case Piece.WKING:
				points += evalKing(p, b);
				break;

			}
		}

		return points;
	}
	
	public static int debugAI(Board b)
	{
		ArrayList<Piece> myPieces = b.getPiecesOfColor(Vars.BLACK);
		int points = 0;

		for (Piece p : myPieces) {
			switch (p.getType()) {
			case Piece.BPAWN:
			case Piece.WPAWN:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", PawnRating: " + evalPawn(p, b));
				break;
			case Piece.BBISHOP:
			case Piece.WBISHOP:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", BishopRating: " + evalBishop(p, b));
				break;
			case Piece.BKNIGHT:
			case Piece.WKNIGHT:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", KnightRating: " + evalKnight(p, b));
				break;
			case Piece.BROOK:
			case Piece.WROOK:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", RookRating: " + evalRook(p, b));
				break;
			case Piece.BQUEEN:
			case Piece.WQUEEN:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", QueenRating: " + evalQueen(p, b));
				break;
			case Piece.BKING:
			case Piece.WKING:
				System.out.println("Pos: " + p.getFile() + " " + p.getRank() + ", KingRating: " + evalKing(p, b));
				break;

			}
		}

		return points;
	}
	
	
	/*
	 *  Tools til evaluation
	 */
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
