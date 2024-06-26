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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;



//inicialização da classe principal do jogo
public class JavaInvaders extends ApplicationAdapter {
    TextButton.TextButtonStyle textButtonStyle;
    TextButton startButton, exitButton, loadButton1,loadButton2,loadButton3; 
    SpriteBatch batch;
    Sprite sprite;
    Texture img, tNave, tMissile, tEnemy, tEnemy2, tEnemy3, tPowerUp, img_2, img_3,ini,tNave2, buttonBackgroundTexture, tMissile2;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile, powerUpSpeed, velocity_missile;
    private boolean attack, gameover, powerUp_1, powerUp_2, powerUp_3, game_win, isGameStarted, slotsAdded;
    private Array<Rectangle> powerUps;
    private Array<Enemy> enemies;
    private long lastEnemyTime, countdownStartTime;
    private int score, power, current_level, countdownValue, isGamemultiplayer;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter, winParameter, startParameter;
    private BitmapFont bitmap, winFont, startFont;
    private Stage stage;
    private long levelChangeDuration = 3000; 
    private long levelChangeStartTime;
    private TextButton loadSavedGameButton, multiplayerGameButton;
    private Sprite nave2, missile2;
    private float posX2, posY2, xMissile2, yMissile2;
    private boolean attack2;

    /**
 * Classe principal do jogo JavaInvaders.
 */

    private enum GameState {
        MENU,
        INITIALIZING,
        PLAYING,
        GAME_OVER,
        LEVEL_2,
        LEVEL_3,
        LEVEL_CHANGE,
        SAVE_MENU,
        SAVE_EXIT,
        MULTIPLAYER
    }

    private GameState gameState;

