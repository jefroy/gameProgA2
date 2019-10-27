package tilegame.sprites;

import graphics.Animation;

import java.lang.reflect.Constructor;

/**
    The Player.
*/
public class Player extends Creature {

    public static final float JUMP_SPEED = -.95f;
    public static final int STATE_JUMPING = 3;
    public static final int STATE_FALLING = 4;

    // additional animations
    private Animation idleLeft;
    private Animation idleRight;
    private Animation jumpRight;
    private Animation jumpLeft;
    private Animation fallRight;
    private Animation fallLeft;

    private boolean onGround;
    public boolean jumped;
    public boolean isFalling;
    public boolean facingLeft;
    public boolean facingRight;
    public int health;
    public int score;
    public int damage;
    public int newHeight;
    public int newWidth;

    public Player(
            Animation runLeft, Animation runRight,
            Animation deadLeft, Animation deadRight,
            Animation idleLeft, Animation idleRight,
            Animation jumpLeft, Animation jumpRight,
            Animation fallLeft, Animation fallRight
    )
    {
        super(runLeft, runRight, deadLeft, deadRight);
        this.idleLeft = idleLeft;
        this.idleRight = idleRight;
        this.jumpLeft = jumpLeft;
        this.jumpRight = jumpRight;
        this.fallLeft = fallLeft;
        this.fallRight = fallRight;
        newWidth = 80;
        newHeight = 64;
        score = 0;
        health = 5;
        damage = 1;
        jumped = false;
        isFalling = false;
        facingLeft = false;
        facingRight =true;
        DIE_TIME = 1000;
    }

    @Override
    public void update(long elapsedTime) {

        // select the correct Animation
//        System.out.println(
//                "player update:"
//                + "\nvelX: " + getVelocityX()
//                + "\nvelY: " + getVelocityY()
//                + "\nstate: " + state
//        );
        Animation newAnim = anim;
        // moving left
        if (getVelocityX() < 0) { // try adding getVelocityY() == 0
            newAnim = left;
        }
        // moving right
        else if (getVelocityX() > 0) {
            newAnim = right;
        }
        else if (state == STATE_NORMAL && newAnim == left) {
            newAnim = idleLeft;
        }
        else if (state == STATE_NORMAL && newAnim == right) {
            newAnim = idleRight;
        }
        // jumping ??
        else if (getVelocityY() < 0) {
            if(newAnim == left || newAnim == idleLeft) newAnim = jumpLeft;
            else if(newAnim == right || newAnim == idleRight) newAnim = jumpRight;
            jumped = false;
            System.out.println(
                    "player update:"
                            + "\nvelX: " + getVelocityX()
                            + "\nvelY: " + getVelocityY()
                            + "\nstate: " + state
            );

        }
        // falling ??
        else if (getVelocityY() > 0) {
            if(newAnim == left || newAnim == idleLeft || newAnim == jumpLeft) newAnim = fallLeft;
            else if(newAnim == right || newAnim == idleRight || newAnim == jumpRight) newAnim = fallRight;
            System.out.println(
                    "player update:"
                            + "\nvelX: " + getVelocityX()
                            + "\nvelY: " + getVelocityY()
                            + "\nstate: " + state
            );
        }


        if (state == STATE_DYING) {
            if(newAnim == left || newAnim == idleLeft) newAnim = deadLeft;
            else newAnim = deadRight;
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
            Object o;
            o = constructor.newInstance(
                    (Animation) left.clone(),
                    (Animation) right.clone(),
                    (Animation) deadLeft.clone(),
                    (Animation) deadRight.clone(),
                    (Animation) idleLeft.clone(),
                    (Animation) idleRight.clone(),
                    (Animation) jumpLeft.clone(),
                    (Animation) jumpRight.clone(),
                    (Animation) fallLeft.clone(),
                    (Animation) fallRight.clone());
            return o;
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
            System.out.println(
                    "Jumping"
                            + "\ngetVelocityY(): " + getVelocityY()
                            + "\njumped: " + jumped
            );
        }
    }

    /**
        Makes the player FALL if the player is on the AIR or
        if forceFall is true.
    */
    public void fall(boolean forceFall) {
        if (!onGround || forceFall) {
            onGround = true;
            setVelocityY(-JUMP_SPEED);
        }
    }

    public float getMaxSpeed() {
        return 0.5f;
    }
}
