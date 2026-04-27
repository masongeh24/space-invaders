import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * GameModel.java
 * 
 * This class handles the game logic and state for Space Invaders.
 * It manages the positions of the player, invaders, and projectiles.
 * 
 * Constraints: No Swing or AWT imports allowed here.
 */
public class GameModel {
    // Game Constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 20;
    public static final int ALIEN_WIDTH = 30;
    public static final int ALIEN_HEIGHT = 20;
    public static final int BULLET_WIDTH = 9;
    public static final int BULLET_HEIGHT = 21;

    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    public static final int ALIEN_SPACING_X = 50;
    public static final int ALIEN_SPACING_Y = 40;

    public static final int SHIELD_COUNT = 4;
    public static final int SHIELD_WIDTH = 80;
    public static final int SHIELD_HEIGHT = 60;
    public static final int SHIELD_SEGMENT_SIZE = 5;

    // Game State
    private int playerX;
    private int playerY;
    private List<Alien> aliens;
    private List<Shield> shields;
    private Bullet playerBullet;
    private List<Bullet> alienBullets;
    private int score;
    private int lives;
    private int highScore;
    private boolean paused = false;
    private boolean onTitleScreen = true;
    private GameView view;
    private static final String SCORE_FILE = "Score.txt";

    private int alienDirection = 1; // 1 for right, -1 for left
    private int alienMoveCounter = 0;
    private int alienMoveThreshold = 60; // Current threshold
    private int waveBaseMoveThreshold = 60; // Starting threshold for current wave

    private Random random = new Random();

    // Bonus Ship State
    private boolean bonusShipActive = false;
    private int bonusShipX;
    private int bonusShipY = 30; // Near the top
    private int bonusShipDirection; // 1 for left-to-right, -1 for right-to-left
    private int bonusShipTimer; // Ticks until next spawn

    private boolean animframe = false;
    private boolean bulletAnimFrame = false;
    private int bulletAnimCounter = 0;

