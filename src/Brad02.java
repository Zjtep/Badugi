import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
/**
 * Created by Bradley A. Chow on 11/10/2016.
 */
public class Brad02 implements BadugiPlayer {
    private final String author = "Chow, Bradley";
    private final String nickname = "Badugi Agent: 500377584";
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
    public Brad02() {
        //put any inits here
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
        //System.out.println("----------------------new hand -------------------------");
        //System.out.println("Folded Count " + folded);
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
        //System.out.println("Hand: " + hand + " discard pile: " + discardPile.toString() + " draws remaining: " + drawsRemaining + " did we draw: " + weDrew);
 
        potSize = pot;
 
        //default to call
        int betDecision = 0;
 
        //increases raises if raised.
        updateRaiseCount(toCall);
 
        //decreases cardsindeck count if opponent drew
        updateDeckCount(opponentDrew);
 
        /*
        * Main Decision Branches
        */
 
        //if we have a badugi
        if (hand.getInactiveCards().size() == 0) {
            if (totalRaises < 5 || potSize < 25) {
                //System.out.println("we raise");
                betDecision = +1;
                betted++;
            } else {
                betDecision = 0;
                called++;
            }
            return betDecision;
        }
 
        if (opponentDrew == 0 && drawsRemaining <= 1) {
            if (hand.getActiveCards().size() >= 3) {
                if (hand.getActiveRanks()[0] <= 11) {
                    called++;
                    betDecision = 0;
                } else {
                    folded++;
                    betDecision = -1;
                }
            } else {
                folded++;
                betDecision = -1;
            }
            return betDecision;
        }
 
 
        if (weDrew) {
            if (drawsRemaining == 3) {
                betDecision = 0;
                called++;
            }
            if (drawsRemaining == 2) {
                betDecision = 0;
                called++;
            }
            if (drawsRemaining == 1) {
                betDecision = 0;
                called++;
            }
            if (drawsRemaining == 0) {
                if (hand.getActiveCards().size() <= 2) {
                    betDecision = -1;
                    folded++;
                    return betDecision;
                }
            }
            return betDecision;
 
        }
 
        if (!weDrew && drawsRemaining < 3) {
            if (hand.getActiveCards().size() <= 2) {
                betDecision = -1;
                folded++;
            } else if (hand.getActiveCards().size() == 3) {
                if (totalRaises < 3 || potSize < 10) {
                    betDecision = +1;
                    betted++;
                } else {
                    betDecision = 0;
                    called++;
                }
            } else if (hand.getActiveCards().size() == 4) {
                if (totalRaises < 7 || potSize < 25) {
                    betDecision = +1;
                    betted++;
                }
 
            }
            return betDecision;
        }
 
        //System.out.println("We folded by default: " + hand + " discard pile: " + discardPile.toString() + " draws remaining: " + drawsRemaining + " did we draw: " + weDrew);
 
        //if there are no decisions and defaults, then return call
        return betDecision;
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
        weDrew = false;
        ArrayList<Card> drawCards = new ArrayList<Card>();
        //System.out.println("Active Cards: " + hand.getActiveCards() + " Inactive Cards: " + hand.getInactiveCards());
 
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
 
        if (hand.getInactiveCards().size() >= 2) {
            for (Card card : hand.getActiveCards()) {
                if (card.getRank() > 8) {
                    cardsInDeck--;
                    weDrew = true;
                    discardPile.add(card);
                    drawCards.add(card);
                }
            }
            for (Card card : hand.getInactiveCards()) {
                cardsInDeck--;
                weDrew = true;
                discardPile.add(card);
                drawCards.add(card);
            }
            return drawCards;
        }
 
        if (hand.getInactiveCards().size() == 1) {
            if (drawsRemaining > 2) {
                for (Card card : hand.getAllCards()) {
                    if (card.getRank() >= 11) {
                        drawCards.add(card);
                        weDrew = true;
                    }
                }
            }
            for (int i = 0; i < hand.getInactiveCards().size(); i++) {
                Card currentConsideredCard = hand.getInactiveCards().get(i);
                //need to write a check
                Double cardOdds = getInactiveCurrentOdds(currentConsideredCard, hand, drawsRemaining);
                //System.out.println("Rank for Card: " + currentConsideredCard + " is " + currentConsideredCard.getRank());
                //System.out.println("Odds for card : " + currentConsideredCard + " is " + cardOdds);
                if (cardOdds >= (14 * drawsRemaining) && hand.getInactiveCards().size() >= 1) {
                    weDrew = true;
                    drawCards.add(hand.getInactiveCards().get(i));
                    cardsInDeck--;
                    discardPile.add(hand.getInactiveCards().get(i));
                }
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
 
        //System.out.println("Showdown seen by human player " + getAgentName() + ".");
//        System.out.println("Your hand at showdown: " + yourHand);
//        System.out.println("Opponent hand at showdown: " + opponentHand);
        //System.out.println("Pot Size: " + potSize);
        if (yourHand.compareTo(opponentHand) > 0) {
            sdWon++;
            winPotSizeTotal = winPotSizeTotal + potSize;
            //System.out.println("We won :" + sdWon + " avg win pot size " + (double) (winPotSizeTotal / (sdWon + sdLost)));
 
        } else if (yourHand.compareTo(opponentHand) < 0) {
            sdLost++;
            lossPotSizeTotal = lossPotSizeTotal + potSize;
            //System.out.println("We lost :" + sdLost + " avg loss pot size " + (double) (lossPotSizeTotal / (sdWon + sdLost)));
        } else {
            sdTied++;
            //System.out.println("We tied :" + sdTied);
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
        int outs = 10;
        int rankDiff = 13 - card.getRank();
        outs -= rankDiff;
        for (int i = 0; i < discardPile.size(); i++) {
            if (card.badugiConflict(discardPile.get(i))) {
                outs--;
            }
        }
 
        return (2 * outs * drawsRemaining);
    }
 
    /**
     * Updates the totalRaise count based on if the call is 0 or not
     * @param toCall 0 if the call is a check, else positive int if the call costs more
     */
    private void updateRaiseCount(int toCall){
        if (toCall > 0) {
            totalRaises++;
        }
    }
 
    private void updateDeckCount(int cardsDrew){
        if (cardsDrew > -1) {
//            System.out.println("The opponent drew " + opponentDrew + " cards in the previous draw.");
            cardsInDeck -= cardsDrew;
        }
    }
}