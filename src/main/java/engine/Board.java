package engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import common.Datalink;
import common.Vars;

public class Board implements Serializable
{
	private byte[] board;
	private ArrayList<Piece> allPieces;
	private Hashtable<Byte, ArrayList<Piece>> piecesOfType;
	private Hashtable<Byte, ArrayList<Piece>> piecesOfColor;
	private Piece enPassantPawn = null;
	private Piece checkPiece = null;
	private LinkedList<Move> moveHistory;
	private short fiftyMoveRuleCounter;
	private boolean fiftyMoveRuleUsed;

	/**
	 * Constructor
	 * Initialiserer br�ttet og brikkerne
	 */
	public Board()
	{
		moveHistory = new LinkedList<Move>();
		resetBoard(NORMAL_CHESS);
	}

	/**
	 * Denne metode nulstiller br�ttet, s� alle brikker kommer tilbage til
	 * startpositionen for den p�g�ldende spiltype.
	 * 
	 * 0 = normal skak 1 = bonde skak
	 * 
	 * @param gameType
	 *            Den spiltype, der bruges
	 */
	public void resetBoard(int gameType)
	{
		board = new byte[120];
		fiftyMoveRuleCounter = 1;
		fiftyMoveRuleUsed = false;
		boardInit();

		// Der er to forskellige spiltyper
		switch (gameType)
		{
		case (NORMAL_CHESS):
		{
			// Hvid
			board[A1] = Piece.WROOK;
			board[B1] = Piece.WKNIGHT;
			board[C1] = Piece.WBISHOP;
			board[D1] = Piece.WQUEEN;
			board[E1] = Piece.WKING;
			board[F1] = Piece.WBISHOP;
			board[G1] = Piece.WKNIGHT;
			board[H1] = Piece.WROOK;

			for (int i = A2; i <= H2; i++)
			{
				board[i] = Piece.WPAWN;
			}

			// Sort
			board[A8] = Piece.BROOK;
			board[B8] = Piece.BKNIGHT;
			board[C8] = Piece.BBISHOP;
			board[D8] = Piece.BQUEEN;
			board[E8] = Piece.BKING;
			board[F8] = Piece.BBISHOP;
			board[G8] = Piece.BKNIGHT;
			board[H8] = Piece.BROOK;

			for (int i = A7; i <= H7; i++)
			{
				board[i] = Piece.BPAWN;
			}

			pieceInit();
			break;
		}
		case (PAWN_CHESS):
		{
			// HVID
			board[D1] = Piece.WQUEEN;
			board[E1] = Piece.WKING;

			for (int i = A2; i <= H2; i++)
				board[i] = Piece.WPAWN;

			// SORT
			board[D8] = Piece.BQUEEN;
			board[E8] = Piece.BKING;

			for (int i = A7; i <= H7; i++)
				board[i] = Piece.BPAWN;

			pieceInit();
			Datalink.usingOpening = false;
			break;
		}
		case (PATT_SCHACH_CHESS):
		{
			// Hvid
			board[A1] = Piece.WROOK;
			board[B1] = Piece.WKNIGHT;
			board[C1] = Piece.WBISHOP;
			board[D1] = Piece.WQUEEN;
			board[E1] = Piece.WKING;
			board[F1] = Piece.WBISHOP;
			board[G1] = Piece.WKNIGHT;
			board[H1] = Piece.WROOK;

			for (int i = B2; i <= G2; i++)
			{
				board[i] = Piece.WPAWN;
			}
			board[B3] = Piece.WPAWN;
			board[G3] = Piece.WPAWN;

			// Sort
			board[A8] = Piece.BROOK;
			board[B8] = Piece.BKNIGHT;
			board[C8] = Piece.BBISHOP;
			board[D8] = Piece.BQUEEN;
			board[E8] = Piece.BKING;
			board[F8] = Piece.BBISHOP;
			board[G8] = Piece.BKNIGHT;
			board[H8] = Piece.BROOK;

			for (int i = B7; i <= G7; i++)
			{
				board[i] = Piece.BPAWN;
			}
			board[B6] = Piece.BPAWN;
			board[G6] = Piece.BPAWN;

			pieceInit();
			break;
		}
		case (PAWNS_GAME_CHESS):
		{
			// Hvid
			board[A1] = Piece.WROOK;
			board[B1] = Piece.WKNIGHT;
			board[C1] = Piece.WBISHOP;
			board[E1] = Piece.WKING;
			board[F1] = Piece.WBISHOP;
			board[G1] = Piece.WKNIGHT;
			board[H1] = Piece.WROOK;

			for (int i = B2; i <= G2; i++)
			{
				board[i] = Piece.WPAWN;
			}
			board[B3] = Piece.WPAWN;
			board[C3] = Piece.WPAWN;
			board[F3] = Piece.WPAWN;
			board[G3] = Piece.WPAWN;
			for (int i = C4; i <= F4; i++)
			{
				board[i] = Piece.WPAWN;
			}

			// Sort
			board[A8] = Piece.BROOK;
			board[B8] = Piece.BKNIGHT;
			board[C8] = Piece.BBISHOP;
			board[D8] = Piece.BQUEEN;
			board[E8] = Piece.BKING;
			board[F8] = Piece.BBISHOP;
			board[G8] = Piece.BKNIGHT;
			board[H8] = Piece.BROOK;

			for (int i = A7; i <= H7; i++)
			{
				board[i] = Piece.BPAWN;
			}

			pieceInit();
			break;
		}
		case (UPSIDE_DOWN_CHESS):
		{
			// Hvid
			board[A1] = Piece.BROOK;
			board[B1] = Piece.BKNIGHT;
			board[C1] = Piece.BBISHOP;
			board[D1] = Piece.BQUEEN;
			board[E1] = Piece.BKING;
			board[F1] = Piece.BBISHOP;
			board[G1] = Piece.BKNIGHT;
			board[H1] = Piece.BROOK;

			for (int i = A2; i <= H2; i++)
			{
				board[i] = Piece.BPAWN;
			}

			// Sort
			board[A8] = Piece.WROOK;
			board[B8] = Piece.WKNIGHT;
			board[C8] = Piece.WBISHOP;
			board[D8] = Piece.WQUEEN;
			board[E8] = Piece.WKING;
			board[F8] = Piece.WBISHOP;
			board[G8] = Piece.WKNIGHT;
			board[H8] = Piece.WROOK;

			for (int i = A7; i <= H7; i++)
			{
				board[i] = Piece.WPAWN;
			}

			pieceInit();
			break;
		}
		}

		// Opdaterer tr�k for alle brikker, s� de er klar fra begyndelsen
		updateAllLegalMovesForColor(Vars.BLACK, false);
		updateAllLegalMovesForColor(Vars.WHITE, true);
		moveHistory.clear();
	}

	/**
	 * Nulstiller selve br�ttet, s� alle pladser p� br�ttet f�r de rigtige v�rdier
	 */
	private void boardInit()
	{
		// S�tter v�rdier p� ulovlige pladser for brikkerne
		for (int i = 0; i < board.length; i++)
		{
			if ((i % 10 == 0) || (i % 10 == 9) || (i < 21) || (i > 98))
			{
				board[i] = 100; // Ulovlig omr�de for brikker
			} else
				board[i] = 0;
		}
	}

	/**
	 * Initialiserer brikker og putter dem de relevante lister
	 */
	private void pieceInit()
	{
		piecesOfType = new Hashtable<Byte, ArrayList<Piece>>();
		piecesOfColor = new Hashtable<Byte, ArrayList<Piece>>();
		ArrayList<Piece> blackArray = new ArrayList<Piece>();
		ArrayList<Piece> whiteArray = new ArrayList<Piece>();
		piecesOfColor.put(Vars.BLACK, blackArray);
		piecesOfColor.put(Vars.WHITE, whiteArray);
		allPieces = new ArrayList<Piece>();

		for (byte i = 0; i < board.length; i++)
		{
			if (board[i] != 0 && board[i] != 100)
			{
				Piece newPiece = new Piece(board[i], i);
				addPiece(newPiece);
			}
		}
	}

	/**
	 * Tilf�jer en brik til de tre lister
	 * @param piece
	 * 			Den brik der skal tilf�jes
	 */
	private void addPiece(Piece piece)
	{
		if (piecesOfType.containsKey(piece.getType()))
		{
			piecesOfType.get(piece.getType()).add(piece);
		} else
		{
			ArrayList<Piece> tempList = new ArrayList<Piece>();
			tempList.add(piece);
			piecesOfType.put(piece.getType(), tempList);
		}

		if (piece.getType() > 0)
			piecesOfColor.get(Vars.WHITE).add(piece);
		else
			piecesOfColor.get(Vars.BLACK).add(piece);

		allPieces.add(piece);
	}

	/**
	 * Returnerer skakbr�ttets datastruktur
	 * @return skakbr�ttet som et byte[] array
	 */
	public byte[] getBoard()
	{
		return board;
	}

	/**
	 * Returnerer en liste over alle brikker i spillet
	 * @return en ArrayList med brikker i spillet
	 */
	public ArrayList<Piece> getPieces()
	{
		return allPieces;
	}

	/**
	 * Returnerer en liste over brikker af den �nskede type
	 * 
	 * @param pieceType
	 *            den type brik, der �nskes en liste over, f.eks. -1 for sorte
	 *            b�nder.
	 * @return En liste med de �nskede brikker
	 */
	public ArrayList<Piece> getPiecesOfType(byte pieceType)
	{
		return piecesOfType.get(pieceType);
	}

