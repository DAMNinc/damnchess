package common;

import java.util.ArrayList;

import ai.EvalFunction;
import engine.*;

public class Testsuite
{
	private Datalink datalink;
	
	public Testsuite(Datalink datalink)
	{
		this.datalink = datalink;
	}
	
	public void runStartTests()
	{
		System.out.println("***************************************************");
		System.out.println("Kører globale test på skakbrættet");
		System.out.println("***************************************************");
		
		long start;
		long stop;
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			datalink.getBoardData().movePiece(datalink.getBoardData().getPieceAtPos(Board.G8), Board.F6);
			datalink.getBoardData().movePiece(datalink.getBoardData().getPieceAtPos(Board.F6), Board.G8);
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 200.000 ryk: " + (stop-start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			datalink.getBoardData().movePiece(datalink.getBoardData().getPieceAtPos(Board.G8), Board.F6);
			datalink.getBoardData().undoLastMove();
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 ryk og undo ryk: " + (stop-start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			datalink.getBoardData().undoLastMove();
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 undo ryk: " + (stop-start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			for (Piece piece : datalink.getBoardData().getPiecesOfColor(Vars.WHITE))
				datalink.getBoardData().getLegalMoves(piece);
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 ryk fetches for alle hvide brikker: " + (stop-start));		
		
		System.out.println("***************************************************");
		System.out.println("Kører test på evalueringer");
		System.out.println("***************************************************");
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			EvalFunction.eval(datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 100.000 evalueringer " + (stop-start));		
		
		start = System.currentTimeMillis();
		
		ArrayList<Piece> pieces = datalink.getBoardData().getPiecesOfType(Piece.WPAWN);
		Board b = datalink.getBoardData();
		
		for (int i = 0; i < 10000; i++)
		{
			for (int j = 0 ; j < pieces.size(); j++)
				EvalFunction.evalPawn(pieces.get(j), b);
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 pawneval " + (stop-start));
		
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
		{
			for (Piece p : datalink.getBoardData().getPiecesOfType(Piece.WKNIGHT))
				EvalFunction.evalKnight(p, datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 knighteval " + (stop-start));
		
		
		
		
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
		{
			for (Piece p : datalink.getBoardData().getPiecesOfType(Piece.WBISHOP))
				EvalFunction.evalBishop(p, datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 bishopeval " + (stop-start));
		
		
		
		
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
		{
			for (Piece p : datalink.getBoardData().getPiecesOfType(Piece.WROOK))
				EvalFunction.evalRook(p, datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 rookeval " + (stop-start));
		
		
		
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
		{
			for (Piece p : datalink.getBoardData().getPiecesOfType(Piece.WQUEEN))
				EvalFunction.evalQueen(p, datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 queeneval " + (stop-start));
		
		
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++)
		{
			for (Piece p : datalink.getBoardData().getPiecesOfType(Piece.WKING))
				EvalFunction.evalKing(p, datalink.getBoardData());
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for 10.000 kingeval " + (stop-start));
		
		start = System.currentTimeMillis();
		for(int i = 0; i < 1000000; i++){
			long v1 = System.currentTimeMillis();
			long v2 = System.currentTimeMillis();
			long res = v2-v1;
		}
		stop = System.currentTimeMillis();
		System.out.println("Tid for currentTimeMillis(): " + (stop-start));
	}
	
	public void runBoardTests()
	{
		datalink.getBoardData().runStartTests();
	}
}
