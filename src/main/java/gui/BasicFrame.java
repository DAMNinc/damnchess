package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class BasicFrame extends JFrame
{
	public BasicFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Nedenstående laver layout, som er afhængig af styresystemet
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// centrere frame på skærmen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	/*
	 * Nedenstående metoder bruges til at vise og gemme vinduet på forskellige
	 * måder
	 */
	public void showIt()
	{
		setVisible(true);
		pack();
	}

	public void showIt(int newPosX, int newPosY)
	{
		setLocation(newPosX, newPosY);
		setVisible(true);
		pack();
	}

	public void showIt(String title, int posX, int posY)
	{
		setTitle(title);
		setLocation(posX, posY);
		setVisible(true);
		pack();
	}

	public void hideIt()
	{
		setVisible(false);
	}

}