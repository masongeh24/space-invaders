import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public static final int BULLET_WIDTH = 5;
    public static final int BULLET_HEIGHT = 10;
    
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    public static final int ALIEN_SPACING_X = 50;
    public static final int ALIEN_SPACING_Y = 40;

    // Game State
    private int playerX;
    private int playerY;
    private List<Alien> aliens;
    private Bullet playerBullet;
    private List<Bullet> alienBullets;
    private int score;
    private int lives;
    
    private int alienDirection = 1; // 1 for right, -1 for left
    private int alienMoveCounter = 0;
    private final int ALIEN_MOVE_THRESHOLD = 20; // Move aliens every N ticks
    
    private Random random = new Random();

    public GameModel() {
        resetGame();
    }

    public void resetGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - 50;
        playerBullet = null;
        alienBullets = new ArrayList<>();
        score = 0;
        lives = 3;
        initAliens();
    }

    private void initAliens() {
        aliens = new ArrayList<>();
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                int x = 50 + col * ALIEN_SPACING_X;
                int y = 50 + row * ALIEN_SPACING_Y;
                aliens.add(new Alien(x, y));
            }
        }
    }

    // Logic for updating game state
    public void tick() {
        updatePlayerBullet();
        updateAliens();
        updateAlienBullets();
        checkCollisions();
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
        if (alienMoveCounter >= ALIEN_MOVE_THRESHOLD) {
            alienMoveCounter = 0;
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
                alienBullets.add(new Bullet(shooter.x + ALIEN_WIDTH / 2, shooter.y + ALIEN_HEIGHT));
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
                score += 10;
            }
        }

        // Alien bullets vs Player
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            if (intersects(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT,
                           playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                toRemove.add(b);
                lives--;
                if (lives <= 0) {
                    // Game Over logic could go here
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
        playerX += delta;
        if (playerX < 0) playerX = 0;
        if (playerX > WIDTH - PLAYER_WIDTH) playerX = WIDTH - PLAYER_WIDTH;
    }

    public void firePlayerBullet() {
        if (playerBullet == null) {
            playerBullet = new Bullet(playerX + PLAYER_WIDTH / 2, playerY);
        }
    }

    // Getters
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public List<Alien> getAliens() { return aliens; }
    public Bullet getPlayerBullet() { return playerBullet; }
    public List<Bullet> getAlienBullets() { return alienBullets; }
    public int getScore() { return score; }
    public int getLives() { return lives; }

    // Inner classes for entities
    public static class Alien {
        public int x, y;
        public Alien(int x, int y) { this.x = x; this.y = y; }
    }

    public static class Bullet {
        public int x, y;
        public Bullet(int x, int y) { this.x = x; this.y = y; }
    }
}
