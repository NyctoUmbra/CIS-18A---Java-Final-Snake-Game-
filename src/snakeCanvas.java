//Main Class for Snake Game
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class snakeCanvas extends Canvas implements Runnable, KeyListener
{
	private final int BOX_HEIGHT = 15;
	private final int BOX_WIDTH = 15;
	private final int GRID_WIDTH = 25;
	private final int GRID_HEIGHT = 25;
	
	private LinkedList<Point> snake;
	private Point fruit;
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;									//God object - Runs objects in the background - Multitasking
	private int score = 0;
	private String highScore = "";
	
	private Image menuImage = null;
	private boolean isInMenu = true;							//while true, only menu will display
	
	private boolean isAtEndGame = false;
	private boolean won = false;
	private Image endGameImage;
	//TODO Paint Method
	public void paint(Graphics g)
	{
		if (runThread == null)
		{
			this.setPreferredSize(new Dimension(376, 400));		//set dimensions to applet
			this.addKeyListener(this);
			
			runThread = new Thread(this);
			runThread.start();
		}
		if (isInMenu)
		{
			//draw menu
			DrawMenu(g);
		}
		else if (isAtEndGame)
		{
			//draw end game screen
			DrawEndGame (g);
		}
		else
		{
			//draw everything else
			if (snake == null)
			{
				snake = new LinkedList<Point>();
				GenerateDefaultSnake();
				PlaceFruit();									//Fruit is placed randomly
			}
			
			if (highScore.equals(""))
			{
				//initiate the high score
				highScore = this.GetHighScore();
			}
			setBackground(Color.BLACK);
			
			DrawFruit(g);
			DrawGrid(g);
			DrawSnake(g);
			DrawScore(g);
		}
	}
	//TODO GAME OVER - End of game
	public void DrawEndGame (Graphics g)
	{
		URL imagePath = snakeCanvas.class.getResource("game-over.png");
		this.endGameImage = Toolkit.getDefaultToolkit().getImage(imagePath);
		
		g.drawImage(endGameImage, 0, 0, 376, 400, this);
		BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics endGameGraphics = endGameImage.getGraphics();
		
		if (won)
			endGameGraphics.drawString("You maxed out the grid. You Won!!!", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
		else
			endGameGraphics.drawString("Your Score: " + this.score, (this.getPreferredSize().width / 2) - 45, (this.getPreferredSize().height / 2) + 20);
			endGameGraphics.drawString("High Score: " + this.highScore, (this.getPreferredSize().width / 2) - 60, (this.getPreferredSize().height / 2) + 40);
			endGameGraphics.drawString("Press SPACE for a New Game", (this.getPreferredSize().width / 2) - 80, (this.getPreferredSize().height / 2) + 60);
	
		g.drawImage(endGameImage, 0, 0, this);
	}
	//TODO GAME MENU
	public void DrawMenu (Graphics g)
	{
		if (this.menuImage == null)
		{
			try
			{
				URL imagePath = snakeCanvas.class.getResource("SnakeMenu.png");
				this.menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch (Exception e)
			{
				//image does not exist
				e.printStackTrace();
			}
		}
		g.drawImage(menuImage, 0, 0, 376, 400, this);
		//esc key = PAUSE
		g.setColor(Color.WHITE);
		g.drawString("Move with Arrow Keys", 128, BOX_HEIGHT * GRID_HEIGHT - 50);
		g.drawString("Press Esc to PAUSE", 130, BOX_HEIGHT * GRID_HEIGHT - 25);
	}
	//TODO Update GAME Image
	public void update(Graphics g)	//update method that implements double buffering
	{
		//this is the default update method which will contain our double buffering
		Graphics offScreenGraphics;	//these are the graphics we will use to draw off screen
		BufferedImage offscreen = null;
		Dimension d = this.getSize();
		
		offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offscreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		//flip
		g.drawImage(offscreen, 0, 0, this);
	}
	//TODO DEFAULT SNAKE
	public void GenerateDefaultSnake()
	{
		score = 0;
		snake.clear();
		
		snake.add(new Point(0,2));
		snake.add(new Point(0,1));
		snake.add(new Point(0,0));
		direction = Direction.NO_DIRECTION;
	}
	//TODO MOVEMENT
	public void Move()
	{
		if (direction == Direction.NO_DIRECTION)
			return;									//exit from Move Method
		
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction)
		{
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1, head.y);
			break;
		}
		
		if (this.direction != Direction.NO_DIRECTION)
			snake.remove(snake.peekLast());				//removes last part of tail so head can pass
		
		if (newPoint.equals(fruit))
		{
			//the snake has hit the fruit
			score+=1;
			
			Point addPoint = (Point) newPoint.clone();			//add new point(head/tail)
			
			switch (direction)
			{
			case Direction.NORTH:
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.WEST:
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1, head.y);
				break;
			}
			
			snake.push(addPoint);								//popping in new point (head/tail)
			PlaceFruit();
			
		}
		else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1))
		{
			//we went out of bounds, reset game
			CheckScore();
			won = false;
			isAtEndGame = true;
			return;
		}
		else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1))
		{
			//we went out of bounds, reset game
			CheckScore();
			won = false;
			isAtEndGame = true;
			return;
		}
		else if (snake.contains(newPoint))
		{
			//we ran into ourselves, reset game
			if (direction != Direction.NO_DIRECTION)
			{
				CheckScore();
				won = false;
				isAtEndGame = true;
				return;
			}
		}
		else if (snake.size() == (GRID_WIDTH * GRID_HEIGHT))
		{
			//we won
			CheckScore();
			won = true;
			isAtEndGame = true;
			return;
		}
		
		//if we reach this point in code, we're still good
		snake.push(newPoint);
	}
	//TODO SCORE
	public void DrawScore(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, 300, BOX_HEIGHT * GRID_HEIGHT + 15);
		g.drawString("Highscore: " + highScore, 0, BOX_HEIGHT * GRID_HEIGHT + 15);
	}
	//TODO CHECK SCORE
	public void CheckScore()
	{
		if (highScore.equals(""))
			return;
		
		//format	NAME/:/HighScore
		if (score > Integer.parseInt((highScore.split(":")[1])))
		{
			//user has set a new record
			String name = JOptionPane.showInputDialog("You set a new highscore, Please insert your name");
			highScore = name + ":" + score;
			
			File scoreFile = new File("highscore.dat");	//HighScore Data file (located in bin) (open up using Notepad)
			if (!scoreFile.exists())
			{
				try 
				{
					scoreFile.createNewFile();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try
			{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch (Exception e)
			{
				//errors
			}
			finally
			{
				try
				{
					if (writer != null)
					writer.close();
				}
				catch (Exception e) 
				{
					//
				}
			}
			
		}
	}
	//TODO GRID
	public void DrawGrid(Graphics g)
	{
		g.setColor(Color.WHITE);
		//Drawing an outside rectangle
		g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);
		//Drawing the vertical lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x+=BOX_WIDTH)
		{
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
		//Drawing the horizontal lines
		for (int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y+=BOX_HEIGHT)
		{
			g.drawLine(0, y, GRID_WIDTH * BOX_WIDTH, y);
		}
	}
	//TODO SNAKE
	public void DrawSnake(Graphics g)							//Snake Drawing
	{
		g.setColor(Color.GREEN);
		for (Point p : snake)
		{
			g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}
	//TODO FRUIT 
	public void DrawFruit(Graphics g)							//Fruit Drawing
	{
		g.setColor(Color.RED);
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
	}
	//TODO Place FRUIT
	public void PlaceFruit()
	{
		Random rand = new Random();								//generates random point for fruit
		int randomX = rand.nextInt(GRID_WIDTH);
		int randomY = rand.nextInt(GRID_HEIGHT);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint))						//snake does NOT contain that new point
		{
			randomX = rand.nextInt(GRID_WIDTH);
			randomY = rand.nextInt(GRID_HEIGHT);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint;
	}
	//TODO RUN GAME
	@Override
	public void run() 
	{
		while (true)
		{
			//Runs indefinitely
			repaint();
			if (!isInMenu && !isAtEndGame)			//if not in menu or end game screen, move
				Move();
													//buffer
			try
			{
				Thread.currentThread();
				Thread.sleep(100);					//Update-Speed at milliseconds  *10fps*
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	//TODO Get HIGHSCORE
	public String GetHighScore()
	{
		//format:	NAME:HighScore
		FileReader readFile = null;
		BufferedReader reader = null;
		
		try
		{
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch (Exception e)
		{
			return "Nobody:0";
		}
		finally
		{
			try 
			{
				if (reader != null)
					reader.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	//TODO KEY Typed/Pressed/Released
	@Override
	public void keyTyped(KeyEvent e) 
	{
		//
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:						//VK = Virtual Key
			if (direction != Direction.SOUTH)
			direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.NORTH)
			direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.WEST)
			direction = Direction.EAST;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.EAST)
			direction = Direction.WEST;
			break;
		case KeyEvent.VK_ENTER:
			if (isInMenu)
			{
				isInMenu = false;
				repaint();						//allows the game to start after pressing ENTER
			}
			break;
		case KeyEvent.VK_ESCAPE:				//pressing ESCAPE pauses the game and brings up the Menu
			isInMenu = true;
			break;
		case KeyEvent.VK_SPACE:
			if (isAtEndGame)
			{
				isAtEndGame = false;
				won = false;
				GenerateDefaultSnake();
				repaint();
			}
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		//
	}
}
