package tilegame.sprites;

import graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Creep_Fly extends Creature {

    private int id;
    private static int idCounter = 0;

    public Creep_Fly(Animation left, Animation right,
                     Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        damage = 1;
        newHeight = 48;
        newWidth = 64;
        id = idCounter;
        idCounter++;
        health = 2;
        worth = health;
        tileID = '3';
    }

    public float getMaxSpeed() {
        return 0.3f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