    public GameModel() {
        loadHighScore();
        reset();
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public boolean isPaused() {
        return paused;
    }

    public void handleLifeLost() {
        paused = true;
        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    if (view != null) {
                        view.flashPlayer();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // Ignore
            } finally {
                paused = false;
            }
        }).start();
    }

    public void reset() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - 50;
        playerBullet = null;
        alienBullets = new ArrayList<>();
        score = 0;
        lives = 3;
        alienDirection = 1;
        alienMoveCounter = 0;
        waveBaseMoveThreshold = 60;
        alienMoveThreshold = 60;
        animframe = false;
        initAliens();
        initShields();
        resetBonusShipTimer();
        bonusShipActive = false;
    }

    private void resetBonusShipTimer() {
        // 10-30 seconds at ~60 FPS
        bonusShipTimer = 600 + random.nextInt(1201);
    }

    private void initShields() {
        shields = new ArrayList<>();
        int gap = (WIDTH - (SHIELD_COUNT * SHIELD_WIDTH)) / (SHIELD_COUNT + 1);
        for (int i = 0; i < SHIELD_COUNT; i++) {
            int x = gap + i * (SHIELD_WIDTH + gap);
            int y = HEIGHT - 150;
            shields.add(new Shield(x, y));
        }
    }

    private void initAliens() {
        aliens = new ArrayList<>();
        for (int row = 0; row < ALIEN_ROWS; row++) {
            int points;
            if (row == 0) {
                points = 30; // Top row
            } else if (row <= 2) {
                points = 20; // Middle two rows
            } else {
                points = 10; // Bottom two rows
            }

            for (int col = 0; col < ALIEN_COLS; col++) {
                int x = 50 + col * ALIEN_SPACING_X;
                int y = 50 + row * ALIEN_SPACING_Y;
                aliens.add(new Alien(x, y, points, row));
            }
        }
    }

    // Logic for updating game state
    public void tick() {
        if (lives <= 0 || paused || onTitleScreen)
            return;

        bulletAnimCounter++;
        if (bulletAnimCounter >= 8) {
            bulletAnimCounter = 0;
            bulletAnimFrame = !bulletAnimFrame;
        }

        updatePlayerBullet();
        updateAliens();
        updateAlienBullets();
        updateBonusShip();
        checkCollisions();
    }

    private void updateBonusShip() {
        if (!bonusShipActive) {
            bonusShipTimer--;
            if (bonusShipTimer <= 0) {
                spawnBonusShip();
            }
        } else {
            bonusShipX += bonusShipDirection * 3; // Slightly faster than aliens
            if (bonusShipDirection == 1 && bonusShipX > WIDTH) {
                bonusShipActive = false;
                resetBonusShipTimer();
            } else if (bonusShipDirection == -1 && bonusShipX < -PLAYER_WIDTH) {
                bonusShipActive = false;
                resetBonusShipTimer();
            }
        }
    }

    private void spawnBonusShip() {
        bonusShipActive = true;
        if (random.nextBoolean()) {
            // Start from left
            bonusShipX = -PLAYER_WIDTH;
            bonusShipDirection = 1;
        } else {
            // Start from right
            bonusShipX = WIDTH;
            bonusShipDirection = -1;
        }
    }

    private void updatePlayerBullet() {
        if (playerBullet != null) {
            playerBullet.y -= 10;
            if (playerBullet.y < 0) {
                playerBullet = null;
            }
        }
    }

    private void updateAliens() {
        alienMoveCounter++;
        if (alienMoveCounter >= alienMoveThreshold) {
            alienMoveCounter = 0;
            animframe = !animframe;
            boolean shiftDown = false;

            // Check if any alien hit the edge
            for (Alien alien : aliens) {
                if (alienDirection == 1 && alien.x + ALIEN_WIDTH >= WIDTH - 20) {
                    shiftDown = true;
                    break;
                } else if (alienDirection == -1 && alien.x <= 20) {
                    shiftDown = true;
                    break;
                }
            }

            if (shiftDown) {
                alienDirection *= -1;
                for (Alien alien : aliens) {
                    alien.y += 20;
                }
            } else {
                for (Alien alien : aliens) {
                    alien.x += (10 * alienDirection);
                }
            }
        }

        // Randomly fire alien bullets
        if (random.nextInt(100) < 2) { // 2% chance per tick to fire from a random alien
            if (!aliens.isEmpty()) {
                Alien shooter = aliens.get(random.nextInt(aliens.size()));
                alienBullets.add(new Bullet(shooter.x + (ALIEN_WIDTH - BULLET_WIDTH) / 2, shooter.y + ALIEN_HEIGHT));
            }
        }
    }

    private void updateAlienBullets() {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            b.y += 5;
            if (b.y > HEIGHT) {
                toRemove.add(b);
            }
        }
        alienBullets.removeAll(toRemove);
    }

    private void checkCollisions() {
        // Player bullet vs Aliens
        if (playerBullet != null) {
            Alien hit = null;
            for (Alien alien : aliens) {
                if (intersects(playerBullet.x, playerBullet.y, BULLET_WIDTH, BULLET_HEIGHT,
                        alien.x, alien.y, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                    hit = alien;
                    break;
                }
            }
            if (hit != null) {
                aliens.remove(hit);
                playerBullet = null;
                score += hit.points;
                if (aliens.isEmpty()) {
                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                    startNextWave();
                } else {
                    // Speed up aliens: threshold decreases as aliens are destroyed
                    int totalAliens = ALIEN_ROWS * ALIEN_COLS;
                    int remainingAliens = aliens.size();
                    alienMoveThreshold = 2
                            + (int) ((waveBaseMoveThreshold - 2) * ((double) remainingAliens / totalAliens));
                }
            } else if (bonusShipActive && intersects(playerBullet.x, playerBullet.y, BULLET_WIDTH, BULLET_HEIGHT,
                    bonusShipX, bonusShipY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                // Player bullet vs Bonus Ship
                bonusShipActive = false;
                playerBullet = null;
                score += 300;
                resetBonusShipTimer();
            }
        }

        // Player bullet vs Shields
        if (playerBullet != null) {
            for (Shield shield : shields) {
                if (shield.hit(playerBullet.x, playerBullet.y, true)) {
                    playerBullet = null;
                    break;
                }
            }
        }

        // Alien bullets vs Player
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            if (intersects(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT,
                    playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                toRemove.add(b);
                lives--;
                if (lives > 0) {
                    handleLifeLost();
                } else {
                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                }
            }
        }
        alienBullets.removeAll(toRemove);

        // Alien bullets vs Shields
        toRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            for (Shield shield : shields) {
                if (shield.hit(b.x, b.y, false)) {
                    toRemove.add(b);
                    break;
                }
            }
        }
        alienBullets.removeAll(toRemove);
    }

    private boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    // Actions
    public void movePlayer(int delta) {
        if (paused)
            return;
        playerX += delta;
        if (playerX < 0)
            playerX = 0;
        if (playerX > WIDTH - PLAYER_WIDTH)
            playerX = WIDTH - PLAYER_WIDTH;
    }

    public void firePlayerBullet() {
        if (paused)
            return;
        if (playerBullet == null) {
            playerBullet = new Bullet(playerX + (PLAYER_WIDTH - BULLET_WIDTH) / 2, playerY);
        }
    }

    // Getters
    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public List<Alien> getAliens() {
        return aliens;
    }

    public List<Shield> getShields() {
        return shields;
    }

    public Bullet getPlayerBullet() {
        return playerBullet;
    }

    public List<Bullet> getAlienBullets() {
        return alienBullets;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isBonusShipActive() {
        return bonusShipActive;
    }

    public int getBonusShipX() {
        return bonusShipX;
    }

    public int getBonusShipY() {
        return bonusShipY;
    }

    public boolean isAnimFrame() {
        return animframe;
    }

    public boolean isBulletAnimFrame() {
        return bulletAnimFrame;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public boolean isOnTitleScreen() {
        return onTitleScreen;
    }

    public void startGame() {
        onTitleScreen = false;
    }

    private void startNextWave() {
        waveBaseMoveThreshold -= 5;
        if (waveBaseMoveThreshold < 10)
            waveBaseMoveThreshold = 10;
        alienMoveThreshold = waveBaseMoveThreshold;
        initAliens();
        initShields();
        resetBonusShipTimer();
        playerBullet = null;
        alienBullets = new ArrayList<>();
    }

    // Inner classes for entities
    public static class Alien {
        public int x, y, points, row;

        public Alien(int x, int y, int points, int row) {
            this.x = x;
            this.y = y;
            this.points = points;
            this.row = row;
        }
    }

    public static class Bullet {
        public int x, y;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Shield {
        public int x, y;
        public boolean[][] segments; // [row][col]

        public Shield(int x, int y) {
            this.x = x;
            this.y = y;
            int rows = SHIELD_HEIGHT / SHIELD_SEGMENT_SIZE;
            int cols = SHIELD_WIDTH / SHIELD_SEGMENT_SIZE;
            segments = new boolean[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    segments[r][c] = true;
                }
            }
        }

        public boolean hit(int bx, int by, boolean isUpward) {
            int rows = segments.length;
            int cols = segments[0].length;

            if (isUpward) {
                // Player bullet: search from bottom up
                for (int r = rows - 1; r >= 0; r--) {
                    for (int c = 0; c < cols; c++) {
                        if (segments[r][c]) {
                            int sx = x + c * SHIELD_SEGMENT_SIZE;
                            int sy = y + r * SHIELD_SEGMENT_SIZE;
                            if (bx < sx + SHIELD_SEGMENT_SIZE && bx + BULLET_WIDTH > sx &&
                                    by < sy + SHIELD_SEGMENT_SIZE && by + BULLET_HEIGHT > sy) {
                                clearVertical(r, c, bx, true);
                                return true;
                            }
                        }
                    }
                }
            } else {
                // Alien bullet: search from top down
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (segments[r][c]) {
                            int sx = x + c * SHIELD_SEGMENT_SIZE;
                            int sy = y + r * SHIELD_SEGMENT_SIZE;
                            if (bx < sx + SHIELD_SEGMENT_SIZE && bx + BULLET_WIDTH > sx &&
                                    by < sy + SHIELD_SEGMENT_SIZE && by + BULLET_HEIGHT > sy) {
                                clearVertical(r, c, bx, false);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        private void clearVertical(int hitRow, int hitCol, int bx, boolean isUpward) {
            int rows = segments.length;
            int cols = segments[0].length;
            int sx = x + hitCol * SHIELD_SEGMENT_SIZE;

            // Determine affected columns
            int startCol = hitCol;
            int endCol = (bx + BULLET_WIDTH > sx + SHIELD_SEGMENT_SIZE) ? hitCol + 1 : hitCol;
            if (endCol >= cols)
                endCol = cols - 1;

            // Clear a vertical chunk of 6 segments (50% of shield height)
            for (int c = startCol; c <= endCol; c++) {
                if (isUpward) {
                    for (int r = hitRow; r > hitRow - 6 && r >= 0; r--) {
                        segments[r][c] = false;
                    }
                } else {
                    for (int r = hitRow; r < hitRow + 6 && r < rows; r++) {
                        segments[r][c] = false;
                    }
                }
            }
        }
    }
}
