import java.util.List;
import java.util.Random;

/**
 * A simple rule-based player for heads-up badugi.
 * 
 * @author Johnny Zhu
 */
public class BadugiPlayerAi04 implements BadugiPlayer
{
    private static int count = 0;
    private int id;
    private String nick = "2 Chainz 04";
    private int position;
    private int previousRaises;
    private int totalRaises; // total number of bets and raises in the entire hand
    private Random rng = new Random();

    private int player_win = 0;
    private int player_loss= 0;
    private int player_tie= 0;

    public BadugiPlayerAi04() { this.id = ++count; }
    public BadugiPlayerAi04(String nick) { this.nick = nick; }
 
    
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
        
    }
    
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
    	
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	int checkCall=0;
    	int betRaise=1;
    	int foldCheck=-1;
    	boolean isCorrectSize;
    	boolean didOpponentRaise=false;
    	int opponentDrawStatus;

    	prints("my active hand -----"+activeHand);
    	previousRaises=totalRaises;
    	if(toCall > 0) { totalRaises++; }
    	

 //   	prints("total raises    "+totalRaises + "previous rasied     " + previousRaises);
    	prints("HUMAN asshole drew      " +opponentDrew);
    	
    	
    	if (discardHand.size() < opponentDrew){
    		prints("Opponent Drew more");
    		return 1;
    	}else if ((discardHand.size() > opponentDrew) &&(opponentDrew != -1)){
    		prints("Opponent Drew less");
    		return -1;
    	}else if (discardHand.size() == opponentDrew){
    		prints("Opponent Same");
    		return 0;
    	}
    	
 //   	opponentDrawStatus = CheckOpponentDraw(discardHand.size(),opponentDrew);
   // 	prints("opponentDrawStatus" +opponentDrawStatus );
    	
//   //ROUND ONE
//    	if (drawsRemaining == 3){
//    		
//    			if(activeHand.size() ==4){
//    				prints("Size 4---" + activeHand);
//    				return checkCall;
//    			} else if (activeHand.size()==3) {
//    				prints("Size 3---" + activeHand);
//    				isCorrectSize = CheckCardRank(activeHand,7);
//    				if (isCorrectSize == true){
//
//    					return checkCall;
//    				}else{
//    					return checkCall;
//    				}
//    			}else if (activeHand.size()==2) {
//    				prints("Size 2---" + activeHand);
//    				isCorrectSize = CheckCardRank(activeHand,6);
//    				if (isCorrectSize){
//    					return checkCall;
//    				}else{
//    					return checkCall;
//    				}
//    			}else if (activeHand.size()==1) {
//    				prints("Size 1---" + activeHand);
//    				isCorrectSize = CheckCardRank(activeHand,6);
//    				if (isCorrectSize == true){
//    					return foldCheck;
//    				}else{
//    					return foldCheck;
//    				}
//    			}else if (activeHand.size()==0) {
//    				prints("Size 0---" + activeHand);
//    				didOpponentRaise = CheckIfOpponentRaise(totalRaises,previousRaises);
//    				isCorrectSize = CheckCardRank(activeHand,6);
//    				if (isCorrectSize == true){
//    					return foldCheck;
//    				}else{
//    					return foldCheck;
//    				}
//    			}
//    	}			
//   //ROUND TWO   			
//    	if (drawsRemaining == 2){
//			if(activeHand.size() ==4){
//				
//				prints("Size 4---" + activeHand);
//				
//				return betRaise;
//			} else if (activeHand.size()==3) {
//				prints("Size 3---" + activeHand);
//				return checkCall;
//			}else if (activeHand.size()==2) {
//				prints("Size 2---" + activeHand);
//				return checkCall;
//			}else if (activeHand.size()==1) {
//				prints("Size 1---" + activeHand);
//				return foldCheck;
//
//
//			}else if (activeHand.size()==0) {
//				prints("Size 0---" + activeHand);
//				return foldCheck;
//
//			}
//    	}	
//    	
//    //ROUND THREE   	
//    	
//    	if (drawsRemaining == 1){
//    		
//			if(activeHand.size() ==4){
//				prints("Size 4---" + activeHand);
//				return betRaise;
//			} else if (activeHand.size()==3) {
//				prints("Size 3---" + activeHand);
//				return checkCall;
//
//			}else if (activeHand.size()==2) {
//				prints("Size 2---" + activeHand);
//				return foldCheck;
//
//			}else if (activeHand.size()==1) {
//				prints("Size 1---" + activeHand);
//				return foldCheck;
//
//			}else if (activeHand.size()==0) {
//				prints("Size 0---" + activeHand);
//				return foldCheck;
//			}
//    	}	  
//
//    //ROUND FOUR   	
//    	
//    	if (drawsRemaining == 0){
//    		
//			if(activeHand.size() ==4){
//				prints("Size 4---" + activeHand);
//				return betRaise;
//			} else if (activeHand.size()==3) {
//				prints("Size 3---" + activeHand);
//				return checkCall;
//
//			}else if (activeHand.size()==2) {
//				prints("Size 2---" + activeHand);
//				return foldCheck;
//
//			}else if (activeHand.size()==1) {
//				prints("Size 1---" + activeHand);
//				return foldCheck;
//
//			}else if (activeHand.size()==0) {
//				prints("Size 0---" + activeHand);
//				return foldCheck;
//
//
//			}
//    	}	
    	
    			
    			
    			
    		    	//else if(activeHand.size()==3){
