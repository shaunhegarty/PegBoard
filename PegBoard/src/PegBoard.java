/**This class initialises the pegboard and contains methods to 
 * provide a full solution, make individual moves and return various
 * pieces of information about the state of the board
 * 
 * @author Shaun Hegarty
 *
 */



public class PegBoard {
	
	////// INSTANCE VARIABLES //////////
	private int[] board;
	private int moves;
	private int hole;
	private int direction;
	//If n = total slots (n - 1 pegs + 1 space)
	//Total required moves f => f(n) = f(n-2) + n
	//And f(n) = 0 for n<3; n >= 3 and is an odd number
	//if input n is even, it is upped to the next odd number.
	
	
	//Some constants
	//Convenient representations of the colours, empty space, directions
	public final int EMPTY = 0;
	public final int WHITE = 1;
	public final int BLACK = -1;
	
	//Only used internally so kept private
	private final int LEFT = 1;
	private final int RIGHT = -1;

	
	/*public static void main(String[] args){
		
		PegBoard pegs = new PegBoard(11);
		pegs.displayPegs();
		pegs.solve();
	}*/
	
	
	////// CONSTRUCTOR ///////////////
	public PegBoard(int n){
		//Setup the board
		n = 2*(n/2) + 1;
		board = new int[n];
		//Black on the left
		for(int i = 0; i < n/2; i++){
			board[i] = BLACK;
		}
		hole = n/2;
		board[hole] = EMPTY;
		for(int i = n/2 + 1; i < n; i++){
			board[i] = WHITE;
		}	
		direction = LEFT;

	}
	
	/**
	 * Prints a text representation of the current board 
	 * state to the console
	 */
    public void displayPegs(){
        String out;
        for(int i = 0; i < board.length; i++){
            switch(board[i]){
                case BLACK: out = "X";
                            break;
                case WHITE: out = "O";
                            break;
                case EMPTY:  out = "-";
                            break;
                default: out = "";
                            break;                            
            }
            System.out.print(out + " ");
            
        }
        System.out.println("");
    }
    
    
    /**
     * Swaps the pieces at indexA and indexB and adds
     * to the current tally of moves made.
     * Prints the new state to the console
     * Updates the current index of the hole
     * @param indexA
     * @param indexB
     */
    private void swap(int indexA, int indexB){
    	//Swap the values at indexA and indexB on the board
        int temp = board[indexA];
        board[indexA] = board[indexB];
        board[indexB] = temp;
        moves++;
        
        displayPegs();
        hole = getHoleIndex();
    }
    
    
    /**
     * Swaps a piece into the hole, putting the hole 
     * at the piece's previous location
     * @param move the location of the p
     */
    private void move(int move){    	
    	try{
    		swap(hole + move, hole);
    	} catch (ArrayIndexOutOfBoundsException e){
    		System.out.println("Impossible move");
    	}
    	//4 possible moves about the empty spot
    	//Unnecessary complexity but was helpful in conveying my original logic
    	/*switch(move){
    	case LEFT: swap(hole + 1, hole);
    			break;
    	case RIGHT: swap(hole - 1, hole);
    			break;
    	case LEFT * 2: swap(hole + 2, hole);
    			break;
    	case RIGHT * 2: swap(hole - 2, hole);
    			break;
		default: System.out.println("Invalid move attempted.");
				break;    	
    	}*/
    }
    
    public void nextMove(){
    	//White can only move left, Black can only move right
    	//We keep track of the current direction
    	//Move two steps if jumping a piece of a different colour
    	//else move one step in the current direction
    	//else change direction

    	
    	if(		hole + 2*direction >= 0 && hole + 2*direction < board.length 
				&& board[hole + 1 * direction] != board[hole + 2 * direction] 
				&& direction == board[hole + 2*direction])
		{
    			/* Happens if the piece being moved is going in the correct direction
    			 * And it is jumping over a piece of a different colour */    			 
				move(direction * 2);
		} 		
    	
    	//If the piece adjacent to the hole can move in the current direction and be placed
    	//next to a piece of a different colour
		else if(hole >= 1 && hole < board.length - 1
				&& board[hole + 1 * direction] != board[hole - 1 * direction] 
				)
		{			
				move(direction);
		} 
		
    	//Case where hole is at edge of the board or all pegs beyond the hole are in 
		//their desired positions
		else if(	(hole == 0 
					&& direction == LEFT)
				|| 	(hole == board.length - 1 
					&& direction == RIGHT)
				|| 	isPartDone())
		{
				move(direction);
		} 
		
		else {
			changeDirection();
		}   	
    	

    	
    	
    	/*OLD LOGIC, partially works but not optimal when it does
    	 *The way the move method works has also changed 
    	 * 
    	 * if(hole + 1 < board.length && board[hole + 1] == WHITE){
    		move(1);
    	}
    	
    	if(isFinished()){
    		return;
    	}
    	if(hole - 2 >= 0 && board[hole - 2] == BLACK){
    		move(4);
    	} else {
    		move(3);
    		while(board[hole - 2] != WHITE && board[hole - 1] != WHITE && board[hole + 1] != WHITE){
    			move(3);
    		}
    		if(board.length/2%2 != 0){
    			move(2);
    			move(4);
    		}
    	}   */	
    }

    
    public boolean attemptMove(int i){
		//For playing the game in the GUI. 
		//Only performs the move if the move is valid. 
		if(Math.abs(i - hole) <= 2 && i != hole && i >= 0 && i < board.length){
			move(i - hole);
			return true;
		}
		return false;
	}

	private boolean isPartDone(){
		//returns true if everything to *direction* side of the hole is 
		//in the end state
		for(int i = hole - direction; i >= 0 && i < board.length ; i -= direction){
			if(board[i] != direction){    			
				return false;
			}
		}
		
		return true;
	}

	private void changeDirection(){    	
		if(direction == LEFT){
			direction = RIGHT;
		} else {
			direction = LEFT;
		}
	}

	public void solve(){
    	while(!isFinished()){
    		nextMove();
    	}
    	
    }
    
    public boolean isFinished(){
    	if(board[board.length/2] != EMPTY){
    		return false;
    	}
    	for(int i = 0; i < board.length/2; i++){
    		if(board[i] != WHITE){
    			return false;
    		}
    	}
    	System.out.println("Done in " + moves + " !");
    	return true;
    }
    
    ////// Accessors /////////////
    
    public int[] getBoard(){
    	return board;
    }
    
    public int getMoves(){
    	return moves;
    }
    
    public int getHoleIndex(){
		//return the current index of the empty space on the board
	    for(int i = 0; i < board.length; i++){
	        if(board[i] == EMPTY){
	            return i;
	        }
	    }
	    return -1;
	}
    
    //Minimum moves to solve for a board of a given size
	public int minMoves(){
    	return minMoves(board.length);
    }
    
    public int minMoves(int curr){
    	if(curr%2 == 0){
    		System.out.println("Board size must be odd.");
    		return 0;
    	}
    	if(curr < 3){
    		return 0;
    	} else {
    		return minMoves(curr - 2) + curr;
    	}
    }
}

/*    	long beforeTime = System.nanoTime();
    	long afterTime = System.nanoTime();
    	System.out.println("Time taken: " + (afterTime - beforeTime) + " ns");*/
