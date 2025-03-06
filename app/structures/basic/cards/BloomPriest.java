package structures.basic.cards;

import java.util.List;

import akka.actor.ActorRef;
import structures.GameService;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class BloomPriest extends Unit {
	public static void BloomPriestDeath(ActorRef out, GameState gameState, GameService gs, Unit victim) {
    for (Unit unit : gameState.getUnits()) {
        // Find the unit named BloomPriest and make sure the victim is not itself(找到名为 BloomPriest 的单位，并确保 victim 不是它自己)
        if (unit.getName().equals("BloomPriest" ) && !victim.getName().equals("BloomPriest")) {
            
            // Get the Tile corresponding to the location of BloomPriest 获取 BloomPriest 的位置对应的 Tile
            Position BloomPriestPosition = unit.getPosition();
            Tile BloomPriestTile = gameState.getBoard().getTile(BloomPriestPosition.getTilex(), BloomPriestPosition.getTiley());
            
            // Use Board's getAdjacentTiles method to get adjacent Tiles 使用 Board 的 getAdjacentTiles 方法获取相邻的 Tiles——在棋盘上已经定义了找到相邻位置的方法了
            List<Tile> adjacentTiles = gameState.getBoard().getAdjacentTiles(BloomPriestTile);
            
            for (Tile adjTile : adjacentTiles) {
	if(adjTile==null){
		continue;}
                Unit adjUnit = adjTile.getUnit();
	
                if (adjUnit != null) {
                    // Check if it is an enemy unit 检查是否为敌方单位
                    if (adjUnit .getOwner()!=gameState.getHuman()&&!unit.getName().equals("AI Avatar")) {
		if(adjUnit.getHealth()<unit.getMaxHealth()){
  	//Determine if it is empty, if not, then call a wraithling 判断是否而为空，如果不为空的话，那么就召唤一只wraithling---这里应该用wraithling这个类来召唤的，现在暂且用unit来创建替代一下 
 		if (adjTile.getUnit() == null) {    
 			Unit wraithling=new Unit();
 			adjTile.setUnit(wraithling);
                                    System.out.println("Wraithling summoned next to BloomPriest!");                  
                                } else {
                                    System.out.println("Adjacent tile is occupied, cannot summon Wraithling.");
                                }
                            }
                        }
                    }
                }
        }
    }
	}
}
