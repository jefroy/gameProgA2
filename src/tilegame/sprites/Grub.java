package tilegame.sprites;

import graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {

    public Grub(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        tileID = '1';
        newHeight = getHeight();
        newWidth = getWidth();
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
