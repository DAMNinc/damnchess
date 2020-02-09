package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import common.Datalink;
import common.Vars;
import engine.Board;


public class NewGame extends BasicFrame implements ActionListener, ItemListener, KeyListener
{
	// Forbindelse til resten af programmet
	private Datalink datalink;
	private ChessFrame chessFrame;
	
	// Frame egenskaber
	private Insets insets = getContentPane().getInsets();
	private Dimension size;
	
	// Swing komponenter
	private JLabel lblGameType;
	private JLabel lblPlayers;
	private JLabel lblSettings;
	private JLabel lblColor;
	private JLabel lblDepth;
	private JLabel lblDifficulty;
	private JLabel lblThinkTime;
	
	private JComboBox comboBoxGameType;
	private JComboBox comboBoxAIColor;
	private JComboBox comboBoxDifficulty;
	private JComboBox comboBoxThinkingTime;
	private JComboBox comboBoxDepth;
	
	private JRadioButton radioHumanVsAI;
	private JRadioButton radioAIVsAI;
	private JRadioButton radioTwo;
	
	private JButton btnOK;
	private JButton btnCancel;
	
	// Indhold til comboboxe
	String[] color = {"Sort", "Hvid"};
	String[] difficulty = {"let", "middel", "svær", "brugerdefineret"};
	String[] thinktime = {"5", "10", "15", "20", "25", "30"};
	String[] depth = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};
	  
	/**
	 * Opretter en frame hvor med componenter til at angive spilindstillinger
	 * Kaldes fra menuen
	 * 
	 * @param chessFrame Den frame, som opretter dette objekt
	 * @param datalink Forbindelse til alle spilindstillinger
	 */
	public NewGame(ChessFrame chessFrame, Datalink datalink)
	{
		this.chessFrame = chessFrame;
		this.datalink = datalink;
		
		chessFrame.setEnabled(false);
		setPreferredSize(new Dimension(300,350));
		setLocation(chessFrame.getLocation().x + 30, chessFrame.getLocation().y + 30);
		setDefaultCloseOperation(1);
		setTitle("Nyt spil");
		getContentPane().setLayout(null);
		buildGUI();
		addKeyListener(this);
		
	}
	
	/**
	 * Opretter swing komponenter og sætter dem i frame'en
	 */
	private void buildGUI()
	{
		// Spiltype
		lblGameType = new JLabel("Spiltype:");
		lblGameType.setFont(new Font("Arial", Font.PLAIN, 14));
		PlacerJComponent(lblGameType, 10,10);
		String[] gameTypes = {"Normal", "Pawn Chess", "Patt-Schach Chess", "Pawns Game Chess", "Upside Down Chess"};
		
		comboBoxGameType = new JComboBox(gameTypes);
		comboBoxGameType.setEditable(false);
		comboBoxGameType.setSelectedIndex(0);
		comboBoxGameType.addKeyListener(this);
		PlacerJComponent(comboBoxGameType, 130, 10);
		
		
		// Spillere
		lblPlayers = new JLabel("Spillere:");
		lblPlayers.setFont(new Font("Arial", Font.PLAIN, 14));
		PlacerJComponent(lblPlayers, 10, 50);
		radioTwo = new JRadioButton("Human vs. Human");
		radioTwo.addActionListener(this);
		radioTwo.addKeyListener(this);
		PlacerJComponent(radioTwo, 130, 50);
		
		radioHumanVsAI = new JRadioButton("Human vs. AI");
		radioHumanVsAI.addActionListener(this);
		radioHumanVsAI.addKeyListener(this);
		PlacerJComponent(radioHumanVsAI, 130, 70);		
		
		radioAIVsAI = new JRadioButton("AI vs. AI");
		radioAIVsAI.addActionListener(this);
		radioAIVsAI.addKeyListener(this);
		PlacerJComponent(radioAIVsAI, 130, 90);	
		
	    ButtonGroup group = new ButtonGroup();
	    group.add(radioTwo);
	    group.add(radioHumanVsAI);
	    group.add(radioAIVsAI);
	    
		// AI indstillinger
	    lblSettings = new JLabel("AI indstillinger:");
	    lblSettings.setFont(new Font("Arial", Font.PLAIN, 14));
	    PlacerJComponent(lblSettings, 10, 130);
	    
	    lblColor = new JLabel ("Farve:");
	    lblColor.setFont(new Font("Arial", Font.PLAIN, 14));
	    PlacerJComponent(lblColor, 20, 150);
	    lblDifficulty = new JLabel("Sværhedsgrad:");
	    lblDifficulty.setFont(new Font("Arial", Font.PLAIN, 14));
	    PlacerJComponent(lblDifficulty, 20, 180);
	    
	    lblThinkTime = new JLabel("Tænketid:");
	    lblThinkTime.setFont(new Font("Arial", Font.PLAIN, 14));
	    PlacerJComponent(lblThinkTime, 20, 210);
	    
	    lblDepth = new JLabel("Spiltræsdybde:");
	    lblDepth.setFont(new Font("Arial", Font.PLAIN, 14));
	    PlacerJComponent(lblDepth, 20, 240);
	    
	    comboBoxAIColor = new JComboBox(color);
	    comboBoxAIColor.setSelectedIndex(0);
	    comboBoxAIColor.addKeyListener(this);
	    PlacerJComponent(comboBoxAIColor, 130, 150);
	    
	       
	    comboBoxDifficulty = new JComboBox(difficulty);
	    comboBoxDifficulty.addItemListener(this);
	    comboBoxDifficulty.addKeyListener(this);
	    PlacerJComponent(comboBoxDifficulty, 130, 180);
	    	  
	    comboBoxThinkingTime = new JComboBox(thinktime);
	    comboBoxThinkingTime.addItemListener(this);
	    comboBoxThinkingTime.addKeyListener(this);
	    PlacerJComponent(comboBoxThinkingTime, 130, 210);
	   	   
	    
	    comboBoxDepth = new JComboBox(depth);
	    comboBoxDepth.addItemListener(this);
	    comboBoxDepth.addKeyListener(this);
	    PlacerJComponent(comboBoxDepth, 130, 240);
	   
	    
	    // Knapper
	    btnCancel = new JButton("Annuller");
	    btnOK = new JButton("OK");
	    PlacerJComponent(btnCancel, 130, 280);
	    PlacerJComponent(btnOK, 210, 280);
	    btnOK.addActionListener(this);
	    btnOK.addKeyListener(this);
	    btnCancel.addActionListener(this);
	    btnCancel.addKeyListener(this);
	    
	    // Startindstilinger
	    comboBoxGameType.setSelectedIndex(Datalink.gameType);
	    radioTwo.setSelected(Datalink.twoPlayerGame);
	    radioHumanVsAI.setSelected(!Datalink.twoPlayerGame);
	    radioAIVsAI.setSelected(Datalink.aivsai);
	    comboBoxAIColor.setSelectedIndex((Datalink.aiColor+1)%2);
	    comboBoxThinkingTime.setSelectedIndex((Vars.aiThinkTimeInSeconds/5)-1);
	    comboBoxDepth.setSelectedIndex(Vars.aiTreeDepth-2);
	    validateSettings();
//		comboBoxAIColor.setEnabled(false);
//		comboBoxDifficulty.setEnabled(false);
//		comboBoxDepth.setEnabled(false);
//		comboBoxThinkingTime.setEnabled(false);
//		lblThinkTime.setEnabled(false);
//		lblColor.setEnabled(false);
//		lblDifficulty.setEnabled(false);
//		lblDepth.setEnabled(false);
//		comboBoxDifficulty.setSelectedItem("middel");
	}
	
	/**
	 * Indsætter en JComponent i denne frame
	 * @param component Den JComponent der skal indsættes
	 * @param x Vandret afstand til øverste venstre hjørne på frame
	 * @param y Lodret afstand til øverste venstre hjørne på frame
	 */
	private void PlacerJComponent(JComponent component, int x, int y)
	{
		getContentPane().add(component);
		size = component.getPreferredSize();
		component.setBounds(x + insets.left, y + insets.top, size.width, size.height);
	}

	/**
	 * Listener til knapper og radiobuttons - Lytter efter museklik
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals("OK"))
		{
			chessFrame.setEnabled(true);
			datalink.resetVariables();

			
			// Spiltype
			if (comboBoxGameType.getSelectedItem().equals("Normal"))
			{
				Datalink.gameType = Board.NORMAL_CHESS;
			}
			else if (comboBoxGameType.getSelectedItem().equals("Pawn chess"))
			{
				Datalink.gameType = Board.PAWN_CHESS;
				Datalink.usingOpening = false;
			}
			else if (comboBoxGameType.getSelectedItem().equals("Patt-Schach Chess"))
			{
				Datalink.gameType = Board.PATT_SCHACH_CHESS;
				Datalink.usingOpening = false;
			}
			else if (comboBoxGameType.getSelectedItem().equals("Pawns Game Chess"))
			{
				Datalink.gameType = Board.PAWNS_GAME_CHESS;
				Datalink.usingOpening = false;
			}
			else if (comboBoxGameType.getSelectedItem().equals("Upside Down Chess"))
			{
				Datalink.gameType = Board.UPSIDE_DOWN_CHESS;
				Datalink.usingOpening = false;
			}
				
			datalink.getBoardData().resetBoard(Datalink.gameType);
			
			
			// Spillere
			Datalink.twoPlayerGame = radioTwo.isSelected();

			
			// AI indstillinger
			if (!Datalink.twoPlayerGame)
			{
				if (radioAIVsAI.isSelected())
					Datalink.aivsai = true;
				
				// Farve
				if (comboBoxAIColor.getSelectedItem().equals("Sort"))
				{
					Datalink.aiColor = Vars.BLACK;
				}
				else if (comboBoxAIColor.getSelectedItem().equals("Hvid"))
				{
					Datalink.aiColor = Vars.WHITE;
					
				}
				
				// Sværhedsgrad
				Vars.aiThinkTimeInSeconds = Integer.parseInt(comboBoxThinkingTime.getSelectedItem().toString());
				Vars.aiTreeDepth = Integer.parseInt(comboBoxDepth.getSelectedItem().toString());
			
				chessFrame.newGame();
				hideIt(); // Luk vindue
				chessFrame.getChessboard().repaintBoard();
				
				
				// Åbning
				if (Datalink.usingOpening)
				{
					Datalink.opening = datalink.pickOpening();
					Datalink.openingMoveNumberWhite = 0;
					Datalink.openingMoveNumberBlack = 0;
				}
				
				// Start spillet, hvis AI skal starte
				if (Datalink.aiColor == Vars.WHITE || Datalink.aivsai)
				{
					Datalink.currentPlayer = Vars.BLACK;
					datalink.play();
				}
			}
			else
			{
				chessFrame.newGame();
				hideIt();
				chessFrame.getChessboard().repaintBoard();
			}
		}
		else if (e.getActionCommand().equals("Annuller"))
		{
			chessFrame.setEnabled(true);
			this.setVisible(false);
		}
		else if (e.getActionCommand().equals("Human vs. Human"))
		{
			comboBoxAIColor.setEnabled(false);
			comboBoxDifficulty.setEnabled(false);
			comboBoxDepth.setEnabled(false);
			comboBoxThinkingTime.setEnabled(false);
			lblThinkTime.setEnabled(false);
			lblColor.setEnabled(false);
			lblDifficulty.setEnabled(false);
			lblDepth.setEnabled(false);
		}
		else if (e.getActionCommand().equals("Human vs. AI"))
		{
			comboBoxAIColor.setEnabled(true);
			comboBoxDifficulty.setEnabled(true);
			comboBoxDepth.setEnabled(true);
			comboBoxThinkingTime.setEnabled(true);
			lblThinkTime.setEnabled(true);
			lblColor.setEnabled(true);
			lblDifficulty.setEnabled(true);
			lblDepth.setEnabled(true);
		}
		else if (e.getActionCommand().equals("AI vs. AI"))
		{
			comboBoxAIColor.setEnabled(false);
			comboBoxDifficulty.setEnabled(true);
			comboBoxDepth.setEnabled(true);
			comboBoxThinkingTime.setEnabled(true);
			lblThinkTime.setEnabled(true);
			lblColor.setEnabled(false);
			lblDifficulty.setEnabled(true);
			lblDepth.setEnabled(true);
		}
		
	}

	/**
	 * Enablér og diseabler comboboxe ud fra valgt spiltype
	 *
	 */
	private void validateSettings()
	{
		if (radioTwo.isSelected())
		{
			comboBoxAIColor.setEnabled(false);
			comboBoxDifficulty.setEnabled(false);
			comboBoxDepth.setEnabled(false);
			comboBoxThinkingTime.setEnabled(false);
			lblThinkTime.setEnabled(false);
			lblColor.setEnabled(false);
			lblDifficulty.setEnabled(false);
			lblDepth.setEnabled(false);
		}
		else if (radioHumanVsAI.isSelected())
		{
			comboBoxAIColor.setEnabled(true);
			comboBoxDifficulty.setEnabled(true);
			comboBoxDepth.setEnabled(true);
			comboBoxThinkingTime.setEnabled(true);
			lblThinkTime.setEnabled(true);
			lblColor.setEnabled(true);
			lblDifficulty.setEnabled(true);
			lblDepth.setEnabled(true);
		}
		else if (radioAIVsAI.isSelected())
		{
			comboBoxAIColor.setEnabled(false);
			comboBoxDifficulty.setEnabled(true);
			comboBoxDepth.setEnabled(true);
			comboBoxThinkingTime.setEnabled(true);
			lblThinkTime.setEnabled(true);
			lblColor.setEnabled(false);
			lblDifficulty.setEnabled(true);
			lblDepth.setEnabled(true);
		}
	}
	
	/**
	 * Listener til combobox'ene
	 * Lytter efter ændringer i boxene - Bruges ved ændring af sværhedsgrad
	 */
	
	public void itemStateChanged(ItemEvent e) 
	{
		JComboBox combobox = (JComboBox)e.getSource();
	
		// Hvis sværhedsgraden ændres, ændres comboboxe med tænketid og dybde
		if (combobox.getSelectedItem().equals("let"))
		{
			comboBoxThinkingTime.setSelectedItem("10");
			comboBoxDepth.setSelectedItem("3");
		}
		else if (combobox.getSelectedItem().equals("middel"))
		{
			comboBoxThinkingTime.setSelectedItem("15");
			comboBoxDepth.setSelectedItem("4");
		}
		else if (combobox.getSelectedItem().equals("svær"))
		{
			comboBoxThinkingTime.setSelectedItem("20");
			comboBoxDepth.setSelectedItem("6");
		}
		
		// Hvis dybde og tænketid svarer til en bestemt sværhedsgrad, vælges denne sværhedsgrad
		if(!comboBoxDifficulty.getSelectedItem().equals("let") && comboBoxThinkingTime.getSelectedItem().equals("10") && comboBoxDepth.getSelectedItem().equals("3"))
		{
			comboBoxDifficulty.setSelectedItem("let");
		}
		else if (!comboBoxDifficulty.getSelectedItem().equals("middel") && comboBoxThinkingTime.getSelectedItem().equals("15") && comboBoxDepth.getSelectedItem().equals("4"))
		{
			comboBoxDifficulty.setSelectedItem("middel");
		}
		else if (!comboBoxDifficulty.getSelectedItem().equals("svær") && comboBoxThinkingTime.getSelectedItem().equals("20") && comboBoxDepth.getSelectedItem().equals("6"))
		{
			comboBoxDifficulty.setSelectedItem("svær");
		}
		
		// Hvis der ændres på tid eller dybde
		if (!combobox.getItemAt(0).equals("let"))
		{
			// Hvis dybde og tænketid ikke svarer til en bestemt sværhedsgrad, sættes sværhedsgrad til "brugerdefineret"
			if (!(comboBoxThinkingTime.getSelectedItem().equals("10") && comboBoxDepth.getSelectedItem().equals("3")) &&
			!(comboBoxThinkingTime.getSelectedItem().equals("15") && comboBoxDepth.getSelectedItem().equals("4")) &&
			!(comboBoxThinkingTime.getSelectedItem().equals("20") && comboBoxDepth.getSelectedItem().equals("6")))
			{
				comboBoxDifficulty.setSelectedItem("brugerdefineret");
			}
		}
	}

	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) 
	{
		
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
		{
			chessFrame.setEnabled(true);
			hideIt();
		}
	}
	public void keyTyped(KeyEvent e) {}
}
