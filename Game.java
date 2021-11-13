/*Sina Vahidi
 * WORKS CITED:
 *
 * https://www.youtube.com/watch?v=TErboGLHZGA
 *
 * https://stackoverflow.com/questions/3775373/java-how-to-add-image-to-jlabel
 *
 * https://www.w3schools.com/java/java_arraylist.asp
 *
 * I learned setRGB on stack overflow when we were doing Pong but I can't find that thread anymore :(
 * */
package FlappyBird;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;

public class Game implements ActionListener, KeyListener
{

	public static Game flappyBird;
	private int red=255, green=0, blue=0, m=0;
	public final int WIDTH = 800, HEIGHT = 800;
	private final GameRenderer RENDERER;
	private Rectangle bird;
	private final ArrayList<Rectangle> COLUMNS;
	private int ticks, yMotion, score;
	private boolean gameOver, started;
	private Color bac;
	private final Random RAND;
	private String[] optionsBackground = {"Light","Dark"};
	private JFrame playground;


	//Game constructor
	public Game()
	{
		playground = new JFrame();
		Timer delay = new Timer(20, this); //

		RENDERER = new GameRenderer();
		playgroundSetup();
		RAND = new Random();

		bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		COLUMNS = new ArrayList<>();

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		delay.start();
	}

	/**
	 * The initiall commands for setting up the environment
	 */
	private void playgroundSetup(){
		playground.add(RENDERER);
		playground.setTitle("Flappy Bird");
		playground.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		playground.setSize(WIDTH, HEIGHT);
		playground.setLocationRelativeTo(null);
		playground.addKeyListener(this);
		playground.setResizable(false);
		playground.setVisible(true);
		colorPrompt();
	}

	/**
	 * Initial promt for the background color of the enviorment
	 */
	private void colorPrompt() {
		int background = JOptionPane.showOptionDialog(null,"Select your background color:",
				"Flaps",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsBackground, optionsBackground[0]);
		switch(background) {
			case 0:
				bac = Color.lightGray;
				break;
			case 1:
				bac = Color.black;
				break;
		}

//		int columns = JOptionPane.showOptionDialog(null,"Select the color of the columns:", "Flaps",
//				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsColumns, optionsColumns[0]);
//		switch(columns) {
//			case 0:
//				col = Color.RED;
//				break;
//			case 1:
//				col = Color.GREEN;
//				break;
//			case 2:
//				col = Color.BLUE;
//				break;
//			case 3:
//				col = Color.PINK;
//				break;
//			case 4:
//				col = Color.YELLOW;
//				break;
//			case 5:
//				col = setRGB();
//				break;
//		}
		//Decided not to do this because I'd rather have rgb colors

	}

	/**
	 * Generates a new column with a randomly generated gap
	 */
	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 100;
		int height = 50 + RAND.nextInt(300);

