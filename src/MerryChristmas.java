import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;  
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

public class MerryChristmas extends JApplet{  
	
	//TODO create information panel
	//TODO berechne number per row / number of subimages andersrum

	private static final long serialVersionUID = 42;
	private static final int NUMBER_OF_IMAGES = 4;
	private static int NUMBER_OF_SUBIMAGES = 9;
	private static int NUMBER_PER_ROW = (int) Math.sqrt(NUMBER_OF_SUBIMAGES);	
	private static final int TIMER_SPEED = 3;
	private static final int EMPTY_INDICATOR = -1;
	private static final String WELCOME_TEXT = "<html><div style=\"text-align: center;\"><body>↑ Das Feld hier oben kann auch genutzt werden! <br/><br/> Bei Bugs bitte eine fiese Hatermail an den Ersteller senden!</body></html>";
	private static final String WIN_TEXT="<html><div style=\"text-align: center;\"><body>Congratulations! You finished this game! </body></html>";
	
	private static boolean mouseEnabled;
	private static ChristmasLabel[] subimages;
	private static int subimageWidth;
	private static Container costumPane;
	private static boolean isHard;
	
	AudioClip music; 
	
	protected enum Direction {
		LEFT, RIGHT, UP, DOWN
	}
	
	@Override
	public void init() {
		costumPane = new JLabel();
		costumPane.setLayout(null);
		
		BufferedImage startImage = loadStartImage();	
		isHard = false;

		//set applet size
		//surely not the best solution, but it works...
		int appletWidth = startImage.getWidth() + startImage.getWidth()/NUMBER_PER_ROW;
		setSize(appletWidth, startImage.getWidth());
		
		displayStartScreen(startImage);
		
		//music = getAudioClip(getCodeBase(),"../music/song.wav"); 
		//music.play();
	
	}
	
