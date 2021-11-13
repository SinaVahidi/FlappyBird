package FlappyBird;

import java.awt.*;

import javax.swing.*;

public class GameRenderer extends JPanel
{

	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g); //Calls code from parent class
		Game.flappyBird.repaint(g);
	}
	
}
