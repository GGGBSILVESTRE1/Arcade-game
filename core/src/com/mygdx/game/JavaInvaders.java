package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;




public class JavaInvaders extends ApplicationAdapter {
    TextButton.TextButtonStyle textButtonStyle;
    TextButton startButton, exitButton, loadButton1,loadButton2,loadButton3; 
    SpriteBatch batch;
    Sprite sprite;
    Texture img, tNave, tMissile, tEnemy, tEnemy2, tEnemy3, tPowerUp, img_2, img_3,ini, buttonBackgroundTexture;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile, powerUpSpeed, velocity_missile;
    private boolean attack, gameover, powerUp_1, powerUp_2, powerUp_3, game_win, isGameStarted, slotsAdded;
    private Array<Rectangle> powerUps;
    private Array<Enemy> enemies;
    private long lastEnemyTime, countdownStartTime;
    private int score, power, current_level, countdownValue;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter, winParameter, startParameter;
    private BitmapFont bitmap, winFont, startFont;
    private Stage stage;
    private long levelChangeDuration = 3000; 
    private long levelChangeStartTime;
    private TextButton loadSavedGameButton;

    


    private enum GameState {
        MENU,
        INITIALIZING,
        PLAYING,
        GAME_OVER,
        LEVEL_2,
        LEVEL_3,
        LEVEL_CHANGE,
        SAVE_MENU,
        SAVE_EXIT
    }
    private GameState gameState;


    public void create() {

        try {
            

            slotsAdded = false;
            textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = new BitmapFont();  // Use uma fonte adequada para seu projeto
            textButtonStyle.fontColor = Color.WHITE;  // Defina a cor desejada para o texto

    
        

        stage = new Stage(new ScreenViewport());
        gameState = GameState.MENU;
        batch = new SpriteBatch();

        countdownStartTime = TimeUtils.millis();
        countdownValue = 4;
       
    


        setupButtons();


        Gdx.input.setInputProcessor(stage);



        isGameStarted = false;
        current_level = 1;
        ini = new Texture("ini.jpg");
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        try {


        switch (gameState) {
            case MENU:
                renderMenu();
                break;
            case SAVE_MENU:
                renderSaveMenu();
            
                break;

            case INITIALIZING:
                renderinitialgame();
                break;    
            
            case PLAYING:
                renderGame();
                removeSaveMenuButtons();


                break;
            case LEVEL_CHANGE:

            renderLevelChange();
            break;
               

            case GAME_OVER:
                renderGameOver();
                break;
            default:
                break;
        
    }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (img != null) {
            img.dispose();
            img = null;
        }
        if (tNave != null) {
            tNave.dispose();
            tNave = null;
        }
        if (generator != null) {
            generator.dispose();
            generator = null;
        }
        if (bitmap != null) {
            bitmap.dispose();
            bitmap = null;
        }
        if (winFont != null) {
            winFont.dispose();
            winFont = null;
        }
        if (startFont != null) {
            startFont.dispose();
            startFont = null;
        }
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
		if( score >= 15 && current_level == 1) {
			gameState = GameState.LEVEL_CHANGE;
            current_level = 2;
            levelChangeStartTime = TimeUtils.millis();
            saveGame(1);
		}
		if(score >=35 && current_level == 2) {
            current_level = 3;
            gameState = GameState.LEVEL_CHANGE;
            levelChangeStartTime = TimeUtils.millis();
            saveGame(1);
		}
		if(score >= 70 && current_level == 3){
			current_level = 4;
			game_win = true;
            saveGame(1);
		}


	}
    private void renderMenu() {
        try {
            stage.act(Gdx.graphics.getDeltaTime()); 
        
            stage.getBatch().begin(); 
            stage.getBatch().draw(ini, 0,0);
            stage.getBatch().end(); 
        
            stage.draw(); 
            Gdx.input.setInputProcessor(stage);
    
            if (startButton.isPressed()) {
                gameState = GameState.SAVE_MENU;
                slotsAdded = false;
            } else if (exitButton.isPressed()) {
                Gdx.app.exit(); 
            }else if (loadSavedGameButton.isPressed()) {
                loadGame(1); 
                gameState = GameState.PLAYING;
            }
          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    

        
    private void renderGame() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
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
						gameState = GameState.MENU;
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
                gameState = GameState.GAME_OVER;

                }
            

            for (Rectangle powerUp : powerUps) {
                batch.draw(tPowerUp, powerUp.x, powerUp.y);
            }
        
        batch.end();

    }

    private void renderGameOver() {
        try {
            batch.begin();
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(batch, "GAME OVER", 890, 600);
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                
                gameState = GameState.MENU;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        batch.end();
    }

    private void resetGame() {
        score = 0;
        power = 3;
        current_level = 1;
        posX = Gdx.graphics.getWidth() / 2;
        posY = 0;
        clearAllSaves();
    }

    private void clearAllSaves() {
        for (int i = 1; i <= 3; i++) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + i);
            prefs.clear();
            prefs.flush();
        }
    }

    private void renderLevelChange() {
        ScreenUtils.clear(0.75f, 0.75f, 0.75f, 1);
        batch.begin();

        bitmap.draw(batch, "LEVEL "+ current_level, 890, 600);
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);

        batch.end();    
        long currentTime = TimeUtils.millis();
    if (currentTime - levelChangeStartTime >= levelChangeDuration) {
        gameState = GameState.PLAYING;
    }
}
    private void renderinitialgame() {
        ScreenUtils.clear(0,0,0,0);
        batch.begin();


        long currentTime = TimeUtils.millis();
        long elapsed = currentTime - countdownStartTime;

        if (elapsed >= 1000) { 
            countdownValue--;
            countdownStartTime = currentTime;
        }
    
        if (countdownValue > 0) {
            bitmap.draw(batch, String.valueOf(countdownValue), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        } else {
            gameState = GameState.PLAYING;
        }
    
        batch.end();
    }
        private void saveGame(int slot) {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + slot);
         prefs.putInteger("score", score);
         prefs.putInteger("power", power);
         prefs.putInteger("current_level", current_level);
         prefs.putFloat("posX", posX);
         prefs.putFloat("posY", posY);
         prefs.flush();
        }

