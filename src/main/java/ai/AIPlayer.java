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
	boolean loop; // sættes når der er fundet loop
	Piece nextBestPiece;
	Byte nextBestMove;
	Integer nextBestVal;
	static boolean timesup = false;
	Piece forcePiece; // hvis vi skal tvinge alphabeta til kun at benytte dette ryk som roden.
	byte forceMove;
	static HashMap<Byte, HashMap<Integer, Piece>> bestPieces; // indeholder de mulige træk der er i roden af træet
	static int counter = 0; // holder styr på antal knuder der er besøgt
	timer t;

	public AIPlayer(Datalink datalink) {
		this.datalink = datalink;
		b = this.datalink.getBoardData();
		bestPieces = new HashMap<Byte, HashMap<Integer, Piece>>();
		loop = false;
	}

	/**
	 * Finder ud af, om det træk i en åbning, der er benyttet er fordelagtigt. Det undersøges om brættets værdi (ved kald af evalueringsfunktionen) er markant dårligere, ved at benytte åbningen frem for alpha-beta
	 * 
	 * @param root
	 *            Benyttes ikke her, men gives videre til alphabeta()
	 * @param player
	 *            Aktuelle spiller
	 * @param depth
	 *            Spiltræsdybde
	 * @param p
	 *            Brikken fra åbningstrækket
	 * @param move
	 *            Feltet brikken vil rykke til
	 * @param bound
	 *            Grænseværdi for, hvor dårligt brættet må være i forhold til alpha-beta
	 * @return Den returnerede værdi fra alphabeta()
	 */
	public int AB(boolean root, byte player, int depth, Piece p, byte move, int bound) {
		b.movePiece(p, move);
		int boardVal = EvalFunction.eval(b);
		b.undoLastMove();
		forcePiece = p;
		forceMove = move;
		int res = alphabeta(root, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		if (Math.abs(boardVal - res) < bound) { // hvis resultatet er dårligere sættes best-værdierne til dem før alphabeta.
			datalink.bestPiece = forcePiece;
			datalink.bestMove = forceMove;
		} else {
			Datalink.usingOpening = false;
		}
		return res;
	}

	/**
	 * Bruges til at kalde den rigtige alphabeta-funktion. Denne funktion sørger for at vi ikke overskrider den tidsgrænse der er sat i spillet indstillinger. Der foregår en slags iterativ dybdesøgning, hvor der søges ved dybde 1 og dybden herefter forøges med 1 per søgning. Når tiden er gået bruger man det sidst fundne træk.
	 * 
	 * @param root
	 *            Sendes videre til alphabeta()
	 * @param player
	 *            Sendes videre til alphabeta()
	 * @param depth
	 *            Maksimal spiltræsdybde. Hvis denne nås før tiden er gået, søges ikke videre, men dennes værdi returneres.
	 * @param alpha
	 *            Beskrives i alphabeta()
	 * @param beta
	 *            Beskrives i alphabeta()
	 * @return Dybden med det sidst fundne træk
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
	 * Alphabeta pruning foregår her.
	 * 
	 * @param root
	 *            Angiver om vi er i roden. Den sættes til true ved første kald, og herfra vil den altid være false. Det er kun i roden, at de fundne brikker gemmes.
	 * @param player
	 *            Den aktuelle spiller.
	 * @param depth
	 *            Angiver hvor langt fra grænsen vi er. Når depth=0 kaldes evalueringsfunktionen.
	 * @param alpha
	 *            Lille værdi. Bruges ved alpha-cutoffs
	 * @param beta
	 *            Stor værdi. Bruges ved beta-cutoffs.
	 * @param hint
	 *            Angiver om alphabeta er kaldt for at finde et hint, eller for at finde AI-spillerens ryk.
	 * @return Den bedste værdi i træet.
	 */
	public int alphabeta(boolean root, byte player, int depth, int alpha, int beta, boolean hint) {
		// Antal noder vi besøger
		counter++;

		Piece bestPiece = null;
		byte bestMove = 0;

		nextBestPiece = null;
		nextBestMove = null;
		nextBestVal = null;

		// Evaluer, hvis der er en vinder, brættet er fuldt eller dybden er 0
		if (depth == 0)
			return EvalFunction.eval(b);

		ArrayList<byte[]> availMoves = new ArrayList<byte[]>();
		ArrayList<Piece> availPieces = new ArrayList<Piece>();
		ArrayList<Piece> pieces = b.getPiecesOfColor(player);

		// tager kun den aktuelle spillers brikker
		TreeSet<Piece> sortedPieces = new TreeSet<Piece>(new Comparator<Piece>() {
			public int compare(Piece p1, Piece p2) {
				// hvis objektet er ens skal returnes 0 ifølge definitionen på en comparator
				if (p1.equals(p2)) {
					return 0;
				}
				// hvis de er af samme type, må ikke returneres 0, da det vil opfattes som ens objekter
				// kun den ene vil derfor blive tilføjet i mappet
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
		
		if (player == Vars.WHITE) { // min-værdier
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

					if (bestPiece == null) { // sikrer at vi altid har en bestPiece => undgår nullpointer exception
						bestPiece = p;
						bestMove = move;
					}
					// rykker brikken
					b.movePiece(p, move);
					int val;
					// hvis skakmat returneres hhv. maximal og minimal værdi
					if (Datalink.blackKingCheckMate)
						val = Integer.MIN_VALUE;
					else if (Datalink.whiteKingCheckMate)
						val = Integer.MAX_VALUE;
					else if (Datalink.gameDraw)
						val = 0;
					else if (timesup) // tiden er gået, og vi vil derfor komme til at benytte forrige dybdes resultat
						val = 0;
					else
						// ellers søg videre
						val = alphabeta(false, Vars.BLACK, depth - 1, alpha, beta, hint);
					// fortryd ryk
					b.undoLastMove();

					if (val < beta) {
						if (root) {
							// tilføjer brikken til en liste over de bedste brikker at rykke
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
					// vælger en tilfældig brik og sætter den til nextBestMove.
					// grunden til at vi ikke tager den næstbedste skyldes, at det giver større risiko for dobbel og trippelloops
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
						// hvis vi har trykket hint, er det hintPiece ders kal sættes
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
			// Black player fungerer på samme måde, omvendt.
		} else if (player == Vars.BLACK) { // max værdier
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

			// placerer det bedste sted, hvis vi har søgt hele træet igennem
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
	 * Undersøger om der har været et loop i de sidste par træk.
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
	 * Timeren der sørger for at vi ikke bruger længere tid end sat i spilindstillingerne.
	 */
	public void run() {
		try {
			Thread.sleep(Vars.aiThinkTimeInSeconds * 1000); // efter 15 sek sættes boolean
			AIPlayer.timesup = true; // herefter afbrydes alphabeta
		} catch (Exception e) {
		}
	}
}
