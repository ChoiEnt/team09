package structures.basic.cards;

import akka.actor.ActorRef;

import structures.GameState;
import structures.GameService;
import structures.basic.Unit;
import structures.basic.Tile;
import structures.basic.Position;

import java.util.List;

public class RockPulveriser extends Unit {
	public static void applyProvokeEffect(ActorRef out, GameState gameState, GameService gs, Unit victim) {
    for (Unit unit : gameState.getUnits()) {
        // Find the unit named "RockPulveriser" and make sure the victim is not itself 找到名为 "RockPulveriser" 的单位，并确保 victim 不是它自己
        if (unit.getName().equals("RockPulveriser") && !victim.getName().equals("RockPulveriser")) {
            
            // Get the Tile corresponding to the location of the RockPulveriser 获取 RockPulveriser 的位置对应的 Tile
            Position rockPosition = unit.getPosition();
            Tile rockTile = gameState.getBoard().getTile(rockPosition.getTilex(), rockPosition.getTiley());
            
            // Use Board's getAdjacentTiles method to get adjacent Tiles 使用 Board 的 getAdjacentTiles 方法获取相邻的 Tiles——在棋盘上已经定义了找到相邻位置的方法了
            List<Tile> adjacentTiles = gameState.getBoard().getAdjacentTiles(rockTile);
            
            for (Tile adjTile : adjacentTiles) {
            	if(adjTile==null){
            		continue;
            		}
                Unit adjUnit = adjTile.getUnit();
	
                if (adjUnit != null) {
                    // Check if it is an enemy unit 检查是否为敌方单位
                    if (adjUnit .getOwner()!=gameState.getHuman()&&!unit.getName().equals("AI Avatar")) {
                        // Apply the Provoke effect 应用 Provoke 效果
		          adjUnit.setHasProvoke(true);

                        //Use GameService to update the unit status of neighboring units 使用 GameService 更新相邻单位的单位状态
                        gs.checkProvoked(adjUnit);
                    }
                }
            }
        }
    }
}
}