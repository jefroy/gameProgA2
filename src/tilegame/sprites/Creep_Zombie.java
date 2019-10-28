package tilegame.sprites;

import graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Creep_Zombie extends Creature {

    private int id;
    protected int startNextSpawn;
    private static int idCounter = 0;

    public Creep_Zombie(Animation left, Animation right,
                        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        damage = 1;
        newWidth = 80;
        newHeight = 64;
        id = idCounter;
        idCounter++;
        startNextSpawn = 10;
        health = 4;
        worth = health;
        tileID = '4';
    }

    public float getMaxSpeed() {
        return 0.6f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
