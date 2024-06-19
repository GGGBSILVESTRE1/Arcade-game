package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class JavaInvaders extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img, tNave, tMissile, tEnemy, tEnemy2, tEnemy3, tPowerUp, img_2, img_3;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile, powerUpSpeed, velocity_missile;
    private boolean attack, gameover, powerUp_1, powerUp_2, powerUp_3, game_win, isGameStarted;
    private Array<Rectangle> powerUps;
    private Array<Enemy> enemies;
    private long lastEnemyTime;
    private int score, power, power_enemy, current_level;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter, winParameter, startParameter;
    private BitmapFont bitmap, winFont, startFont;

    public void create() {
        isGameStarted = false;
        current_level = 1;
        img_3 = new Texture("c.jpg");
        img_2 = new Texture("b.png");
        batch = new SpriteBatch();
        img = new Texture("a.jpg");
        tNave = new Texture("Nave.png");
        nave = new Sprite(tNave);
        posX = 940;
        posY = 0;
        velocity = 10;
        velocity_missile = 20;
        attack = false;
        tMissile = new Texture("bala.png");
        missile = new Sprite(tMissile);
        nave.setPosition(posX, posY);
        tPowerUp = new Texture("powerup.png");
        tEnemy3 = new Texture("enemy3.png");
        tEnemy2 = new Texture("enemy2.png");
        tEnemy = new Texture("enemy1.png");

        enemies = new Array<Enemy>();
        powerUps = new Array<Rectangle>();
        lastEnemyTime = 0;
        score = 0;
        power = 3;
        powerUpSpeed = 4;
        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.color = Color.WHITE;
        bitmap = generator.generateFont(parameter);

        winParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        winParameter.size = 100; // tamanho maior
        winParameter.borderWidth = 2;
        winParameter.color = Color.YELLOW;
        winFont = generator.generateFont(winParameter);

        startParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        startParameter.size = 50;
        startParameter.color = Color.WHITE;
        startFont = generator.generateFont(startParameter);

        game_win = false;
        gameover = false;
        powerUp_1 = false;
        powerUp_2 = false;
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();

        if (!isGameStarted) {
			batch.draw(img, 0, 0);
            startFont.draw(batch, "Press P to Start", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
            if (Gdx.input.isKeyPressed(Input.Keys.P)) {
                isGameStarted = true;
            }
        } else {
            nextLevel();
            Spaceshoot();
            moveNave();
            moveEnemys();
            movePowerup();

            switch (current_level) {
                case 1:
                    batch.draw(img, 0, 0);
                    break;
                case 2:
                    batch.draw(img_2, 0, 0);
                    break;
                case 3:
                    batch.draw(img_3, 0, 0);
					break;
                case 4:
					batch.draw(img_3, 0,0);
                    winFont.draw(batch, "GAME WIN", 750, 700);
                    bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
					if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
						current_level = 1;
						score = 0;
						power = 3;
						posX = Gdx.graphics.getWidth() / 2;
						posY = 0;
						gameover = false;
						enemies.clear();
						powerUps.clear();
						game_win = false;
					}
                    break;
            }

            if (!gameover ) {
                if (attack) {
                    batch.draw(missile, xMissile, yMissile);
                }
                batch.draw(nave, posX, posY);

                for (Enemy enemy : enemies) {
                    batch.draw(tEnemy, enemy.rectangle.x, enemy.rectangle.y);
                }

                bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
                bitmap.draw(batch, "Vida: " + power, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
            } else {
                bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
                bitmap.draw(batch, "GAME OVER", 890, 600);
                if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                    current_level = 1;
                    score = 0;
                    power = 3;
                    posX = Gdx.graphics.getWidth() / 2;
                    posY = 0;
                    gameover = false;
                    enemies.clear();
                    powerUps.clear();
                    game_win = false;
                }
            }

            for (Rectangle powerUp : powerUps) {
                batch.draw(tPowerUp, powerUp.x, powerUp.y);
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        tNave.dispose();
        generator.dispose();
        bitmap.dispose();
        winFont.dispose();
        startFont.dispose();
    }

    private void moveNave() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (posX < Gdx.graphics.getWidth() - 90) {
                posX += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (posX > 0) {
                posX -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (posY < Gdx.graphics.getHeight() - 90) {
                posY += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (posY > 0) {
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
            attack = true;
        }
        if (attack) {
            yMissile += velocity_missile;
        }
        if (yMissile > Gdx.graphics.getHeight()) {
            attack = false;
        }
    }

    private class Enemy {
        Rectangle rectangle;
        int health;

        Enemy(Rectangle rectangle, int health) {
            this.rectangle = rectangle;
            this.health = health;
        }
    }

    private void spawnEnemies() {
        if (!game_win && !gameover) {
            Rectangle enemyRectangle = new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - tEnemy.getWidth()),
                    Gdx.graphics.getHeight(), tEnemy.getWidth(), tEnemy.getHeight());
            int enemyHealth = current_level;
            enemies.add(new Enemy(enemyRectangle, enemyHealth));
            lastEnemyTime = TimeUtils.nanoTime();
        }
    }

    private void spawnPowerup() {
        Rectangle powerup = new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - tPowerUp.getWidth()),
                Gdx.graphics.getHeight(), tPowerUp.getWidth(), tPowerUp.getHeight());
        powerUps.add(powerup);
    }

    private void movePowerup() {
        for (Iterator<Rectangle> iter = powerUps.iterator(); iter.hasNext();) {
            Rectangle powerUp = iter.next();
            powerUp.y -= powerUpSpeed;
            if (powerUp.y + tPowerUp.getHeight() < 0) {
                iter.remove();
            }
            if (collide(powerUp.x, powerUp.y, powerUp.getWidth(), powerUp.height, posX, posY, nave.getWidth(),
                    nave.getHeight())) {
                iter.remove();
                Random random = new Random();

                int randomNumber = random.nextInt(3);

                switch (randomNumber) {
                    case 0:
                        powerUp_1 = true;
                        power += 1;
                        break;
                    
					case 1:
						powerUp_2 = true;
						velocity += 1;
						break;
					case 2:
						powerUp_3 = true;
						velocity_missile += 2;
						break;
					
				}

				
			}
		}
	}

	private void moveEnemys() {
		Random rand = new Random();

	switch (current_level) {
		case 1:
		if ((TimeUtils.nanoTime() - lastEnemyTime > 2000000000)){
			this.spawnEnemies();
		}
			break;
		case 2:
		if ((TimeUtils.nanoTime() - lastEnemyTime > 1500000000)){
			this.spawnEnemies();
		}
			break;
		case 3:
		if ((TimeUtils.nanoTime() - lastEnemyTime > 1000000000)){
			this.spawnEnemies();
		}


			break;
	}	
    

    for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext();) {
        Enemy enemy = iter.next();
        enemy.rectangle.y -= 3;

        // Colisão com o míssil
        if (collide(enemy.rectangle.x, enemy.rectangle.y, enemy.rectangle.getWidth(), enemy.rectangle.height, 
                    xMissile, yMissile, missile.getWidth(), missile.getHeight()) && attack && !game_win) {

            enemy.health--;

            if (enemy.health <= 0) {
                ++score;
                iter.remove();
            }
            attack = false;

            if (rand.nextInt(7) < 1) {
                spawnPowerup();
            }

        // Colisão com a nave
        } else if (collide(enemy.rectangle.x, enemy.rectangle.y, enemy.rectangle.width, enemy.rectangle.height, 
                           posX, posY, nave.getWidth(), nave.getHeight()) && !gameover && !game_win) {
            --power;

            if (power <= 0) {
                gameover = true;
            }
            iter.remove();
        }

        if (enemy.rectangle.y + tEnemy.getHeight() < 0 && !game_win) {
            iter.remove();
            power--;
            if (power == 0) {
                gameover = true;
				}
			}

		}

	}
	private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2 ){
		if( x1 + w1 > x2 && x1 <x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2  ){
			return true;
		}
		return false;
	}

	public void nextLevel() {
		if( score >= 2 && current_level == 1) {
			current_level = 2;
		}
		if(score >=4 && current_level == 2) {
			current_level = 3;
			
		}
		if(score >= 5 && current_level == 3){
			current_level = 4;
			game_win = true;
		}


	}
	

	
}
 
