package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by johnlhota on 2/16/16.
 */
public class BHPlayer extends Actor {

    private BulletHell bh;
    private Rectangle rect;
    private float vy, ay;
    private Texture texture;

    public BHPlayer(BulletHell b) {
        bh=b;
        rect = new Rectangle(BulletHell.MIN_X, 0, 150, 150);
        texture = new Texture(Gdx.files.internal("bh_player.png"));
    }

    @Override
    public void act(float delta) {
        if(Gdx.input.isKeyPressed(Keys.LEFT) && rect.x >= BulletHell.MIN_X) {
            rect.x -= 420 * delta;
        }
        else if(Gdx.input.isKeyPressed(Keys.RIGHT) && rect.x+rect.width <= BulletHell.MAX_X) {
            rect.x += 420 * delta;
        }

        if(Gdx.input.isKeyPressed(Keys.UP) && rect.y == 0) {
            vy = 880 * delta;
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

    @Override
    public void draw(Batch batch, float a) {
        //Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }
}
