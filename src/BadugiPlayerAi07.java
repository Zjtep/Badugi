import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple rule-based player for heads-up badugi.
 * 
 * @author Johnny Zhu
 */
public class BadugiPlayerAi07 implements BadugiPlayer
{
    private static int count = 0;
    private int id;
    private String nick = "Katy Perry 07";
    private int position;
    private int previousRaises;
    private int totalRaises; // total number of bets and raises in the entire hand
    private int ttotalRaise;
    private Random rng = new Random();

    private int player_win = 0;
    private int player_loss= 0;
    private int player_tie= 0;

    private int handsToGo=0;
    private int currentScore=0;


    private int myOpponentDrew = 0;
    private int playStyle = 0;

    private boolean shouldWeDraw = true;


    
    public BadugiPlayerAi07() { this.id = ++count; }
    public BadugiPlayerAi07(String nick) { this.nick = nick; }
 
    
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
        this.handsToGo=handsToGo;
        this.currentScore = currentScore;
        ttotalRaise=0;
        
                
//    	if (handsToGo%2==0){   		
//    		this.currentScore = currentScore;
//    	}else {
//    		this.currentScore = -(currentScore);
//
//    	}

//        System.out.println("------------------");

        //System.out.println("pos+currentScore  "+position+"|"+this.currentScore+"|");
        
    }
    
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
    	
  //  	if(toCall > 0) { ttotalRaise++; }
      //  if (ttotalRaise==4){
      //  	System.out.println("pot     "+pot);
//        }   
    		
   //     System.out.println("ttotalRaise" + ttotalRaise);
    	
    	
    	//;if (numCurrentScore < 5)
   // 	potSize = pot/2;
    	
    	myOpponentDrew = opponentDrew;
//    	prints2("drawsRemaining   " + drawsRemaining);
//    	prints2("bets   " + bets);
//    	prints2("pot      " + pot);
//    	prints2("ToCall   " + toCall);
//    	prints2("potSize     " + potSize);
    	

//
//         System.out.println("Current Score: " + currentScore + " Position: " + position);
//         System.out.println("Current Score: " + myCurrentScore);
    	
//
    	prints2("handsToGo    " + handsToGo);
    	prints2("currentScore " + currentScore);
    	
    	
//    	if (currentScore>(handsToGo+1)){
//    		
////    		return -1;
//    		playStyle=0;
//    		prints2("WE THROWING");
//    	
//    		return -1;
//    	}

    		
    	
    	
    	return playStyleStandard(drawsRemaining, hand, bets, pot, toCall, opponentDrew);
    	
   // 	return 1;
    	
    
    }
    
    
    
    public int playStyleAggressive(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew){
    	 


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
    

    	if(toCall > 0) { totalRaises++; }

    	
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
    				
    				isCorrectSize = CheckCardRank(activeHand,6);
    				if (isCorrectSize){
    					return checkCall;
    				}else{
    					return checkCall;
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
		    		
		    		return checkCall;
		    	}else if (discardHand.size() > opponentDrew){
		    		
		    		return checkCall;
		    		
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
		    		
		    		return checkCall;
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
			    		
			    		return checkCall;
			    	}else if (discardHand.size() > opponentDrew){
//			    		System.out.println("we foldCheck");
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
    	
//    	List<Card> discardHand = hand.getInactiveCards();
    	if (shouldWeDraw == false){
    		ArrayList<Card> drawCards = new ArrayList<Card>();
    		shouldWeDraw = true;
			return drawCards;
		}
    	
    	
//    	if (playStyle == 0){
//    		return drawingActionStandard (drawsRemaining,hand,pot,dealerDrew);
//    		
//    	}else if(playStyle ==1){
//    		return drawingActionAggressive(drawsRemaining,hand,pot,dealerDrew);
//    		
//    		
//    	}
    	
    	return drawingActionStandard (drawsRemaining,hand,pot,dealerDrew);
    	
  //  	return discardHand;
    }
    
    
	public List<Card> drawingActionStandard(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
		
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	
//    	prints(getAgentName()  +"|Hand=" + hand+ "|activeHand=" + activeHand +"|discardHand=" + discardHand);
   

   
    	if (drawsRemaining ==3){
    		
    		if (activeHand.size() <=2) {
    			for(Card c : activeHand) {
    				if (c.getRank() >= 8){
    					discardHand.add(c);
//    					prints("yes2");
    					prints("AI'S discardHand IS " + discardHand);
    				}				   				
        		} 
        	}
    		
    		 if (activeHand.size() ==4) {
     			for(Card c : activeHand) {
    				if (c.getRank() > 10){
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
//    	prints("--------------AI'S HAND IS  " + hand+ "|AI'S activeHand IS " + activeHand +"|AI'S discardHand IS " + discardHand);
    	
        return discardHand;
	
	}
	
	public List<Card> drawingActionAggressive(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
		
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	
    	prints("WE PLAYING drawingActionAggressive");
    	
        return discardHand;
	
	}
    
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {
    	
//        if (yourHand.compareTo(opponentHand) > 0) {
//        	player_win++;
//        	

//        } else if (yourHand.compareTo(opponentHand) < 0) {
//        	player_loss++;

//        } else {
//        	player_tie++;
//        }
        

     //   System.out.println("Your hand at showdown: " + yourHand);
      //  System.out.println("Opponent hand at showdown: " + opponentHand);
       // prints("WINS   " + player_win + "|LOSS    " + player_loss + "|tie    " + player_tie);
//        System.out.println("WINS   " + player_win + "|LOSS    " + player_loss + "|tie    " + player_tie);
    	
    }
    
    public String getAgentName() { 
        if(nick != null) { return nick; } else { return "Ruleplayer #" + id; }
    }
    
    public String getAuthor() { return "Zhu, Johnny"; }
    
    public void prints(String message){
//        System.out.println(message);       
    }
    public void prints2(String message){
//          System.out.println(message);       
      }
}
