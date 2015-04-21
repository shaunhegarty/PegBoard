package pegboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;


/**
 * The GameBoard class sets up each of of the graphical components
 * and draws them in a JPanel. It draws rectangles and colours them 
 * based on the values stored in a PegBoard object.
 * 
 * This class also sets up the mouse listeners, places and colours 
 * the individual components
 * 
 * @author Shaun Hegarty
 *
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {
	
	//Set some values to keep some amount of consistency in the display
	final float PEG_HEIGHT = 60f;
	final float PEG_WIDTH = 60f;
	final float PADDING = 5f;
	
	final int EMPTY = 0;
	final int WHITE = 1;
	final int BLACK = -1;
	
	private Rectangle2D[] pegboard;
	private Rectangle2D resetButton;	
	private Rectangle2D solveButton;
	private PegBoard game;
	private int size;
	
	
	////// CONSTRUCTORS //////
	/**
	 * Initialises the board to a default size of 11
	 */
	public GameBoard(){		
		this(11); //default
	}
	
	/**
	 * Creates a new PegBoard object and pulls out the board
	 * @param initSize The number of spaces which will be on the board
	 */
	public GameBoard(int initSize){
		size = initSize;
		game = new PegBoard(size);
		pegboard = new Rectangle2D[size];
		initBoard();
		this.addMouseListener(new PegMover());		
	}
	
	
	/**
	 * Reinitialises the game with the given initSize parameter
	 * @param initSize The new size of the board
	 */
	public void remake(int initSize){
		//remake the board with a new size
		size = initSize;
		game = new PegBoard(size);
		pegboard = new Rectangle2D[size];
		initBoard();
	}
	
	/**
	 * Sets up the board with position of each object
	 */
	private void initBoard(){
		
		for(int i = 0; i < pegboard.length; i++){
			this.pegboard[i] = new Rectangle2D.Float(PADDING + i*PEG_WIDTH + i*PADDING, PADDING, PEG_WIDTH, PEG_HEIGHT);
		}
		
		resetButton = new Rectangle2D.Float(PADDING, 2*PADDING + PEG_HEIGHT, 3*PEG_WIDTH, PEG_HEIGHT);	
		solveButton = new Rectangle2D.Float((float)(resetButton.getX() + resetButton.getWidth()) + PADDING, 2*PADDING + PEG_HEIGHT, 3*PEG_WIDTH, PEG_HEIGHT);	
		
	}
	
	/**
	 * Performs the drawing of each component in appropriate 
	 * colours, fonts etc. along with the text for each button
	 * @param g
	 */
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        Color bright = new Color (255, 150, 00);
        Color dark = new Color(0, 0, 0);

        //remove aliasing on edges of text and curved objects
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        
        //Draw pegs
        int[] board = game.getBoard();
        for(int i = 0; i < pegboard.length; i++){
       	
        	if(i < 0 || i >= board.length || board[i] == EMPTY){
        		continue;
        	}
        	if(board[i] == WHITE){
        		g2d.setColor(bright);
        	} else if (board[i] == BLACK){
        		g2d.setColor(dark);
        	}
        	g2d.fill(pegboard[i]);
        }
        
        //Draw Buttons
        g2d.setColor(new Color(150, 50, 50));
        g2d.fill(resetButton);
        g2d.fill(solveButton);
        
        //Display button text within the buttons
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(240, 240, 240));
        g2d.drawString("RESET", (float) resetButton.getX() + PADDING, (float)(resetButton.getY() + 0.5*(resetButton.getHeight() + g2d.getFont().getSize())));
        g2d.drawString("SOLVE", (float) solveButton.getX() + PADDING, (float)(solveButton.getY() + 0.5*(solveButton.getHeight() + g2d.getFont().getSize())));
        
        //Display number of moves made
        g2d.setColor(dark);
        g2d.drawString("Moves: " + game.getMoves() + " (" + game.minMoves()+ ") ", (float) (solveButton.getX() + solveButton.getWidth() + 2*PADDING), (float)(solveButton.getY() + 0.5*(solveButton.getHeight() + g2d.getFont().getSize())));
        
    }
    
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	doDrawing(g);
    }
	
    
    /**
     * Accessor for board size
     * @return
     */
    public int getBoardSize(){
    	return size;
    }
    
    /**
     * Inner class which manages the clickable objects and animates
     * (in a rudimentary fashion) the solving of the board
     * @author Shaun Hegarty
     *
     */
    class PegMover extends MouseAdapter implements Runnable{
    	
		Thread solver;
    	
		/**
		 * Set up mouse listener for clickable objects
		 */
		@Override
		public void mousePressed(MouseEvent e){
			int x = e.getX();
			int y = e.getY();
			
			//If a piece is clicked, try to make a move. 
			for(int i = 0; i < pegboard.length; i++){
				
				if(pegboard[i].contains(x, y)){
					//If a click occurs inside the bounds of one of the 
					//rectangles attempt to move it to the space
					System.out.println(game.attemptMove(i));
					repaint();
					game.isFinished();
					
					//move peg to empty space if possible
				}
			}
			
			//Click reset button
			if(resetButton.contains(x, y)){
				System.out.println("Resetting");
				game = new PegBoard(size);
				repaint();
			}
			
			//Click solve button
			if(solveButton.contains(x, y)){
				System.out.println("Solving");
				solver = new Thread(this);
				solver.start();
			}
		}
    
		/**
		 * sets up a basic animation for solving 
    	 * no animation for peg movement, just step forward a frame
    	 * for each individual move
		 */
    	@Override
		public void run(){
    		//
    		
    		//Reset the board when solving
			game = new PegBoard(size);
			//Run it in 10 seconds or 200ms per move, whichever is shorted.
			long minFrameTime = 400;
			long timePerMove = (10000/game.minMoves() < minFrameTime) ? 10000/game.minMoves() : minFrameTime;
			while(!game.isFinished()){
				repaint();
				int curr = game.getMoves();
				game.nextMove();
				try{
					if(timePerMove > 15 && curr != game.getMoves()){
						//Delay when the 
						Thread.sleep(timePerMove);
					}					
				} catch (InterruptedException ex){}
				
			}
		}

    }
}