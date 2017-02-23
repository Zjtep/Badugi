
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple rule-based player for heads-up badugi.
 * 
 * @author Johnny Zhu
 */
public class BadugiPlayerAi11 implements BadugiPlayer
{
    private static int count = 0;
    private int id;
    private String nick = "500405991";
    private int position;
    private int previousRaises;
    private int totalRaises; // total number of bets and raises in the entire hand
    private int ttotalRaise;
    private Random rng = new Random();

    private boolean isNewGame = false;
    private int player_win = 0;
    private int player_loss= 0;
    private int player_tie= 0;
    
    private int player_bluff_win=0;
    private int player_bluff_loss=0;

    private int handsToGo=0;
    private int currentScore=0;
    private int lastCurrentScore=0;

    private int myOpponentDrew = 0;
    private int playStyle = 0;

    private boolean shouldWeDraw = true;
    private boolean weBluffing = false;
 

   private boolean bluff_01_inUse=false;
   private double bluff_01_rngValue=0.5;
   
   private boolean bluff_02_inUse=false;
   private double bluff_02_rngValue=0.5;

   
 
    
    public BadugiPlayerAi11() { this.id = ++count; }
    public BadugiPlayerAi11(String nick) { this.nick = nick; }
 
    
    private static String[][] thresholdString = {
        // 0 draws remaining
        { "Kh7h6d5c", "9h8d7s6c", "8h7d6s5c", "7h6d5s4c", "6h5d4s3c" },
        
        // 1 draw remaining
        { "Kh9h8s7d", "Kh7h6d5c", "9h8d7s6c", "8h7d6s5c", "7h6d5s4c" },
        
        // 2 draws remaining
        { "KhQh3h2s", "Kh9h8s7d", "Kh7h6d5c", "KhQdJsTc", "9h8d7s6c" },
        
        // 3 draws remaining
        { "KhQh8h4s", "KhQh3h2s", "Kh9h8s7d", "Kh7h4d3s", "KhQdJsTc" }
       
    };
    
    private static BadugiHand[][] thresholdHands = new BadugiHand[4][5];
    static { // static initializer block is executed once when JVM loads the class bytecode 
        for(int draws = 0; draws < 4; draws++) {
            for(int bets = 0; bets < 5; bets++) {
                thresholdHands[draws][bets] = new BadugiHand(thresholdString[draws][bets]);
            }
        }
    }
    
    public void startNewHand(int position, int handsToGo, int currentScore) {
        this.position = position; totalRaises = 0;previousRaises=0;
        this.handsToGo =handsToGo;
        lastCurrentScore=this.currentScore; 
        this.currentScore = currentScore;
        ttotalRaise=0;
        
 
	/**
	 * Used for old agent.jar file
	 */        
//    	if (handsToGo%2==0){   		
//    		this.currentScore = currentScore;
//    	}else {
//    		this.currentScore = -(currentScore);
//
//    	}
    	
    	
    	
	/**
	 * Records how many wins/losses it has in total
	 * REcords its bluff wins/losses seperately to see if its actually working
	 * Resets stats when playing a new agent
	 */
    	if (isNewGame==false){
        	if (lastCurrentScore<this.currentScore){
        		player_win++;
        		if (weBluffing == true){player_bluff_win++;}
        		
    			if (bluff_01_inUse== true){
    				bluff_01_rngValue=UpdateBetRngValue(true,bluff_01_rngValue);
    				bluff_01_inUse=false;
    			}else if (bluff_02_inUse== true){
    				bluff_02_rngValue=UpdateBetRngValue(true,bluff_02_rngValue);
    				bluff_02_inUse=false;
    			}
        		
        	}else if (lastCurrentScore>this.currentScore){
        		player_loss++;
        		if (weBluffing == true){player_bluff_loss++;}
        		
    			if (bluff_01_inUse== true){
    				bluff_01_rngValue=UpdateBetRngValue(false,bluff_01_rngValue);
    				bluff_01_inUse=false;
    			}else if (bluff_02_inUse== true){
    				bluff_02_rngValue=UpdateBetRngValue(false,bluff_02_rngValue);
    				bluff_02_inUse=false;
    			}
        		
        		
        		
        	}else if (lastCurrentScore==this.currentScore){
        		player_tie++;
        	}	
    	}else if(isNewGame==true){
    		isNewGame=false;
    		player_win=0;
    		player_loss=0;
    		player_tie=0;
    		player_bluff_win=0;
    		player_bluff_loss=0;
    		
    		bluff_01_inUse=false;
    		bluff_01_rngValue=0.5;
    		
    		bluff_02_inUse=false;
    		bluff_02_rngValue=0.5;
    		
    		
    	}

    	
        if (handsToGo==0){
        	isNewGame=true;
        }
    	
        weBluffing = false;
        
    }
    
    
	/**
	 * The method Updating the bluffing value depending if you win or lose
	 * @param isWin Did you win or lose the game
	 * @param rngValue the bluff value for that specific bluff
	 * @return the updated rng value depending if you win or lose
	 */ 
    public double UpdateBetRngValue(boolean isWin,double rngValue){

    	double updateRngValue=0;
    	
    	if (isWin==true){
    		updateRngValue = rngValue + 0.2;
    		
    	}else if(isWin==false){
    		updateRngValue = rngValue - 0.2;
    		
    	}
    	
    	if(updateRngValue>=0.99){
    		updateRngValue=0.99;
    	}else if (updateRngValue <=0){
    		updateRngValue=0.0;
    		
    	}
    	
    	return updateRngValue;
    }
    
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
    	
    	
    	myOpponentDrew = opponentDrew;
    	double ran = rng.nextDouble();

//         System.out.println("Current Score: " + currentScore + " Position: " + position);
//         System.out.println("Current Score: " + currentScore);

    	
    	/**
    	 * If our score is good enough to win by folding the rest of our  hands. We will fold the rest of the game
    	 */
    	if (currentScore>(handsToGo+1)){
    		

    		playStyle=0;
    		//prints2("WE THROWING");
    	
    		return -1;
    }	
    		
