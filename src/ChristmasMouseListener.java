import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class ChristmasMouseListener extends MouseAdapter{

	protected ChristmasMouseListener(){}
	
	private int previousX;
	private int previousY;
	
	private static final int BUFFER = 25;
	
	@Override
	public void mousePressed(MouseEvent e) {
		previousX = e.getX();
		previousY = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!MerryChristmas.isMouseEnabled() || MerryChristmas.hasWon()) return;
		
		int x = e.getX();
		int y = e.getY();
		
			if (x < (previousX - BUFFER)){
				MerryChristmas.disableMouse();
				MerryChristmas.moveImage((JLabel) e.getSource(), MerryChristmas.Direction.LEFT);

			}
			if (x > (previousX + BUFFER)) {
				MerryChristmas.disableMouse();
				MerryChristmas.moveImage((JLabel) e.getSource(), MerryChristmas.Direction.RIGHT);

			}				
			if (y < (previousY - BUFFER)) {
				MerryChristmas.disableMouse();
				MerryChristmas.moveImage((JLabel) e.getSource(), MerryChristmas.Direction.UP);

			}
			if (y > (previousY + BUFFER)) {
				MerryChristmas.disableMouse();
				MerryChristmas.moveImage((JLabel) e.getSource(), MerryChristmas.Direction.DOWN);

			}
			
	}
}