	/**
	 * Returnerer en liste over brikker af den �nskede farve
	 * 
	 * @param color
	 *            den farve af brikker, der �nskes liste over
	 * @return En liste med de �nskede brikker
	 */
	public ArrayList<Piece> getPiecesOfColor(byte color)
	{
		return piecesOfColor.get(color);
	}

	/**
	 * Fjerner en brik fra spillet
	 * 
	 * @param piece
	 *            Den brik der skal fjernes
	 * @return True hvis brikken blev fjernet, ellers false
	 */
	private void removePiece(Piece piece)
	{
		piecesOfType.get(piece.getType()).remove(piece);
		if (piece.getType() > 0)
			piecesOfColor.get(Vars.WHITE).remove(piece);
		else
			piecesOfColor.get(Vars.BLACK).remove(piece);

		allPieces.remove(piece);
		board[piece.getPos()] = 0;
	}

	/**
	 * Finder og returnerer den brik, der st�r p� et givet felt
	 * 
	 * @param position
	 * @return en brik, hvis feltet ikke er tomt, ellers null
	 */
	public Piece getPieceAtPos(byte position)
	{
		if (board[position] == 0)
			return null;
		else
		{
			for (Piece piece : piecesOfType.get(board[position]))
			{
				if (piece.getPos() == position)
					return piece;
			}
		}
		return null;
	}

	/**
	 * Rydder listen over fjendtlige brikker for alle brikker
	 */
	private void clearEnemyList()
	{
		for (Piece piece : allPieces)
		{
			piece.getEnemies().clear();
		}
	}

	/*
	 * *********************************************************************************
	 * Tjek af ryk, foretagning af ryk og indl�sning af regels�t:
	 * *********************************************************************************
	 */

	/**
	 * Rykker en brik. Tjekker, om der st�r en brik p� feltet i forvejen.
	 * 
	 * Metoden antager, at der indtastes et gyldigt ryk!
	 * Der valideres ikke p� rykket
	 * 
	 * @param piece
	 *            Den brik, der skal rykkes
	 * @param to
	 *            den position, brikkes skal flyttes hen til
	 * @return En brik, hvis der st�r en i forvejen, ellers null
	 */
	public Move movePiece(Piece piece, byte to)
	{
		Piece res = null;

		piece.increaseNumMoves(); // For�ger brikkens spilt�ller

		// Et Move objekt oprettes, s� oplysninger om rykket kan gemmes
		Move saveMove = new Move(piece, to);

		// Alle ryk for alle brikker gemmes
		for (Piece savePiece : allPieces)
		{
			saveMove.pieceMoves.put(savePiece, copyArray(savePiece.getLegalMoves()));
		}
		
		// Nuv�rende 50-ryks t�ller gemmes og for�ges derefter
		saveMove.fiftyMoveCounter = fiftyMoveRuleCounter;
		fiftyMoveRuleCounter++;
		
		// Hvis der rykkes en bonde, nulstilles 50 ryks t�lleren
		if (piece.getType() == Piece.BPAWN || piece.getType() == Piece.WPAWN)
		{
			fiftyMoveRuleCounter = 1;
			fiftyMoveRuleUsed = false;
		}

		// Hvis kongen er sat i skak gemmes denne oplysning i Move objektet
		// Samtidig gemmes den brik, som har sat kongen i skak
		if (Datalink.whiteKingCheck || Datalink.blackKingCheck)
		{
			saveMove.kingWasChecked = true;
			saveMove.checkPiece = checkPiece;
		}

		// En brik vil if�lge spillets opbygning altid bringe kongen ud af skak.
		// Ellers er der tale om et ulovligt tr�k.
		// Dette skal ikke kunne lade sig g�re.
		// Derfor s�ttes disse variable.
		Datalink.whiteKingCheck = false;
		Datalink.blackKingCheck = false;
		checkPiece = null;

		// Datastrukturen opdateres
		board[piece.getPos()] = 0;
		
		// Hvis destinationsfeltet ikke er tomt, findes brikken, der st�r der
		if (board[to] != 0)
		{
			res = getPieceAtPos(to);
		}
		
		// Hvis der er fundet en brik, bliver denne fjernet fra spillet
		if (res != null)
		{
			// 50-ryks regel t�lleren nulstilles, fordi der er blevet taget en brik
			fiftyMoveRuleCounter = 1;
			fiftyMoveRuleUsed = false;
			saveMove.takenPiece = res;
			removePiece(res);
		}

		// Hvis 50-ryks reglen er benyttet, er spillet uafgjort.
		if (fiftyMoveRuleUsed)
		{
			Datalink.gameDraw = true;
			saveMove.gameDrawed = true;
			moveHistory.offer(saveMove);
			return saveMove;
		}

		// Tjekker for eventuelle specialryk, se checkSpecialMove
		checkSpecialMove(piece, piece.getPos(), to, saveMove);
		board[to] = piece.getType();
		piece.setPos(to);

		// Opdaterer spiltilstanden og tjekker om kongen er sat i skak.
		// Opdaterer ryk for alle brikker
		// Hvis ingen brikker kan rykke for den spiller,
		// der skal til at rykke, er spilleren skakmat
		// eller spillet er remis
		clearEnemyList();
		if (piece.getType() > 0)
		{
			updateAllLegalMovesForColor(Vars.WHITE, false);
			checkKingStatus(Vars.BLACK);
			if (Datalink.blackKingCheck)
				saveMove.checkedKing = true;
			updateAllLegalMovesForColor(Vars.BLACK, true);

			if (cannotMove(Vars.BLACK))
			{
				if (Datalink.blackKingCheck)
				{
					Datalink.blackKingCheckMate = true;
					saveMove.checkMatedKing = true;
				}
				else
				{
					Datalink.gameDraw = true;
					saveMove.gameDrawed = true;
				}
			}
		}
		else
		{
			updateAllLegalMovesForColor(Vars.BLACK, false);
			checkKingStatus(Vars.WHITE);
			if (Datalink.whiteKingCheck)
				saveMove.checkedKing = true;
			updateAllLegalMovesForColor(Vars.WHITE, true);

			if (cannotMove(Vars.WHITE))
			{
				if (Datalink.whiteKingCheck)
				{
					Datalink.whiteKingCheckMate = true;
					saveMove.checkMatedKing = true;
				}
				else
				{
					Datalink.gameDraw = true;
					saveMove.gameDrawed = true;
				}
			}
		}

		moveHistory.offer(saveMove);
		return saveMove;
	}

	/**
	 * Metoden unders�ger tr�kket for bestemte egenskaber:
	 * -castle rokade
	 * -en passant
	 * -pawn promotion
	 * 
	 * Metoden opdaterer samtidig spillets tilstand, hvis det er n�dvendigt
	 * 
	 * @param piece
	 *            Den brik der unders�ges
	 * @param to
	 *            Den position, som brikkes rykker hen til
	 */
	private void checkSpecialMove(Piece piece, byte from, byte to, Move saveMove)
	{
		switch (piece.getType())
		{
		// Hvis kongen kan lave rokade og rykker hen til den rigtige position
		// udf�res rokaden. Koden er n�sten selvforklarende
		case Piece.WKING:
		{
			if (to == C1)
			{
				if (Datalink.whiteCanCastleLeft)
				{
					getPieceAtPos(A1).setPos(D1);
					board[A1] = 0;
					board[D1] = Piece.WROOK;
					Datalink.whiteHasCastled = true;
					saveMove.castledLeft = true;
				}
			} else if (to == G1)
			{
				if (Datalink.whiteCanCastleRight)
				{
					getPieceAtPos(H1).setPos(F1);
					board[H1] = 0;
					board[F1] = Piece.WROOK;
					Datalink.whiteHasCastled = true;
					saveMove.castledRight = true;
				}
			}
			Datalink.whiteCanCastleLeft = false;
			Datalink.whiteCanCastleRight = false;
			break;
		}
		// Det samme g�lder den sorte konge.
		case (Piece.BKING):
		{
			if (to == C8)
			{
				if (Datalink.blackCanCastleLeft)
				{
					getPieceAtPos(A8).setPos(D8);
					board[A8] = 0;
					board[D8] = Piece.BROOK;
					Datalink.blackHasCastled = true;
					saveMove.castledLeft = true;
				}
			} else if (to == G8)
			{
				if (Datalink.blackCanCastleRight)
				{
					getPieceAtPos(H8).setPos(F8);
					board[H8] = 0;
					board[F8] = Piece.BROOK;
					Datalink.blackHasCastled = true;
					saveMove.castledRight = true;
				}
			}
			Datalink.blackCanCastleLeft = false;
			Datalink.blackCanCastleRight = false;
			break;
		}
		// Hvis t�rnet rykker fra sin udgangsposition er det ikke muligt
		// at foretage rokade mere fra denne side.
		case (Piece.WROOK):
		{
			if (from == A1)
				Datalink.whiteCanCastleLeft = false;
			else if (from == H1)
				Datalink.whiteCanCastleRight = false;
			break;
		}
		case (Piece.BROOK):
		{
			if (from == A8)
				Datalink.blackCanCastleLeft = false;
			else if (from == H8)
				Datalink.blackCanCastleRight = false;
			break;
		}
		case (Piece.WPAWN):
		{
			if ((to >= A8) && (to <= H8))
			{
				saveMove.promotion = true;
				promotePawn(piece, Vars.WHITE, to);
			}
			else if (piece.getPos() + N + N == to)
				enPassantPawn = piece;
			else if (enPassantPawn != null
					&& getPieceAtPos((byte) (to + S)) != null
					&& getPieceAtPos((byte) (to + S)).equals(enPassantPawn))
			{
				saveMove.takenPiece = enPassantPawn;
				removePiece(enPassantPawn);
				enPassantPawn = null;
			}
			break;
		}
		case (Piece.BPAWN):
		{
			if ((to >= A1) && (to <= H1))
			{
				saveMove.promotion = true;
				promotePawn(piece, Vars.BLACK, to);
			}
			else if (piece.getPos() + S + S == to)
				enPassantPawn = piece;
			else if (enPassantPawn != null
					&& getPieceAtPos((byte) (to + N)) != null
					&& getPieceAtPos((byte) (to + N)).equals(enPassantPawn))
			{
				saveMove.takenPiece = enPassantPawn;
				removePiece(enPassantPawn);
				enPassantPawn = null;
			}
			break;
		}
		}
		if (enPassantPawn != null && (!enPassantPawn.equals(piece)))
			enPassantPawn = null;
	}