    		return playStyleStandard(drawsRemaining, hand, bets, pot, toCall, opponentDrew);

    	
    
    }
    
    
    
    public int playStyleAggressive(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew){
    	 
    	
    	//TODO
    	
//    	double ran = rng.nextDouble();

    	playStyle=1;
    	prints("WE PLAYING playStyleAggressive");
   
    	
    	return 0;    	
    	
    }
    
    
    
    public int playStyleStandard(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew){
 
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	int checkCall=0;
    	int betRaise=1;
    	int foldCheck=-1;
    	boolean isCorrectSize;

    	playStyle = 0;
//    	prints3("toCall+bets" +toCall+"|"+bets);
    	
    	/**
    	 * Checks if we are bluffing. If we are It will not draw and stay pat.
    	 * If we get called for our bluff we will fold
    	 */
    	if (weBluffing ==true){

    		if (toCall==8)
    		{

    			return foldCheck;
    		}
    		prints2("we bluffing");
    		shouldWeDraw = false;
    		return betRaise;
    	}
    	
    	
    	if(toCall > 0) { totalRaises++; }

    	double ran = rng.nextDouble();
    	
	/**
	 * Our betting action depending on  
	 * 		how many betting rounds left
	 * 		How well is our hand
	 *		How well we draw 
	 *		How well our opponent draws
	 *Depending on our analysis we will bet accordingly 
	 */
   //ROUND ONE
    	if (drawsRemaining == 3){
    		
    			if(activeHand.size() ==4){
    				
    				isCorrectSize = CheckCardRank(activeHand,7);
    				if (isCorrectSize == true){

    					return betRaise;
    				}else{
    					return checkCall;
    				}
    				
    			} else if (activeHand.size()==3) {
    				
    				isCorrectSize = CheckCardRank(activeHand,6);
    				if (isCorrectSize == true){

    					return betRaise;
    				}else{
    					return checkCall;
    				}
    			}else if (activeHand.size()==2) {
    				
    				isCorrectSize = CheckCardRank(activeHand,5);
    				if (isCorrectSize){
    					return checkCall;
    				}else{
    					return foldCheck;
    				}
    			}else if (activeHand.size()==1) {
    				
    				isCorrectSize = CheckCardRank(activeHand,6);
    				if (isCorrectSize == true){
    					return foldCheck;
    				}else{
    					return foldCheck;
    				}
    			}else if (activeHand.size()==0) {
    				
    				isCorrectSize = CheckCardRank(activeHand,6);
    				if (isCorrectSize == true){
    					return foldCheck;
    				}else{
    					return foldCheck;
    				}
    			}
    	}			
    	
   //ROUND TWO   			
    	if (drawsRemaining == 2){
    			

    		
			if(activeHand.size() ==4){
				
				
				
				return betRaise;
			} else if (activeHand.size()==3) {
				
			    if (discardHand.size() < opponentDrew){
		    		
    				isCorrectSize = CheckCardRank(activeHand,5);
    				if (isCorrectSize == true){

    					return betRaise;
    				}else{
    					return checkCall;
    				}
		    	}else if (discardHand.size() == opponentDrew){
		    		
		    		
		    		isCorrectSize = CheckCardRank(activeHand,9);
    				if (isCorrectSize){
    					return checkCall;
    				}else{
//    					if(ran < bluff_02_rngValue) { weBluffing =true;bluff_02_inUse=true;shouldWeDraw = false;return betRaise; }
    					return foldCheck;
    				}


		    	}else if (discardHand.size() > opponentDrew){
		    		
    				isCorrectSize = CheckCardRank(activeHand,9);
    				if (isCorrectSize){
    					return checkCall;
    				}else{


    					return foldCheck;
    				}
		    		
		    	}
			}else if (activeHand.size()==2) {
				
			    if (discardHand.size() < opponentDrew){
		    		
			    	return checkCall;
		    	}else if (discardHand.size() == opponentDrew){
		    		
		    		
		    		return checkCall;
		    	}else if (discardHand.size() > opponentDrew){
		    		
		    		return foldCheck;
		    		
		    	}
			}else if (activeHand.size()==1) {
				
				
			    if (discardHand.size() < opponentDrew){
		    		

		    		return foldCheck;
		    	}else if (discardHand.size() == opponentDrew){

		    		
		    		return foldCheck;
		    	}else if (discardHand.size() > opponentDrew){
		

		    		return foldCheck;
		    	}
				return foldCheck;


			}else if (activeHand.size()==0) {
				
				return foldCheck;

			}
    	}	
    	
    //ROUND THREE   	
    	
    	if (drawsRemaining == 1){
    		
			if(activeHand.size() ==4){
				
				return betRaise;
			} else if (activeHand.size()==3) {
				
			    if (discardHand.size() < opponentDrew){
		    		
    				isCorrectSize = CheckCardRank(activeHand,5);
    				if (isCorrectSize == true){

    					return betRaise;
    				}else{
    					return checkCall;
    				}
		    	}else if (discardHand.size() == opponentDrew){
	

    				isCorrectSize = CheckCardRank(activeHand,4);
    				if (isCorrectSize == true){

    					return betRaise;
    				}else{
    					return checkCall;
    				}
		    	}else if (discardHand.size() > opponentDrew){
		    		
		    		
		    		return foldCheck;
		    	}

			}else if (activeHand.size()==2) {
				
				
			    if (discardHand.size() < opponentDrew){
		    		

		    		return foldCheck;
		    	}else if (discardHand.size() == opponentDrew){
		    																							//bluff 
		    		
	    			
//	    			if(ran < bluff_01_rngValue) { weBluffing =true;bluff_01_inUse=true;shouldWeDraw = false;return betRaise; }
//	    			if(ran < 0.99) { weBluffing =true;bluff_01_inUse=true;shouldWeDraw = false;return betRaise; }
	    			
		    		return foldCheck;
		    	}else if (discardHand.size() > opponentDrew){
		    		
		    		if (opponentDrew !=0){
		    			
//		    			if(ran < bluff_02_rngValue) { weBluffing =true;bluff_02_inUse=true;shouldWeDraw = false;return betRaise; }
//		    			if(ran < 0.99) { weBluffing =true;bluff_02_inUse=true;shouldWeDraw = false;return betRaise; }
		    		}
		    		
		    		return foldCheck;
		    	}
			    return foldCheck;

			}else if (activeHand.size()==1) {
				

		    		return foldCheck;
		    	

			}else if (activeHand.size()==0) {
				
				return foldCheck;
			}
    	}	  

    //ROUND FOUR   	
    	
    	if (drawsRemaining == 0){
			if(activeHand.size() ==4){
				
				return betRaise;
			} else if (activeHand.size()==3) {
				
			    	if (discardHand.size() < opponentDrew){
			    		
	    				isCorrectSize = CheckCardRank(activeHand,5);
	    				if (isCorrectSize == true){

	    					return betRaise;
	    				}else{
	    					return checkCall;
	    				}
			    	}else if (discardHand.size() == opponentDrew){
			    		
	    				isCorrectSize = CheckCardRank(activeHand,4);
	    				if (isCorrectSize == true){

	    					return betRaise;
	    				}else{
	    					return checkCall;
	    				}
			    	}else if (discardHand.size() > opponentDrew){
			    		
			    		return foldCheck;
			    	}
		    		
			}else if (activeHand.size()==2) {
				
				return foldCheck;

			}else if (activeHand.size()==1) {
				
				return foldCheck;

			}else if (activeHand.size()==0) {
				
				return foldCheck;


			}
    	}	
    	
    	return 0;    	
    	
    }
    

	/**
	 * this method checks if our hand lower then the card rank specified 
	 * @param activeHand Passes in what we currently have in our hand
	 * @parama rank Passes in what card rank we are checking for
	 * @return Returns true if indeed our active hand is better then the rank
	 */
    public boolean CheckCardRank(List<Card> activeHand,int rank){
    	

		for(Card c : activeHand){
			if (c.getRank() <= rank){
		//		prints("below"+rank+"---" +c.getRank());
			} else{
				return false;
			}				
		}
		return true;
    	
    }

    
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {

    	/**
    	 * This will make us not draw if we are bluffing
    	 */
    	if (shouldWeDraw == false){
    		ArrayList<Card> drawCards = new ArrayList<Card>();
    		shouldWeDraw = true;
			return drawCards;
		}
    	
    	return drawingActionStandard (drawsRemaining,hand,pot,dealerDrew);

    }
    
    
    
	public List<Card> drawingActionStandard(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
		
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	
  
    	/**
    	 * this decides how we will draw
    	 * Depending on how many draws remaining and how well your hand is, this will discard more cards
    	 */
    	if (drawsRemaining ==3){
    		
    		if (activeHand.size() <=2) {
    			for(Card c : activeHand) {
    				if (c.getRank() >= 8){
    					discardHand.add(c);
    					prints("AI'S discardHand IS " + discardHand);
    				}				   				
        		} 
        	}



   		 if (activeHand.size() ==4) {
  			for(Card c : activeHand) {
 				if (c.getRank() >= 10){  //10
 					discardHand.add(c);
 					prints("LOW BADUGI " + discardHand);
 				}	
 			}	 
 		 }
    		

    		 
    		 if (activeHand.size() ==3) {
      			for(Card c : activeHand) {
     				if (c.getRank() >= 9){
     					discardHand.add(c);
     					
     				}	
     			}	 
     		 }
    		
    		
    	}
    	
    	if (drawsRemaining ==2){
    		
    		if (activeHand.size() <=2) {
    			for(Card c : activeHand) {
    				if (c.getRank() >= 9){
    					discardHand.add(c);
    					prints("AI'S discardHand IS " + discardHand);
    				}				   				
        		} 
        	}
    		
   		 if (activeHand.size() ==3) {
   			for(Card c : activeHand) {
  				if (c.getRank() >=10){
  					discardHand.add(c);
  					
  				}	
  			}	 
  		 }
   		 
   		 if (activeHand.size() ==4) {
  			for(Card c : activeHand) {
 				if (c.getRank() >= 10){  //10
 					discardHand.add(c);
 					prints("LOW BADUGI " + discardHand);
 				}	
 			}	 
 		 }
    		
    		
    	}
    	
    	if (drawsRemaining ==1){
      		 if (activeHand.size() ==4) {
       			for(Card c : activeHand) {
      				if (c.getRank() >=10){  //10
      					discardHand.add(c);
      					prints("LOW BADUGI " + discardHand);
      				}	
      			}	 
      		 }
      		 
      		 
       		 if (activeHand.size() ==3) {
        			for(Card c : activeHand) {
       				if (c.getRank() >=10){
       					discardHand.add(c);
       					
       				}	
       			}	 		
       		 }
       		 
     		if (activeHand.size() <=2) {
    			for(Card c : activeHand) {
    				if (c.getRank() >= 9){
    					discardHand.add(c);
    					prints("AI'S discardHand IS " + discardHand);
    				}				   				
        		} 
        	}
    		
    	}
    	

    	
        return discardHand;
	
	}
	
	public List<Card> drawingActionAggressive(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
		//TODO
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	
    	prints("WE PLAYING drawingActionAggressive");
    	
        return discardHand;
	
	}
    
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {
    	
//        if (yourHand.compareTo(opponentHand) > 0) {
//        	player_win++;        
//        } else if (yourHand.compareTo(opponentHand) < 0) {
//        	player_loss++;
//        } else {
//        	player_tie++;
//        }
        

     //   System.out.println("Your hand at showdown: " + yourHand);
      //  System.out.println("Opponent hand at showdown: " + opponentHand);
       // prints("WINS   " + player_win + "|LOSS    " + player_loss + "|tie    " + player_tie);
//        System.out.println("WINS=" + player_win + "|LOSS=" + player_loss + "|tie=" + player_tie);
    	
    }
    
    public String getAgentName() { 
        if(nick != null) { return nick; } else { return "Ruleplayer #" + id; }
    }
    
    
    
    public String getAuthor() { return "Zhu, Johnny"; }
    
    //Testing purposes
    public void prints(String message){
//        System.out.println(message);       
    }
    public void prints2(String message){
//          System.out.println(message);       
      }
    public void prints3(String message){
//        System.out.println(message);       
    }
}


//TODO
//class BluffType {
//    private boolean inUse;
//    private double rngValue;
//
//    // Constructor or setter
//    BluffType(boolean inUse, double rng) {
//
//        this.inUse = inUse;
//        this.rngValue = rng;
//    }
//
//    // getters
//
//    public boolean getInUse() {
//        return this.inUse;
//    }
//
//    public double getRngValue() {
//        return this.rngValue;
//    }
//}