    /**
     * Método de criação do jogo.
     */
    public void create() {
            // avaliar se o jogo é multiplayer 
            isGamemultiplayer = 0;

            //inicialização da nave2 e sua textura 
            tNave2 = new Texture("Nave.png");
            nave2 = new Sprite(tNave2);

            posX2 = 1000;  // Posição inicial da segunda nave
            posY2 = 0;
            attack2 = false;

            
            
            slotsAdded = false;
            textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = new BitmapFont();
            textButtonStyle.fontColor = Color.WHITE;  // Definição da cor das fontees

    
        
        // inicialização dos estados, começando com estado igual MENU
        stage = new Stage(new ScreenViewport());
        gameState = GameState.MENU;
        batch = new SpriteBatch();

        countdownStartTime = TimeUtils.millis();
        countdownValue = 4;
       
    

        setupButtons();


        Gdx.input.setInputProcessor(stage);


        //parametro para inicialização do jogo
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
        //inicialização das texturas do projétil da nave 1 e nave 2
        tMissile = new Texture("bala.png");
        tMissile2 = new Texture("bala.png");
        missile = new Sprite(tMissile);
        missile2 = new Sprite(tMissile2);
        nave.setPosition(posX, posY);
        //inicialização das texturas powerup e inimigos 
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
/**
     * Método para renderizar as telas do jogo.
     */    
    public void render() {
        try {
                
            if (gameState != null) {
                // switch para mudanças das telas, tomando o argumento "gameState" que é alterado durante o código para mudança de tela 
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

            case MULTIPLAYER:
                renderGameMultiplayer();
                removeSaveMenuButtons();
                break;

            default:
                break;
        }
        } else {
            System.out.println("game state is NULL");
        }
        
    
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    @Override
    /**
 * Libera todos os recursos alocados pelo jogo.
 *
 * Este método deve ser chamado para garantir que todos os recursos sejam
 * corretamente liberados da memória quando não forem mais necessários.
 * Ele verifica se cada recurso é diferente de null antes de chamar o método
 * dispose e, em seguida, define a referência para null para facilitar o
 * gerenciamento de memória.
 *
 * Recursos liberados:
 * - stage: o palco do jogo.
 * - batch: o sprite batch.
 * - img, img_2, img_3: texturas de imagem.
 * - tNave, tNave2: texturas da nave.
 * - tMissile, tMissile2: texturas dos mísseis.
 * - tEnemy, tEnemy2, tEnemy3: texturas dos inimigos.
 * - tPowerUp: textura dos power-ups.
 * - ini: recurso inicializador.
 * - generator: gerador de fontes.
 * - bitmap, winFont, startFont: fontes bitmap.
 *
 * Certifique-se de chamar este método ao finalizar o jogo ou ao
 * mudar de tela para evitar vazamentos de memória.
 */
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
        if (tNave2 != null){
        tNave2.dispose();
        tNave2 = null;
        }

        if (tNave != null) {
            tNave.dispose();
            tNave = null;
        }
        if (tMissile2 != null) {
            tMissile2.dispose();
            tMissile2 = null;
            
        }

        if (tMissile != null) {
            tMissile.dispose();
            tMissile = null;
        }
        if (tEnemy != null) {
            tEnemy.dispose();
            tEnemy = null;
        }
        if (tEnemy2 != null) {
            tEnemy2.dispose();
            tEnemy2 = null;
        }
        if (tEnemy3 != null) {
            tEnemy3.dispose();
            tEnemy3 = null;
        }
        if (tPowerUp != null) {
            tPowerUp.dispose();
            tPowerUp = null;
        }
        if (img_2 != null) {
            img_2.dispose();
            img_2 = null;
        }
        if (img_3 != null) {
            img_3.dispose();
            img_3 = null;
        }
        if (ini != null) {
            ini.dispose();
            ini = null;
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
   /**
     * Move a primeira nave com base nas teclas de seta pressionadas.
     * Restringe o movimento da nave aos limites da tela.
     */
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

     /**
     * Move a segunda nave com base nas teclas WASD pressionadas.
     * Restringe o movimento da nave aos limites da tela.
     */
    private void moveNave2() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (posX2 < Gdx.graphics.getWidth() - 90) {
                posX2 += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (posX2 > 0) {
                posX2 -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (posY2 < Gdx.graphics.getHeight() - 90) {
                posY2 += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (posY2 > 0) {
                posY2 -= velocity;
            }
        }
    
        if (posX2 > 1810) {
            posX2 = 1810;
        }
        if (posY2 < 90) {
            posY2 = 90;
        }
    }
     /**
     * Dispara um míssil da segunda nave quando a tecla 'X' é pressionada.
     * O míssil continua a se mover para cima até sair da tela.
     */
    private void Spaceshoot2() {
        if (Gdx.input.isKeyPressed(Input.Keys.X) && !attack2) {
            xMissile2 = posX2;
            yMissile2 = posY2;
            attack2 = true;
        }
        if (attack2) {
            yMissile2 += velocity_missile;
        }
        if (yMissile2 > Gdx.graphics.getHeight()) {
            attack2 = false;
        }
    }
    
    /**
     * Dispara um míssil da primeira nave quando a tecla 'ESPAÇO' é pressionada.
     * O míssil continua a se mover para cima até sair da tela.
     */
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
     /**
     * Classe representando um inimigo.
     */

    private class Enemy {
        Rectangle rectangle;
        int health;
        /**
         * Construtor da classe Enemy.
         * 
         * @param rectangle O retângulo representando o inimigo.
         * @param health A saúde do inimigo.
         */

        Enemy(Rectangle rectangle, int health) {
            this.rectangle = rectangle;
            this.health = health;
        }
    }
        /**
     * Cria novos inimigos e os adiciona ao jogo.
     * A frequência dos inimigos depende do nível atual do jogo.
     */

    private void spawnEnemies() {
        if (!game_win && !gameover) {
            Rectangle enemyRectangle = new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - tEnemy.getWidth()),
                    Gdx.graphics.getHeight(), tEnemy.getWidth(), tEnemy.getHeight());
            int enemyHealth = current_level;
            enemies.add(new Enemy(enemyRectangle, enemyHealth));
            lastEnemyTime = TimeUtils.nanoTime();
        }
    }
     /**
     * Cria novos power-ups e os adiciona ao jogo.
     */

    private void spawnPowerup() {
        Rectangle powerup = new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - tPowerUp.getWidth()),
                Gdx.graphics.getHeight(), tPowerUp.getWidth(), tPowerUp.getHeight());
        powerUps.add(powerup);
    }
    /**
     * Move os power-ups para baixo na tela.
     * Remove os power-ups que saem da tela ou são coletados pelas naves.
     * Aplica os efeitos dos power-ups quando coletados.
     */