         private void loadGame(int slot) {
           
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + slot);
            score = prefs.getInteger("score", 0);
            power = prefs.getInteger("power", 3);
            current_level = prefs.getInteger("current_level", 1);
            posX = prefs.getFloat("posX", Gdx.graphics.getWidth() / 2);
            posY = prefs.getFloat("posY", 0);
          
        }
        private void renderSaveMenu() {
             Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            addSaveMenuButtons();

             stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();

            batch.begin();
            ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1);
            batch.draw(ini,0,0);

            bitmap.draw(batch, "Press 1 to Save in Slot 1", 100, 400);
            bitmap.draw(batch, "Press 2 to Save in Slot 2", 100, 300);
            bitmap.draw(batch, "Press 3 to Save in Slot 3", 100, 200);


            batch.end();
        
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                saveGame(1);
                gameState = GameState.INITIALIZING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                saveGame(2);
                gameState = GameState.INITIALIZING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                saveGame(3);
                gameState = GameState.INITIALIZING;
            }


            if (!slotsAdded) {
               addSaveMenuButtons();
                slotsAdded = true;
            }
        }
        


        private void setupButtons() {
        
            if (startButton == null) {
                startButton = new TextButton("Start Game", textButtonStyle);
                startButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50);
                stage.addActor(startButton);
            }
        
            if (exitButton == null) {
                exitButton = new TextButton("Exit Game", textButtonStyle);
                exitButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 50);
                stage.addActor(exitButton);
            }
            if (loadSavedGameButton == null) {
                loadSavedGameButton = new TextButton("Load Saved Game", textButtonStyle);
                loadSavedGameButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 150);
                stage.addActor(loadSavedGameButton);
            }

            stage.addActor(startButton);
            stage.addActor(exitButton);
            stage.addActor(loadSavedGameButton);
        

        
        }
    
        
        private void addSaveMenuButtons() {
            if (loadButton1 == null) {
                loadButton1 = new TextButton("Load Slot 1", textButtonStyle);
                loadButton1.setPosition(100, 400);
                stage.addActor(loadButton1);
            }
        
            if (loadButton2 == null) {
                loadButton2 = new TextButton("Load Slot 2", textButtonStyle);
                loadButton2.setPosition(100, 300);
                stage.addActor(loadButton2);
            }
        
            if (loadButton3 == null) {
                loadButton3 = new TextButton("Load Slot 3", textButtonStyle);
                loadButton3.setPosition(100, 200);
                stage.addActor(loadButton3);
            }
        }
        
        private void removeSaveMenuButtons() {
            if (loadButton1 != null) {
                loadButton1.remove();
                loadButton1 = null;
            }
            if (loadButton2 != null) {
                loadButton2.remove();
                loadButton2 = null;
            }
            if (loadButton3 != null) {
                loadButton3.remove();
                loadButton3 = null;
            }
        }


}
