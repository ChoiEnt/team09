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

public class SilverguardKnight extends Unit {
    private static final int COST = 3;    // 假设成本为 3，可调整
    private static final int ATTACK = 2;  // 初始攻击力为 2，Zeal 会增加
    private static final int HEALTH = 5;  // 假设生命为 5，可调整

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
            BasicCommands.addPlayer1Notification(out, "Silverguard Knight is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Silverguard Knight 的 Card 对象
        Card knightCard = new Card();
        knightCard.setCardname("Silverguard Knight");
        knightCard.setUnitConfig(StaticConfFiles.silverguard_knight); // 假设配置文件存在
        knightCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        knightCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.silverguard_knight, gameState.getNextUnitId(), knightCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位
        Unit knight = targetTile.getUnit();
        if (knight != null) {
            // 设置 Zeal 触发器（需要在其他地方实现，例如 GameService）
            knight.setName("Silverguard Knight"); // 确保名称正确，用于 Zeal 检查
            applyProvokeEffect(out, gameState, gs, targetTile); // 应用 Provoke 效果
            BasicCommands.addPlayer1Notification(out, "Silverguard Knight summoned with Provoke!", 2);
        }
    }

    // 应用 Provoke 效果，限制相邻敌方单位
    private static void applyProvokeEffect(ActorRef out, GameState gameState, GameService gs, Tile knightTile) {
        Tile[][] tiles = gameState.getBoard().getTiles();
        int x = knightTile.getTilex();
        int y = knightTile.getTiley();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}}; // 包括斜向
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < tiles.length && newY >= 0 && newY < tiles[0].length) {
                Tile adjTile = tiles[newX][newY];
                if (adjTile.isOccupied()) {
                    Unit adjUnit = adjTile.getUnit();
                    if (adjUnit.getOwner() != gameState.getCurrentPlayer()) {
                        adjUnit.setHasProvoke(true); // 设置 Provoke 标记（假设 Unit 有此方法）
                        gs.checkProvoked(adjUnit); // 调用 GameService 检查 Provoke 状态
                    }
                }
            }
        }
    }

    // Zeal 效果：当 Avatar 受损时增加攻击力
    public static void applyZealEffect(ActorRef out, GameState gameState, GameService gs, Unit avatar) {
        if (avatar.getOwner() instanceof AIPlayer && avatar.getHealth() < avatar.getMaxHealth()) {
            for (Unit unit : gameState.getCurrentPlayer().getUnits()) {
                if (unit.getName().equals("Silverguard Knight")) {
                    int newAttack = unit.getAttack() + 2;
                    gs.updateUnitAttack(unit, newAttack);
                    BasicCommands.addPlayer1Notification(out, "Silverguard Knight's Zeal: +2 Attack!", 2);
                    // 可选：播放增益动画
                    EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
                    BasicCommands.playEffectAnimation(out, effect, unit.getCurrentTile(gameState.getBoard()));
                    try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }
    }
}