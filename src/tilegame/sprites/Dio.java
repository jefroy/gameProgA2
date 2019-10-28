package tilegame.sprites;

import graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Dio extends Creature {

    private int id;
    protected int startNextSpawn;
    private static int idCounter = 0;

    public Dio(Animation left, Animation right,
               Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        damage = 1;
        newWidth = 80;
        newHeight = 64;
        id = idCounter;
        idCounter++;
        startNextSpawn = 10;
        health = 30;
        worth = health;
        tileID = '5';
    }

    public float getMaxSpeed() {
        return 0.06f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
