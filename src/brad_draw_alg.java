import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Bradley A. Chow on 11/10/2016.
 */
public class brad_draw_alg implements BadugiPlayer {
    private final String author = "Chow, Bradley";
    private final String nickname = "Badugi Agent: Draw Alg";
    private final int numPlayers = 2;

    private int position;
    private int handsToGo;
    private int currentScore;
    private int cardsInDeck;
    private int ourActiveCardCount;
    private int potSize;
    private boolean weDrew;
    private int totalRaises;
    private Random rng = new Random();


    //test variables
    private int folded = 0;
    private int foldedbydefault = 0;
    private int called = 0;
    private int betted = 0;
    private int AIbadugicount = 0;
    private int sdWon = 0;
    private int sdLost = 0;
    private int sdTied = 0;
    private int winPotSizeTotal = 0;
    private int lossPotSizeTotal = 0;


    private List<Card> discardPile;
    private double opponentAggresionLvl;
    private double selfAggresionLvl;

    /**
     * Default Constructor for Badugi Agent
     */
    public brad_draw_alg() {
        //put any inits here
    }

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


    /**
     * The method to tell the agent that a new hand is starting.
     *
     * @param position     0 if the agent is the dealer in this hand, 1 if the opponent.
     * @param handsToGo    The number of hands left to play in this heads-up tournament.
     * @param currentScore The current score of the tournament.
     */
    public void startNewHand(int position, int handsToGo, int currentScore) {
        this.position = position;
        this.handsToGo = handsToGo;
        this.currentScore = currentScore;
        potSize = 0;
        cardsInDeck = 52 - (4 * numPlayers);
        discardPile = new ArrayList<Card>();
        ourActiveCardCount = 0;
        weDrew = false;
        totalRaises = 0;

    }

    /**
     * The method to ask the agent what betting action it wants to perform.
     *
     * @param drawsRemaining How many draws are remaining after this betting round.
     * @param hand           The current hand held by this player.
     * @param bets           How many bets and raises there have been in this betting round.
     * @param pot            The current size of the pot.
     * @param toCall         The cost to call to stay in the pot.
     * @param opponentDrew   How many cards the opponent drew in the previous drawing round. In the
     *                       first betting round, this argument will be -1.
     * @return The desired betting action given as an integer whose sign determines the action. Any negative
     * number means FOLD/CHECK, zero means CHECK/CALL, and any positive number means BET/RAISE.
     */
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
    	return 0;
    }

    /**
     * The method to ask the agent which cards it wants to replace in this drawing round.
     *
     * @param drawsRemaining How many draws are remaining, including this drawing round.
     * @param hand           The current hand held by this player.
     * @param pot            The current size of the pot.
     * @param dealerDrew     How many cards the dealer drew in this drawing round. When this method is called
     *                       for the dealer, this argument will be -1.
     * @return The list of cards in the hand that the agent wants to replace.
     */
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
        ArrayList<Card> drawCards = new ArrayList<Card>();

        if (dealerDrew > -1) {
            cardsInDeck--;
        }

        //check if we have a 4-hand
        if (hand.getInactiveCards().isEmpty()) {
            if (drawsRemaining == 3) {
                for (Card card : hand.getActiveCards()) {
                    //face cards to be rerolled
                    if (card.getRank() > 12) {
                        cardsInDeck--;
                        weDrew = true;
                        discardPile.add(card);
                        drawCards.add(card);
                    }
                }
            }
            return drawCards;
        }

        for (int i = 0; i < hand.getInactiveCards().size(); i++) {
            Card currentConsideredCard = hand.getInactiveCards().get(i);
            //need to write a check
            Double cardOdds = getInactiveCurrentOdds(currentConsideredCard, hand, drawsRemaining);
            if (cardOdds >= (12 * drawsRemaining) && hand.getInactiveCards().size() >= 1) {
                weDrew = true;
                drawCards.add(hand.getInactiveCards().get(i));
                cardsInDeck--;
                discardPile.add(hand.getInactiveCards().get(i));
            }
        }
        if (drawCards.size() == 0) {
            weDrew = false;
        }
        return drawCards;
    }


    /**
     * The agent observes the showdown at the end of the hand.
     *
     * @param yourHand     The hand held by this agent.
     * @param opponentHand The hand held by the opponent.
     */
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {

        if (yourHand.compareTo(opponentHand) > 0) {
            sdWon++;
            winPotSizeTotal = winPotSizeTotal + potSize;

        } else if (yourHand.compareTo(opponentHand) < 0) {
            sdLost++;
            lossPotSizeTotal = lossPotSizeTotal + potSize;
        } else {
            sdTied++;
        }
    }

    /**
     * Returns the nickname of this agent.
     *
     * @return The nickname of this agent.
     */
    public String getAgentName() {
        return nickname;
    }

    /**
     * Returns the author of this agent. The name should be given in the format "Last, First".
     *
     * @return The author of this agent.
     */
    public String getAuthor() {
        return author;
    }

    private double getInactiveCurrentOdds(Card card, BadugiHand hand, int drawsRemaining) {
        int outs = 10;
        for (int i = 0; i < discardPile.size(); i++) {
            if (card.badugiConflict(discardPile.get(i))) {
                outs--;
            }
        }

        return (2 * outs * drawsRemaining);
    }

    private double getActiveCurrentOdds(Card card, BadugiHand hand, int drawsRemaining) {
        int outs = 13;
        int rankDiff = 13 - card.getRank();
        outs -= rankDiff;
        for (int i = 0; i < discardPile.size(); i++) {
            if (card.badugiConflict(discardPile.get(i))) {
                outs--;
            }
        }

        return (2 * outs * drawsRemaining);
    }
}