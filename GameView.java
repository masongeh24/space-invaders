import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * GameView.java
 * 
 * This class handles the rendering of the Space Invaders game.
 * It extends JPanel to provide a drawing surface.
 */
public class GameView extends JPanel {
    private GameModel model;
    private Color playerColor = Color.GREEN;

    public GameView(GameModel model) {
        this.model = model;
        this.setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        this.setBackground(Color.BLACK);
    }

    public void flashPlayer() {
        playerColor = Color.WHITE;
        repaint();
        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            playerColor = Color.GREEN;
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        drawPlayer(g);
        drawAliens(g);
        drawShields(g);
        drawBullets(g);
        drawUI(g);
    }

    private void drawPlayer(Graphics g) {
        g.setColor(playerColor);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), GameModel.PLAYER_WIDTH, GameModel.PLAYER_HEIGHT);
    }

    private void drawAliens(Graphics g) {
        g.setColor(Color.WHITE);
        for (GameModel.Alien alien : model.getAliens()) {
            g.fillRect(alien.x, alien.y, GameModel.ALIEN_WIDTH, GameModel.ALIEN_HEIGHT);
        }
    }

    private void drawShields(Graphics g) {
        g.setColor(Color.GREEN);
        for (GameModel.Shield shield : model.getShields()) {
            boolean[][] segments = shield.segments;
            for (int r = 0; r < segments.length; r++) {
                for (int c = 0; c < segments[r].length; c++) {
                    if (segments[r][c]) {
                        int sx = shield.x + c * GameModel.SHIELD_SEGMENT_SIZE;
                        int sy = shield.y + r * GameModel.SHIELD_SEGMENT_SIZE;
                        g.fillRect(sx, sy, GameModel.SHIELD_SEGMENT_SIZE, GameModel.SHIELD_SEGMENT_SIZE);
                    }
                }
            }
        }
    }

    private void drawBullets(Graphics g) {
        if (model.isGameOver()) return;

        // Player bullet
        GameModel.Bullet pb = model.getPlayerBullet();
        if (pb != null) {
            g.setColor(Color.YELLOW);
            g.fillRect(pb.x, pb.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
        }

        // Alien bullets
        g.setColor(Color.RED);
        for (GameModel.Bullet ab : model.getAlienBullets()) {
            g.fillRect(ab.x, ab.y, GameModel.BULLET_WIDTH, GameModel.BULLET_HEIGHT);
        }
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + model.getScore(), 10, 20);
        g.drawString("Lives: " + model.getLives(), 10, 40);
        
        if (model.getLives() <= 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", GameModel.WIDTH / 2 - 150, GameModel.HEIGHT / 2);
        } else if (model.getAliens().isEmpty()) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("YOU WIN!", GameModel.WIDTH / 2 - 120, GameModel.HEIGHT / 2);
        }
    }
}
