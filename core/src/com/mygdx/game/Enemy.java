package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by johnlhota on 2/16/16.
 */
public class Enemy extends Actor {

    private BulletHell bh;
    private Texture texture;
    private int vx;

    public Enemy(BulletHell b) {
        bh=b;
        setBounds((BulletHell.MIN_X+BulletHell.MAX_X)/2, GameScreen.HEIGHT-150, 250, 100);
        texture = new Texture(Gdx.files.internal("enemy.jpeg"));
        vx = 420;
    }

    @Override
    public void act(float delta) {
        float dx = vx*delta;
        if(getX()+dx <= BulletHell.MIN_X+25) {
            vx = +Math.abs(vx);
        }
        else if(getX()+getWidth()+dx >= BulletHell.MAX_X-25) {
            vx = -Math.abs(vx);
        }
        else {
            setX(getX() + dx);
        }


        if(Math.random()<0.05) {
            bh.newBullet(this, bh.getPlayer());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
