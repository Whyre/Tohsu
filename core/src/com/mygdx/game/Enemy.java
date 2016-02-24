package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;

/**
 * Created by johnlhota on 2/16/16.
 */
public class Enemy extends Sprite {
//    private Texture img;

    private BulletHell bh;
    private int vx;

    public Enemy(BulletHell b) {
        bh=b;
        setBounds((BulletHell.MIN_X+BulletHell.MAX_X)/2, GameScreen.HEIGHT-150, 250, 100);
        setTexture(new Texture(Gdx.files.internal("enemy.jpeg")));
        vx = 420;
    }

    public void update() {
        float dx = vx*Gdx.graphics.getDeltaTime();
        if(getX()+dx >= BulletHell.MIN_X && getX()+getWidth()+dx <= BulletHell.MAX_X) {
            setX(getX() + dx);
        }
        else {
            vx *= -1;
            setX(getX() + dx);
        }


        if(Math.random()<0.05) {
            bh.newBullet(this, bh.getPlayer());
        }
    }

    public void draw(SpriteBatch batch) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(getTexture(), getX(), getY(), getWidth(), getHeight());
    }
}
