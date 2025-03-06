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
import utils.StaticConfFiles;

public class SkyrockGolem extends Unit {
    private static final int COST = 2;    // 成本为 2
    private static final int ATTACK = 4;  // 初始攻击力为 4
    private static final int HEALTH = 2;  // 初始生命值为 2

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
            BasicCommands.addPlayer1Notification(out, "Skyrock Golem is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Skyrock Golem 的 Card 对象
        Card golemCard = new Card();
        golemCard.setCardname("Skyrock Golem");
        golemCard.setUnitConfig(StaticConfFiles.skyrock_golem); // 假设配置文件存在
        golemCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        golemCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.skyrock_golem, gameState.getNextUnitId(), golemCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位
        Unit golem = targetTile.getUnit();
        if (golem != null) {
            golem.setName("Skyrock Golem"); // 确保名称正确
            BasicCommands.addPlayer1Notification(out, "Skyrock Golem summoned!", 2);
        }
    }
}
