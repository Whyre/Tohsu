package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;


import java.util.Set;
import java.util.HashSet;

/**
 * Created by johnlhota on 2/16/16.
 */
public class BulletHell extends Stage {

    private BHPlayer player;
    private Enemy enemy;
    public static int MIN_X, MAX_X;
    private BulletPool bulletPool;

    public BulletHell(int minx, int maxx) {
        MIN_X = minx;
        MAX_X = maxx;
        bulletPool = new BulletPool();
        player = new BHPlayer(this);
        enemy = new Enemy(this);
        addActor(player);
        addActor(enemy);
    }

    @Override
    public void act() {
        super.act();
//        player.update();
//        enemy.update();
//        bullets.removeAll(removeBullets);
//        for(Bullet b : bullets) {
//            b.act();
//            if(rectCollision(b, player)) {
//                System.out.println("rekt");
//            }
//        }
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

    public BHPlayer getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void newBullet(Actor shooter, Actor target) {
        Bullet b = (Bullet) (bulletPool.obtain());
        b.setPath(shooter, target);
        addActor(b);
    }

    public class BulletPool extends Pool<Bullet> {

        @Override
        public Bullet newObject() {
            return new Bullet(BulletHell.this);
        }
    }

}