	/**
	 * Denne metode unders�ger, om modstanderen bliver sat i skak.
	 * @param kingColor
	 * 				Farven p� den konge, der skal unders�ges
	 */
	private void checkKingStatus(byte kingColor)
	{
		if (kingColor == Vars.WHITE) // Hvid
		{
			// K�rer igennem alle brikker for modstanderen
			for (Piece piece : getPiecesOfColor(Vars.BLACK))
			{
				// K�rer igennem alle ryk for modstanderens brik
				for (byte move : piece.getLegalMoves())
				{
					// Hvis brikken kan tage kongen, er kongen blevet sat i skak
					if (board[move] == Piece.WKING)
					{
						Datalink.whiteKingCheck = true;
						checkPiece = piece;
						break;
					}
				}
				if (Datalink.whiteKingCheck == true)
					break;
			}
		}
		else // Sort fungerer p� samme m�de som hvid
		{
			for (Piece piece : getPiecesOfColor(Vars.WHITE))
			{
				for (byte move : piece.getLegalMoves())
				{
					if (board[move] == Piece.BKING)
					{
						Datalink.blackKingCheck = true;
						checkPiece = piece;
						break;
					}
				}
				if (Datalink.blackKingCheck == true)
					break;
			}
		}
	}

	/**
	 * Afg�rer om spilleren ikke kan rykke
	 * @param kingColor
	 * 				Farven p� kongen, hvis brikker skal unders�ges
	 * @return true hvis ingen brikker kan rykke, ellers false
	 */
	private boolean cannotMove(byte kingColor)
	{
		boolean result = true;
		for (Piece piece : getPiecesOfColor(kingColor))
		{
			if (piece.getLegalMoves().length > 0)
			{
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * Benytter 50-ryks reglen
	 */
	public void useFiftyMoveRule()
	{
		if (fiftyMoveRuleCounter == 49)
			fiftyMoveRuleUsed = true;
		else if (fiftyMoveRuleCounter >= 50)
			Datalink.gameDraw = true;
	}

	/**
	 * Afg�rer om 50-ryks regelen kan benyttes
	 * @return true hvis det er muligt, ellers false
	 */
	public boolean canUseFiftyMoveRule()
	{
		if (fiftyMoveRuleCounter >= 49)
			return true;
		else
			return false;
	}

	/**
	 * Finder den direkte vej fra en brik til kongen, hvis en s�dan findes.
	 * Metoden benyttes, n�r kongen skal beskyttes.
	 * @param piece
	 * 			Den brik der kigges p�. 
	 * @return En liste best�ende af de ryk, som f�rer brikken hen til kongen.
	 */
	private byte[] optimizeMovePathToKing(Piece piece)
	{
		// F�rst opdateres rykkene
		piece.setLegalMoves(getLegalMoves(piece, false));
		byte[] result = new byte[0];
		byte searchPieceType;

		// Afg�rer hvilken konge der skal s�ges efter
		if (piece.getType() < 0)
			searchPieceType = Piece.WKING;
		else
			searchPieceType = Piece.BKING;

		byte curPos = piece.getPos();

		// Fors�ger at g� i alle retninger for at finde kongen
		for (int i = 0; i < DIRECTIONS.length; i++)
		{
			if (arrayContains(piece.getLegalMoves(), (byte) (curPos + DIRECTIONS[i])))
			{
				// Hvis kongen kun st�r ved siden af brikken, vi kigger p�, returneres blot dette ryk
				if (board[curPos + DIRECTIONS[i]] == searchPieceType)
					return new byte[] { (byte) (curPos + DIRECTIONS[i]) };
				
				// Beregner sti til kongen
				result = recursivePathFinder(curPos, DIRECTIONS[i], searchPieceType, piece.getLegalMoves());
				
				// Hvis der er fundet en sti, returneres denne
				if (result.length > 0)
					return result;
			}
		}

		return result;
	}

	/**
	 * Gennems�ger en briks ryk for at optimere vejen til en bestemt anden brik.
	 * 
	 * Metoden k�rer rekursivt, heraf navnet.
	 * 
	 * @param curPos
	 *            nuv�rende position
	 * @param direction
	 *            retning, der kigges i
	 * @param searchPieceType
	 *            den briktype der s�ges efter
	 * @param legalMoveList
	 *            de ryk, som brikken kan foretage sig
	 * @return en opdateret liste med kun de ryk, der leder hen til brikken
	 */
	private byte[] recursivePathFinder(byte curPos, byte direction,
			byte searchPieceType, byte[] legalMoveList)
	{
		byte[] result = new byte[0];
		// F�rst unders�ges, om den fortsat kan g� i retningen
		// Eller returneres en tom liste
		if (arrayContains(legalMoveList, (byte) (curPos + direction)))
		{
			byte[] temp = new byte[0];
			
			// Hvis algoritmen har fundet den �nskede brik
			// Returneres en liste med de relevante ryk
			if (board[curPos + direction] == searchPieceType)
			{
				return new byte[] { (byte) (curPos + direction), curPos };
			}
			else // Eller s�ges der videre
				temp = recursivePathFinder((byte) (curPos + direction),
						direction, searchPieceType, legalMoveList);
			// Der tilf�jes et ekstra ryk til ryklisten frem mod den brik,
			// der s�ges efter
			if (temp.length > 0)
			{
				result = new byte[temp.length + 1];
				byte index = 0;
				for (byte tempMove : temp)
				{
					result[index++] = tempMove;
				}
				result[index++] = curPos;
				return result;
			}
			else
				return temp;
		}
		else
			return result;
	}

	/**
	 * Metoden forfremmer en bonde, som er n�et hen over br�ttet til den anden
	 * side
	 * 
	 * @param piece
	 *            den brik, der skal forfremmes
	 * @param color
	 *            den farve, brikken har
	 * @param pos
	 *            den position, hvor brikken st�r
	 */
	public void promotePawn(Piece piece, byte color, byte pos)
	{
		removePiece(piece);

		if (color == Vars.BLACK)
			piece.setType(Datalink.blackPawnPromoteType);
		else
			piece.setType(Datalink.whitePawnPromoteType);

		board[pos] = piece.getType();
		addPiece(piece);
	}

	public void updateAllLegalMovesForColor(byte color, boolean evalMoves)
	{
		for (Piece piece : getPiecesOfColor(color))
			piece.setLegalMoves(getLegalMoves(piece, evalMoves));
	}

	public byte[] getLegalMoves(Piece piece)
	{
		return piece.getLegalMoves();
	}

	/**
	 * Metoden returnerer en liste over tr�k, der kan foretages i �t ryk.
	 * 
	 * Denne metode giver udelukkende en liste over ryk, der KAN foretages. Den
	 * rykker ikke, evaluerer ikke eller noget andet.
	 * 
	 * @param piece
	 *            Den brik, der unders�ges
	 * @return en liste over mulige ryk.
	 */
	private byte[] getLegalMoves(Piece piece, boolean evalMoves)
	{
		byte[] result = new byte[0];
		byte index = 0;
		byte curPos = piece.getPos();
		/*
		 * Kommentarer skrives f�r hvert case for at holde det overskueligt
		 */
		switch (piece.getType())
		{
		/*
		 * Bonden 1. Der tjekkes, om bonden er i sin startposition og ikke
		 * blokeret af andre brikker = 2 ryk frem 2. Ellers kun �t ryk frem 3.
		 * Der tjekkes, om der st�r en modstanderbrik p� hver af de to felter
		 * skr�t fremme i forhold til bonden = ryk til disse og tag brikken 4.
		 * Der tjekkes, om bonden har mulighed for at lave et en passant tr�k
		 * 
		 * Ovenst�ende punkter g�lder for b�de sort og hvid bonde
		 */
		case (Piece.WPAWN):
		{
			result = new byte[4];

			// 1
			if ((curPos >= A2) && (curPos <= H2) && board[curPos + N + N] == 0
					&& board[curPos + N] == 0)
			{
				result[index++] = (byte) (curPos + N);
				result[index++] = (byte) (curPos + N + N);
			}
			// 2
			else if (board[curPos + N] == 0)
				result[index++] = (byte) (curPos + N);

			// 3
			if (board[curPos + NE] < 0)
			{
				result[index++] = (byte) (curPos + NE);
				getPieceAtPos((byte) (curPos + NE)).getEnemies().add(piece);
			}

			if (board[curPos + NW] < 0)
			{
				result[index++] = (byte) (curPos + NW);
				getPieceAtPos((byte) (curPos + NW)).getEnemies().add(piece);
			}

			// 4
			if (board[curPos + W] == Piece.BPAWN
					&& getPieceAtPos((byte) (curPos + W)).equals(enPassantPawn))
				result[index++] = (byte) (curPos + NW);
			if (board[curPos + E] == Piece.BPAWN
					&& getPieceAtPos((byte) (curPos + E)).equals(enPassantPawn))
				result[index++] = (byte) (curPos + NE);

			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
				result = checkMoves(piece, result);
			break;
		}
		case (Piece.BPAWN):
		{
			result = new byte[4];
			if ((curPos >= A7) && (curPos <= H7)
					&& (board[curPos + S + S] == 0) && (board[curPos + S] == 0))
			{
				result[index++] = (byte) (curPos + S);
				result[index++] = (byte) (curPos + S + S);
			} else if (board[curPos + S] == 0)
				result[index++] = (byte) (curPos + S);

			if (board[curPos + SE] > 0 && board[curPos + SE] != 100)
			{
				result[index++] = (byte) (curPos + SE);
				if (board[curPos + SE] > 0)
					getPieceAtPos((byte) (curPos + SE)).getEnemies().add(piece);
			}

			if (board[curPos + SW] > 0 && board[curPos + SW] != 100)
			{
				result[index++] = (byte) (curPos + SW);
				if (board[curPos + SW] > 0)
					getPieceAtPos((byte) (curPos + SW)).getEnemies().add(piece);
			}

			if (board[curPos + W] == Piece.WPAWN
					&& getPieceAtPos((byte) (curPos + W)).equals(enPassantPawn))
				result[index++] = (byte) (curPos + SW);
			if (board[curPos + E] == Piece.WPAWN
					&& getPieceAtPos((byte) (curPos + E)).equals(enPassantPawn))
				result[index++] = (byte) (curPos + SE);
			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
				result = checkMoves(piece, result);
			break;
		}
		case (Piece.WKNIGHT):
		{
			result = new byte[8];
			if (board[curPos + N + N + E] <= 0)
			{
				result[index++] = (byte) (curPos + N + N + E);
				if (board[curPos + N + N + E] < 0)
					getPieceAtPos((byte) (curPos + N + N + E)).getEnemies()
							.add(piece);
			}

			if (board[curPos + N + N + W] <= 0)
			{
				result[index++] = (byte) (curPos + N + N + W);
				if (board[curPos + N + N + W] < 0)
					getPieceAtPos((byte) (curPos + N + N + W)).getEnemies()
							.add(piece);
			}

			if (board[curPos + E + E + N] <= 0)
			{
				result[index++] = (byte) (curPos + E + E + N);
				if (board[curPos + E + E + N] < 0)
					getPieceAtPos((byte) (curPos + E + E + N)).getEnemies()
							.add(piece);
			}

			if (board[curPos + E + E + S] <= 0)
			{
				result[index++] = (byte) (curPos + E + E + S);
				if (board[curPos + E + E + S] < 0)
					getPieceAtPos((byte) (curPos + E + E + S)).getEnemies()
							.add(piece);
			}

			if (board[curPos + W + W + N] <= 0)
			{
				result[index++] = (byte) (curPos + W + W + N);
				if (board[curPos + W + W + N] < 0)
					getPieceAtPos((byte) (curPos + W + W + N)).getEnemies()
							.add(piece);
			}

			if (board[curPos + W + W + S] <= 0)
			{
				result[index++] = (byte) (curPos + W + W + S);
				if (board[curPos + W + W + S] < 0)
					getPieceAtPos((byte) (curPos + W + W + S)).getEnemies()
							.add(piece);
			}

			if (board[curPos + S + S + W] <= 0)
			{
				result[index++] = (byte) (curPos + S + S + W);
				if (board[curPos + S + S + W] < 0)
					getPieceAtPos((byte) (curPos + S + S + W)).getEnemies()
							.add(piece);
			}

			if (board[curPos + S + S + E] <= 0)
			{
				result[index++] = (byte) (curPos + S + S + E);
				if (board[curPos + S + S + E] < 0)
					getPieceAtPos((byte) (curPos + S + S + E)).getEnemies()
							.add(piece);
			}

			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.BKNIGHT):
		{
			result = new byte[8];
			if (board[curPos + N + N + E] != 100
					&& board[curPos + N + N + E] >= 0)
			{
				result[index++] = (byte) (curPos + N + N + E);
				if (board[curPos + N + N + E] > 0)
					getPieceAtPos((byte) (curPos + N + N + E)).getEnemies()
							.add(piece);
			}

			if (board[curPos + N + N + W] != 100
					&& board[curPos + N + N + W] >= 0)
			{
				result[index++] = (byte) (curPos + N + N + W);
				if (board[curPos + N + N + W] > 0)
					getPieceAtPos((byte) (curPos + N + N + W)).getEnemies()
							.add(piece);
			}

			if (board[curPos + E + E + N] != 100
					&& board[curPos + E + E + N] >= 0)
			{
				result[index++] = (byte) (curPos + E + E + N);
				if (board[curPos + E + E + N] > 0)
					getPieceAtPos((byte) (curPos + E + E + N)).getEnemies()
							.add(piece);
			}

			if (board[curPos + E + E + S] != 100
					&& board[curPos + E + E + S] >= 0)
			{
				result[index++] = (byte) (curPos + E + E + S);
				if (board[curPos + E + E + S] > 0)
					getPieceAtPos((byte) (curPos + E + E + S)).getEnemies()
							.add(piece);
			}

			if (board[curPos + W + W + N] != 100
					&& board[curPos + W + W + N] >= 0)
			{
				result[index++] = (byte) (curPos + W + W + N);
				if (board[curPos + W + W + N] > 0)
					getPieceAtPos((byte) (curPos + W + W + N)).getEnemies()
							.add(piece);
			}

			if (board[curPos + W + W + S] != 100
					&& board[curPos + W + W + S] >= 0)
			{
				result[index++] = (byte) (curPos + W + W + S);
				if (board[curPos + W + W + S] > 0)
					getPieceAtPos((byte) (curPos + W + W + S)).getEnemies()
							.add(piece);
			}

			if (board[curPos + S + S + W] != 100
					&& board[curPos + S + S + W] >= 0)
			{
				result[index++] = (byte) (curPos + S + S + W);
				if (board[curPos + S + S + W] > 0)
					getPieceAtPos((byte) (curPos + S + S + W)).getEnemies()
							.add(piece);
			}

			if (board[curPos + S + S + E] != 100
					&& board[curPos + S + S + E] >= 0)
			{
				result[index++] = (byte) (curPos + S + S + E);
				if (board[curPos + S + S + E] > 0)
					getPieceAtPos((byte) (curPos + S + S + E)).getEnemies()
							.add(piece);
			}

			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.BBISHOP):
		{
			byte i = 1;
			result = new byte[14];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NE] != 100
						&& board[curPos + i * NE] >= 0)
				{
					result[index++] = (byte) (curPos + i * NE);
					if (board[curPos + i * NE] > 0)
					{
						getPieceAtPos((byte) (curPos + i * NE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NW] != 100
						&& board[curPos + i * NW] >= 0)
				{
					result[index++] = (byte) (curPos + i * NW);
					if (board[curPos + i * NW] > 0)
					{
						getPieceAtPos((byte) (curPos + i * NW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SW] != 100
						&& board[curPos + i * SW] >= 0)
				{
					result[index++] = (byte) (curPos + i * SW);
					if (board[curPos + i * SW] > 0)
					{
						getPieceAtPos((byte) (curPos + i * SW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SE] != 100
						&& board[curPos + i * SE] >= 0)
				{
					result[index++] = (byte) (curPos + i * SE);
					if (board[curPos + i * SE] > 0)
					{
						getPieceAtPos((byte) (curPos + i * SE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}
			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.WBISHOP):
		{
			byte i = 1;
			result = new byte[14];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NE] <= 0)
				{
					result[index++] = (byte) (curPos + i * NE);
					if (board[curPos + i * NE] < 0)
					{
						getPieceAtPos((byte) (curPos + i * NE)).getEnemies().add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NW] <= 0)
				{
					result[index++] = (byte) (curPos + i * NW);
					if (board[curPos + i * NW] < 0)
					{
						getPieceAtPos((byte) (curPos + i * NW)).getEnemies().add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SW] <= 0)
				{
					result[index++] = (byte) (curPos + i * SW);
					if (board[curPos + i * SW] < 0)
					{
						getPieceAtPos((byte) (curPos + i * SW)).getEnemies().add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SE] <= 0)
				{
					result[index++] = (byte) (curPos + i * SE);
					if (board[curPos + i * SE] < 0)
					{
						getPieceAtPos((byte) (curPos + i * SE)).getEnemies().add(piece);
						break;
					}
				} else
					break;
			}

			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.WROOK):
		{
			byte i = 1;
			result = new byte[14];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * N] <= 0)
				{
					result[index++] = (byte) (curPos + i * N);
					if (board[curPos + i * N] < 0)
					{
						getPieceAtPos((byte) (curPos + i * N)).getEnemies().add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * W] <= 0)
				{
					result[index++] = (byte) (curPos + i * W);
					if (board[curPos + i * W] < 0)
					{
						getPieceAtPos((byte) (curPos + i * W)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * S] <= 0)
				{
					result[index++] = (byte) (curPos + i * S);
					if (board[curPos + i * S] < 0)
					{
						getPieceAtPos((byte) (curPos + i * S)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * E] <= 0)
				{
					result[index++] = (byte) (curPos + i * E);
					if (board[curPos + i * E] < 0)
					{
						getPieceAtPos((byte) (curPos + i * E)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}
			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.BROOK):
		{
			byte i = 1;
			result = new byte[14];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * N] != 100 && board[curPos + i * N] >= 0)
				{
					result[index++] = (byte) (curPos + i * N);
					if (board[curPos + i * N] > 0)
					{
						getPieceAtPos((byte) (curPos + i * N)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * W] != 100 && board[curPos + i * W] >= 0)
				{
					result[index++] = (byte) (curPos + i * W);
					if (board[curPos + i * W] > 0)
					{
						getPieceAtPos((byte) (curPos + i * W)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * S] != 100 && board[curPos + i * S] >= 0)
				{
					result[index++] = (byte) (curPos + i * S);
					if (board[curPos + i * S] > 0)
					{
						getPieceAtPos((byte) (curPos + i * S)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * E] != 100 && board[curPos + i * E] >= 0)
				{
					result[index++] = (byte) (curPos + i * E);
					if (board[curPos + i * E] > 0)
					{
						getPieceAtPos((byte) (curPos + i * E)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}
			result = compressArray(result, index);
			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.WKING):
		{
			result = new byte[8];
			if (board[curPos + E] <= 0)
			{
				result[index++] = (byte) (curPos + E);
				if (board[curPos + E] < 0)
					getPieceAtPos((byte) (curPos + E)).getEnemies().add(piece);
			}
			if (board[curPos + SE] <= 0)
			{
				result[index++] = (byte) (curPos + SE);
				if (board[curPos + SE] < 0)
					getPieceAtPos((byte) (curPos + SE)).getEnemies().add(piece);
			}
			if (board[curPos + S] <= 0)
			{
				result[index++] = (byte) (curPos + S);
				if (board[curPos + S] < 0)
					getPieceAtPos((byte) (curPos + S)).getEnemies().add(piece);
			}
			if (board[curPos + SW] <= 0)
			{
				result[index++] = (byte) (curPos + SW);
				if (board[curPos + SW] < 0)
					getPieceAtPos((byte) (curPos + SW)).getEnemies().add(piece);
			}
			if (board[curPos + W] <= 0)
			{
				result[index++] = (byte) (curPos + W);
				if (board[curPos + W] < 0)
					getPieceAtPos((byte) (curPos + W)).getEnemies().add(piece);
			}
			if (board[curPos + NW] <= 0)
			{
				result[index++] = (byte) (curPos + NW);
				if (board[curPos + NW] < 0)
					getPieceAtPos((byte) (curPos + NW)).getEnemies().add(piece);
			}
			if (board[curPos + N] <= 0)
			{
				result[index++] = (byte) (curPos + N);
				if (board[curPos + N] < 0)
					getPieceAtPos((byte) (curPos + N)).getEnemies().add(piece);
			}
			if (board[curPos + NE] <= 0)
			{
				result[index++] = (byte) (curPos + NE);
				if (board[curPos + NE] < 0)
					getPieceAtPos((byte) (curPos + NE)).getEnemies().add(piece);
			}
			if (Datalink.whiteCanCastleLeft)
			{
				if ((curPos == E1) && (board[D1] == 0) && (board[C1] == 0)
						&& (board[B1] == 0) && (board[A1] == Piece.WROOK))
					result[index++] = (byte) (curPos + W + W);
			}
			if (Datalink.whiteCanCastleRight)
			{
				if ((curPos == E1) && (board[F1] == 0) && (board[G1] == 0)
						&& (board[H1] == Piece.WROOK))
					result[index++] = (byte) (curPos + E + E);
			}
			result = compressArray(result, index);
			if (!evalMoves)
				break;
			else
				result = checkKingMoves(result, piece);
			break;
		}
		case (Piece.BKING):
		{
			result = new byte[8];
			if (board[curPos + E] >= 0 && board[curPos + E] != 100)
			{
				result[index++] = (byte) (curPos + E);
				if (board[curPos + E] > 0)
					getPieceAtPos((byte) (curPos + E)).getEnemies().add(piece);
			}
			if (board[curPos + SE] >= 0 && board[curPos + SE] != 100)
			{
				result[index++] = (byte) (curPos + SE);
				if (board[curPos + SE] > 0)
					getPieceAtPos((byte) (curPos + SE)).getEnemies().add(piece);
			}
			if (board[curPos + S] >= 0 && board[curPos + S] != 100)
			{
				result[index++] = (byte) (curPos + S);
				if (board[curPos + S] > 0)
					getPieceAtPos((byte) (curPos + S)).getEnemies().add(piece);
			}
			if (board[curPos + SW] >= 0 && board[curPos + SW] != 100)
			{
				result[index++] = (byte) (curPos + SW);
				if (board[curPos + SW] > 0)
					getPieceAtPos((byte) (curPos + SW)).getEnemies().add(piece);
			}
			if (board[curPos + W] >= 0 && board[curPos + W] != 100)
			{
				result[index++] = (byte) (curPos + W);
				if (board[curPos + W] > 0)
					getPieceAtPos((byte) (curPos + W)).getEnemies().add(piece);
			}
			if (board[curPos + NW] >= 0 && board[curPos + NW] != 100)
			{
				result[index++] = (byte) (curPos + NW);
				if (board[curPos + NW] > 0)
					getPieceAtPos((byte) (curPos + NW)).getEnemies().add(piece);
			}
			if (board[curPos + N] >= 0 && board[curPos + N] != 100)
			{
				result[index++] = (byte) (curPos + N);
				if (board[curPos + N] > 0)
					getPieceAtPos((byte) (curPos + N)).getEnemies().add(piece);
			}
			if (board[curPos + NE] >= 0 && board[curPos + NE] != 100)
			{
				result[index++] = (byte) (curPos + NE);
				if (board[curPos + NE] > 0)
					getPieceAtPos((byte) (curPos + NE)).getEnemies().add(piece);
			}
			if (Datalink.blackCanCastleLeft)
			{
				if ((curPos == E8) && (board[D8] == 0) && (board[C8] == 0)
						&& (board[B8] == 0) && (board[A8] == Piece.BROOK))
					result[index++] = (byte) (curPos + W + W);
			}
			if (Datalink.blackCanCastleRight)
			{
				if ((curPos == E8) && (board[F8] == 0) && (board[G8] == 0)
						&& (board[H8] == Piece.BROOK))
					result[index++] = (byte) (curPos + E + E);
			}
			result = compressArray(result, index);
			if (!evalMoves)
				break;
			else
				result = checkKingMoves(result, piece);
			break;
		}
		case (Piece.WQUEEN):
		{
			byte i;
			result = new byte[28];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * N] <= 0)
				{
					result[index++] = (byte) (curPos + i * N);
					if (board[curPos + i * N] < 0)
					{
						getPieceAtPos((byte) (curPos + i * N)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * W] <= 0)
				{
					result[index++] = (byte) (curPos + i * W);
					if (board[curPos + i * W] < 0)
					{
						getPieceAtPos((byte) (curPos + i * W)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * S] <= 0)
				{
					result[index++] = (byte) (curPos + i * S);
					if (board[curPos + i * S] < 0)
					{
						getPieceAtPos((byte) (curPos + i * S)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * E] <= 0)
				{
					result[index++] = (byte) (curPos + i * E);
					if (board[curPos + i * E] < 0)
					{
						getPieceAtPos((byte) (curPos + i * E)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NE] <= 0)
				{
					result[index++] = (byte) (curPos + i * NE);
					if (board[curPos + i * NE] < 0)
					{
						getPieceAtPos((byte) (curPos + i * NE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NW] <= 0)
				{
					result[index++] = (byte) (curPos + i * NW);
					if (board[curPos + i * NW] < 0)
					{
						getPieceAtPos((byte) (curPos + i * NW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SW] <= 0)
				{
					result[index++] = (byte) (curPos + i * SW);
					if (board[curPos + i * SW] < 0)
					{
						getPieceAtPos((byte) (curPos + i * SW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SE] <= 0)
				{
					result[index++] = (byte) (curPos + i * SE);
					if (board[curPos + i * SE] < 0)
					{
						getPieceAtPos((byte) (curPos + i * SE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}
			result = compressArray(result, index);

			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		case (Piece.BQUEEN):
		{
			byte i;
			result = new byte[28];

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * N] != 100 && board[curPos + i * N] >= 0)
				{
					result[index++] = (byte) (curPos + i * N);
					if (board[curPos + i * N] > 0)
					{
						getPieceAtPos((byte) (curPos + i * N)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * W] != 100 && board[curPos + i * W] >= 0)
				{
					result[index++] = (byte) (curPos + i * W);
					if (board[curPos + i * W] > 0)
					{
						getPieceAtPos((byte) (curPos + i * W)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * S] != 100 && board[curPos + i * S] >= 0)
				{
					result[index++] = (byte) (curPos + i * S);
					if (board[curPos + i * S] > 0)
					{
						getPieceAtPos((byte) (curPos + i * S)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * E] != 100 && board[curPos + i * E] >= 0)
				{
					result[index++] = (byte) (curPos + i * E);
					if (board[curPos + i * E] > 0)
					{
						getPieceAtPos((byte) (curPos + i * E)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NE] != 100
						&& board[curPos + i * NE] >= 0)
				{
					result[index++] = (byte) (curPos + i * NE);
					if (board[curPos + i * NE] > 0)
					{
						getPieceAtPos((byte) (curPos + i * NE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * NW] != 100
						&& board[curPos + i * NW] >= 0)
				{
					result[index++] = (byte) (curPos + i * NW);
					if (board[curPos + i * NW] > 0)
					{
						getPieceAtPos((byte) (curPos + i * NW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SW] != 100
						&& board[curPos + i * SW] >= 0)
				{
					result[index++] = (byte) (curPos + i * SW);
					if (board[curPos + i * SW] > 0)
					{
						getPieceAtPos((byte) (curPos + i * SW)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}

			for (i = 1; i <= 7; i++)
			{
				if (board[curPos + i * SE] != 100
						&& board[curPos + i * SE] >= 0)
				{
					result[index++] = (byte) (curPos + i * SE);
					if (board[curPos + i * SE] > 0)
					{
						getPieceAtPos((byte) (curPos + i * SE)).getEnemies()
								.add(piece);
						break;
					}
				} else
					break;
			}
			result = compressArray(result, index);
			if (!evalMoves)
				break;
			else
			{
				result = checkMoves(piece, result);
			}
			break;
		}
		default:
		{
			result = new byte[]
			{ D5 };
			break;
		}
		} // end switch

		return result;
	}

	private byte[] checkMoves(Piece piece, byte[] moves)
	{
		byte[] result = new byte[moves.length];
		byte index = 0;

		if (piece.getType() > 0) // Hvid
		{
			if (Datalink.whiteKingCheck)
				return protectKingMoves(piece, moves);
			else
			{
				Piece tempPiece = pieceProtectsKingFrom(piece);
				if (tempPiece != null)
				{
					// Hvis den beskytter kongen fra kun 1 brik,
					// kan den muligvis rykke imod denne brik. Unders�ges her

					// Vi snyder og fjerner brikken
					board[piece.getPos()] = 0;
					tempPiece.setLegalMoves(optimizeMovePathToKing(tempPiece));
					for (byte tempPieceMove : tempPiece.getLegalMoves())
					{
						for (byte pieceMove : moves)
						{
							if (pieceMove == tempPieceMove)
								result[index++] = pieceMove;
						}
					}
					board[piece.getPos()] = piece.getType();

					tempPiece.setLegalMoves(getLegalMoves(tempPiece, false));
					result = compressArray(result, index);
					return result;
				} else
					return moves;
			}
		} else
		// Sort
		{
			if (Datalink.blackKingCheck)
				return protectKingMoves(piece, moves);
			else
			{
				Piece tempPiece = pieceProtectsKingFrom(piece);
				if (tempPiece != null)
				{
					// Hvis den beskytter kongen fra kun 1 brik,
					// kan den muligvis rykke imod denne brik. Unders�ges her

					// Vi snyder og fjerner brikken
					board[piece.getPos()] = 0;
					tempPiece.setLegalMoves(optimizeMovePathToKing(tempPiece));
					for (byte tempPieceMove : tempPiece.getLegalMoves())
					{
						for (byte pieceMove : moves)
						{
							if (pieceMove == tempPieceMove)
								result[index++] = pieceMove;
						}
					}
					board[piece.getPos()] = piece.getType();

					tempPiece.setLegalMoves(getLegalMoves(tempPiece, false));
					result = compressArray(result, index);
					return result;
				} else
					return moves;
			}
		}
	}

	/**
	 * Unders�ger om brikken kan beskytte sin konge, hvis kongen er i skak
	 * 
	 * @param moves
	 *            de ryk som brikken kan foretage
	 * @return de ryk som brikken kan foretage for at redde kongen
	 */
	private byte[] protectKingMoves(Piece piece, byte[] moves)
	{
		byte[] result = new byte[moves.length];
		byte index = 0;

		if (pieceProtectsKingFrom(piece) == null)
		{
			// Det skal tjekkes, om brikken kan blokere for den brik, som har
			// sat kongen i skak, eller eventuelt tage brikken
			checkPiece.setLegalMoves(optimizeMovePathToKing(checkPiece));
			for (byte move : moves)
			{
				for (byte checkPieceMove : checkPiece.getLegalMoves())
				{
					if (move == checkPieceMove && move != checkPiece.getPos())
					{
						result[index++] = move;
					}
				}
				if (move == checkPiece.getPos())
					result[index++] = move;
			}
			checkPiece.setLegalMoves(getLegalMoves(checkPiece, false));
		} else
		{
			return new byte[0];
		}

		result = compressArray(result, index);
		return result;
	}

	/**
	 * Metoden bestemmer, hvor kongen kan rykke hen, hvis den st�r i skak
	 * 
	 * @param kingMoves
	 *            De ryk kongen har beregnet, at den kan foretage
	 * @return De ryk kongen rent faktisk kan foretage
	 */
	private byte[] checkKingMoves(byte[] kingMoves, Piece kingPiece)
	{
		byte[] result = new byte[kingMoves.length];
		byte index = 0;

		boolean illegalMove = false;

		// Vi skal unders�ge for alle andre brikker, at vi ikke blot bringer
		// kongen i en ny skaksituation
		if (kingPiece.getType() > 0)
		{
			for (byte kingMove : kingMoves)
			{
				illegalMove = false;
				for (Piece piece : getPiecesOfColor(Vars.BLACK))
				{
					if (kingMove == piece.getPos())
					{
						if (pieceIsProtected(piece))
						{
							illegalMove = true;
							break;
						}
					}
					for (byte pieceMove : piece.getLegalMoves())
					{
						if (kingMove == pieceMove)
						{
							if (piece.getType() == Piece.BPAWN)
								illegalMove = false;
							else
								illegalMove = true;
							break;
						}
					}
					if (illegalMove == true)
						break;
				}

				// if (kingMove == kingPiece.getPos() + N)
				// System.out.println("Fors�ger ulovligt ryk" + kingMove +
				// illegalMove);

				// Sidste tjek er for at sikre,
				// at den ikke ligger i retning af den fjentlige brik
				if (!illegalMove && Datalink.whiteKingCheck
						&& checkPiece.getType() != Piece.BPAWN)
				{
					checkPiece.setLegalMoves(optimizeMovePathToKing(checkPiece));
					for (byte checkPieceMove : checkPiece.getLegalMoves())
					{
						if (kingPiece.getPos() + N == checkPieceMove
								|| kingPiece.getPos() + N == checkPiece.getPos())
						{
							if (kingMove == kingPiece.getPos() + S)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + NE == checkPieceMove
								|| kingPiece.getPos() + NE == checkPiece.getPos())
						{
							if (kingMove == kingPiece.getPos() + SW)
							{
								illegalMove = true;
								break;
							}
						} 
						else if (kingPiece.getPos() + E == checkPieceMove
								|| kingPiece.getPos() + E == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + W)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + SE == checkPieceMove
								|| kingPiece.getPos() + SE == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + NW)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + S == checkPieceMove
								|| kingPiece.getPos() + S == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + N)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + SW == checkPieceMove
								|| kingPiece.getPos() + SW == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + NE)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + W == checkPieceMove
								|| kingPiece.getPos() + W == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + E)
							{
								illegalMove = true;
								break;
							}
						}
						else if (kingPiece.getPos() + NW == checkPieceMove
								|| kingPiece.getPos() + NW == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + SE)
							{
								illegalMove = true;
								break;
							}
						}
					}
					checkPiece.setLegalMoves(getLegalMoves(checkPiece, false));
				}

				// Tjekker om tr�kket f�rer hen til nogle bonder
				// - disse har nemlig ikke skr� ryk som legalmove normalt
				if (!illegalMove)
				{
					if (board[kingMove + NE] == Piece.BPAWN
							|| board[kingMove + NW] == Piece.BPAWN)
						illegalMove = true;
				}

				if (!illegalMove)
					result[index++] = kingMove;
			}
		} else
		{
			for (byte kingMove : kingMoves)
			{
				illegalMove = false;
				for (Piece piece : getPiecesOfColor(Vars.WHITE))
				{
					if (kingMove == piece.getPos())
					{
						if (pieceIsProtected(piece))
						{
							illegalMove = true;
							break;
						}
					}
					for (byte pieceMove : piece.getLegalMoves())
					{
						if (kingMove == pieceMove)
						{
							if (piece.getType() == Piece.WPAWN)
								illegalMove = false;
							else
								illegalMove = true;
							break;
						}
					}
					if (illegalMove == true)
						break;
				}

				// Sidste tjek er for at sikre,
				// at den ikke ligger i retning af den fjentlige brik
				if (!illegalMove && Datalink.blackKingCheck
						&& checkPiece.getType() != Piece.WPAWN)
				{
					checkPiece
							.setLegalMoves(optimizeMovePathToKing(checkPiece));
					for (byte checkPieceMove : checkPiece.getLegalMoves())
					{
						if (kingPiece.getPos() + N == checkPieceMove
								|| kingPiece.getPos() + N == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + S)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + NE == checkPieceMove
								|| kingPiece.getPos() + NE == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + SW)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + E == checkPieceMove
								|| kingPiece.getPos() + E == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + W)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + SE == checkPieceMove
								|| kingPiece.getPos() + SE == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + NW)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + S == checkPieceMove
								|| kingPiece.getPos() + S == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + N)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + SW == checkPieceMove
								|| kingPiece.getPos() + SW == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + NE)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + W == checkPieceMove
								|| kingPiece.getPos() + W == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + E)
							{
								illegalMove = true;
								break;
							}
						} else if (kingPiece.getPos() + NW == checkPieceMove
								|| kingPiece.getPos() + NW == checkPiece
										.getPos())
						{
							if (kingMove == kingPiece.getPos() + SE)
							{
								illegalMove = true;
								break;
							}
						}
					}
					checkPiece.setLegalMoves(getLegalMoves(checkPiece, false));
				}

				// Tjekker om tr�kket f�rer hen til nogle bonder
				// - disse har nemlig ikke skr� ryk som legalmove normalt
				if (!illegalMove)
				{
					if (board[kingMove + SE] == Piece.WPAWN
							|| board[kingMove + SW] == Piece.WPAWN)
						illegalMove = true;
				}

				if (!illegalMove)
					result[index++] = kingMove;
			}
		}

		result = compressArray(result, index);
		return result;
	}

	/**
	 * Unders�ger om en brik er beskyttet af sine egne. Dette bruges til at
	 * finde ud af, om kongen m� flytte derhen
	 * 
	 * @param piece
	 *            Den brik, der unders�ges
	 * @return true hvis den er beskyttet ellers false
	 */
	private boolean pieceIsProtected(Piece piece)
	{
		// Vi snyder og fjerner brikken midlertidigt for at lette beregningerne
		board[piece.getPos()] = 0;

		if (piece.getType() > 0)
		{
			for (Piece tempPiece : getPiecesOfColor(Vars.WHITE))
			{
				// Der skal selvf�lgelig ikke unders�ges, om den beskytter sig
				// selv.
				if (tempPiece.equals(piece))
					continue;
				for (byte tempPieceMove : getLegalMoves(tempPiece, false))
				{
					if (tempPieceMove == piece.getPos())
					{
						board[piece.getPos()] = piece.getType();
						return true;
					}
				}
			}
		} else
		{
			for (Piece tempPiece : getPiecesOfColor(Vars.BLACK))
			{
				if (tempPiece.equals(piece))
					continue;
				for (byte tempPieceMove : getLegalMoves(tempPiece, false))
				{
					if (tempPieceMove == piece.getPos())
					{
						board[piece.getPos()] = piece.getType();
						return true;
					}
				}
			}
		}
		board[piece.getPos()] = piece.getType();
		return false;
	}

	/**
	 * Unders�ger om en brik kan rykke v�k fra sin plads.
	 * 
	 * Dette er f.eks. ikke tilf�ldet, hvis den beskytter kongen.
	 * 
	 * @param piece
	 *            Den brik, der under�sges
	 * @return true hvis den beskytter kongen, ellers false
	 */
	private Piece pieceProtectsKingFrom(Piece piece)
	{
		// Vi snyder og fjerner brikken midlertidigt for at lette beregningerne
		board[piece.getPos()] = 0;

		Piece result = null;

		// Hvis brikken er hvid
		if (piece.getType() > 0)
		{
			// K�rer igennem alle modstanderens brikker
			for (Piece tempPiece : piece.getEnemies())
			{
				// Vi skal ikke medregne, hvis en brik har sat kongen i skak
				if (tempPiece.equals(checkPiece))
					continue;

				// K�rer igennem alle ryk for modstanderens brik
				for (byte tempPieceMove : getLegalMoves(tempPiece, false))
				{
					// Hvis modstanderen kan ramme kongen,
					// beskytter brikken kongen
					if (board[tempPieceMove] == Piece.WKING)
					{
						result = tempPiece;
						break;
					}
				}
				if (result != null)
					break;
			}
		} else
		// sort brik
		{
			for (Piece tempPiece : piece.getEnemies())
			{
				// Vi skal ikke medregne, hvis en brik har sat kongen i skak
				if (tempPiece.equals(checkPiece))
					continue;

				// K�rer igennem alle ryk for modstanderens brik
				for (byte tempPieceMove : getLegalMoves(tempPiece, false))
				{
					// Hvis modstanderen kan ramme kongen,
					// beskytter brikken kongen
					if (board[tempPieceMove] == Piece.BKING)
					{
						result = tempPiece;
						break;
					}
				}

				if (result != null)
					break;
			}
		}
		board[piece.getPos()] = piece.getType();
		return result;
	}

	/**
	 * Optimerer et array, s� kun det n�dvendige kommer med
	 * 
	 * @param array
	 *            det array, der skal optimeres
	 * @param index
	 *            st�rrelsen af det nye array
	 * @return et nyt array, som er trimmet
	 */
	private byte[] compressArray(byte[] array, int index)
	{
		byte[] res = new byte[index];
		for (byte i = 0; i < index; i++)
		{
			res[i] = array[i];
		}
		return res;
	}
	
	private byte[] copyArray(byte[] array)
	{
		byte[] res = new byte[array.length];
		byte index = 0;
		for (byte content : array)
			res[index++] = content;
		return res;
	}
	
	private boolean arrayContains(byte[] array, byte searchFor)
	{
		for (byte i = 0; i < array.length; i++)
		{
			if (array[i] == searchFor)
				return true;
		}
		return false;
	}

	/**
	 * Fortryder et givent ryk
	 * 
	 * @param move
	 *            Det ryk, der skal fortrydes
	 */
	public void undoMove(Move move)
	{
		/*
		 * F�rst h�ndteres special tr�kkene.
		 */
		// Hvis en bonde er blevet promoted
		if (move.promotion)
		{
			// Hvis bonden har taget en brik p� vej op mod sin promotion
			if (move.takenPiece != null)
			{
				if (move.movedPiece.getType() > 0)
				{
					removePiece(move.movedPiece);
					move.movedPiece.setType(Piece.WPAWN);
					move.movedPiece.setPos(move.from);
					board[move.from] = Piece.WPAWN;
					board[move.to] = move.takenPiece.getType();
					addPiece(move.movedPiece);
					addPiece(move.takenPiece);
				}
				else
				{
					removePiece(move.movedPiece);
					move.movedPiece.setType(Piece.BPAWN);
					move.movedPiece.setPos(move.from);
					board[move.from] = Piece.BPAWN;
					board[move.to] = move.takenPiece.getType();
					addPiece(move.movedPiece);
					addPiece(move.takenPiece);
				}
			} else
			// Hvis bonden blot rykkede lige frem til sin promotion
			{
				if (move.movedPiece.getType() > 0)
				{
					removePiece(move.movedPiece);
					move.movedPiece.setType(Piece.WPAWN);
					move.movedPiece.setPos(move.from);
					board[move.from] = Piece.WPAWN;
					addPiece(move.movedPiece);
				} else
				{
					removePiece(move.movedPiece);
					move.movedPiece.setType(Piece.BPAWN);
					move.movedPiece.setPos(move.from);
					board[move.from] = Piece.BPAWN;
					addPiece(move.movedPiece);
				}
			}
		} // end if promotion
		else if (move.castledLeft) // Hvis rykket var en castling til venstre
		{
			if (move.movedPiece.getType() > 0)
			{
				move.movedPiece.setPos(E1);
				getPieceAtPos(D1).setPos(A1);
				board[E1] = Piece.WKING;
				board[A1] = Piece.WROOK;
				board[C1] = 0;
				board[D1] = 0;
				Datalink.whiteCanCastleLeft = true;
				Datalink.whiteHasCastled = false;
			}
			else
			{
				move.movedPiece.setPos(E8);
				getPieceAtPos(D8).setPos(A8);
				board[E8] = Piece.BKING;
				board[A8] = Piece.BROOK;
				board[C8] = 0;
				board[D8] = 0;
				Datalink.blackCanCastleLeft = true;
				Datalink.blackHasCastled = false;
			}
		} // end if castledleft
		else if (move.castledRight) // hvis rykket var en castling til h�jre
		{
			if (move.movedPiece.getType() > 0)
			{
				move.movedPiece.setPos(E1);
				getPieceAtPos(F1).setPos(H1);
				board[E1] = Piece.WKING;
				board[H1] = Piece.WROOK;
				board[G1] = 0;
				board[F1] = 0;
				Datalink.whiteCanCastleRight = true;
				Datalink.whiteCanCastleLeft = true;
			}
			else
			{
				move.movedPiece.setPos(E8);
				getPieceAtPos(F8).setPos(H8);
				board[E8] = Piece.BKING;
				board[H8] = Piece.BROOK;
				board[G8] = 0;
				board[F8] = 0;
				Datalink.blackCanCastleRight = true;
				Datalink.blackHasCastled = false;
			}
		} // end if castledright
		else
		{
			if (move.takenPiece == null)
			{
				move.movedPiece.setPos(move.from);
				board[move.from] = move.movedPiece.getType();
				board[move.to] = 0;
			}
			else
			{
				move.movedPiece.setPos(move.from);
				board[move.from] = move.movedPiece.getType();
				board[move.to] = 0;
				board[move.takenPiece.getPos()] = move.takenPiece.getType();
				addPiece(move.takenPiece);
			}
		}

		if (move.movedPiece.getType() > 0)
			Datalink.currentPlayer = Vars.WHITE;
		else
			Datalink.currentPlayer = Vars.BLACK;

		if (move.checkedKing)
			if (move.movedPiece.getType() > 0)
				Datalink.blackKingCheck = false;
			else
				Datalink.whiteKingCheck = false;

		if (move.checkMatedKing)
			if (move.movedPiece.getType() > 0)
				Datalink.blackKingCheckMate = false;
			else
				Datalink.whiteKingCheckMate = false;

		if (move.gameDrawed)
			Datalink.gameDraw = false;

		move.movedPiece.decreaseNumMoves();

		// Hvis et tr�k med kongen eller t�rnet fortrydes,
		// skal det tjekkes, om det er muligt at lave castling
		if (move.movedPiece.getType() == Piece.WKING
				&& move.movedPiece.getNumMoves() == 0
				&& Datalink.gameType == NORMAL_CHESS)
		{
			for (Piece rook : piecesOfType.get(Piece.WROOK))
			{
				if (rook.getNumMoves() == 0 && rook.getPos() == H1)
					Datalink.whiteCanCastleRight = true;
				else if (rook.getNumMoves() == 0 && rook.getPos() == A1)
					Datalink.whiteCanCastleLeft = true;
			}
		} else if (move.movedPiece.getType() == Piece.BKING
				&& move.movedPiece.getNumMoves() == 0
				&& Datalink.gameType == NORMAL_CHESS)
		{
			for (Piece rook : piecesOfType.get(Piece.BROOK))
			{
				if (rook.getNumMoves() == 0 && rook.getPos() == H8)
					Datalink.blackCanCastleRight = true;
				else if (rook.getNumMoves() == 0 && rook.getPos() == A8)
					Datalink.blackCanCastleLeft = true;
			}
		} else if (move.movedPiece.getType() == Piece.WROOK
				&& move.movedPiece.getNumMoves() == 0
				&& piecesOfType.get(Piece.WKING).get(0).getNumMoves() == 0)
		{
			if (move.from == H1)
				Datalink.whiteCanCastleRight = true;
			else
				Datalink.whiteCanCastleLeft = true;
		} else if (move.movedPiece.getType() == Piece.BROOK
				&& move.movedPiece.getNumMoves() == 0
				&& piecesOfType.get(Piece.BKING).get(0).getNumMoves() == 0)
		{
			if (move.from == H8)
				Datalink.blackCanCastleRight = true;
			else
				Datalink.blackCanCastleLeft = true;
		}

		if (move.kingWasChecked)
		{
			checkPiece = move.checkPiece;
			if (move.movedPiece.getType() > 0)
				Datalink.whiteKingCheck = true;
			else
				Datalink.blackKingCheck = true;
		}

		fiftyMoveRuleCounter = move.fiftyMoveCounter;
		fiftyMoveRuleUsed = false;
		
		for (Piece piece : move.pieceMoves.keySet())
		{
			piece.setLegalMoves(move.pieceMoves.get(piece));
		}
	}

	/**
	 * Fortryder det sidste ryk.
	 * 
	 * @return Det ryk, der bliver fortrudt
	 */
	public Move undoLastMove()
	{
		Move res = null;
		if (!moveHistory.isEmpty())
		{
			if (Datalink.justMovedOpening)
				if (Datalink.currentPlayer == Vars.WHITE)
					Datalink.openingMoveNumberWhite--;
				else
					Datalink.openingMoveNumberBlack--;

			res = moveHistory.removeLast();
			undoMove(res);
		}
		return res;
	}

	public Piece getCheckPiece()
	{
		return checkPiece;
	}

	public Move getLastMove()
	{
		Move res = null;
		if (!moveHistory.isEmpty())
			res = moveHistory.getLast();

		return res;
	}

	public LinkedList<Move> getMoveHistory()
	{
		return moveHistory;
	}

	public static final byte A1 = 21;
	public static final byte B1 = 22;
	public static final byte C1 = 23;
	public static final byte D1 = 24;
	public static final byte E1 = 25;
	public static final byte F1 = 26;
	public static final byte G1 = 27;
	public static final byte H1 = 28;
	
	public static final byte A2 = 31;
	public static final byte B2 = 32;
	public static final byte C2 = 33;
	public static final byte D2 = 34;
	public static final byte E2 = 35;
	public static final byte F2 = 36;
	public static final byte G2 = 37;
	public static final byte H2 = 38;

	public static final byte A3 = 41;
	public static final byte B3 = 42;
	public static final byte C3 = 43;
	public static final byte D3 = 44;
	public static final byte E3 = 45;
	public static final byte F3 = 46;
	public static final byte G3 = 47;
	public static final byte H3 = 48;

	public static final byte A4 = 51;
	public static final byte B4 = 52;
	public static final byte C4 = 53;
	public static final byte D4 = 54;
	public static final byte E4 = 55;
	public static final byte F4 = 56;
	public static final byte G4 = 57;
	public static final byte H4 = 58;
	
	public static final byte A5 = 61;
	public static final byte B5 = 62;
	public static final byte C5 = 63;
	public static final byte D5 = 64;
	public static final byte E5 = 65;
	public static final byte F5 = 66;
	public static final byte G5 = 67;
	public static final byte H5 = 68;

	public static final byte A6 = 71;
	public static final byte B6 = 72;
	public static final byte C6 = 73;
	public static final byte D6 = 74;
	public static final byte E6 = 75;
	public static final byte F6 = 76;
	public static final byte G6 = 77;
	public static final byte H6 = 78;
	
	public static final byte A7 = 81;
	public static final byte B7 = 82;
	public static final byte C7 = 83;
	public static final byte D7 = 84;
	public static final byte E7 = 85;
	public static final byte F7 = 86;
	public static final byte G7 = 87;
	public static final byte H7 = 88;
	
	public static final byte A8 = 91;
	public static final byte B8 = 92;
	public static final byte C8 = 93;
	public static final byte D8 = 94;
	public static final byte E8 = 95;
	public static final byte F8 = 96;
	public static final byte G8 = 97;
	public static final byte H8 = 98;

	// Ryk
	public static final byte N = 10;
	public static final byte NE = 11;
	public static final byte E = 1;
	public static final byte SE = -9;
	public static final byte S = -10;
	public static final byte SW = -11;
	public static final byte W = -1;
	public static final byte NW = 9;
	public static final byte[] DIRECTIONS = {N,NE,E,SE,S,SW,W,NW};

	// Skaktyper
	public static final int NORMAL_CHESS = 0;
	public static final int PAWN_CHESS = 1;
	public static final int PATT_SCHACH_CHESS = 2; // Patt-Schach
	public static final int PAWNS_GAME_CHESS = 3;
	public static final int UPSIDE_DOWN_CHESS = 4; // upside-down chess

	/**
	 * Printer skakbr�ttet. Bruges til debugging
	 */
	public void printBoard()
	{
		for (int i = 11; i >= 0; i--)
		{
			for (int j = 0; j <= 9; j++)
			{
				// System.out.print("" + board[i * 10 + j] + " ");
				System.out.printf("%3d ", board[i * 10 + j]);
			}
			System.out.println();
		}
	}

	public void runStartTests()
	{
		System.out
				.println("***************************************************");
		System.out.println("Kører test på board");
		System.out
				.println("***************************************************");

		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			updateAllLegalMovesForColor(Vars.WHITE, false);
		}
		long stop = System.currentTimeMillis();
		System.out
				.println("Tid for 100.000 ryk beregninger for alle hvide brikker - uden evaluering: "
						+ (stop - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			updateAllLegalMovesForColor(Vars.WHITE, true);
		}
		stop = System.currentTimeMillis();
		System.out
				.println("Tid for 100.000 ryk beregninger for alle hvide brikker - med evaluering: "
						+ (stop - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			byte[] test = new byte[10];
			byte tal = 5;
			byte j;
			for (j = 0; j < 6; j++)
				test[j] = tal++;
			test = compressArray(test, j);
		}
		stop = System.currentTimeMillis();
		System.out
				.println("Tid for 100.000 oprettelser af array samt compressarray kørsler "
						+ (stop - start));

		Piece temp = getPieceAtPos(G8);
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			getLegalMoves(temp, false);
			checkMoves(temp, temp.getLegalMoves());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 checkMoves kørsler "
				+ (stop - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			pieceIsProtected(temp);
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 pieceIsProtected kørsler "
				+ (stop - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			pieceProtectsKingFrom(temp);
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 pieceProtecstKingFrom kørsler "
				+ (stop - start));
	}

	public void runInGameTests()
	{
		long start;
		long stop;
		System.out
				.println("***************************************************");
		System.out.println("Kører spiltests på board");
		System.out
				.println("***************************************************");

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			updateAllLegalMovesForColor(Vars.WHITE, false);
		}
		stop = System.currentTimeMillis();
		System.out
				.println("Tid for 100.000 ryk beregninger for alle hvide brikker - uden evaluering: "
						+ (stop - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			updateAllLegalMovesForColor(Vars.WHITE, true);
		}
		stop = System.currentTimeMillis();
		System.out
				.println("Tid for 100.000 ryk beregninger for alle hvide brikker - med evaluering: "
						+ (stop - start));

		if (Datalink.whiteKingCheck || Datalink.blackKingCheck)
		{
			checkPiece.setLegalMoves(getLegalMoves(checkPiece, false));
			start = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++)
			{
				optimizeMovePathToKing(checkPiece);
			}
			stop = System.currentTimeMillis();
			System.out.println("Tid for 100.000 optimizeMovePath: "
					+ (stop - start));

			if (checkPiece.getType() > 0)
			{
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					checkKingStatus(Vars.BLACK);
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 checkKingStatus: "
						+ (stop - start));

				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					cannotMove(Vars.BLACK);
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 cannotMove: "
						+ (stop - start));

				Piece p = getPiecesOfType(Piece.BPAWN).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - pawn: "
						+ (stop - start));

				p = getPiecesOfType(Piece.BBISHOP).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out
						.println("Tid for 100.000 protectKingMoves - bishop: "
								+ (stop - start));

				p = getPiecesOfType(Piece.BKNIGHT).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out
						.println("Tid for 100.000 protectKingMoves - knight: "
								+ (stop - start));

				p = getPiecesOfType(Piece.BROOK).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - rook: "
						+ (stop - start));

				p = getPiecesOfType(Piece.BQUEEN).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - queen: "
						+ (stop - start));
			} else
			{
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					checkKingStatus(Vars.WHITE);
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 checkKingStatus: "
						+ (stop - start));

				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					cannotMove(Vars.WHITE);
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 cannotMove: "
						+ (stop - start));

				Piece p = getPiecesOfType(Piece.WPAWN).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - pawn: "
						+ (stop - start));

				p = getPiecesOfType(Piece.WBISHOP).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out
						.println("Tid for 100.000 protectKingMoves - bishop: "
								+ (stop - start));

				p = getPiecesOfType(Piece.WKNIGHT).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out
						.println("Tid for 100.000 protectKingMoves - knight: "
								+ (stop - start));

				p = getPiecesOfType(Piece.WROOK).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - rook: "
						+ (stop - start));

				p = getPiecesOfType(Piece.WQUEEN).get(0);
				p.setLegalMoves(getLegalMoves(p, false));
				start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++)
				{
					protectKingMoves(p, p.getLegalMoves());
				}
				stop = System.currentTimeMillis();
				System.out.println("Tid for 100.000 protectKingMoves - queen: "
						+ (stop - start));
			}
		}
	}
}