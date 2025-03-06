package structures.basic.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameService;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.Player.HumanPlayer;

public class TrueStrike {
    private static final int COST = 1; // 假设魔法值成本为 1，可调整
    private static final int DAMAGE = 2; // 造成 2 点伤害

    public static void cast(ActorRef out, GameState gameState, GameService gs, Unit targetUnit) {
        // 检查魔法值
        if (gameState.getCurrentPlayer().getMana() < COST) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
            return;
        }

        // 检查目标单位是否有效且为敌方单位
        if (targetUnit == null || !gameState.getUnits().contains(targetUnit) ||
            targetUnit.getOwner() instanceof HumanPlayer) { // 假设不能对己方单位使用
            return;
        }

        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 对目标单位造成伤害
        gs.updateUnitHealth(targetUnit, targetUnit.getHealth() - DAMAGE);
        gs.strike(targetUnit.getCurrentTile(gameState.getBoard()));
        BasicCommands.addPlayer1Notification(out, "True Strike! -2 Health", 2);
    }
}