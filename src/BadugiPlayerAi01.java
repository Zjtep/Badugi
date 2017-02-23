import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BadugiPlayerAi01 implements BadugiPlayer {


    private int position;
    private int totalRaises; // total number of bets and raises in the entire hand
    private Random rng = new Random();
    private int numHandsToGo=0;
    private int numCurrentScore=0;


    
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
        this.position = position; totalRaises = 0;
        numHandsToGo=handsToGo;
        numCurrentScore=currentScore;
    }
    
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
    	
//        if(toCall > 0) { totalRaises++; }
//        int beatsIdx = 0;
//        if(opponentDrew < 0) { opponentDrew = 0; }
//        while(beatsIdx < 5 && hand.compareTo(thresholdHands[drawsRemaining][beatsIdx]) > 0) { beatsIdx++; }
//        int off = beatsIdx - bets - totalRaises / 3 + position + opponentDrew - 2;
//        double ran = rng.nextDouble();
//        if(off < 0) { // Looks like we are behind
//            if(ran < .6) { return -1; }
//            if(ran < .8) { return 0; }
//            return +1; // raise as a bluff anyway
//        }
//        else if(off == 0) { // Looks like we are par
//            if(ran < .1) { return -1; }
//            if(ran < .7) { return 0; }
//            else return +1;
//        }
//        else { // Looks like we are ahead, ram and jam
//            if(ran < .5 - .2 * off) { return 0; }
//            else return +1;
//        }
//    	System.out.println("numHandsToGo    "+numHandsToGo);
//    	System.out.println("currentScore     "+numCurrentScore);
    	return 1;
    }
    
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
    	
//    	List<Card> activeHand = hand.getActiveCards();
//    	List<Card> discardHand = hand.getInactiveCards();
//    	
//    	for(Card c : activeHand) {
//    		if ((c.getRank() >= 6) && (drawsRemaining ==3)) {
//    			System.out.println("INININININININ");
//    			discardHand.add(c);
//    		}
//    		//System.out.println("CARD " + c + " VALUE IS " + c.getRank() );
//         }
//    	Prints("AI'S HAND IS  " + hand);
//    	Prints("AI'S activeHand IS " + activeHand);
//    	Prints("AI'S discardHand IS " + discardHand);
//        return discardHand;
       return hand.getInactiveCards();
    }
    

    
    
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) { }
    
    public String getAgentName() { 
        return "Lil Wayne 01";
    }
    
    public String getAuthor() { return "Zhu, Johnny"; }
    
    public void Prints(String message){
        System.out.println(message);       
    }


}





