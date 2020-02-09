package common;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Globale variabler
 */
public class Vars
{
	// Programtitel
	public static final String APPTITLE = "DAMN Chess";
	
	// Farve
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	
	// Spilindstillinger
	public static int gameLengthInSeconds = 1800;
	public static int maxNumberOfMoves = 0; // 0 = uendelig!
	public static int aiThinkTimeInSeconds = 15;
	public static int aiTreeDepth = 6;
	public static long startTime = System.currentTimeMillis();
	public static String filename = "";
	
//	public final static int[] pieceValues = {100, 330, 330, 520, 980, 28800, 100}; // ligger i array for at kunne tilgå dem udfra brikkens type (pieceValues[p.getType()])
	public final static int[] pieceValues = {91, 425, 389, 556, 900, 24538, 89};
	public final static int pawnValue =  pieceValues[0];
	public final static int knightValue =  pieceValues[1];
	public final static int bishopValue =  pieceValues[2];
	public final static int rookValue =  pieceValues[3];
	public final static int queenValue =  pieceValues[4];
	public final static int kingValue =  pieceValues[5];
	public final static int checkValue =  pieceValues[6];
	
	public static boolean ctrlPressed = false;
	
	public static boolean debugMode = false;
	
	public static Image picBPawn;
	
	
	
}
