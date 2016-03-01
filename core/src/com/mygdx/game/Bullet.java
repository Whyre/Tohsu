package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by johnlhota on 2/23/16.
 */
public class Bullet  extends Sprite {

    private BulletHell bh;
    private float vx, vy, theta;
    private Sprite shooter, target;

    public static final float SPEED = 500f;

    public Bullet(BulletHell b, Sprite shooter, Sprite target) {
        bh=b;
        this.shooter=shooter;
        this.target=target;
        setBounds(shooter.getX()+shooter.getWidth()/2-10, shooter.getY()-20, 20, 20);
        setTexture(new Texture(Gdx.files.internal("bullet.png")));
        double dx = target.getX()+target.getWidth()/2-getX();
        double dy = target.getY()+target.getHeight()/2-getY();
        double len = Math.sqrt(dx*dx + dy*dy);
        vx = (float) (dx*SPEED/len);
        vy = (float) (dy*SPEED/len);
        //System.out.println(dx);
    }

    public void update() {
        setX(getX()+vx*Gdx.graphics.getDeltaTime());
        setY(getY()+vy*Gdx.graphics.getDeltaTime());
        if(getX()+getWidth()>BulletHell.MAX_X || getX()<BulletHell.MIN_X || getY()+getHeight()<0 || getY()>GameScreen.HEIGHT) {
            bh.removeBullet(this);
        }
    }
    public void draw(SpriteBatch batch) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(getTexture(), getX(), getY(), getWidth(), getHeight());
    }
}
