package common;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import ai.*;
import engine.*;
import gui.ChessFrame;

public class Datalink {
	private static ChessFrame chessFrame;
	private Board boardData;
	protected AIPlayer ai;
	public Piece bestPiece;
	public byte bestMove;

	/*
	 * Diverse spiltilstandsvariabler
	 */
	public static byte gameType = Board.NORMAL_CHESS;
	public static byte currentPlayer = Vars.WHITE;
	public static boolean whiteCanCastleLeft = true;
	public static boolean blackCanCastleLeft = true;
	public static boolean whiteCanCastleRight = true;
	public static boolean blackCanCastleRight = true;
	public static boolean whiteHasCastled = false;
	public static boolean blackHasCastled = false;
	public static boolean gameEnded = true;
	public static boolean gameDraw = false;
	public static int turnNumber = 1;
	public static boolean whiteKingCheck = false;
	public static boolean blackKingCheck = false;
	public static boolean whiteKingCheckMate = false;
	public static boolean blackKingCheckMate = false;
	public static byte whitePawnPromoteType = Piece.WQUEEN;
	public static byte blackPawnPromoteType = Piece.BQUEEN;
	public static byte aiColor = Vars.BLACK;
	public static boolean twoPlayerGame = false;
	public static boolean aivsai = false; // ai mod ai
	public static boolean usingOpening = true; // åbninger bruges kun når denne er true
	public static Opening opening; // den åbning der skal bruges
	public static int openingMoveNumberWhite;
	public static int openingMoveNumberBlack;

	public Piece hintPiece; // Sættes i aiplayer
	public byte hintMove; // bruges til hintfunktionen
	public int nodesExpanded = 0;
	public static boolean justMovedOpening = false; // har vi lige brugt et åbningstræk?

	static int countPlays = 0; // debugging
	public static boolean abStart = false; // om vi stadig er i gang med åbninger

	public Datalink() {
		boardData = new Board();
		ai = new AIPlayer(this);
	}

	public static void main(String[] args) {
		Datalink datalink = new Datalink();
		chessFrame = new ChessFrame(datalink);
		chessFrame.showIt();
		// Testsuite tests = new Testsuite(datalink);
		// tests.runSt artTests();
		// tests.runBoardTests();
	}

