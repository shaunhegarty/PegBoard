/*Screencast https://youtu.be/aIoD-PvHgU8 */

package pegboard;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * This class runs the GUI for the peg board. It allows the user
 * to watch the the computer solve the board or to attempt it 
 * themselves.
 * 
 * It does not permit the user to perform any illegal moves. 
 * The board can be reset by clicking the Reset button.
 * The board can be solved by clicking the Solve button.
 * The board can be resized by pressing up or down on the keyboard.
 * 
 * @author Shaun Hegarty  14212390
 *
 */
		
@SuppressWarnings("serial")
public class PlayPegBoard extends JFrame {
	////// INSTANCE VARIABLES //////
	private GameBoard game;
	private int size;
	
	////// CONSTRUCTORS //////
	/**
	 * Creates a new GameBoard object at a default size of 11.
	 * Sets up key listeners for the up and down keys. 
	 */
	public PlayPegBoard(){
		setTitle("Play Super PegBoard");
		size = 11; //default
		game = new GameBoard(size);
		
		//Anonymous classes for key binding actions. Rebuild the board
		//with a different size.
		Action upAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				size += 2;
				game.remake(size);
				game.repaint();
			}
		};
		
		Action downAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				if(!(size - 2 < 3)){
					size -= 2;
				}
				game.remake(size);
				game.repaint();
			}
		};
		
		//Capture key presses when the window is focused
		InputMap inputMap = game.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = game.getActionMap();
		
		//Map the keypresses to the actions
		inputMap.put(KeyStroke.getKeyStroke("UP"), "upAction");		
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "downAction");
		actionMap.put("upAction", upAction);
		actionMap.put("downAction", downAction);
		
		//Place the GameBoard JPanel onto the JFrame
		add(game);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);	
		setSize(1024, 300);
		
	}
	
	////// MAIN METHOD //////
	/**
	 * Main method which runs the program
	 * @param args
	 */
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				PlayPegBoard pb = new PlayPegBoard();
				pb.setVisible(true);
			}
		});
	}
}


    



