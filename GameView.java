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

    // Pixel patterns for top alien (Squid) - 8x8 grid
    private static final int[] TOP_ALIEN_1 = {
            0b00011000,
            0b00111100,
            0b01111110,
            0b11011011,
            0b11111111,
            0b00100100,
            0b01011010,
            0b10100101
    };

    private static final int[] TOP_ALIEN_2 = {
            0b00011000,
            0b00111100,
            0b01111110,
            0b11011011,
            0b11111111,
            0b01011010,
            0b10100101,
            0b01000010
    };

    // Pixel patterns for bottom alien (Octopus) - 12x8 grid
    private static final int[] BOTTOM_ALIEN_1 = {
            0b000011110000,
            0b011111111110,
            0b111111111111,
            0b111001100111,
            0b111111111111,
            0b000110011000,
            0b001101101100,
            0b110000000011
    };

    private static final int[] BOTTOM_ALIEN_2 = {
            0b000011110000,
            0b011111111110,
            0b111111111111,
            0b111001100111,
            0b111111111111,
            0b001110011100,
            0b011001100110,
            0b001100001100
    };

    // Pixel patterns for middle alien (Crab) - 11x8 grid
    private static final int[] MIDDLE_ALIEN_1 = {
            0b00100000100,
            0b00010001000,
            0b00111111100,
            0b01101110110,
            0b11111111111,
            0b10111111101,
            0b10100000101,
            0b00011011000
    };

    private static final int[] MIDDLE_ALIEN_2 = {
            0b00100000100,
            0b10010001001,
            0b10111111101,
            0b11101110111,
            0b11111111111,
            0b01111111110,
            0b00100000100,
            0b01000000010
    };

    // Pixel pattern for UFO (Mystery Ship) - 16x7 grid
    private static final int[] UFO_SPRITE = {
            0b0000011111100000,
            0b0001111111111000,
            0b0011111111111100,
            0b0110110110110110,
            0b1111111111111111,
            0b0011001111001100,
            0b0011000000001100
    };

    // Pixel pattern for Player (Turret) - 13x8 grid
    private static final int[] PLAYER_SPRITE = {
            0b0000001000000,
            0b0000011100000,
            0b0000011100000,
            0b0011111111100,
            0b0111111111110,
            0b1111111111111,
            0b1111111111111,
            0b1111111111111
    };

    private static final int[] PLAYER_DESTROYED_SPRITE = {
            0b0000010001000,
            0b0001000100100,
            0b0010100101000,
            0b0100010001010,
            0b0011110111000,
            0b1000101101100,
            0b0011111111001,
            0b0111111110101
    };

    private boolean isDying = false;

    public GameView(GameModel model) {
        this.model = model;
        this.setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        this.setBackground(Color.BLACK);
    }

    public void flashPlayer() {
        playerColor = Color.WHITE;
        isDying = true;
        repaint();
        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            playerColor = Color.GREEN;
            isDying = false;
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (model.isOnTitleScreen()) {
            drawTitleScreen(g);
        } else {
            drawPlayer(g);
            drawAliens(g);
            drawBonusShip(g);
            drawShields(g);
            drawBullets(g);
            drawUI(g);
            drawHighScore(g);
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(playerColor);
        int x = model.getPlayerX();
        int y = model.getPlayerY();
        int[] sprite = isDying ? PLAYER_DESTROYED_SPRITE : PLAYER_SPRITE;
        int px = 3; // Pixel width
        int py = 3; // Pixel height
        int offsetX = (GameModel.PLAYER_WIDTH - (13 * px)) / 2;
        int offsetY = (GameModel.PLAYER_HEIGHT - (8 * py)) / 2;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 13; c++) {
                if ((sprite[r] & (1 << (12 - c))) != 0) {
                    g.fillRect(x + offsetX + c * px, y + offsetY + r * py, px, py);
                }
            }
        }
    }

    private void drawAliens(Graphics g) {
        g.setColor(Color.WHITE);
        for (GameModel.Alien alien : model.getAliens()) {
            if (alien.row == 0) {
                drawTopAlien(g, alien.x, alien.y);
            } else if (alien.row >= 3) {
                drawBottomAlien(g, alien.x, alien.y);
            } else {
                drawMiddleAlien(g, alien.x, alien.y);
            }
        }
    }

    private void drawTopAlien(Graphics g, int x, int y) {
        int[] sprite = model.isAnimFrame() ? TOP_ALIEN_2 : TOP_ALIEN_1;
        int px = 3; // Pixel width
        int py = 3; // Pixel height
        int offsetX = (GameModel.ALIEN_WIDTH - (8 * px)) / 2;
        int offsetY = (GameModel.ALIEN_HEIGHT - (8 * py)) / 2;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if ((sprite[r] & (1 << (7 - c))) != 0) {
                    g.fillRect(x + offsetX + c * px, y + offsetY + r * py, px, py);
                }
            }
        }
    }

    private void drawBottomAlien(Graphics g, int x, int y) {
        int[] sprite = model.isAnimFrame() ? BOTTOM_ALIEN_2 : BOTTOM_ALIEN_1;
        int px = 3; // Pixel width
        int py = 3; // Pixel height
        int offsetX = (GameModel.ALIEN_WIDTH - (12 * px)) / 2;
        int offsetY = (GameModel.ALIEN_HEIGHT - (8 * py)) / 2;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 12; c++) {
                if ((sprite[r] & (1 << (11 - c))) != 0) {
                    g.fillRect(x + offsetX + c * px, y + offsetY + r * py, px, py);
                }
            }
        }
    }

    private void drawMiddleAlien(Graphics g, int x, int y) {
        int[] sprite = model.isAnimFrame() ? MIDDLE_ALIEN_2 : MIDDLE_ALIEN_1;
        int px = 3; // Pixel width
        int py = 3; // Pixel height
        int offsetX = (GameModel.ALIEN_WIDTH - (11 * px)) / 2;
        int offsetY = (GameModel.ALIEN_HEIGHT - (8 * py)) / 2;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 11; c++) {
                if ((sprite[r] & (1 << (10 - c))) != 0) {
                    g.fillRect(x + offsetX + c * px, y + offsetY + r * py, px, py);
                }
            }
        }
    }

    private void drawBonusShip(Graphics g) {
        if (model.isBonusShipActive()) {
            g.setColor(Color.RED);
            int x = model.getBonusShipX();
            int y = model.getBonusShipY();
            int px = 3; // Pixel width
            int py = 3; // Pixel height
            int offsetX = (GameModel.PLAYER_WIDTH - (16 * px)) / 2;
            int offsetY = (GameModel.PLAYER_HEIGHT - (7 * py)) / 2;

            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 16; c++) {
                    if ((UFO_SPRITE[r] & (1 << (15 - c))) != 0) {
                        g.fillRect(x + offsetX + c * px, y + offsetY + r * py, px, py);
                    }
                }
            }
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
        if (model.isGameOver())
            return;

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

    private void drawHighScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String text = "High Score: " + model.getHighScore();
        int width = g.getFontMetrics().stringWidth(text);
        g.drawString(text, GameModel.WIDTH - width - 10, 20);
    }

    private void drawTitleScreen(Graphics g) {
        g.setColor(Color.WHITE);
        
        // High Score
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String highScoreText = "HI-SCORE: " + model.getHighScore();
        int hsWidth = g.getFontMetrics().stringWidth(highScoreText);
        g.drawString(highScoreText, (GameModel.WIDTH - hsWidth) / 2, 50);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 72));
        String title = "SPACE INVADERS";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (GameModel.WIDTH - titleWidth) / 2, 150);

        // Prompt
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String prompt = "PRESS SPACE TO START";
        int promptWidth = g.getFontMetrics().stringWidth(prompt);
        g.drawString(prompt, (GameModel.WIDTH - promptWidth) / 2, 220);

        // Points Table
        int tableY = 300;
        int tableX = GameModel.WIDTH / 2 - 100;
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        
        // UFO
        g.setColor(Color.RED);
        drawUFOAt(g, tableX, tableY);
        g.setColor(Color.WHITE);
        g.drawString("= 300 PTS", tableX + 60, tableY + 20);
        
        // Top Alien
        tableY += 50;
        drawTopAlienAt(g, tableX + 5, tableY);
        g.drawString("= 30 PTS", tableX + 60, tableY + 20);
        
        // Middle Alien
        tableY += 50;
        drawMiddleAlienAt(g, tableX + 5, tableY);
        g.drawString("= 20 PTS", tableX + 60, tableY + 20);
        
        // Bottom Alien
        tableY += 50;
        drawBottomAlienAt(g, tableX + 5, tableY);
        g.drawString("= 10 PTS", tableX + 60, tableY + 20);
    }

    private void drawUFOAt(Graphics g, int x, int y) {
        int px = 3; int py = 3;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 16; c++) {
                if ((UFO_SPRITE[r] & (1 << (15 - c))) != 0) {
                    g.fillRect(x + c * px, y + r * py, px, py);
                }
            }
        }
    }

    private void drawTopAlienAt(Graphics g, int x, int y) {
        int px = 3; int py = 3;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if ((TOP_ALIEN_1[r] & (1 << (7 - c))) != 0) {
                    g.fillRect(x + c * px, y + r * py, px, py);
                }
            }
        }
    }

    private void drawMiddleAlienAt(Graphics g, int x, int y) {
        int px = 3; int py = 3;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 11; c++) {
                if ((MIDDLE_ALIEN_1[r] & (1 << (10 - c))) != 0) {
                    g.fillRect(x + c * px, y + r * py, px, py);
                }
            }
        }
    }

    private void drawBottomAlienAt(Graphics g, int x, int y) {
        int px = 3; int py = 3;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 12; c++) {
                if ((BOTTOM_ALIEN_1[r] & (1 << (11 - c))) != 0) {
                    g.fillRect(x + c * px, y + r * py, px, py);
                }
            }
        }
    }
}
