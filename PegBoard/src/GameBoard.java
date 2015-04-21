import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameBoard extends JPanel {
	
	final float PEG_HEIGHT = 60f;
	final float PEG_WIDTH = 60f;
	final float PADDING = 5f;
	
	final int EMPTY = 0;
	final int WHITE = 1;
	final int BLACK = -1;
	
	private Rectangle2D[] pegboard;
	private Rectangle2D resetButton;	
	private Rectangle2D solveButton;
	private int[] board;
	private PegBoard game;
	
	public GameBoard(){
		this(11); //default
	}
	
	public GameBoard(int size){
		game = new PegBoard(size);
		board = game.getBoard();
		System.out.println(game.minMoves());
		pegboard = new Rectangle2D[board.length];
		this.addMouseListener(new PegMover());
		initBoard();
	}
	
	public void remake(int size){
		//remake the board when the size has to change
		game = new PegBoard(size);
		board = game.getBoard();
		pegboard = new Rectangle2D[board.length];
		initBoard();
	}
	
	private void initBoard(){
		//initialise the game board
		for(int i = 0; i < pegboard.length; i++){
			this.pegboard[i] = new Rectangle2D.Float(PADDING + i*PEG_WIDTH + i*PADDING, PADDING, PEG_WIDTH, PEG_HEIGHT);
		}
		
		resetButton = new Rectangle2D.Float(PADDING, 2*PADDING + PEG_HEIGHT, 3*PEG_WIDTH, PEG_HEIGHT);	
		solveButton = new Rectangle2D.Float((float)(resetButton.getX() + resetButton.getWidth()) + PADDING, 2*PADDING + PEG_HEIGHT, 3*PEG_WIDTH, PEG_HEIGHT);	
		
	}
	
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
        board = game.getBoard();
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
	
    public int getBoardSize(){
    	return board.length;
    }
    
    /**
     * Inner class which manages the clickable objects and animates
     * (in a rudimentary fashion) the solving of the board
     * @author Shaun
     *
     */
    class PegMover extends MouseAdapter implements Runnable{
    	//Set up mouse listener for clickable objects
    	
		Thread solver;
    	
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
				game = new PegBoard(board.length);
				board = game.getBoard();
				repaint();
			}
			
			//Click solve button
			if(solveButton.contains(x, y)){
				System.out.println("Solving");
				solver = new Thread(this);
				solver.start();
			}
		}
    
    	@Override
		public void run(){
    		//sets up a basic animation for solving 
    		//no animation for peg movement, just step forward a frame
    		//for each individual move
    		
    		//Reset the board when solving
			game = new PegBoard(board.length);
			board = game.getBoard();
			//Run it in 10 seconds or 200ms per move, whichever is shorted.
			long minFrameTime = 400;
			long timePerMove = (10000/game.minMoves() < minFrameTime) ? 10000/game.minMoves() : minFrameTime;
			while(!game.isFinished()){
				repaint();
				int curr = game.getMoves();
				game.nextMove();
				board = game.getBoard();
				try{
					if(timePerMove > 15 && curr != game.getMoves()){
						//Delay when the 
						Thread.sleep(timePerMove);
					}					
				} catch (InterruptedException ex){}
				
			}
		}
		
		public void movePeg(Rectangle2D peg, float increment){
			peg.setRect(peg.getX() - increment, peg.getY(), peg.getWidth(), peg.getHeight());
		}
    }
}