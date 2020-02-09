package gui;

import javax.swing.*;

import common.Datalink;
import common.Vars;

import java.awt.*;
import java.awt.event.*;
import java.beans.*; 
import java.util.ArrayList;

/**
 * Kan oprette 9 forskellige dialogbokse til kommunikation med brugeren.
 * Det er i disse bokse som brugeren indtaster oplysninger om projekter, aktiviteter og medarbejdere
 * Klassen kommunikere direkte med klassen Data, s� alle nyindtastede oplysninger gemmes med det samme
 * @author 	Niklas Boss, Andreas Jensen, Michael Thomassen, David Lebech
 * @version 1.0
 */

public class Promotion extends JDialog implements PropertyChangeListener 
{
    // Grafiske komponenter, der benyttes i dialogboksen
    private JOptionPane optionPane;
    private JList pieceList;
    private DefaultListModel listModel;
    private JLabel lblHeadline;
       
    // Indeholder de grafiske komponenter der skal vises i dialogboksen,
    // derfor ingen typeangivelse.
    private ArrayList<Object> array = new ArrayList();

	/**
	 * Opretter en dialogbox med en liste over de 4 brikker en bonde kan forfremmes til
     * @param aFrame Hvilket frame h�rer dialogboksen til
     * @param datalink Adgang til skakmotoren
	 */
    
    public Promotion(Frame aFrame)
    {
    	super(aFrame, true);
   
    	this.setLocation((int)(aFrame.getLocationOnScreen().getX()) + 150,(int)(aFrame.getLocationOnScreen().getY()) + 150);
    	array = new ArrayList<Object>();
      
    	setTitle("Forfremmelse"); 
		
		lblHeadline = new JLabel("Vælg ny brik:");
		lblHeadline.setFont(new Font("Arial", Font.BOLD, 13));
		
		listModel = new DefaultListModel();
		pieceList = new JList(listModel);
		pieceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pieceList.setVisibleRowCount(4);
		pieceList.setPreferredSize(new Dimension(10,pieceList.getHeight()));
		JScrollPane listScrollPane = new JScrollPane(pieceList);
		
		String[] navne = {"Springer", "Løber", "Tårn", "Dronning"};
		
		for (String navn : navne)
			listModel.addElement(navn);
			
		pieceList.setSelectedIndex(3);
		array.add(lblHeadline);
		array.add(listScrollPane);

        
        Object[] array2 = array.toArray();
        Object[] options = {"OK"};

        //Opret dialogboksen
        optionPane = new JOptionPane(array2,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.OK_OPTION,
                                    null,
                                    options,
                                    options[0]);

        setContentPane(optionPane);

        //Vinduet skal lukkes ordentligt.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
                }
        });

        // Listener
        optionPane.addPropertyChangeListener(this);
    }

    /**
     * Listener - Reagerer bl.a. ved museklik p� knapperne
     */
    public void propertyChange(PropertyChangeEvent e)
    {
    	Object value = optionPane.getValue(); // Hvad er �ndret?
    	optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
    	
    	if(value.equals("OK")) // Klik på "OK"
    	{
    		byte[] pieces = {2,3,4,5};
    			
    		// S�t brugerens valg i spilmotor
    		if (Datalink.currentPlayer == Vars.BLACK)
    			Datalink.blackPawnPromoteType = (byte)(pieces[pieceList.getSelectedIndex()]*-1);
    		else if (Datalink.currentPlayer == Vars.WHITE)
  				Datalink.whitePawnPromoteType = pieces[pieceList.getSelectedIndex()];
    		    		
    		setVisible(false);
    	}
    }
}
