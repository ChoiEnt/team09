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

public class IroncliffeGuardian extends Unit {
    private static final int COST = 5;
    private static final int ATTACK = 3;
    private static final int HEALTH = 10;

    public static void cast(ActorRef out, GameState gameState, GameService gs, Tile targetTile) {
        // 检查魔法值
        if (gameState.getCurrentPlayer().getMana() < COST) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
            return;
        }

        // 检查目标瓦片是否有效且未被占用（假设需要空瓦片召唤）
        if (targetTile == null || targetTile.isOccupied()) {
            return;
        }

        // 检查是否为 AI 玩家（可选，根据你的设计）
        if (!(gameState.getCurrentPlayer() instanceof AIPlayer)) {
            BasicCommands.addPlayer1Notification(out, "Ironcliffe Guardian is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Ironcliffe Guardian 的 Card 对象
        Card guardianCard = new Card();
        guardianCard.setCardname("Ironcliffe Guardian");
        guardianCard.setUnitConfig(StaticConfFiles.ironcliff_guardian); // 假设配置文件存在
        guardianCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        guardianCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.ironcliff_guardian, gameState.getNextUnitId(), guardianCard, targetTile, gameState.getCurrentPlayer());

        // 应用 Provoke 效果
        applyProvokeEffect(out, gameState, gs, targetTile);

        // 显示通知
        BasicCommands.addPlayer1Notification(out, "Ironcliffe Guardian summoned with Provoke!", 2);
    }

    // 应用 Provoke 效果，限制相邻敌方单位
    private static void applyProvokeEffect(ActorRef out, GameState gameState, GameService gs, Tile guardianTile) {
        // 获取相邻瓦片
        Tile[][] tiles = gameState.getBoard().getTiles();
        int x = guardianTile.getTilex();
        int y = guardianTile.getTiley();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}}; // 包括斜向
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < tiles.length && newY >= 0 && newY < tiles[0].length) {
                Tile adjTile = tiles[newX][newY];
                if (adjTile.isOccupied()) {
                    Unit adjUnit = adjTile.getUnit();
                    // 检查是否为敌方单位
                    if (adjUnit.getOwner() != gameState.getCurrentPlayer()) {
                        adjUnit.setHasProvoke(true); // 设置 Provoke 标记（假设 Unit 有这个方法）
                        gs.checkProvoked(adjUnit); // 调用 GameService 检查 Provoke 状态
                    }
                }
            }
        }
    }
}