    private void movePowerup() {
        for (Iterator<Rectangle> iter = powerUps.iterator(); iter.hasNext();) {
            Rectangle powerUp = iter.next();
            powerUp.y -= powerUpSpeed;
            if (powerUp.y + tPowerUp.getHeight() < 0) {
                iter.remove();
            }
            if (collide(powerUp.x, powerUp.y, powerUp.getWidth(), powerUp.height, posX, posY, nave.getWidth(),
                    nave.getHeight()) || collide(powerUp.x, powerUp.y, powerUp.getWidth(), powerUp.height, posX2, posY2, nave2.getWidth(), nave2.getHeight())) {
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


         /**
     * Move os inimigos e verifica colisões com os mísseis e as naves.
     * Remove inimigos que saem da tela ou são destruídos.
     * Aplica os efeitos das colisões.
     */
        private void moveEnemys() {
            Random rand = new Random();
        try {
            switch (current_level) {
                case 1:
                    if ((TimeUtils.nanoTime() - lastEnemyTime > 2000000000)) {
                        this.spawnEnemies();
                    }
                    break;
                case 2:
                    if ((TimeUtils.nanoTime() - lastEnemyTime > 1500000000)) {
                        this.spawnEnemies();
                    }
                    break;
                case 3:
                    if ((TimeUtils.nanoTime() - lastEnemyTime > 1000000000)) {
                        this.spawnEnemies();
                    }
                    break;
            }
        
            for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext();) {
                Enemy enemy = iter.next();
                enemy.rectangle.y -= 3;
        
                // Colisão com o míssil da primeira nave
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
        
                // Colisão com a primeira nave
                } else if (collide(enemy.rectangle.x, enemy.rectangle.y, enemy.rectangle.width, enemy.rectangle.height, 
                                   posX, posY, nave.getWidth(), nave.getHeight()) && !gameover && !game_win) {
                    --power;
        
                    if (power <= 0) {
                        gameover = true;
                    }
                    iter.remove();
                }
        
                // Colisão com o míssil da segunda nave
                if (collide(enemy.rectangle.x, enemy.rectangle.y, enemy.rectangle.getWidth(), enemy.rectangle.height, 
                            xMissile2, yMissile2, missile2.getWidth(), missile2.getHeight()) && attack2 && !game_win) {
        
                    enemy.health--;
        
                    if (enemy.health <= 0) {
                        ++score;
                        iter.remove();
                    }
                    attack2 = false;
        
                    if (rand.nextInt(7) < 1) {
                        spawnPowerup();
                    }
        
                // Colisão com a segunda nave
                } else if (collide(enemy.rectangle.x, enemy.rectangle.y, enemy.rectangle.width, enemy.rectangle.height, 
                                   posX2, posY2, nave2.getWidth(), nave2.getHeight()) && !gameover && !game_win) {
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
        }catch (Exception e) {
                e.printStackTrace();

            }
        }
         /**
     * Verifica se há colisão entre dois retângulos.
     *
     * @param x1 Coordenada X do primeiro retângulo.
     * @param y1 Coordenada Y do primeiro retângulo.
     * @param w1 Largura do primeiro retângulo.
     * @param h1 Altura do primeiro retângulo.
     * @param x2 Coordenada X do segundo retângulo.
     * @param y2 Coordenada Y do segundo retângulo.
     * @param w2 Largura do segundo retângulo.
     * @param h2 Altura do segundo retângulo.
     * @return true se os retângulos colidirem, false caso contrário.
     */
        
	private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2 ){
		if( x1 + w1 > x2 && x1 <x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2  ){
			return true;
		}
		return false;
	}

         /**
     * Avança para o próximo nível se a pontuação atingir os critérios especificados.
     * Salva o estado do jogo ao avançar de nível.
     */
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
      /**
     * Renderiza o menu principal do jogo.
     * Processa as entradas do usuário para iniciar o jogo, carregar um jogo salvo ou sair.
     */
    private void renderMenu() {
        try {
            stage.act(Gdx.graphics.getDeltaTime());
        
            stage.getBatch().begin(); 
            stage.getBatch().draw(ini, 0, 0);
            stage.getBatch().end(); 
        
            stage.draw(); 
            Gdx.input.setInputProcessor(stage);
    
            if (startButton.isPressed()) {
                gameState = GameState.SAVE_MENU;
                slotsAdded = false;
            } else if (exitButton.isPressed()) {
                Gdx.app.exit();
            } else if (loadSavedGameButton.isPressed()) {
                loadGame(1); 
                gameState = GameState.INITIALIZING;
            } else if (multiplayerGameButton.isPressed() ){
                gameState = GameState.MULTIPLAYER;
            }
          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    

        /**
     * Renderiza o jogo em andamento.
     * Processa a movimentação da nave, disparo de mísseis, movimentação dos inimigos e colisões.
     */
    private void renderGame() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        try {
            nave.draw(batch);
        
        if (!gameover) {
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
                    tEnemy = tEnemy2;
                    break;
                case 3:
                    batch.draw(img_3, 0, 0);
                    tEnemy = tEnemy3;
                    break;
                case 4:
                    batch.draw(img_3, 0, 0);
                    winFont.draw(batch, "GAME WIN", 750, 700);
                    bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
                    if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                        gameState = GameState.MENU;
                    }
                    break;
            }
            
            if (!gameover) {
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
        } else {
            gameState = GameState.GAME_OVER;
        } }catch (Exception e) {
            e.printStackTrace();
        }
        
        batch.end();
    }
         /**
     * Renderiza a tela de game over.
     * Mostra a pontuação final e permite que o jogador retorne ao menu principal.
     */
    private void renderGameOver() {
        try {
            batch.begin();
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(batch, "GAME OVER", 890, 600);
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                gameState = GameState.MENU;
                resetGame();  // Resetar o jogo quando voltar ao menu principal
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        batch.end();
    }
        /**
     * Reseta o jogo, reiniciando a pontuação, vida, nível atual, e posições.
     * Limpa os inimigos e power-ups do jogo.
     */
    private void resetGame() {
        score = 0;
        power = 3;
        current_level = 1;
        posX = Gdx.graphics.getWidth() / 2;
        posY = 0;
        attack = false;
        gameover = false;
        game_win = false;
        enemies.clear();
        powerUps.clear();
        clearAllSaves();
    }
    /**
     * Limpa todos os jogos salvos, removendo as preferências armazenadas.
     */
    private void clearAllSaves() {
        for (int i = 1; i <= 3; i++) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + i);
            prefs.clear();
            prefs.flush();
        }
    }
     /**
     * Renderiza a tela de transição de nível.
     * Mostra a mensagem de nível atual e transita para o próximo estado após a duração definida.
     */

    private void renderLevelChange() {
        ScreenUtils.clear(0.75f, 0.75f, 0.75f, 1);
        batch.begin();

        bitmap.draw(batch, "LEVEL "+ current_level, 890, 600);
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);

        batch.end();    
        long currentTime = TimeUtils.millis();
    if (currentTime - levelChangeStartTime >= levelChangeDuration) {
        switch (isGamemultiplayer) {
            case 0:
                gameState = GameState.PLAYING;
                
                break;
            case 1:
                gameState = GameState.MULTIPLAYER;
        
            default:
                break;
        }
    }
}
        /**
     * Renderiza a tela inicial do jogo.
     * Mostra a mensagem de preparação e conta o tempo para iniciar o jogo.
     */
    private void renderinitialgame() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        startFont.draw(batch, "Level " + current_level, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
        startFont.draw(batch, "Prepare-se para iniciar o jogo", Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2);
        batch.end();

        switch (current_level) {
            case 1:
                tEnemy = new Texture("enemy1.png");
                break;
            case 2:
                tEnemy = new Texture("enemy2.png");
                break;
            case 3:
                tEnemy = new Texture("enemy3.png");
                break;
            default:
                tEnemy = new Texture("enemy1.png");
                break;
        }

        if (!isGameStarted) {
            countdownStartTime = TimeUtils.millis();
            isGameStarted = true;
        }

        if (TimeUtils.timeSinceMillis(countdownStartTime) < levelChangeDuration) {
            return;
        }

        if (TimeUtils.timeSinceMillis(countdownStartTime) >= levelChangeDuration) {
            gameState = GameState.PLAYING;
            isGameStarted = false;
        }
    }
         /**
     * Salva o estado atual do jogo em um slot específico.
     * 
     * @param slot O número do slot onde o jogo será salvo.
     */

