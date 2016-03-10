package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by johnlhota on 2/23/16.
 */
public class Bullet  extends Actor implements Pool.Poolable {

    private BulletHell bh;
    private float vx, vy, theta;
    private Actor shooter, target;
    private Texture texture;

    public static final float SPEED = 500f;

    public Bullet(BulletHell b) {
        bh=b;
    }

    public void setPath(Actor shooter, Actor target) {
        this.shooter=shooter;
        this.target=target;
        setBounds(shooter.getX()+shooter.getWidth()/2-10, shooter.getY()-20, 20, 20);
        texture = new Texture(Gdx.files.internal("bullet.png"));
        double dx = target.getX()+target.getWidth()/2-(getX()+getWidth()/2);
        double dy = target.getY()+target.getHeight()/2-getY();
        double len = Math.sqrt(dx*dx + dy*dy);
        vx = (float) (dx*SPEED/len);
        vy = (float) (dy*SPEED/len);
        //System.out.println(dx);
    }

    @Override
    public void act(float delta) {
        setX(getX()+vx*delta);
        setY(getY()+vy*delta);
        if(getX()+getWidth()>BulletHell.MAX_X || getX()<BulletHell.MIN_X || getY()+getHeight()<0 || getY()>GameScreen.HEIGHT) {
            //bh.removeBullet(this);
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void reset() {

    }
}
