package gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import common.Vars;

public class AboutFrame extends BasicFrame
{
	
	private Insets insets = getContentPane().getInsets();
	private Dimension size;
	
	public AboutFrame(JFrame owner)
	{
		Dimension frameSize = new Dimension(300,300); 
		setPreferredSize(frameSize);
		Point location = new Point(owner.getLocation().x+(owner.getSize().width/2)-(frameSize.width/2),
								   owner.getLocation().y+(owner.getSize().height/2)-(frameSize.height/2));
		setLocation(location);
		setTitle("Om " + Vars.APPTITLE);
		setDefaultCloseOperation(1); //Klik på kryds => frame lukkes
		getContentPane().setLayout(null);
		buildGUI();
	}

	/**
	 * Tilføjer JComponents til frame
	 */
	private void buildGUI() 
	{
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new BListener(this));
		PlacerJComponent(btnOk, 210,230);
		JLabel lblOm = new JLabel();
		lblOm.setText("<html>Udviklet af DAMN Inc. <br> <a href=\"http://www.damn.dk\">www.damn.dk</a></html>");
		PlacerJComponent(lblOm, 10, 10);
	}
	
	private void PlacerJComponent(JComponent component, int x, int y)
	{
		getContentPane().add(component);
		size = component.getPreferredSize();
		component.setBounds(x + insets.left, y + insets.top, size.width, size.height);
	}
	
	
}

class BListener implements ActionListener
{
	private JFrame mother;
	
	public BListener(JFrame mother)
	{
		this.mother = mother;
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand().equals("OK"))
			mother.setVisible(false);
	}

}