	private void displayStartScreen(BufferedImage image){
		try {
			ChristmasPanel p = new ChristmasPanel(new URL(getCodeBase(), "images/startScreen.png"));
			p.setLayout(null);
			JButton start = new JButton("Start!");
			final BufferedImage img = image;
			start.addActionListener(new ActionListener() {
				
				
				@Override
				public void actionPerformed(ActionEvent e) {
					startGame(img);
					
				}
			});
			start.setBackground(Color.RED);
			start.setForeground(Color.WHITE);
			start.setBounds(283,420,100,50);
			p.setBounds(0, 0, 666, 500);
			p.add(start);
			costumPane.add(p);
			setContentPane(costumPane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startGame(BufferedImage image){
		
		
		//some settings
		costumPane.removeAll();
		costumPane.revalidate();
		costumPane.repaint();

		costumPane = new Container();
		costumPane.repaint();
		enableMouse();
		
		NUMBER_OF_SUBIMAGES = isHard ? 16 : 9;
		NUMBER_PER_ROW = isHard ? 4 : 3;
		int appletWidth = image.getWidth() + image.getWidth()/NUMBER_PER_ROW;
		setSize(appletWidth, image.getWidth());
		
		subimageWidth = image.getWidth() / NUMBER_PER_ROW;
		subimages = new ChristmasLabel[NUMBER_OF_SUBIMAGES];
		
		cutImage(image);
		mixSubimages();
		setBounds();
		
		for (int i = 0; i < subimages.length; i++)
			costumPane.add(subimages[i].getLabel());
		
		initInformationPanel();
			
		
		setContentPane(costumPane);
		getContentPane().revalidate();
		getContentPane().repaint();

	}
	
	private void initInformationPanel(){
		
		JPanel informationPanel = new JPanel();
		informationPanel.setOpaque(true);
		informationPanel.setBackground(new Color(238,232,170));

		informationPanel.setBounds(subimageWidth * NUMBER_PER_ROW, subimageWidth, subimageWidth, subimageWidth * (NUMBER_PER_ROW-1));
		JLabel welcome = new JLabel(WELCOME_TEXT);
		welcome.setPreferredSize(new Dimension(subimageWidth, isHard ? subimageWidth * 2: subimageWidth));
		
		JButton newGame = new JButton("Start new Game!");
		newGame.setBackground(Color.RED);
		newGame.setForeground(Color.WHITE);
		newGame.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BufferedImage image = loadRandomImage();
				startGame(image);				
			}
		});
		
		String s = isHard ? "Hard": "Easy";
		JButton difficulty = new JButton(s);
		difficulty.setBackground(Color.RED);
		difficulty.setForeground(Color.WHITE);
		
		difficulty.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchDifficulty((JButton) e.getSource());
			}
		});
		
		
		informationPanel.add(welcome, BorderLayout.NORTH);
		informationPanel.add(newGame, BorderLayout.CENTER);
		informationPanel.add(difficulty, BorderLayout.SOUTH);
		costumPane.add(informationPanel);
	}
	
	private void switchDifficulty(JButton source){
		isHard = !isHard;
		source.setText(isHard ? "Hard" : "Easy");
		
		source.repaint();
	}
	
	private BufferedImage loadStartImage(){
		try {
			URL url = new URL(getCodeBase(), "images/startImage.png");
			BufferedImage img = ImageIO.read(url);
			return img;
		} catch (IOException e) {}
		return null;
	}
	
	private BufferedImage loadRandomImage() {
		int rnd = 1 + (int) (Math.random()*NUMBER_OF_IMAGES);

		try {
		    URL url = new URL(getCodeBase(), "images/image_" + rnd + ".png");
		    BufferedImage img = ImageIO.read(url);
			return img;
		} catch (IOException e) {}
		
		return null;
	}
	
	private static void cutImage(BufferedImage image) {
		
		//TODO that NUMBER_OF_SUBIMAGES is perfect square number
		//TODO check if image is a square

		ChristmasMouseListener mouse = new ChristmasMouseListener();
		
		for (int yIter = 0; yIter < NUMBER_PER_ROW; yIter++) {
			for (int xIter = 0; xIter < NUMBER_PER_ROW; xIter++) {
				int x = xIter * subimageWidth;
				int y = yIter * subimageWidth;
				
				int subimageIndex = xIter + NUMBER_PER_ROW * yIter;
				
				subimages[subimageIndex] = new ChristmasLabel(new JLabel(
						new ImageIcon(image.getSubimage(x,y,subimageWidth,subimageWidth))),subimageIndex);
				subimages[subimageIndex].getLabel().addMouseListener(mouse);
				subimages[subimageIndex].getLabel().addMouseMotionListener(mouse);
			}
		}
	}
	
	private static void mixSubimages(){

		List<Integer> tmp = new ArrayList<Integer>();
		for (int i = 0; i < NUMBER_OF_SUBIMAGES; i++)
			tmp.add(i);
		
		java.util.Collections.shuffle(tmp);

		for (int i = 0; i < NUMBER_OF_SUBIMAGES; i++)
			subimages[i].setPosition(tmp.get(i));
		
	
		//the upper right image has to be correct
		int swapIndex = Arrays.asList(getPositionArray()).indexOf(NUMBER_PER_ROW - 1);
		int swapValue = subimages[NUMBER_PER_ROW - 1].getPosition();
		subimages[NUMBER_PER_ROW - 1].setPosition(NUMBER_PER_ROW - 1);
		subimages[swapIndex].setPosition(swapValue);
		
		if (!isSolvable())
			mixSubimages();
	}
	
	private static void setBounds(){
		for (int i = 0; i < subimages.length; i++) {				
			subimages[i].getLabel().setBounds(subimageWidth * (subimages[i].getPosition() % NUMBER_PER_ROW), 
				subimageWidth * (subimages[i].getPosition() / NUMBER_PER_ROW), subimageWidth, subimageWidth);
		}
	}
	
	protected static void moveImage(JLabel label, Direction d) {
		
		// get index of label
		int index = computeIndex(label);
		
		// check validity
		if (!isValidMove(index, d)) {
			// unmovable
			enableMouse();
			return;
		}
		
		// create the direction modifier, that will be used by the action listener
		final int xModifier;
		final int yModifier;
		
		if (d == Direction.LEFT) {
			xModifier = -1;
			yModifier = 0;
		}
		else if (d == Direction.RIGHT) {
			xModifier = 1;
			yModifier = 0;
		}
		else if (d == Direction.UP) {
			xModifier = 0;
			yModifier = -1;
		}
		else {
			xModifier = 0;
			yModifier = 1;
		}

		//create a timer, that schedules the animation
		final int subimageIndex = Arrays.asList(MerryChristmas.getPositionArray()).indexOf(index);
		final Timer timer = new Timer(TIMER_SPEED, new ActionListener() {
			
			int count = 0;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				subimages[subimageIndex].getLabel().setLocation(
						subimages[subimageIndex].getLabel().getX() + xModifier, 
						subimages[subimageIndex].getLabel().getY() + yModifier);
				subimages[subimageIndex].getLabel().repaint();

				count++;

				if (count >= subimageWidth) {
					enableMouse();
					((Timer) e.getSource()).stop();
					
					updatePosition(xModifier, yModifier, subimageIndex);
					
					if (hasWon()){
						//TODO put on content pane
						JOptionPane.showMessageDialog(costumPane, WIN_TEXT);
					}
					
				}

			}
		});
		timer.start();
		
	}
	
	private static int computeIndex(JLabel label){
		//make sure, that 'index' has a value
		int index = -1;
		for (int i = 0; i < NUMBER_OF_SUBIMAGES; i++){
			if (subimages[i].getLabel() == label)
				index = subimages[i].getPosition();
		}
		
		return index;
	}
	
	private static boolean isValidMove(int index, Direction d){
		/*
		 * We have to check, whether the label is in the following states:
		 * 1. the subimage could be at the edge
		 * 1.1 but it could be the move to the empty field in the upper right corner
		 * 2. the subimage shall be moved to an non empty field
		 * 2.1 could be the upper right corner
		 */
		
		
		List<Integer> positions = Arrays.asList(getPositionArray());

		if (index == EMPTY_INDICATOR && (d != Direction.LEFT 
				|| positions.contains(NUMBER_PER_ROW - 1))) return false;
		
		// test 1.
		if (d == Direction.LEFT && index % NUMBER_PER_ROW == 0) 	return false;
		if (d == Direction.RIGHT && (index + 1) % NUMBER_PER_ROW == 0 ){
			// test 1.1

			if (index != (NUMBER_PER_ROW - 1) || 
					positions.contains(EMPTY_INDICATOR)) return false;	
		}
		if (d == Direction.UP && index < NUMBER_PER_ROW)	return false;
		if (d == Direction.DOWN && (NUMBER_OF_SUBIMAGES - 1 - index) < NUMBER_PER_ROW) return false;

		// test 2.			
		if (d == Direction.LEFT && positions.contains(index-1)) 				 return false;	
		if (d == Direction.RIGHT) {
			// test 2.1
			if (index == NUMBER_PER_ROW - 1 && positions.contains(EMPTY_INDICATOR))	 return false;
			if (index != NUMBER_PER_ROW - 1 && positions.contains(index + 1)) return false;
		}
		if (d == Direction.UP && positions.contains(index-NUMBER_PER_ROW)) 	 return false;
		if (d == Direction.DOWN && positions.contains(index+NUMBER_PER_ROW)) 	 return false;

		return true;
	}
	
	private static void updatePosition(int x, int y, int index){
		if (subimages[index].getPosition() == EMPTY_INDICATOR){
			subimages[index].setPosition(NUMBER_PER_ROW - 1);
			return;
		}
		List<Integer> positions = Arrays.asList(getPositionArray());
		if (subimages[index].getPosition() == NUMBER_PER_ROW - 1 && !positions.contains(EMPTY_INDICATOR)){
			subimages[index].setPosition(EMPTY_INDICATOR);
			return;
		}

		subimages[index].setPosition(subimages[index].getPosition() + x + NUMBER_PER_ROW * y);
	}
	
	protected static boolean hasWon() {
		//TODO debug mode
		for (int i = 0; i < subimages.length; i++){
			if (subimages[i].getPosition() != i)
				return false;
		}
		return true;

	}
	
	private static Integer[] getPositionArray(){
		Integer[] positions = new Integer[subimages.length];
		for (int i = 0; i < subimages.length; i++)
			positions[i] = subimages[i].getPosition();
		
		return positions;
	}
	
	private static boolean isSolvable(){
		double parity = 1;
		Integer[] positions = getPositionArray();
		
		for (int j = 1; j < positions.length; j++) {
			for (int i = 0; i < j; i++){
				parity *= (((double) positions[j]+1) - ((double) positions[i]+1)) / (((double) j+1) - ((double) i+1));
			}
		}
		
		//if the parity is 1, its solvable
		// ( I take already in account, in which line the empty field is, so this is not a general answer)
		if (parity > 0) return true;
		else 			return false;
	}
	
	protected static void disableMouse(){
		mouseEnabled = false;
	}
	protected static void enableMouse(){
		mouseEnabled = true;
	}
	protected static boolean isMouseEnabled() {
		return mouseEnabled;
	}
	
}
