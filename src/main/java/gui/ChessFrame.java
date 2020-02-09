package gui;

import ai.Opening;
import common.*;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.*;

import engine.Move;
import engine.Piece;



public class ChessFrame extends BasicFrame implements KeyListener, ActionListener
{
	private Datalink datalink; 	// Forbindelse til skakmotor og AI
	
	// Frame egenskaber
	private Insets insets = getContentPane().getInsets();
	private Dimension size;
	
	// Specielle grafiske objekter
	private ChessBoard chessboard;
	private ChessBoardBackground chessboardBackground;
	
	// Almindelinge Swing komponenter
	private JMenuBar menuBar;
	private JTextArea txtHistorik;
	private JScrollPane scrollPane1;
	private JTextArea txtAI;
	private JScrollPane scrollPane2;	
	private JCheckBox debugMode;
	public JLabel lblCurrentPlayer;
	private JMenu mnuSpil;
	private JMenu mnuDepth;
	private JMenu mnuThinkTime;
	private JButton test;
	
	int indexKode = 0; // T?ller til snydekode
	
	private final int NORMAL_MENUITEM = 0;
	private final int RADIO_MENUITEM = 1;
	
	public ChessFrame(Datalink datalink)
	{
		this.datalink = datalink;
		
		setPreferredSize(new Dimension(800, 620));
		
		setLocation(100,100);
		setTitle(Vars.APPTITLE);
		setupMenu();
		buildGUI();
		
		addKeyListener(this);
	}
	
	/**
	 * Opretter menuen-linjen med menuer
	 */ 
	private void setupMenu() 
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// Menuer
		String[] fileItems = {"Quick start", "Nyt spil", "-", "Åben", "Gem", "Gem som","-", "Luk"};
		newMenu(menuBar,"Filer", fileItems, NORMAL_MENUITEM);
		
		String[] gameItems = {"Fortryd sidste træk", "Vis hint", "-"};
		mnuSpil = newMenu(menuBar,"Spil", gameItems, NORMAL_MENUITEM);
		 
		String[] thinkTime = {"5 sek.","10 sek.","15 sek.","20 sek.","25 sek.", "30 sek."};
		mnuThinkTime = newMenu(mnuSpil, "AI tænketid", thinkTime, RADIO_MENUITEM);
		
