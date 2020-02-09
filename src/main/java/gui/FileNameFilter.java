package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileNameFilter extends FileFilter
{
	/**
	 * Tjekker om en fil har det rigtige format
	 * @return true hvis filen er en mappe eller har endelsen ".dcg". false i andre tilfælde
	 */
    public boolean accept(File f)
    {
        if(f.isDirectory())
            return true;
        return f.toString().endsWith(".dcg");
    }

   /**
    * Giver en tekstbeskrivelse af vores filformat
    * @return Beskrivelse af vores filformat
    */
    public String getDescription()
    {
        return "DAMN Chess Game (*.dcg)";
    }
}
