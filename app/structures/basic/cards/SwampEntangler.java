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

public class SwampEntangler extends Unit {
    private static final int COST = 1;    // 成本为 1
    private static final int ATTACK = 0;  // 初始攻击力为 0
    private static final int HEALTH = 3;  // 初始生命值为 3

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
            BasicCommands.addPlayer1Notification(out, "Swamp Entangler is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Swamp Entangler 的 Card 对象
        Card entanglerCard = new Card();
        entanglerCard.setCardname("Swamp Entangler");
        entanglerCard.setUnitConfig(StaticConfFiles.swamp_entangler); // 假设配置文件存在
        entanglerCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        entanglerCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.swamp_entangler, gameState.getNextUnitId(), entanglerCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位
        Unit entangler = targetTile.getUnit();
        if (entangler != null) {
            entangler.setName("Swamp Entangler"); // 确保名称正确
            entangler.setHasProvoke(true); // 设置 Provoke 标记（假设 Unit 类有此方法）
            applyProvokeEffect(out, gameState, gs, targetTile); // 应用 Provoke 效果
            BasicCommands.addPlayer1Notification(out, "Swamp Entangler summoned with Provoke!", 2);
        }
    }

    // 应用 Provoke 效果，限制相邻敌方单位
    private static void applyProvokeEffect(ActorRef out, GameState gameState, GameService gs, Tile entanglerTile) {
        int x = entanglerTile.getTilex();
        int y = entanglerTile.getTiley();

        // 定义所有方向（包括上下左右和斜向）
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}};

        // 遍历相邻的瓦片
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // 检查瓦片是否在边界内
            if (newX >= 0 && newX < gameState.getBoard().getTiles().length &&
                newY >= 0 && newY < gameState.getBoard().getTiles()[0].length) {
                Tile adjTile = gameState.getBoard().getTile(newX, newY);

                // 检查瓦片是否被敌方单位占据
                if (adjTile.isOccupied()) {
                    Unit adjUnit = adjTile.getUnit();
                    if (adjUnit.getOwner() != gameState.getCurrentPlayer()) {
                        adjUnit.setHasProvoke(true); // 设置 Provoke 标记（假设 Unit 类有此方法）
                        gs.checkProvoked(adjUnit); // 调用 GameService 检查 Provoke 状态
                    }
                }
            }
        }
    }
}