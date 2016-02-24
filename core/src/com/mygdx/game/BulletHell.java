package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by johnlhota on 2/16/16.
 */
public class BulletHell implements Disposable, InputProcessor {

    private BHPlayer player;
    private Enemy enemy;
    public static int MIN_X, MAX_X;
    private Set<Bullet> bullets;
    private Set<Bullet> removeBullets;

    public BulletHell(int minx, int maxx) {
        MIN_X = minx;
        MAX_X = maxx;
        player = new BHPlayer(this);
        enemy = new Enemy(this);
        bullets = new HashSet<Bullet>();
        removeBullets = new HashSet<Bullet>();
    }

    public void update() {
        player.update();
        enemy.update();
        bullets.removeAll(removeBullets);
        for(Bullet b : bullets) {
            b.update();
            if(rectCollision(b, player)) {
                System.out.println("rekt");
            }
        }
    }

    public boolean rectCollision(Sprite a, Sprite b) {
        Sprite xSmall, xBig, ySmall, yBig;
        if(a.getWidth() < b.getWidth()) {
            xSmall = a;
            xBig = b;
        }
        else {
            xSmall = b;
            xBig = a;
        }
        if(a.getHeight() < b.getHeight()) {
            xSmall = a;
            xBig = b;
        }
        else {
            xSmall = b;
            xBig = a;
        }
        return false;
    }

    public void draw(SpriteBatch batch) {
        player.draw(batch);
        enemy.draw(batch);
        for(Bullet b : bullets) {
            b.draw(batch);
        }
    }

    public BHPlayer getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void newBullet(Sprite shooter, Sprite target) {
        bullets.add(new Bullet(this, shooter, target));
    }

    public void removeBullet(Bullet b) {
        removeBullets.add(b);
    }

    @Override public void dispose() { }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
