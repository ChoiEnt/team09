package structures.basic.Player;
import structures.basic.Player.Player;
import structures.basic.Player.HumanPlayer;


public class HumanPlayer extends Player {

    private int wraithlingSwarmCounter = 3;
    public HumanPlayer() {
            super();
        }

    public int getWraithlingSwarmCounter() {
        return wraithlingSwarmCounter;
    }

    public void setWraithlingSwarmCounter(int i) {
        wraithlingSwarmCounter = i;
    }

}
