package structures.basic.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameService;
import structures.GameState;
import structures.basic.Unit;

public class SundropElixir {
    private static final int COST = 1;
    private static final int HEAL_AMOUNT = 4;
    
    public static void cast(ActorRef out, GameState gameState, GameService gs, Unit targetUnit) {
        // 检查魔法值
        if (gameState.getCurrentPlayer().getMana() < COST) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
            return;
        }

        // 检查目标单位是否有效
        if (targetUnit == null || !gameState.getUnits().contains(targetUnit)) {
            return; // Target must be a valid unit
        }
        
        // 扣除魔法值
        gameState.getCurrentPlayer().setMana(gameState.getCurrentPlayer().getMana() - COST);
        gs.updatePlayerMana(gameState.getCurrentPlayer(), gameState.getCurrentPlayer().getMana());

        // 治疗目标单位
        int newHealth = Math.min(targetUnit.getHealth() + HEAL_AMOUNT, targetUnit.getMaxHealth());
        gs.updateUnitHealth(targetUnit, newHealth);
        
        // 显示通知并播放治疗动画
        BasicCommands.addPlayer1Notification(out, "Sundrop Elixir cast: Healed 4 HP", 2);
        gs.healing(targetUnit.getCurrentTile(gameState.getBoard()));
    }
}