		String[] depths = {"2","3","4","5","6","7","8","9","10"};
		mnuDepth = newMenu(mnuSpil, "Spiltræsdybde", depths, RADIO_MENUITEM);
		
		
		String[] helpItems = {Vars.APPTITLE + " hjælp", "-", "Om..."};
		newMenu(menuBar,"Hjælp", helpItems, NORMAL_MENUITEM);
	}
	
	/**
	 * Opretter en ny menu med menupunkter og giver menupunkterne listeners.
	 * @param target Angiver hvilken menubar menuen skal tilf?jes til.
	 * @param menuName Angiver navnet (det viste!) p? menuen.
	 * @param menuItems Et string-array med menupunkterne.
	 */
	private JMenu newMenu(JComponent target, String menuName, String[] menuItems, int type)
	{
		ButtonGroup group = new ButtonGroup();;
		JMenu JM = new JMenu(menuName);
		target.add(JM);
		
		for (String item : menuItems)
		{
			if(item.equals("-")) // Angiver en seperator, som skal tilf?jes specielt
				JM.addSeparator();
			else
			{
				if (type == NORMAL_MENUITEM)
				{
					JMenuItem JMI = new JMenuItem(item);
					// Angiver tastaturgenveje
					if (item.equals("Quick start"))
						JMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
//					else if (item.equals("Nyt spil"))
//						JMI.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
					else if (item.equals("Fortryd sidste træk"))
						JMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.Event.CTRL_MASK));
				
					JM.add(JMI);
					JMI.addActionListener(this);	
				}
				else if (type == RADIO_MENUITEM)
				{
					JRadioButtonMenuItem JRBMI = new JRadioButtonMenuItem(item);
					group.add(JRBMI);
					JM.add(JRBMI);
					JRBMI.addActionListener(this);
					JRBMI.setEnabled(false);
				}
			
			}
		}
		return JM;
	}
	
	/**
	 * Opbygger frame'en. Inds?tter alle grafiske komponenter og giver dem listeners
	 */
	private void buildGUI() 
	{
		// Br?ttet
		chessboard = new ChessBoard(datalink, this);
		chessboard.setDoubleBuffered(true);
		chessboardBackground = new ChessBoardBackground(chessboard);
		
		// Historik
		newLabel("Historik:",550, 25);
		txtHistorik = new JTextArea();
		txtHistorik.setBorder(BorderFactory.createLoweredBevelBorder());
		txtHistorik.setEditable(false);
		txtHistorik.setText(" ");
		txtHistorik.setFocusable(false);
		
		scrollPane1 = new JScrollPane(txtHistorik);
		scrollPane1.setPreferredSize(new Dimension(220,180));
		scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// AI output
		newLabel("AI output:",550, 240);
		txtAI = new JTextArea();
		txtAI.setBorder(BorderFactory.createLoweredBevelBorder());
		txtAI.setEditable(false);
		txtAI.setText("");
		txtAI.setFocusable(false);
		
		scrollPane2 = new JScrollPane(txtAI);
		scrollPane2.setPreferredSize(new Dimension(220,180));
		scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
		
		debugMode = new JCheckBox();
		debugMode.setText("Debugmode");
		debugMode.addActionListener(this);
		debugMode.setFocusable(false);
		
		lblCurrentPlayer = new JLabel("");
		lblCurrentPlayer.setFont(new Font("Arial",Font.BOLD,18));
		lblCurrentPlayer.setPreferredSize(new Dimension(200,30));
		getContentPane().setLayout(null);
		PlacerJComponent(chessboardBackground, 0,0);
		PlacerJComponent(chessboard, 25, 25);
		PlacerJComponent(scrollPane1, 550, 50);
		PlacerJComponent(scrollPane2, 550, 265);
		PlacerJComponent(lblCurrentPlayer, 550, 470);
		
		// ########################
		// debugstuff
		JButton eval = new JButton("TEST");
		eval.addActionListener(this);
		PlacerJComponent(eval, 550, 0);
		setFocusable(true);
	
		// ########################
	
	}
	
	private void newLabel(String title, int x, int y)
	{
		JLabel tempLabel = new JLabel(title);
		tempLabel.setFont(new Font("Arial",Font.BOLD,18));
		PlacerJComponent(tempLabel, x, y);
	}
	
	private void PlacerJComponent(JComponent component, int x, int y)
	{
		getContentPane().add(component);
		size = component.getPreferredSize();
		component.setBounds(x + insets.left, y + insets.top, size.width, size.height);
	}

	public void addHistorik(String text)
	{
		String existing = txtHistorik.getText();
		
		if (Datalink.currentPlayer == Vars.BLACK)
		{
			existing += text + " ";
		}
		else if (Datalink.currentPlayer == Vars.WHITE)
		{
			if (Datalink.turnNumber > 1)
				existing += "\n ";
			
			existing += Datalink.turnNumber + ". ";
			existing += text + "\t";
		}
		txtHistorik.setText(existing);
		scrollPane1.paintImmediately(0,0,scrollPane1.getWidth(), scrollPane1.getHeight());
		txtHistorik.paintImmediately(0,0,txtHistorik.getWidth(), txtHistorik.getHeight());
		txtHistorik.setSelectionStart(txtHistorik.getText().length());
	}
	
	public void addAIOutput(String text)
	{
		
		txtAI.setText(txtAI.getText() + text);
		txtAI.paintImmediately(0,0,txtAI.getWidth(), txtAI.getHeight());
		txtAI.setSelectionStart(txtAI.getText().length());
	}
	
	public void newGame()
	{
		chessboard.setMarkedField(ChessBoard.NONE);
		chessboard.clearLegalFields();
		txtHistorik.setText(" ");
		txtAI.setText("");
		lblCurrentPlayer.setText("Hvid trækker");
		chessboard.reloadBoard();
		
		mnuDepth.getItem(Vars.aiTreeDepth - 2).setSelected(true);
		mnuThinkTime.getItem((Vars.aiThinkTimeInSeconds/5)-1).setSelected(true);
		
		if (Datalink.twoPlayerGame)
		{
			for (int i = 0; i < mnuDepth.getItemCount(); i++)
				mnuDepth.getItem(i).setEnabled(false);
			for (int i = 0; i < mnuThinkTime.getItemCount(); i++)
				mnuThinkTime.getItem(i).setEnabled(false);
		}
		else
		{
			for (int i = 0; i < mnuDepth.getItemCount(); i++)
				mnuDepth.getItem(i).setEnabled(true);
			for (int i = 0; i < mnuThinkTime.getItemCount(); i++)
				mnuThinkTime.getItem(i).setEnabled(true);
		}
		
	}
	
	public ChessBoard getChessboard()
	{
		return chessboard;
	}
	
	public ChessBoardBackground getChessboardBackground()
	{
		return chessboardBackground;
	}	
	
	public JTextArea getTxtHistorik() 
	{
		return txtHistorik;
	}
	
	/**********************
	 * LISTENERS
	 **********************/
	
	public void keyPressed(KeyEvent e)
	{
		// SNYDEKODE:
		String[] kode = {"I","D","D","Q","D"};
		
		if (e.getKeyText(e.getKeyCode()).equals(kode[indexKode]))
			indexKode++;
		else
			indexKode = 0;
		
		if (indexKode == kode.length)
		{
			indexKode = 0;
			Datalink.blackPawnPromoteType = Piece.BQUEEN;
			Datalink.whitePawnPromoteType = Piece.WQUEEN;
			
			for (int i = 0 ; i < datalink.getBoardData().getBoard().length ; i++)
			{
				if (Datalink.currentPlayer == Vars.BLACK)
					if (datalink.getBoardData().getBoard()[i] < 0 && datalink.getBoardData().getBoard()[i] != 100)
						if (datalink.getBoardData().getPieceAtPos((byte)i).getType() != Piece.BKING)
							datalink.getBoardData().promotePawn(datalink.getBoardData().getPieceAtPos((byte)i),Datalink.currentPlayer,(byte)i);
				
				if (Datalink.currentPlayer == Vars.WHITE)
					if (datalink.getBoardData().getBoard()[i] > 0  && datalink.getBoardData().getBoard()[i] != 100)
						if (datalink.getBoardData().getPieceAtPos((byte)i).getType() != Piece.WKING)
							datalink.getBoardData().promotePawn(datalink.getBoardData().getPieceAtPos((byte)i),Datalink.currentPlayer,(byte)i);	
				
			}
			datalink.getBoardData().updateAllLegalMovesForColor((byte) ((Datalink.currentPlayer + 1) % 2),false);
			datalink.getBoardData().updateAllLegalMovesForColor(Datalink.currentPlayer,true);
			chessboard.reloadBoard();
		}	
		// ======	
		
		
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			Vars.ctrlPressed = true;
	}
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			Vars.ctrlPressed = false;
			if (chessboard.getMarkedField() == -1)
				chessboard.clearLegalFields();
		}
	}
	
	public void keyTyped(KeyEvent e){}

	public void actionPerformed(ActionEvent evt)
	{
		
		//if (evt.getSource() instanceof JMenuItem) {
			String knap = evt.getActionCommand().toString();
			
			//FILER
			if (knap.equals("Quick start"))
			{
				datalink.newAIgame();
			}
			else if (knap.equals("Nyt spil"))
			{
				NewGame newGame = new NewGame(this, datalink);
				newGame.showIt();
//				datalink.newTwoPlayerGame();
			}
			else if (knap.equals("Åben"))
			{
				// Opretter en fil v?lger
				JFileChooser FC = new JFileChooser();
				FC.setApproveButtonText("Åben");
				FC.setDialogTitle("Åben");

				// Filfilter oprettes og tildeles til vores filechooser,
				// s? det er kun er .dcg-filer der kan ?bnes!
				FileNameFilter FF = new FileNameFilter();
				
				FC.setFileFilter(FF);

				int returnVal = FC.showOpenDialog(this);

				// Hvis en fil er valgt, og der er trykket ?ben
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
					File fileName = new File(FC.getSelectedFile().getAbsolutePath());
					
					// ?ben kun, hvis den valgte fil er af typen .dcg
					if (FF.accept(fileName)) 
					{
						
					      File theFile;
							FileInputStream inStream;
							ObjectInputStream objStream;

							theFile = fileName;

							// look for an existing file
							if(!theFile.exists()) {
								System.err.println("File "+theFile.getAbsolutePath()+
								                   " does not exist.");
								System.exit(1);
							}

					      try {
								// setup a read from physical file on the filesystem
								inStream = new FileInputStream(theFile);

								// attach a stream capable of writing objects to the stream that is
								// connected to the file
								objStream = new ObjectInputStream(inStream);

								   
								// read the objects
								Datalink.gameType = objStream.readByte();
								boolean twoPlayerGame = objStream.readBoolean();
								byte aiColor = objStream.readByte();
								Vars.aiThinkTimeInSeconds = objStream.readInt();
								Vars.aiTreeDepth = objStream.readInt();
								
								Datalink.opening = (Opening) objStream.readObject();
								Datalink.usingOpening = objStream.readBoolean();
								Datalink.openingMoveNumberBlack = objStream.readInt();
								Datalink.openingMoveNumberWhite = objStream.readInt();
				
								
								LinkedList<Move> moveHistory = (LinkedList<Move>)objStream.readObject();
							
								
								// close down the streams
								objStream.close();
								inStream.close();
								
								// Klarg?r "nyt" spil
								datalink.resetVariables();
								if (twoPlayerGame == true)
									datalink.newTwoPlayerGame();
								else 
								{
									datalink.resetVariables();
									Datalink.twoPlayerGame = false;
									datalink.getBoardData().resetBoard(Datalink.gameType);
									newGame();
									Datalink.aiColor = aiColor;
									addAIOutput(" Indstillinger for AI:\n");
									addAIOutput("   Max. dybde: " + Vars.aiTreeDepth + "\n");
									addAIOutput("   Max. betænkningstid: " + Vars.aiThinkTimeInSeconds + " sek\n");
									if(Datalink.usingOpening)
										addAIOutput("   Benytter åbning: \n     " + Datalink.opening.name + "\n");
								}
								
							
								// Udf?r gemte tr?k
								for(int i = 0 ; i < moveHistory.size(); i++)
								{
									
									byte from = moveHistory.get(i).getFrom();
									Piece piece = datalink.getBoardData().getPieceAtPos(from);
									byte to = moveHistory.get(i).getTo();
									
									Datalink.whitePawnPromoteType = moveHistory.get(i).getWhitePawnPromoteType();
									Datalink.blackPawnPromoteType = moveHistory.get(i).getBlackPawnPromoteType();
									
									datalink.move(piece, to);
									
									Datalink.currentPlayer = (byte)((Datalink.currentPlayer + 1) % 2);
									if (i % 2 == 1)
										Datalink.turnNumber++;
									
									// Find array pos for brik ud fra historik
									// Find piece ud fra pos
									// Find array pos for tilFelt ud fra historik
								}
							
//								datalink.getBoardData().updateAllLegalMovesForColor((byte) ((Datalink.currentPlayer + 1) % 2),false);
//								datalink.getBoardData().updateAllLegalMovesForColor(Datalink.currentPlayer,true);
								
								//txtHistorik.setText(objStream.readObject().toString());
	
								if (Datalink.currentPlayer == Vars.WHITE)
									lblCurrentPlayer.setText("Hvid trækker");
								else 
									lblCurrentPlayer.setText("Sort trækker");
								
								
								chessboard.repaintBoard();

							} catch(IOException e) {
								JOptionPane.showMessageDialog(this,
								"Der opstod problemer ved læsning af filen: \n" + fileName,
								"IO-fejl",
								JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							} catch(ClassNotFoundException e) {
							   System.err.println("Things not going as planned.");
								e.printStackTrace();
					      } catch(ClassCastException e) {
								// end up here if one of the objects were read wrong
								System.err.println("Cast didn't work quite right.");
								e.printStackTrace();
							}   // catch
			
					} // end if accept fileName
					else
						JOptionPane.showMessageDialog(this,
								"Filtypen er forkert. Kan kun åbne filer af typen .dcg",
								"Forkert filtype",
								JOptionPane.ERROR_MESSAGE);
				} // end if approve option
			 // end if "?ben"
				
				
			}
			else if (knap.equals("Gem"))
			{
				if (Vars.filename.equals(""))
					knap = "Gem som";
				else
					savegame(new File(Vars.filename));
					
			}
			if (knap.equals("Gem som"))
			{
				// Opretter en fil v?lger
				JFileChooser FC = new JFileChooser();
				FC.setApproveButtonText("Gem som");
				FC.setDialogTitle("Gem som");
		  
				// Filfilter oprettes og tildeles til vores filechooser, 
				// s? det kun er .fcf-filer der kan gemmes!
				FileNameFilter FF = new FileNameFilter();
				FC.setFileFilter(FF);
		  
				int returnVal = FC.showSaveDialog(this);

				// Hvis brugeren klikkede p? ok/gem
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File fileName = new File(FC.getSelectedFile().getAbsolutePath());
			  
					// Hvis brugeren ikke selv har indtastet filtypen fcf, bliver det gjort... 
					if (FC.accept(fileName) == false)
						fileName = new File(fileName.toString() + ".dcg");

					savegame(fileName);
					
					Vars.filename = fileName.getAbsolutePath();
				} // end if approve option
			}
			else if(knap.equals("Luk"))
				System.exit(0);
			
			//SPIL
			else if(knap.equals("Fortryd sidste træk"))
			{
				if (datalink.getBoardData().getMoveHistory().isEmpty() == false && mnuSpil.isEnabled())
				{
					Move move = datalink.getBoardData().undoLastMove();
					
					if (!Datalink.twoPlayerGame)
						move = datalink.getBoardData().undoLastMove();
					
					chessboard.reloadBoard();
					if (Datalink.currentPlayer == Vars.BLACK)
						lblCurrentPlayer.setText("Sort trækker");
					if (Datalink.currentPlayer == Vars.WHITE)
						lblCurrentPlayer.setText("Hvid trækker");
					
					if(Datalink.currentPlayer == Vars.WHITE)
						Datalink.turnNumber--;
					
					txtHistorik.setText("");
					
					LinkedList<Move> moveHistory = datalink.getBoardData().getMoveHistory();
					
					for (int i = 0; i < moveHistory.size() ; i++)
					{
						if (i%2 == 0)
						{
							if (i > 0)
								txtHistorik.setText(txtHistorik.getText() + "\n");
							txtHistorik.setText(txtHistorik.getText()  + " " + ((i/2)+1) + ". ");
							txtHistorik.setText(txtHistorik.getText() + moveHistory.get(i).getMoveHistory() + "\t");
						}
						else
						{
							txtHistorik.setText(txtHistorik.getText() + moveHistory.get(i).getMoveHistory());
						}
					}
					
					datalink.getBoardData().updateAllLegalMovesForColor((byte)((Datalink.currentPlayer + 1)%2), false);
					datalink.getBoardData().updateAllLegalMovesForColor(Datalink.currentPlayer, true);
					
				}
				
			}
			else if (knap.equals("Vis hint"))
			{
				datalink.hint(); // beregning af hint
				
				chessboard.clearLegalFields();
				byte toField = chessboard.big2small(datalink.hintMove);
				byte fromField = chessboard.big2small(datalink.hintPiece.getPos());
				
				chessboard.getLegalFields().add(toField);
				chessboard.setMarkedField(fromField);
				chessboard.getFields()[toField].paintImmediately(0, 0, chessboard.getFields()[toField].getWidth(), chessboard.getFields()[toField].getHeight());
				chessboard.getFields()[fromField].paintImmediately(0, 0, chessboard.getFields()[fromField].getWidth(), chessboard.getFields()[fromField].getHeight());
			}
			// Spiltr?sdybde
			else if (knap.equals("2") || knap.equals("3") || knap.equals("4") || knap.equals("5") || knap.equals("6") || knap.equals("7") || knap.equals("8") || knap.equals("9") || knap.equals("10")) // Spiltr?sdybde 
			{
				Vars.aiTreeDepth = Integer.parseInt(knap.toString());
				addAIOutput(" Max. dybde: " + knap.toString() + "\n");
			}
			// T?nketid
			else if (knap.equals("5 sek.") || knap.equals("10 sek.") || knap.equals("15 sek.") || knap.equals("20 sek.") || knap.equals("25 sek.") || knap.equals("30 sek."))
			{
				Vars.aiThinkTimeInSeconds = Integer.parseInt(knap.substring(0,2).replace(" ", ""));
				addAIOutput(" Max. betænkningstid: " + knap.toString() + "\n");
			}
			//Hj?lp
			else if (knap.equals(Vars.APPTITLE + " hjælp"))
			{
				JOptionPane.showMessageDialog(this, "TO DO :-)");
			}
			else if (knap.equals("Om..."))
			{
				AboutFrame AF = new AboutFrame(this);
				AF.showIt();
			}
			else if (knap.equals("TEST"))
			{
//				EvalFunction.debugAI(datalink.getBoardData());
//				datalink.getBoardData().runInGameTests();
				System.out.println(Datalink.twoPlayerGame);
			}
		//}
		
	}
	
	private void savegame(File fileName)
	{
		FileOutputStream outStream;             // generic stream to the file
		ObjectOutputStream objStream;           // stream for objects to the file

		try {
		   // setup a stream to a physical file on the filesystem
		   outStream = new FileOutputStream(fileName);

		   // attach a stream capable of writing objects to the stream that is
		   // connected to the file
		   objStream = new ObjectOutputStream(outStream);

		   objStream.writeByte(Datalink.gameType);
		   objStream.writeBoolean(Datalink.twoPlayerGame);
		   objStream.writeByte(Datalink.aiColor);
		   objStream.writeInt(Vars.aiThinkTimeInSeconds);
		   objStream.writeInt(Vars.aiTreeDepth);
		   
		   objStream.writeObject(Datalink.opening);
		   objStream.writeBoolean(Datalink.usingOpening);
		   objStream.writeInt(Datalink.openingMoveNumberBlack);
		   objStream.writeInt(Datalink.openingMoveNumberWhite);
		   
		   
		   objStream.writeObject(datalink.getBoardData().getMoveHistory());
		   
		   //objStream.writeByte(Datalink.currentPlayer);
		   //objStream.writeBoolean(Datalink.whiteCanCastleLeft);
		   //objStream.writeBoolean(Datalink.blackCanCastleLeft);
		   //objStream.writeBoolean(Datalink.whiteCanCastleRight);
		   //objStream.writeBoolean(Datalink.blackCanCastleRight);
		   //objStream.writeBoolean(Datalink.whiteHasCastled);
		   //objStream.writeBoolean(Datalink.blackHasCastled);
		   //objStream.writeBoolean(Datalink.gameEnded);
		   //objStream.writeBoolean(Datalink.gameDraw);
		   //objStream.writeByte(Datalink.turnNumber);
		   //objStream.writeBoolean(Datalink.whiteKingCheck);
		   //objStream.writeBoolean(Datalink.blackKingCheck);
		   //objStream.writeBoolean(Datalink.whiteKingCheckMate);
		   //objStream.writeBoolean(Datalink.blackKingCheckMate);
		   //objStream.writeByte(Datalink.whitePawnPromoteType);
		   //objStream.writeByte(Datalink.blackPawnPromoteType);
//		   objStream.writeByte(Datalink.aiColor);
//		   objStream.writeBoolean(Datalink.twoPlayerGame);
		   
		   	// Luk ned
			objStream.flush();
			objStream.close();
			outStream.flush();
			outStream.close();
			
			
			JOptionPane.showMessageDialog(this,
					"Spillet er gemt i filen: \n" + fileName,
					"Filen er gemt",
					JOptionPane.INFORMATION_MESSAGE);
			
		} catch(IOException e) {
			// Messagebox med besked om at der er sket en IO-fejl
			JOptionPane.showMessageDialog(this,
					"Der opstod problemer ved skrivning til filen: \n" + fileName,
					"IO-fejl",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}   // catch
	}

	public JMenu getMnuSpil() {
		return mnuSpil;
	}

	public JLabel getLblCurrentPlayer()
	{
		return lblCurrentPlayer;
	}
}
