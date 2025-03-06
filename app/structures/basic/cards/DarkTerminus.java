package structures.basic.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameService;
import structures.GameState;
import structures.basic.Card;
import structures.basic.BigCard;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.StaticConfFiles;

public class DarkTerminus {
    private static final int COST = 4;

    public static void cast(ActorRef out, GameState gameState, GameService gs, Unit targetUnit) {
        if (gameState.getCurrentPlayer().getMana() < COST) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
            return;
        }
        if (targetUnit == null || !gameState.getUnits().contains(targetUnit) ||
            targetUnit.getOwner() == gameState.getCurrentPlayer() || 
            targetUnit.getName().equals("AI Avatar")) {
            return;
        }
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        Tile targetTile = targetUnit.getCurrentTile(gameState.getBoard());
        gs.performUnitDeath(targetUnit);

        if (targetTile != null) {
            // 创建临时的 Card 对象来模拟 Wraithling
            Card wraithlingCard = new Card();
            wraithlingCard.setCardname("Wraithling");
            wraithlingCard.setUnitConfig(StaticConfFiles.wraithling);
            wraithlingCard.setId(gameState.getNextUnitId());
            BigCard bigCard = new BigCard();
            bigCard.setHealth(1);
            bigCard.setAttack(1);
            wraithlingCard.setBigCard(bigCard);

            // 使用现有的 summonUnit 方法
            gs.summonUnit(StaticConfFiles.wraithling, gameState.getNextUnitId(), wraithlingCard, targetTile, gameState.getCurrentPlayer());
        }
    }
}