package structures.basic.cards;

import akka.actor.ActorRef;
import structures.GameService;
import structures.GameState;
import structures.basic.Unit;

import java.util.ArrayList;

public class ShadowWatcher extends Unit {

    // method to check if bad omen is on the board and increment attack each time someone dies.
    public static void ShadowWatcherDeath ( ActorRef out, GameState gameState, GameService gs, Unit victim) {
        for (Unit unit : gameState.getUnits()) {
            if (unit.getName().equals("ShadowWatcher") &&
            		!victim.getName().equals("ShadowWatcher")) {
                gs.updateUnitAttack(unit, unit.getAttack() + 1);          
                gs.updateUnitHealth(unit,unit.getHealth()+1);          

  }
        }

    }
}