	public void play() {
		new Thread() {
			public void run() {
				chessFrame.getMnuSpil().setEnabled(true);
				
				long start = System.currentTimeMillis();

				if (turnNumber == 1 && currentPlayer == Vars.WHITE && !twoPlayerGame) {
					chessFrame.addAIOutput(" Indstillinger for AI:\n");
					chessFrame.addAIOutput("   Max. dybde: " + Vars.aiTreeDepth + "\n");
					chessFrame.addAIOutput("   Max. betænkningstid: " + Vars.aiThinkTimeInSeconds + " sek\n");
					if (usingOpening)
						chessFrame.addAIOutput("   Benytter åbning: \n     " + opening.name + "\n");
				}

				currentPlayer = (byte) ((currentPlayer + 1) % 2);
				
				if (aivsai)
					aiColor = currentPlayer;

				if (currentPlayer == Vars.WHITE)
					chessFrame.lblCurrentPlayer.setText("Hvid trækker");
				else
					chessFrame.lblCurrentPlayer.setText("Sort trækker");

				chessFrame.lblCurrentPlayer.paintImmediately(0, 0, chessFrame.lblCurrentPlayer.getWidth(), chessFrame.lblCurrentPlayer.getHeight());

				if (currentPlayer == Vars.BLACK)
					turnNumber++;

				if (currentPlayer == aiColor && !twoPlayerGame) {
					chessFrame.getMnuSpil().setEnabled(false);
					bestPiece = null;
					int res = 0;
					try {
						// sættes til false, så undoLastMove ikke trækker 1 fra openingMoveNumberXX hver gang AlphaBeta kalder den
						justMovedOpening = false;
						if (currentPlayer == Vars.BLACK) {
							bestPiece = boardData.getPieceAtPos(opening.fromBlack.get(openingMoveNumberBlack));
							bestMove = opening.toBlack.get(openingMoveNumberBlack);
							openingMoveNumberBlack++;
						} else {
							bestPiece = boardData.getPieceAtPos(opening.fromWhite.get(openingMoveNumberWhite));
							bestMove = opening.toWhite.get(openingMoveNumberWhite);
							openingMoveNumberWhite++;
						}
						res = ai.AB(true, currentPlayer, 2, bestPiece, bestMove, 50);
						if(!usingOpening) throw new Exception("Opening gone bad!");
						justMovedOpening  = true;
					} catch (Exception e) {
						if (!abStart) {
							chessFrame.addAIOutput(" Benytter alpha-beta pruning \n");
							abStart = true;
							usingOpening = false;
						}
						res = ai.AB(true, currentPlayer, Vars.aiTreeDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
					}
					
					DecimalFormat time = new DecimalFormat("#########.00");
					long end = System.currentTimeMillis();
					if (!usingOpening){
						chessFrame.addAIOutput(" " + ((currentPlayer == Vars.BLACK) ? "Sort" : "Hvid") + ": " + time.format(((end - start) / 1000.0)) + " sek (dybde: " + res + ")\n");
						chessFrame.addAIOutput(" Antal knuder besøgt: " + nodesExpanded + "\n");
					}
					
					if (blackKingCheckMate || whiteKingCheckMate || gameDraw) {
						if (whiteKingCheckMate) {
							endGame(Vars.BLACK);
						} else if (blackKingCheckMate) {
							endGame(Vars.WHITE);
						} else if (gameDraw) {
							endGame(Byte.MIN_VALUE);
						}
					} else {

						if (boardData.canUseFiftyMoveRule()) {
							chessFrame.addAIOutput(" Benytter 50-ryks reglen!\n");
							boardData.useFiftyMoveRule();
							if(gameDraw)
								endGame(Byte.MIN_VALUE);
						}
						while ((System.currentTimeMillis() - start) < 1000)
							; // evt. benyttes så AI'en flytter brikken med det samme, selvom trækket blev udregnet på ingen tid.
						
						move(bestPiece, bestMove);
						play();
					}
				} else {
					if (whiteKingCheckMate) {
						endGame(Vars.BLACK);
					} else if (blackKingCheckMate) {
						endGame(Vars.WHITE);
					} else if (gameDraw) {
						endGame(Byte.MIN_VALUE);
					} else if (boardData.canUseFiftyMoveRule()) {
						String color;
						if (currentPlayer == Vars.WHITE)
							color = "hvid";
						else
							color = "sort";

						Object[] options = { "Ja", "Nej" };
						int n = JOptionPane.showOptionDialog(chessFrame, "Vil " + color + " benytte sig af 50-ryks-reglen? \n Det betyder at spille ender i remi", "50-ryks-regel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

						if (n == 0)
							boardData.useFiftyMoveRule();
					}
				}
			}
		}.start();
	}

	public Opening pickOpening() {
		// hvis AI starter, vælger den en tilfældig. Ellers skal den finde en åbning der passer til brugerens startryk
		ArrayList<Opening> openings = Opening.parseOpenings("opengames.txt", boardData);
		for (int i = 0; i < openings.size(); i++) {
			openings.get(i).stringToMove();
		}

		int rand = (int) (Math.random() * openings.size());

		return openings.get(rand);
	}

	public void hint() {
		ai.alphabeta(true, currentPlayer, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

		boardData.updateAllLegalMovesForColor((byte) ((Datalink.currentPlayer + 1) % 2), false);
		boardData.updateAllLegalMovesForColor((byte) Datalink.currentPlayer, true);
	}

	public void move(Piece piece, byte to) {
		byte pieceToMove = piece.getType();
		Move lastMove = boardData.movePiece(piece, to);
		String history = generateHistory(chessFrame.getChessboard().big2small(lastMove.getFrom()), chessFrame.getChessboard().big2small(lastMove.getTo()), pieceToMove, lastMove.getTakenPiece());
		lastMove.setMoveHistory(history);
		chessFrame.addHistorik(history);
		chessFrame.getChessboard().reloadBoard();
	}
	
	public void AddToAIOutput(String t){
		chessFrame.addAIOutput(t);
	}

	public Board getBoardData() {
		return boardData;
	}

	public AIPlayer getAI() {
		return ai;
	}

	public void newAIgame() {
		resetVariables();
		twoPlayerGame = false;
		aiColor = Vars.BLACK;
		boardData.resetBoard(Board.NORMAL_CHESS);
		currentPlayer = Vars.BLACK;
		chessFrame.newGame();
		opening = pickOpening();
		openingMoveNumberWhite = 0;
		openingMoveNumberBlack = 0;
		play();
	}

	public void newTwoPlayerGame() 
	{
		resetVariables();
		twoPlayerGame = true;
		boardData.resetBoard(gameType);
		chessFrame.newGame();
	}

	/**
	 * Hvis der startes et nyt spil, skal alle variabler nulstilles. Det gøres her.
	 */
	public void resetVariables() {
		currentPlayer = Vars.WHITE;
		whiteCanCastleLeft = true;
		blackCanCastleLeft = true;
		whiteCanCastleRight = true;
		blackCanCastleRight = true;
		whiteHasCastled = false;
		blackHasCastled = false;
		turnNumber = 1;
		whiteKingCheck = false;
		blackKingCheck = false;
		whiteKingCheckMate = false;
		blackKingCheckMate = false;
		whitePawnPromoteType = Piece.WQUEEN;
		blackPawnPromoteType = Piece.BQUEEN;
		gameEnded = false;
		gameDraw = false;
		usingOpening = true;
		abStart = false;
		aivsai = false;
	}

	private void endGame(byte winner) {
		if (winner == Vars.BLACK)
			JOptionPane.showMessageDialog(chessFrame, "Sort vinder!", "Spillet er slut!", JOptionPane.INFORMATION_MESSAGE);
		else if (winner == Vars.WHITE)
			JOptionPane.showMessageDialog(chessFrame, "Hvid vinder!", "Spillet er slut!", JOptionPane.INFORMATION_MESSAGE);
		else if (winner == Byte.MIN_VALUE)
			JOptionPane.showMessageDialog(chessFrame, "Remi! Ingen vinder.", "Spillet er slut!", JOptionPane.INFORMATION_MESSAGE);
		gameEnded = true;
	}
	
	/**
	 *  Genererer historik for et træk
	 * @param from Index på feltet der flyttes fra
	 * @param to Index på feltet der flyttes til
	 * @param movingPiece Hvilken type brik flytter (eks. 1 for bonde) 
	 * @param pieceAtNewPos Hvilken brik står i det felt der flyttes til (null hvis tom!)
	 * @return Den generede historik
	 */
	public String generateHistory(byte from, byte to, byte movingPiece, Piece pieceAtNewPos)
	{
		String result = "";
		
		String pieces[] = {"","","S","L","T","D","K"};  
		String files[] = {"a", "b", "c", "d", "e", "f", "g", "h"};
		
		byte fromRank = (byte)((from / 8)+1);
		String fromFile = files[from % 8]; 
		
		byte toRank = (byte)((to / 8)+1);
		String toFile = files[to % 8]; 
		
		String capture = "-";
		// Er der taget en brik?
		if (pieceAtNewPos !=  null)
			capture = "x";
		
		result += pieces[Math.abs(movingPiece)] + fromFile + fromRank + capture + toFile + toRank;
		
		// skakmat og skak?
		if (currentPlayer == Vars.WHITE && blackKingCheckMate)
			result += "#";
		else if (currentPlayer == Vars.BLACK && whiteKingCheckMate)
			result += "#";	
		else if (currentPlayer == Vars.WHITE && blackKingCheck)
			result += "+";
		else if (currentPlayer == Vars.BLACK && whiteKingCheck)
			result += "+";		
		
		// Promotion
		if (Datalink.currentPlayer == Vars.WHITE && toRank == 8 && movingPiece == Piece.WPAWN)
			result += pieces[whitePawnPromoteType];
		else if (Datalink.currentPlayer == Vars.BLACK && toRank == 1 && movingPiece == Piece.BPAWN)
			result += pieces[Math.abs(blackPawnPromoteType)];

		// Castling
		if (boardData.getMoveHistory().getLast().isCastledRight())
			result = "0-0";
		else if (boardData.getMoveHistory().getLast().isCastledLeft())
			result = "0-0-0";
	
		return result;
	}
}