		if (start) {
			COLUMNS.add(new Rectangle(WIDTH + width + COLUMNS.size() * 300, HEIGHT - height - 120, width, height));
			COLUMNS.add(new Rectangle(WIDTH + width + (COLUMNS.size() - 1) * 300, 0, width, HEIGHT - height - space));
		} else {
			COLUMNS.add(new Rectangle(COLUMNS.get(COLUMNS.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			COLUMNS.add(new Rectangle(COLUMNS.get(COLUMNS.size() - 1).x, 0, width, HEIGHT - height - space));
		}

	}


	/**
	 * paint method for columns
	 * @param g
	 * @param column
	 */
	public void paintColumn(Graphics g, Rectangle column)
	{
		g.setColor(setRGB());
		g.fillRect(column.x, column.y, column.width, column.height);
	}


	/**
	 * Decreases the ball's y value whenever called
	 */
	public void jump()
	{
		if (gameOver)
		{
			bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			COLUMNS.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started)
		{
			started = true;
		}
		else if (!gameOver)
		{
			if (yMotion > 0)
			{
				yMotion = 0;
			}

			yMotion -= 10;
		}
		playSound("/Users/hirbod/IdeaProjects/ICS4UE/Flappy Bird/wing.wav");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int v = 10; //The speed of the game (right to left)

		ticks++;

		if (started)
		{
			for (Rectangle column : COLUMNS) {
				column.x -= v;
			}
			/*
			This is how i originally did the loop above, but intelliJ suggested a for each loop
			for (int i = 0; i < COLUMNS.size(); i++)
			{
				Rectangle column = COLUMNS.get(i);

				column.x -= speed;
			}
			*/

			if (ticks % 2 == 0 && yMotion < 15)
				yMotion += 2;

			for (int i = 0; i < COLUMNS.size(); i++) {
				Rectangle column = COLUMNS.get(i);
				if (column.x + column.width < 0)
				{
					COLUMNS.remove(column);
					if (column.y == 0)
						addColumn(false);
				}
			}

			bird.y += yMotion;

			for (Rectangle column : COLUMNS) {
				if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
					score++;
					if (!gameOver)
						playSound("/Users/hirbod/IdeaProjects/ICS4UE/Flappy Bird/point.wav");
				}

				if (column.intersects(bird)) {
					gameOver = true;

					if (bird.x <= column.x)
						bird.x = column.x - bird.width;
					else
					{
						if (column.y != 0)
							bird.y = column.y - bird.height;
						else if (bird.y < column.height)
							bird.y = column.height;
					}
				}
			}

			if (bird.y > HEIGHT - 120 || bird.y < 0)
				gameOver = true;

			if (bird.y + yMotion >= HEIGHT - 120)
			{
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}
		}

		RENDERER.repaint();
	}

	/**
	 * repaints grahic objects everytime that is called
	 * @param g
	 */
	public void repaint(Graphics g)
	{
		g.setColor(bac);
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(bac);
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		g.setColor(setRGB());
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);

		g.setColor(setRGB());
		g.fillRect(bird.x, bird.y, bird.width, bird.height);

		for (Rectangle column : COLUMNS)
		{
			paintColumn(g, column);
		}

		g.setColor(setRGB());
		g.setFont(new Font("Arial", Font.BOLD, 50));

		if (!started)
			g.drawString("Hit space to start!", 185, HEIGHT / 2 - 50);

		if (gameOver)
		{
			g.drawString("Game Over", 250, HEIGHT / 2 - 50);
			g.drawString("Hit space to try again!", 135, (HEIGHT / 2 - 50)+55);
		}

		if (!gameOver && started)
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
	}


	private Color setRGB(){
		if(m==0){ // Starts here
			green++; // Adds increments of one to green until it reaches 255
			if (green==255)
				m=1; //Sets m = 1 so the next statement is reached
		}
		if(m==1){
			red--; //Subtracts increments of red until it reaches 0
			if(red==0)
				m=2; // Sets m=2 so the next statement is reached
		}
		if(m==2){
			blue++; //Hopefully u understand what's going on by now lol
			if(blue==255)
				m=3;
		}
		if(m==3){
			green--;
			if(green==0)
				m=4;
		}
		if(m==4){
			red++;
			if(red==255)
				m=5;
		}
		if(m==5){
			blue--;
			if(blue==0)
				m=0;
		}
		return(new Color(red,green,blue));
	}

	/**
	 * Plays audio file from a given file path, if the file path exists and whoever writes the program isn't stupid
	 * @param directoryPath
	 */
	private void playSound(String directoryPath){
		try{
			File audioPath = new File(directoryPath); // tries to make File object from given path
			if (audioPath.exists()){
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioPath);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
			}
			else
				System.out.println("Incorrect file path"); // Prints if the path is incorrect or file does not exist
		}
		catch (Exception ex){
			System.out.println("Error");
		}
	}

	public static void main(String[] args)
	{
		flappyBird = new Game();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			jump();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

}