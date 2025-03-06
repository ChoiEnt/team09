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

public class YoungFlamewing extends Unit {
    private static final int COST = 4;    // 成本为 4
    private static final int ATTACK = 5;  // 初始攻击力为 5
    private static final int HEALTH = 4;  // 初始生命值为 4

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
            BasicCommands.addPlayer1Notification(out, "Young Flamewing is for AI only!", 2);
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 创建 Young Flamewing 的 Card 对象
        Card flamewingCard = new Card();
        flamewingCard.setCardname("Young Flamewing");
        flamewingCard.setUnitConfig(StaticConfFiles.young_flamewing); // 假设配置文件存在
        flamewingCard.setId(gameState.getNextUnitId());
        BigCard bigCard = new BigCard();
        bigCard.setAttack(ATTACK);
        bigCard.setHealth(HEALTH);
        flamewingCard.setBigCard(bigCard);

        // 召唤单位
        gs.summonUnit(StaticConfFiles.young_flamewing, gameState.getNextUnitId(), flamewingCard, targetTile, gameState.getCurrentPlayer());

        // 获取召唤的单位
        Unit flamewing = targetTile.getUnit();
        if (flamewing != null) {
            flamewing.setName("Young Flamewing"); // 确保名称正确
            flamewing.setAttack(ATTACK); // 设置攻击力
            flamewing.setHealth(HEALTH); // 设置生命值
            flamewing.setMaxHealth(HEALTH); // 设置最大生命值
            BasicCommands.addPlayer1Notification(out, "Young Flamewing summoned with Flying ability!", 2);
        }
    }

    // 移动单位到目标瓦片（Flying 能力允许移动到任何未被占用的瓦片）
    public static void moveUnit(ActorRef out, GameState gameState, GameService gs, Unit unit, Tile targetTile) {
        // 检查目标瓦片是否有效且未被占用
        if (targetTile == null || targetTile.isOccupied()) {
            return;
        }

        // 获取当前单位所在的瓦片
        Tile currentTile = unit.getCurrentTile(gameState.getBoard());
        if (currentTile == null) {
            return;
        }

        // 清除当前瓦片上的单位，并将单位设置到目标瓦片
        currentTile.setUnit(null);
        targetTile.setUnit(unit);

        // 更新单位的位置
        unit.setPositionByTile(targetTile);

        // 显示动画效果
        EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, effect, targetTile);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.addPlayer1Notification(out, "Young Flamewing moved to a new tile!", 2);
    }
}