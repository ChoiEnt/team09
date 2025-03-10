package structures.basic.cards;

import akka.actor.ActorRef;
import structures.Actionable;
import structures.GameState;
import structures.basic.Card;

public class CardAction {
  GameState gameState;
  int handPosition;

  public CardAction(GameState gameState, int handPosition) {
    this.gameState = gameState;
    this.handPosition = handPosition;
    // prepares reflects the action in the frontend
    preAction();
  }

  /**
  * works reflects the cardClicked on the frontend
  *
  */
  private void preAction() {

    // CLear all highlighted tiles
    gameState.gameService.removeHighlightFromAll();

    // Set the current card clicked to the card at the specified position in the
    // player's hand
    gameState.gameService.setCurrentCardClickedAndHighlight(handPosition);
  }

  /**
  * This will prepare the front end
  * If the card is a "creature card"
  */
  public void creaturePreAction() {

    Card currentCard = gameState.getCurrentCardClicked();

    // Highlight the summon range of the current card clicked
    // Highlight the summon range of the current card clicked
    gameState.gameService.highlightSummonRange();

    // Push the current card clicked to the action history
    gameState.getActionHistory().push((Actionable) currentCard);

    // For debug
    System.out.println("Pushed to action history: " + currentCard.getCardname() + " " + currentCard.getId());
  }

  /**
  * This will call the actions
  * specific to the spell card for the
  * front end and the backend
  */
  public void spellPreAction() {
    preAction();

    Card currentCard = gameState.getCurrentCardClicked();
    if (!currentCard.isCreature) {
      gameState.gameService.highlightSpellRange(currentCard, gameState.getHuman());

      // Push the current card clicked to the action history
      gameState.getActionHistory().push((Actionable) currentCard);

      // For debug
    }

  }

}