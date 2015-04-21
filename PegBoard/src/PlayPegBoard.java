
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
 * 
 * @author Shaun Hegarty
 * 
 * This class runs the GUI for the peg board. It allows the user
 * to watch the the computer solve the board or to attempt it 
 * themselves.
 * 
 * It does not permit the user to perform any illegal moves. 
 * The board can be reset by clicking the Reset button.
 * The board can be solved by clicking the Solve button.
 * The board can be resized by pressing up or down on the keyboard
 *
 */
		
@SuppressWarnings("serial")
public class PlayPegBoard extends JFrame {
	private GameBoard game;
	private int size;
	
	
	public PlayPegBoard(){
		setTitle("Play Super PegBoard");
		size = 11; //default
		game = new GameBoard(size);
		
		//Anonymous classes for key bindings
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
		
		inputMap.put(KeyStroke.getKeyStroke("UP"), "upAction");
		actionMap.put("upAction", upAction);
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "downAction");
		actionMap.put("downAction", downAction);
		
		add(game);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);	
		setSize(1024, 300);
		
	}
	
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


    



