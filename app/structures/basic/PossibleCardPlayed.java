package structures.basic;


public abstract class PossibleCardPlayed {
    public Card card;
    public Tile tile;
    public int moveQuality;

    public PossibleCardPlayed(Card card, Tile tile) {
        this.card = card;
        this.tile = tile;
        this.moveQuality = 0;
    }
}