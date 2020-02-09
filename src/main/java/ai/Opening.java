/*
 * Opening.java
 * �bning. Indeholder arraylists med ryk, der skal foretages i en �bning
 * En funktion der parser en txt-fil og gemmer �bninger fra den.
 */

package ai;

import java.io.*;
import java.util.*;
import engine.*;

public class Opening implements Serializable
{
	public ArrayList<String> white;
	public ArrayList<String> black;
	public String name;
	Board b;
	// hvor hhv hvid og sort skal rykke fra og til.
	// brikken bestemmes med kald til getPieceAtPos fra board.
	public ArrayList<Byte> fromWhite; // hvids arrays
	public ArrayList<Byte> toWhite;
	public ArrayList<Byte> fromBlack;
	public ArrayList<Byte> toBlack;

	public Opening(Board board) {
		white = new ArrayList<String>();
		black = new ArrayList<String>();
		b = board;
	}

	/**
	 * Konverterer de tekststrenge der er fundet i parseOpenings til byte-v�rdier, der kan bruges til at flytte brikkerne.
	 * Den kan kun forst� simple tr�k s� som e2-e4, og alts� ikke ting som castling. Dette kunne v�re en mulig forbedring. 
	 */
	public void stringToMove() {
		int j = 0, k = 0;
		for (int i = 0; i < white.size() + black.size(); i++) {
			String move;
			if (i % 2 == 0) {
				move = white.get(j);
				j++;
			} else {
				move = black.get(k);
				k++;
			}

			char[] c = move.toCharArray();
			if (Character.isUpperCase(c[0])) {
				int rankFrom = Integer.parseInt(Character.toString(c[2])) + 1;
				int fileFrom = (int) c[1] - 96; // a = 97 i ascii

				int rankTo = Integer.parseInt(Character.toString(c[5])) + 1;
				int fileTo = (int) c[4] - 96; // a = 97 i ascii

				if (i % 2 == 0) { // white
					fromWhite.add(Byte.parseByte("" + rankFrom + fileFrom));
					toWhite.add(Byte.parseByte("" + rankTo + fileTo));
				} else {
					fromBlack.add(Byte.parseByte("" + rankFrom + fileFrom));
					toBlack.add(Byte.parseByte("" + rankTo + fileTo));
				}
			} else {
				int rankFrom = Integer.parseInt(Character.toString(c[1])) + 1;
				int fileFrom = (int) c[0] - 96; // a = 97 i ascii

				int rankTo = Integer.parseInt(Character.toString(c[4])) + 1;
				int fileTo = (int) c[3] - 96; // a = 97 i ascii

				if (i % 2 == 0) { // white
					fromWhite.add(Byte.parseByte("" + rankFrom + fileFrom));
					toWhite.add(Byte.parseByte("" + rankTo + fileTo));
				} else {
					fromBlack.add(Byte.parseByte("" + rankFrom + fileFrom));
					toBlack.add(Byte.parseByte("" + rankTo + fileTo));
				}
			}
		}
	}

	public String toString() {
		String s;
		s = name + "\n";
		s += "White moves: ";
		for (int i = 0; i < fromWhite.size(); i++)
			s += fromWhite.get(i) + " -> " + toWhite.get(i) + "\n";
		s += "Black moves: ";
		for (int i = 0; i < fromBlack.size(); i++)
			s += fromBlack.get(i) + " -> " + toBlack.get(i) + "\n";
		return s;
	}

	/**
	 * L�ser linierne i en given fil, opretter en �bning for hver, og gemmer hver enkelt ord (separaret med whitespace) i et array.
	 * @param file Filnavn p� txt-fil med �bninger
	 * @param b Board fra engine-pakken.
	 * @return En liste med alle �bninger fra filen.
	 */
	public static ArrayList<Opening> parseOpenings(String file, Board b) {
		ArrayList<Opening> r = new ArrayList<Opening>();

		InputStreamReader input;
		try {
			input = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(file));

			BufferedReader bufRead = new BufferedReader(input);
			String line;

			while ((line = bufRead.readLine()) != null) {
				Opening t = new Opening(b);
				if (line.charAt(0) == '#') continue;
				line.trim();
				String arr[] = line.split(" ");
				t.fromWhite = new ArrayList<Byte>(); // bliver godt nok lidt st�rre end n�dvendigt
				t.toWhite = new ArrayList<Byte>();
				t.fromBlack = new ArrayList<Byte>();
				t.toBlack = new ArrayList<Byte>();
				t.name = arr[0].replace("_", " ");
				for (int i = 1; i < arr.length; i++) {
					if (i % 2 == 1)
						t.white.add(arr[i]);
					else
						t.black.add(arr[i]);
				}
				r.add(t);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}
}
