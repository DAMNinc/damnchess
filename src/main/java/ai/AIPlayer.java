/*
 * AIPlayer.java
 * Indeholder 
 *  funktionen til alpha-beta pruning
 *  loop-detection
 */

package ai;

import java.util.*;

import common.*;
import engine.*;

public class AIPlayer
{

	Datalink datalink; // giver os mulighed for at snakke med motor og gui
	Board b;
	boolean loop; // s�ttes n�r der er fundet loop
	Piece nextBestPiece;
	Byte nextBestMove;
	Integer nextBestVal;
	static boolean timesup = false;
	Piece forcePiece; // hvis vi skal tvinge alphabeta til kun at benytte dette ryk som roden.
	byte forceMove;
	static HashMap<Byte, HashMap<Integer, Piece>> bestPieces; // indeholder de mulige tr�k der er i roden af tr�et
	static int counter = 0; // holder styr p� antal knuder der er bes�gt
	timer t;

	public AIPlayer(Datalink datalink) {
		this.datalink = datalink;
		b = this.datalink.getBoardData();
		bestPieces = new HashMap<Byte, HashMap<Integer, Piece>>();
		loop = false;
	}

	/**
	 * Finder ud af, om det tr�k i en �bning, der er benyttet er fordelagtigt. Det unders�ges om br�ttets v�rdi (ved kald af evalueringsfunktionen) er markant d�rligere, ved at benytte �bningen frem for alpha-beta
	 * 
	 * @param root
	 *            Benyttes ikke her, men gives videre til alphabeta()
	 * @param player
	 *            Aktuelle spiller
	 * @param depth
	 *            Spiltr�sdybde
	 * @param p
	 *            Brikken fra �bningstr�kket
	 * @param move
	 *            Feltet brikken vil rykke til
	 * @param bound
	 *            Gr�nsev�rdi for, hvor d�rligt br�ttet m� v�re i forhold til alpha-beta
	 * @return Den returnerede v�rdi fra alphabeta()
	 */
	public int AB(boolean root, byte player, int depth, Piece p, byte move, int bound) {
		b.movePiece(p, move);
		int boardVal = EvalFunction.eval(b);
		b.undoLastMove();
		forcePiece = p;
		forceMove = move;
		int res = alphabeta(root, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		if (Math.abs(boardVal - res) < bound) { // hvis resultatet er d�rligere s�ttes best-v�rdierne til dem f�r alphabeta.
			datalink.bestPiece = forcePiece;
			datalink.bestMove = forceMove;
		} else {
			Datalink.usingOpening = false;
		}
		return res;
	}

	/**
	 * Bruges til at kalde den rigtige alphabeta-funktion. Denne funktion s�rger for at vi ikke overskrider den tidsgr�nse der er sat i spillet indstillinger. Der foreg�r en slags iterativ dybdes�gning, hvor der s�ges ved dybde 1 og dybden herefter for�ges med 1 per s�gning. N�r tiden er g�et bruger man det sidst fundne tr�k.
	 * 
	 * @param root
	 *            Sendes videre til alphabeta()
	 * @param player
	 *            Sendes videre til alphabeta()
	 * @param depth
	 *            Maksimal spiltr�sdybde. Hvis denne n�s f�r tiden er g�et, s�ges ikke videre, men dennes v�rdi returneres.
	 * @param alpha
	 *            Beskrives i alphabeta()
	 * @param beta
	 *            Beskrives i alphabeta()
	 * @return Dybden med det sidst fundne tr�k
	 */
	public int AB(boolean root, byte player, int depth, int alpha, int beta) {
		forcePiece = null;
		forceMove = 0;
		bestPieces = new HashMap<Byte, HashMap<Integer, Piece>>();
		t = new timer();
		t.start();
		Piece tempPiece = null;
		byte tempMove = 0;
		int tempCounter = 0;
		int d;
		for (d = 1; d <= depth; d++) {
			alphabeta(root, player, d, alpha, beta, false);
			if (!timesup) {
				tempPiece = datalink.bestPiece;
				tempMove = datalink.bestMove;
				tempCounter = datalink.nodesExpanded;
			}
			if (timesup) {
				if (tempPiece != null) {
					datalink.nodesExpanded = tempCounter;
					datalink.bestPiece = tempPiece;
					datalink.bestMove = tempMove;
				}
				break;
			}
		}
		t.interrupt();
		timesup = false;
		return d - 1;
	}

	/**
	 * Alphabeta pruning foreg�r her.
	 * 
	 * @param root
	 *            Angiver om vi er i roden. Den s�ttes til true ved f�rste kald, og herfra vil den altid v�re false. Det er kun i roden, at de fundne brikker gemmes.
	 * @param player
	 *            Den aktuelle spiller.
	 * @param depth
	 *            Angiver hvor langt fra gr�nsen vi er. N�r depth=0 kaldes evalueringsfunktionen.
	 * @param alpha
	 *            Lille v�rdi. Bruges ved alpha-cutoffs
	 * @param beta
	 *            Stor v�rdi. Bruges ved beta-cutoffs.
	 * @param hint
	 *            Angiver om alphabeta er kaldt for at finde et hint, eller for at finde AI-spillerens ryk.
	 * @return Den bedste v�rdi i tr�et.
	 */
	public int alphabeta(boolean root, byte player, int depth, int alpha, int beta, boolean hint) {
		// Antal noder vi bes�ger
		counter++;

		Piece bestPiece = null;
		byte bestMove = 0;

		nextBestPiece = null;
		nextBestMove = null;
		nextBestVal = null;

		// Evaluer, hvis der er en vinder, br�ttet er fuldt eller dybden er 0
		if (depth == 0)
			return EvalFunction.eval(b);

		ArrayList<byte[]> availMoves = new ArrayList<byte[]>();
		ArrayList<Piece> availPieces = new ArrayList<Piece>();
		ArrayList<Piece> pieces = b.getPiecesOfColor(player);

		// tager kun den aktuelle spillers brikker
		TreeSet<Piece> sortedPieces = new TreeSet<Piece>(new Comparator<Piece>() {
			public int compare(Piece p1, Piece p2) {
				// hvis objektet er ens skal returnes 0 if�lge definitionen p� en comparator
				if (p1.equals(p2)) {
					return 0;
				}
				// hvis de er af samme type, m� ikke returneres 0, da det vil opfattes som ens objekter
				// kun den ene vil derfor blive tilf�jet i mappet
				if((p1.getType() == Piece.BKING && p2.getType() != Piece.BPAWN) || (p1.getType() == Piece.WKING || p2.getType() != Piece.WPAWN))
					return 1;
				if((p2.getType() == Piece.BKING && p1.getType() != Piece.BPAWN) || (p2.getType() == Piece.WKING || p1.getType() != Piece.WPAWN))
					return -1;
				
				if (p1.getType() == p2.getType())
					return 1;
				else if (p1.getType() < 0)
					return ((p1).getType() - (p2).getType());
				else
					return ((p2).getType() - (p1).getType());

			}
		}); 
		
		sortedPieces.addAll(pieces);
		for (Piece p : sortedPieces){
			// Finder alle hans lovlige ryk
			byte[] moves = new byte[b.getLegalMoves(p).length];
			for(int i = 0; i < b.getLegalMoves(p).length; i++){
				moves[i] = b.getLegalMoves(p)[i];
			}
			availMoves.add(moves);
			availPieces.add(p);
		}
		
		if (player == Vars.WHITE) { // min-v�rdier
//			for (Piece p : availableMoves.keySet()) {
			for (int i = 0; i < availPieces.size(); i++) {
				Piece p = availPieces.get(i);

				// cutoff
				if (!root && alpha >= beta)
					break;
//				for (byte move : availableMoves.get(p)) {
				for (int j = 0; j < availMoves.get(i).length; j++) {
					byte move = availMoves.get(i)[j];
					
					// cutoff
					if (!root && alpha >= beta)
						break;

					if (bestPiece == null) { // sikrer at vi altid har en bestPiece => undg�r nullpointer exception
						bestPiece = p;
						bestMove = move;
					}
					// rykker brikken
					b.movePiece(p, move);
					int val;
					// hvis skakmat returneres hhv. maximal og minimal v�rdi
					if (Datalink.blackKingCheckMate)
						val = Integer.MIN_VALUE;
					else if (Datalink.whiteKingCheckMate)
						val = Integer.MAX_VALUE;
					else if (Datalink.gameDraw)
						val = 0;
					else if (timesup) // tiden er g�et, og vi vil derfor komme til at benytte forrige dybdes resultat
						val = 0;
					else
						// ellers s�g videre
						val = alphabeta(false, Vars.BLACK, depth - 1, alpha, beta, hint);
					// fortryd ryk
					b.undoLastMove();

					if (val < beta) {
						if (root) {
							// tilf�jer brikken til en liste over de bedste brikker at rykke
							// (dvs alle fundne brikker, men ikke alle mulige brikker grundet cutoff)
							HashMap<Integer, Piece> temp = new HashMap<Integer, Piece>();
							temp.put(val, p);
							bestPieces.put(move, temp);
							bestPiece = p;
							bestMove = move;
						}
						beta = val;
					}
				}
			}
			if (root) {
				if (bestPieces.size() > 0) { // hvis tom, ingen mulige ryk => skakmat/remis
					// v�lger en tilf�ldig brik og s�tter den til nextBestMove.
					// grunden til at vi ikke tager den n�stbedste skyldes, at det giver st�rre risiko for dobbel og trippelloops
					int rand = (int) (Math.random() * bestPieces.size());
					nextBestMove = (Byte) bestPieces.keySet().toArray()[rand];
					nextBestVal = (Integer) bestPieces.get(nextBestMove).keySet().toArray()[0];
					nextBestPiece = bestPieces.get(nextBestMove).get(nextBestVal);

					if (loopDetected()) {
						datalink.AddToAIOutput("Hvid er gået i loop...\n");
						loop = true;
					}
				}
				if (loop) {
					// hvis der er detekteret et loop benytter vi nextBestPiece
					if (hint) {
						// hvis vi har trykket hint, er det hintPiece ders kal s�ttes
						datalink.hintPiece = nextBestPiece;
						datalink.hintMove = nextBestMove;
					} else {
						// ellers bestPiece
						datalink.bestPiece = nextBestPiece;
						datalink.bestMove = nextBestMove;
					}
					loop = false;
					datalink.AddToAIOutput("Loop undgået!\n");
				} else {
					// hvis ikke benytter vi den bedste.
					if (hint) {
						datalink.hintPiece = bestPiece;
						datalink.hintMove = bestMove;
					} else {
						datalink.bestPiece = bestPiece;
						datalink.bestMove = bestMove;
					}
				}
				if (!timesup)
					datalink.nodesExpanded = counter;
				counter = 0;
			}

			return beta;
			// Black player fungerer p� samme m�de, omvendt.
		} else if (player == Vars.BLACK) { // max v�rdier
//			for (Piece p : availableMoves.keySet()) {
			for (int i = 0; i < availPieces.size(); i++) {
				Piece p = availPieces.get(i);

				// cutoff
				if (!root && alpha >= beta)
					break;
//				for (byte move : availableMoves.get(p)) {
				for (int j = 0; j < availMoves.get(i).length; j++) {
					byte move = availMoves.get(i)[j];
					if (!root && alpha >= beta)
						break;

					if (bestPiece == null) {
						bestPiece = p;
						bestMove = move;
					}
					
					b.movePiece(p, move);
					// evaluer boardet
					int val;
					if (Datalink.blackKingCheckMate)
						val = Integer.MIN_VALUE;
					else if (Datalink.whiteKingCheckMate)
						val = Integer.MAX_VALUE;
					else if (Datalink.gameDraw)
						val = 0;
					else if (timesup)
						val = 0;
					else
						val = alphabeta(false, Vars.WHITE, depth - 1, alpha, beta, hint);

					// fortryd ryk
					b.undoLastMove();

					if (val > alpha) {
						if (root) {
							HashMap<Integer, Piece> temp = new HashMap<Integer, Piece>();
							temp.put(val, p);
							bestPieces.put(move, temp);
							bestPiece = p;
							bestMove = move;
						}
						alpha = val;
					}
				}
			}

			// placerer det bedste sted, hvis vi har s�gt hele tr�et igennem
			if (root) {
				if (bestPieces.size() > 0) { // hvis tom, ingen mulige ryk => skakmat/remis
					int rand = (int) (Math.random() * bestPieces.size());
					nextBestMove = (Byte) bestPieces.keySet().toArray()[rand];
					nextBestVal = (Integer) bestPieces.get(nextBestMove).keySet().toArray()[0];
					nextBestPiece = bestPieces.get(nextBestMove).get(nextBestVal);

					if (loopDetected()) {
						datalink.AddToAIOutput("Sort er gået i loop...\n");
						loop = true;
					}
				}

				if (loop) {
					if (hint) {
						datalink.hintPiece = nextBestPiece;
						datalink.hintMove = nextBestMove;
					} else {
						datalink.bestPiece = nextBestPiece;
						datalink.bestMove = nextBestMove;
					}
					loop = false;
					datalink.AddToAIOutput("Loop undgået!\n");
				} else {
					if (hint) {
						datalink.hintPiece = bestPiece;
						datalink.hintMove = bestMove;
					} else {
						datalink.bestPiece = bestPiece;
						datalink.bestMove = bestMove;
					}
				}
				if (!timesup)
					datalink.nodesExpanded = counter;
				counter = 0;
			}

			return alpha;
		}
		return 0;
	}
	
	/**
	 * Unders�ger om der har v�ret et loop i de sidste par tr�k.
	 * 
	 * @return True, hvis der er er fundet et loop. False, hvis ikke.
	 */
	public boolean loopDetected() {
		LinkedList<Move> moveHistory = datalink.getBoardData().getMoveHistory();

		int size = moveHistory.size();

		if (size > 10) {
			if (moveHistory.get(size - 1).getMoveHistory().equals(moveHistory.get(size - 5).getMoveHistory()) && moveHistory.get(size - 2).getMoveHistory().equals(moveHistory.get(size - 6).getMoveHistory()) && moveHistory.get(size - 3).getMoveHistory().equals(moveHistory.get(size - 7).getMoveHistory()) && moveHistory.get(size - 4).getMoveHistory().equals(moveHistory.get(size - 8).getMoveHistory()))

				return true;
		}

		return false;
	}

}

class timer extends Thread
{
	/**
	 * Timeren der s�rger for at vi ikke bruger l�ngere tid end sat i spilindstillingerne.
	 */
	public void run() {
		try {
			Thread.sleep(Vars.aiThinkTimeInSeconds * 1000); // efter 15 sek s�ttes boolean
			AIPlayer.timesup = true; // herefter afbrydes alphabeta
		} catch (Exception e) {
		}
	}
}
