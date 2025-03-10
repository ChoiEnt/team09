package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;
import structures.basic.Card;
import structures.basic.cards.Wraithling;
import structures.basic.Player.AIPlayer;
import structures.basic.Player.HumanPlayer;
import structures.basic.Player.Player;

import java.util.*;

/**
 * This class can be used to hold information about the on-going game. Its
 * created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	

	public boolean gameInitalised = false;

	public boolean isGameFinished = false;

	// Keep track of the player currently taking their turn
	private Player currentPlayer;

	// Keep track of the card that is currently clicked
	private Card currentCardClicked;
	// Keep track of the position of the card that is currently clicked
	private int currentCardPosition;

	// Keep track of the previous plays of the current turn
	private Stack<Actionable> actionHistory;
	// Keep track of the total number of units on the board
	private int totalUnits = 0;

	// Entity objects that are part of the game state
	public GameService gameService;
	private Player human;
	private Player ai;
	private Board board;
	private ArrayList<Unit> unitsOnBoard =  new ArrayList<Unit>();

	public boolean something;
	/**
	 * This function initialises all the assets Board, Player etc As well as
	 * tracking critical game states
	 * 
	 * @param out
	 */

	public void init(ActorRef out) {
						
		this.gameService = new GameService(out, this);
		this.board = gameService.loadBoard();

		// Initialize stack of action history
		this.actionHistory = new Stack<>();

		// Create the human and AI players
		this.human = new HumanPlayer();
		this.ai = new AIPlayer(this);

		// Health initialised to 20
		gameService.updatePlayerHealth(human,20);
		gameService.updatePlayerHealth(ai,20);

		// Player mana initialised to 2
		gameService.updatePlayerMana(human, 2);

		// Create the human and AI avatars
		gameService.loadAvatar(board, human);
		gameService.loadAvatar(board, ai);
		// gameService.loadUnitsForTesting(ai);

		// Set the current player to the human player
		this.currentPlayer = human;

		//Drawing initial 3 cards from the deck for the game start
		gameService.drawCards(human,3);
		gameService.drawCards(ai,3);
	}

	// Switch the current player
	public void switchCurrentPlayer() {
		if (this.currentPlayer == this.human) {
			this.currentPlayer = this.ai;
		} else {
			this.currentPlayer = this.human;
		}
	}

	/**
	 * This will be called when endTurn event
	 * is clicked, to manage the states
	 */
	public void endTurn(){
		handleCardManagement();
		currentPlayer.incrementTurn();
		this.gameService.updatePlayerMana(currentPlayer, 0);
		switchCurrentPlayer();
		this.gameService.updatePlayerMana(currentPlayer, currentPlayer.getTurn() + 1);
	}

	
	/**
	 * This will handle the cards in the deck
	 * and the hand of the player
	 * used when the endTurn is clicked 
	 */
	public void handleCardManagement() {
		try {
			if (currentPlayer.getHand().getNumberOfCardsInHand() >= 6) {
				// Discard the top card from the hand if it's at maximum size.
				if((currentPlayer.getDeck()).getDeck().isEmpty()) {
					} else {
					currentPlayer.getDeck().drawCard();
				}

			} else {
				// The hand is not full, draw a new card.
				gameService.drawCards(currentPlayer, 1);
			}
		}
		catch (IllegalStateException e){
				// Handle the exception, for example by displaying an error message or logging
				System.err.println("Cannot draw a card: " + e.getMessage());
		}
	}


	
	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void addUnitstoBoard(Unit unit) {
		this.unitsOnBoard.add(unit);
		}

	public Player getHuman() {
		return this.human;
	}

	public Player getAi() {
		return this.ai;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	public Player getInactivePlayer() {
		if (currentPlayer == human) {
			return ai;
		} else {
			return human;
		}
	}

	public Card getCurrentCardClicked() {
		return currentCardClicked;
	}

	public void setCurrentCardClicked(Card card) {
		currentCardClicked = card;
	}

	public int getCurrentCardPosition() {
		return currentCardPosition;
	}

	public void setCurrentCardPosition(int position) {
		currentCardPosition = position;
	}

	public Stack<Actionable> getActionHistory() {
		return actionHistory;
	}

	public int getTotalUnits() {
		return totalUnits;
	}

	public void addToTotalUnits(int numberToAdd) {
		this.totalUnits += numberToAdd;
	}

	public void removeFromTotalUnits(int numberToRemove) {
		this.totalUnits -= numberToRemove;
	}
	
	public int getNextUnitId() {
	    return totalUnits + 1; // 简单生成唯一 ID
	}

	// Get all the units on the board
	public ArrayList<Unit> getUnits() {
		ArrayList<Unit> combinedUnits = new ArrayList<>();
		combinedUnits.addAll(ai.getUnits());
		combinedUnits.addAll(human.getUnits());
		return combinedUnits;
	}


	/**
	 * Checks and see if the game has ended If so it will send the apropiate
	 * notifcation
	 * 
	 * @param out
	 */
	public void endGame(ActorRef out) {
		if (this.ai != null && this.ai.getHealth() == 0) {
			BasicCommands.addPlayer1Notification(out, "You Won!", 1000);
			isGameFinished = true;
		} else if (this.human != null && this.human.getHealth() == 0) {
			BasicCommands.addPlayer1Notification(out, "You Lost", 1000);
			isGameFinished = true;
		}
	}
}