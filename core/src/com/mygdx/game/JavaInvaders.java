package com.mygdx.game;

import java.io.File;
import java.sql.Time;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;


public class JavaInvaders extends ApplicationAdapter {
	SpriteBatch batch;
	Texture  img, tNave, tMissile, tEnemy;
	private Sprite nave, missile;
	private float posX, posY, velocity, xMissile, yMissile;
	private boolean attack, gameover;
	private Array<Rectangle> enemies, powerUp;
	private long lastEnemyTime;
	private int score, power;
	private FreeTypeFontGenerator generator;
	private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	private BitmapFont bitmap;
	 


	

	
	
public void create() {
    batch = new SpriteBatch();
    img = new Texture("a.jpg");
    tNave = new Texture("Nave.png");
    nave = new Sprite(tNave);
    posX = 0;
    posY = 0;
    velocity = 10;
	attack = false;
    tMissile = new Texture("bala.png");
	missile = new Sprite(tMissile);
	nave.setPosition(posX, posY);

	tEnemy = new Texture("enemy2.png");
	enemies = new Array<Rectangle>();
	lastEnemyTime =0;
	score = 0;
	power = 3;

	generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
	parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

	parameter.size = 30;
	parameter.borderWidth = 1;
	parameter.color = Color.WHITE;
	bitmap = generator.generateFont(parameter);

	gameover = false;
		
	}

	@Override
	public void render () {

		this.Spaceshoot();
		this.moveNave();
		this.moveEnemys();

		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(img, 0, 0);

		if(!gameover){
			if(attack){
			  // ALTERADO
			  batch.draw(missile, xMissile, yMissile);
			}
			batch.draw(nave, posX, posY);
	  
			for(Rectangle enemy : enemies ){
			  batch.draw(tEnemy, enemy.x, enemy.y);
			}
			bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
			bitmap.draw(
				batch, "Vida: " + power, 
				Gdx.graphics.getWidth() - 150, 
				Gdx.graphics.getHeight() - 20
				);
		  }else{
			bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
			bitmap.draw(
				batch, "GAME OVER", 
				900, 
				1000
				);
	  
			if( Gdx.input.isKeyPressed(Input.Keys.ENTER) ){
			  score = 0;
			  power = 3;
			  posX = 0;
			  posY = 0;
			  gameover = false;
			}
		  }

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		tNave.dispose();
	}


	private void moveNave() {

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			if( posX < Gdx.graphics.getWidth() -90){
			posX += velocity;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			if (posX > 0) {
			posX -= velocity;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			if( posY< Gdx.graphics.getHeight() -90)
			posY += velocity;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			if(posY > 0) {
			posY -= velocity;
			}
		}
		
		if (posX > 1810) {
			posX = 1810;
		}
		if (posY < 90) {
			posY = 90;
		}
	}


	private void Spaceshoot() {
	
		
		
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !attack) {
			xMissile = posX;
			yMissile = posY;
			attack =true;
		}if (attack) {
			yMissile += 20;
		}
		if (yMissile > Gdx.graphics.getHeight()) {
			attack = false;
		}
		if (yMissile > Gdx.graphics.getHeight()) {
			yMissile = posX;
			xMissile = posY;

		}
	}
		
		
	private void spawnEnemies() {
		Rectangle enemy = new Rectangle(MathUtils.random(0,Gdx.graphics.getWidth() - tEnemy.getWidth()), Gdx.graphics.getHeight(), tEnemy.getWidth(), tEnemy.getHeight());
		enemies.add(enemy);
		lastEnemyTime = TimeUtils.nanoTime();
	}

	private void moveEnemys() {
		
		if( TimeUtils.nanoTime() - lastEnemyTime > 1000000000 &&!gameover ){
		this.spawnEnemies();
		}
		
		for (Iterator <Rectangle> iter = enemies.iterator(); iter.hasNext();){
			Rectangle enemy = iter.next();
			enemy.y -= 2;


				//colisão missel
			if( collide(enemy.x, enemy.y, enemy.getWidth(), enemy.height, xMissile, yMissile, missile.getWidth(), missile.getHeight()) && attack){
				++score;
				iter.remove();
				attack = false;
				//colisão nave
			} else if(collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, nave.getWidth(), nave.getHeight() ) && !gameover ) {
				--power;
				if( power <= 0){

					gameover = true;

				}
				iter.remove();
			}

			if(enemy.y + tEnemy.getHeight() <0) {
				iter.remove();
				gameover = true;

			}
		}
	}
	private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2 ){
		if( x1 + w1 > x2 && x1 <x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2  ){
			return true;
		}
		return false;
	}
	
} 
