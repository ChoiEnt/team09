package structures.basic;

import structures.basic.Card;
import structures.basic.cards.Wraithling;
import structures.basic.Player.HumanPlayer;
import structures.basic.Player.Player;
import utils.OrderedCardLoader;

import java.util.List;
import structures.basic.Player.*;
import utils.OrderedCardLoader;
import java.util.List;

public class Deck {
    private List<Card> deck;

    public Deck(Player player) {
        // Check if the player is an instance of HumanPlayer
        if (player instanceof HumanPlayer) {
            this.deck = OrderedCardLoader.getPlayer1Cards(2); // Deck for HumanPlayer
        } else {
            this.deck = OrderedCardLoader.getPlayer2Cards(2); // Deck for AI or other player
        }
    }

    public boolean isEmpty() {
        return this.deck.isEmpty();
    }

    public Card drawCard() {
        if (deck.isEmpty()) {
            System.out.println("Deck is empty");
            return null;
        }
        return deck.remove(0);
    }

    public List<Card> getDeck() {
        return deck;
    }
}