//Class for applet window
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class snakeApplet extends Applet
{

	private snakeCanvas c;
	
	public void init()
	{
		c = new snakeCanvas();
		c.setPreferredSize(new Dimension(376, 400));	//activates the canvas
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.setVisible(true);
		this.setSize(new Dimension(376, 400));
	}
	
	public void paint(Graphics g)
	{
		this.setSize(new Dimension(376, 400));
	}
}
