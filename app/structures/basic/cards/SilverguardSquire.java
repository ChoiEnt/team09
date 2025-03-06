package structures.basic.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameService;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Player.AIPlayer;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SilverguardSquire extends Unit {
    private static final int COST = 1;    // 成本为 1
    private static final int ATTACK = 1;  // 初始攻击力为 1
    private static final int HEALTH = 1;  // 初始生命值为 1

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
            BasicCommands.addPlayer1Notification(out, "Silverguard Squire is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Silverguard Squire 的 Card 对象
        Card squireCard = new Card();
        squireCard.setCardname("Silverguard Squire");
        squireCard.setUnitConfig(StaticConfFiles.silverguard_squire); // 假设配置文件存在
        squireCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        squireCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.silverguard_squire, gameState.getNextUnitId(), squireCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位
        Unit squire = targetTile.getUnit();
        if (squire != null) {
            squire.setName("Silverguard Squire"); // 确保名称正确，用于触发 Opening Gambit
            BasicCommands.addPlayer1Notification(out, "Silverguard Squire summoned!", 2);
        }
    }

 // 触发 Opening Gambit 效果
    public static void applyOpeningGambit(ActorRef out, GameState gameState, GameService gs, Tile avatarTile) {
        // 获取玩家 Avatar 的位置
        int x = avatarTile.getTilex();
        int y = avatarTile.getTiley();

        // 定义前后方向（假设前后方向为上下）
        int[][] directions = {{0, -1}, {0, 1}}; // 上下方向

        // 遍历相邻的前后方瓦片
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // 检查瓦片是否在边界内
            if (newX >= 0 && newX < gameState.getBoard().getTiles().length && 
                newY >= 0 && newY < gameState.getBoard().getTiles()[0].length) {
                Tile adjTile = gameState.getBoard().getTile(newX, newY);

                // 检查瓦片是否被友方单位占据
                if (adjTile.isOccupied()) {
                    Unit adjUnit = adjTile.getUnit();
                    if (adjUnit.getOwner() == gameState.getCurrentPlayer()) {
                        // 提升攻击力和生命值
                        int newAttack = adjUnit.getAttack() + 1;
                        int newHealth = adjUnit.getHealth() + 1;
                        int newMaxHealth = adjUnit.getMaxHealth() + 1;

                        gs.updateUnitAttack(adjUnit, newAttack);
                        gs.updateUnitHealth(adjUnit, newHealth);
                        gs.updateUnitHealth(adjUnit, newMaxHealth);

                        // 播放增益动画
                        EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
                        BasicCommands.playEffectAnimation(out, effect, adjTile);
                        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                        BasicCommands.addPlayer1Notification(out, "Silverguard Squire's Opening Gambit: +1 Attack, +1 Health!", 2);
                    }
                }
            }
        }
    }
}
