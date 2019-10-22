package tilegame.sprites;

import graphics.Animation;

import java.lang.reflect.Constructor;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private Animation idleLeft;
    private Animation idleRight;

    private boolean onGround;
    public int health;
    public int score;
    public int damage;

    public Player(
            Animation runLeft, Animation runRight,
            Animation deadLeft, Animation deadRight,
            Animation idleLeft, Animation idleRight
    )
    {
        super(runLeft, runRight, deadLeft, deadRight);
        this.idleLeft = idleLeft;
        this.idleRight = idleRight;
        facingRight = true;
        facingLeft = false;
        width = 80;
        height = 64;
        score = 0;
        health = 5;
        damage = 1;
    }

    @Override
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            newAnim = left;
        } else if (getVelocityX() > 0) {
            newAnim = right;
        } else if (state == STATE_NORMAL && newAnim == left) {
            newAnim = idleLeft;
        } else if (state == STATE_NORMAL && newAnim == right) {
            newAnim = idleRight;
        }

        if (state == STATE_DYING && newAnim == left) {
            newAnim = deadLeft;
        } else if (state == STATE_DYING && newAnim == right) {
            newAnim = deadRight;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        } else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

    @Override
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                    (Animation)left.clone(),
                    (Animation)right.clone(),
                    (Animation)deadLeft.clone(),
                    (Animation)deadRight.clone(),
                    (Animation)idleLeft.clone(),
                    (Animation)idleRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
