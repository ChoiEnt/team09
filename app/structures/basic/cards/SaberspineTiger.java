package structures.basic.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameService;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Player.AIPlayer;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SaberspineTiger extends Unit {
    private static final int COST = 3;
    private static final int ATTACK = 3;
    private static final int HEALTH = 2;

    public static void cast(ActorRef out, GameState gameState, GameService gs, Tile targetTile) {
        // 检查魔法值
        if (gameState.getCurrentPlayer().getMana() < COST) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
            return;
        }

        // 检查目标瓦片是否有效且未被占用
        if (targetTile == null || targetTile.isOccupied()) {
            return;
        }

        // 检查是否为 AI 玩家
        if (!(gameState.getCurrentPlayer() instanceof AIPlayer)) {
            BasicCommands.addPlayer1Notification(out, "Saberspine Tiger is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Saberspine Tiger 的 Card 对象
        Card tigerCard = new Card();
        tigerCard.setCardname("Saberspine Tiger");
        tigerCard.setUnitConfig(StaticConfFiles.saberspine_tiger); // 假设配置文件存在
        tigerCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        tigerCard.setBigCard(bigCard);

        // 召唤单位并应用 Rush 能力
        gs.summonUnit(StaticConfFiles.saberspine_tiger, gameState.getNextUnitId(), tigerCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位（通过瓦片获取）
        Unit tiger = targetTile.getUnit();
        if (tiger != null) {
            // 应用 Rush 能力：允许立即移动和攻击
            tiger.setMovedThisTurn(false);
            tiger.setAttackedThisTurn(false);
            BasicCommands.addPlayer1Notification(out, "Saberspine Tiger summoned with Rush!", 2);
        }
    }
}