//        			for(Card c : activeHand) {
//        				if (c.getRank() >= 6){
//        					return checkCall;  
    			
  
//    	if (drawsRemaining == 3){
//    		if(activeHand.size() ==4){
//    		prints("BADUGIII ---------" + activeHand);
//    		prints("");
//    			//return betRaise;
//    		}
    	//else if(activeHand.size()==3){
//    			for(Card c : activeHand) {
//    				if (c.getRank() >= 6){
//    					return checkCall;
   	//	}
//    		
//    		
 	
    //	prints ("drawsRemaining"+drawsRemaining);


    	return 0;
    }
    
    public int CheckOpponentDraw(int yourDrew, int opponentDrew ){
    	
    	if (yourDrew < opponentDrew){
    		prints("Opponent Drew more");
    		return 1;
    	}else if (yourDrew > opponentDrew){
    		prints("Opponent Drew less");
    		return -1;
    	}else if (yourDrew == opponentDrew){
    		prints("Opponent Same");
    		return 0;
    	}
    	
    	
    	return -1;
    } 
    
    public boolean CheckCardRank(List<Card> activeHand,int rank){
    	

		for(Card c : activeHand){
			if (c.getRank() <= rank){
				prints("below"+rank+"---" +c.getRank());
			} else{
				return false;
			}				
		}
		return true;
    	
    }
    public boolean CheckIfOpponentRaise(int totalRaises,int previousRaises){
    	
    	if (totalRaises !=previousRaises){return true;}
    	return false;
    }
    
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
    	
    	List<Card> activeHand = hand.getActiveCards();
    	List<Card> discardHand = hand.getInactiveCards();
    	
    	//prints("-------AI'S HAND IS  " + hand+ " ----|AI'S activeHand IS " + activeHand +"----|AI'S discardHand IS " + discardHand);
		if ((activeHand.size() <=2) && (drawsRemaining ==3)) {
			for(Card c : activeHand) {
				if (c.getRank() >= 7){
					discardHand.add(c);
//					prints("yes2");
//					prints("AI'S discardHand IS " + discardHand);
				}				   				
    		} 
    	}
    			
    		

         

//    	prints("--------------AI'S HAND IS  " + hand+ "|AI'S activeHand IS " + activeHand +"|AI'S discardHand IS " + discardHand);
    	
        return discardHand;
     //   return hand.getInactiveCards();
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
//        prints("WINS   " + player_win + "|LOSS    " + player_loss + "|tie    " + player_tie);
//        System.out.println("WINS   " + player_win + "|LOSS    " + player_loss + "|tie    " + player_tie);
    	
    }
    
    public String getAgentName() { 
        if(nick != null) { return nick; } else { return "Ruleplayer #" + id; }
    }
    
    public String getAuthor() { return "Zhu, Johnny"; }
    
    public void prints(String message){
    //    System.out.println(message);       
    }
}