        private void saveGame(int slot) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + slot);
            prefs.putInteger("score", score);
            prefs.putInteger("power", power);
            prefs.putInteger("current_level", current_level);
            prefs.putFloat("posX", posX);
            prefs.putFloat("posY", posY);
            prefs.flush();
        }
        /**
     * Carrega o estado do jogo a partir de um slot específico.
     * 
     * @param slot O número do slot de onde o jogo será carregado.
     */

        private void loadGame(int slot) {
            Preferences prefs = Gdx.app.getPreferences("MyGamePreferences" + slot);
            score = prefs.getInteger("score", 0);
            power = prefs.getInteger("power", 3);
            current_level = prefs.getInteger("current_level", 1);
            posX = prefs.getFloat("posX", Gdx.graphics.getWidth() / 2);
            posY = prefs.getFloat("posY", 0);
            
            // Restaurar o estado do jogo
            gameover = (power <= 0);
            game_win = (current_level == 4 && score >= 70);
            
            if (gameover) {
                gameState = GameState.GAME_OVER;
            } else if (game_win) {
                gameState = GameState.LEVEL_CHANGE; // Ou outro estado apropriado
            } else {
                gameState = GameState.INITIALIZING; // Alterar para INITIALIZING para preparar o jogo antes de jogar
            }
        }
        /**
         * Renderiza o menu de salvamento do jogo
         * permite ao jogador salvar o jogo em diferentes slots
         */

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
                gameState = GameState.INITIALIZING; // Assegurar que o estado do jogo seja atualizado
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                saveGame(2);
                gameState = GameState.INITIALIZING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                saveGame(3);
                gameState = GameState.INITIALIZING;
            }
        }
        

            /**
     * Configura os botões do menu principal do jogo.
     * Adiciona os botões de iniciar jogo, sair, carregar jogo salvo e multiplayer.
     */
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
            if (multiplayerGameButton == null ) {
                multiplayerGameButton = new TextButton("Multiplayer", textButtonStyle);
                multiplayerGameButton.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 250);

            }
            stage.addActor(multiplayerGameButton);
            stage.addActor(startButton);
            stage.addActor(exitButton);
            stage.addActor(loadSavedGameButton);
    
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameState = GameState.SAVE_MENU;
                    slotsAdded = false;
                }
            });
    
            loadSavedGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    loadGame(1); 
                    gameState = GameState.INITIALIZING;
                }
            });
        }
    
         /**
     * Adiciona os botões do menu de salvamento do jogo.
     * Permite ao jogador carregar jogos salvos dos diferentes slots.
     */
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
             /**
     * Remove os botões do menu de salvamento do jogo.
     * Limpa os botões de carregar jogo dos diferentes slots.
     */
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
            /**
     * Renderiza o modo de jogo multiplayer.
     * Adiciona movimentação, disparo e colisões para duas naves.
     */
        private void renderGameMultiplayer() {
            ScreenUtils.clear(0, 0, 0, 1);
            batch.begin();
            try {
                isGamemultiplayer = 1;
                if (!gameover) {
                    nextLevel();
                    Spaceshoot();
                    Spaceshoot2(); // Adiciona o atirar da segunda nave
                    moveNave();
                    moveNave2(); // Adiciona o movimento da segunda nave
                    moveEnemys();
                    movePowerup();
        
                    if (nave != null) {
                        nave.draw(batch);
                    } else {
                        System.out.println("Nave is null");
                    }
        
                    if (nave2 != null) {
                        nave2.draw(batch);
                    } else {
                        System.out.println("Nave2 is null");
                    }
        
                    switch (current_level) {
                        case 1:
                            if (img != null) {
                                batch.draw(img, 0, 0);
                            } else {
                                System.out.println("Image 1 is null");
                            }
                            break;
                        case 2:
                            if (img_2 != null) {
                                batch.draw(img_2, 0, 0);
                                tEnemy = tEnemy2;
                            } else {
                                System.out.println("Image 2 is null");
                            }
                            break;
                        case 3:
                            if (img_3 != null) {
                                batch.draw(img_3, 0, 0);
                                tEnemy = tEnemy3;
                            } else {
                                System.out.println("Image 3 is null");
                            }
                            break;
                        case 4:
                            if (img_3 != null) {
                                batch.draw(img_3, 0, 0);
                            } else {
                                System.out.println("Image 3 is null");
                            }
                            if (winFont != null) {
                                winFont.draw(batch, "GAME WIN", 750, 700);
                            } else {
                                System.out.println("WinFont is null");
                            }
                            if (bitmap != null) {
                                bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
                            } else {
                                System.out.println("Bitmap is null");
                            }
                            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                                gameState = GameState.MENU;
                            }
                            break;
                    }
        
                    if (attack) {
                        if (missile != null) {
                            batch.draw(missile, xMissile, yMissile);
                        } else {
                            System.out.println("Missile is null");
                        }
                    }
                    if (attack2) {
                        if (missile2 != null) {
                            batch.draw(missile2, xMissile2, yMissile2);
                        } else {
                            System.out.println("Missile2 is null");
                        }
                    }
                    if (nave != null) {
                        batch.draw(nave, posX, posY);
                    }
                    if (nave2 != null) {
                        batch.draw(nave2, posX2, posY2);
                    }
        
                    for (Enemy enemy : enemies) {
                        if (enemy != null && tEnemy != null) {
                            batch.draw(tEnemy, enemy.rectangle.x, enemy.rectangle.y);
                        } else {
                            System.out.println("Enemy or tEnemy is null");
                        }
                    }
        
                    if (bitmap != null) {
                        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
                        bitmap.draw(batch, "Vida: " + power, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);
                    }
        
                    for (Rectangle powerUp : powerUps) {
                        if (tPowerUp != null) {
                            batch.draw(tPowerUp, powerUp.x, powerUp.y);
                        } else {
                            System.out.println("tPowerUp is null");
                        }
                    }
                } else {
                    gameState = GameState.GAME_OVER;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                batch.end();
            }
        }

}

    

