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
public class BHPlayer extends Sprite {

    private BulletHell bh;
    private Rectangle rect;
    private float vy, ay;
    private Texture img;

    public BHPlayer(BulletHell b) {
        bh=b;
        rect = new Rectangle(BulletHell.MIN_X, 0, 150, 150);
        img = new Texture(Gdx.files.internal("bh_player.png"));
    }

    public void update() {
        if(Gdx.input.isKeyPressed(Keys.LEFT) && rect.x >= BulletHell.MIN_X) {
            rect.x -= 420 * Gdx.graphics.getDeltaTime();
        }
        else if(Gdx.input.isKeyPressed(Keys.RIGHT) && rect.x+rect.width <= BulletHell.MAX_X) {
            rect.x += 420 * Gdx.graphics.getDeltaTime();
        }

        if(Gdx.input.isKeyPressed(Keys.UP) && rect.y == 0) {
            vy = 580 * Gdx.graphics.getDeltaTime();
        }
        rect.y += vy;
        if(rect.y > 0) {
            vy--;
        }
        if(rect.y<=0) {
            rect.y = 0;
            vy = 0;
        }
    }
    public void draw(SpriteBatch batch) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(img, rect.x, rect.y, rect.width, rect.height);
    }
}
