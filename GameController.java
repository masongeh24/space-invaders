import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * GameController.java
 * 
 * This class is the entry point of the application.
 * It initializes the Model, View, and Controller, and sets up the JFrame.
 * It also handles the game loop and user input.
 */
public class GameController {
    private GameModel model;
    private GameView view;
    private Timer gameLoop;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Clip shootClip;
    private Clip explosionClip;
    private Clip invaderKilledClip;
    private Clip ufoClip;
    private Clip[] invaderClips = new Clip[4];
    private int currentInvaderSound = 0;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        model.setView(view);
        
        loadSounds();
        setupWindow();
        setupInput();
        startGameLoop();
    }

    private void loadSounds() {
        try {
            File file = new File("SoundEffects/shoot.wav");
            if (file.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                shootClip = AudioSystem.getClip();
                shootClip.open(audioIn);
            }
            File explosionFile = new File("SoundEffects/explosion.wav");
            if (explosionFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(explosionFile);
                explosionClip = AudioSystem.getClip();
                explosionClip.open(audioIn);
            }
            File invaderKilledFile = new File("SoundEffects/invaderkilled.wav");
            if (invaderKilledFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(invaderKilledFile);
                invaderKilledClip = AudioSystem.getClip();
                invaderKilledClip.open(audioIn);
            }
            File ufoFile = new File("SoundEffects/ufo.wav");
            if (ufoFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(ufoFile);
                ufoClip = AudioSystem.getClip();
                ufoClip.open(audioIn);
            }
            for (int i = 0; i < 4; i++) {
                File f = new File("SoundEffects/fastinvader" + (i + 1) + ".wav");
                if (f.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                    invaderClips[i] = AudioSystem.getClip();
                    invaderClips[i].open(audioIn);
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void setupWindow() {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupInput() {
        view.setFocusable(true);
        view.requestFocusInWindow();
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    model.reset();
                    leftPressed = false;
                    rightPressed = false;
                    if (!gameLoop.isRunning()) {
                        gameLoop.start();
                    }
                    view.repaint();
                    return;
                }

                if (model.isGameOver()) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = true;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (model.isOnTitleScreen()) {
                            model.startGame();
                        } else if (model.getPlayerBullet() == null) {
                            model.firePlayerBullet();
                            playShootSound();
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = false;
                        break;
                }
            }
        });
    }

    private void startGameLoop() {
        // Run game logic and repaint every 16ms (~60 FPS)
        gameLoop = new Timer(16, e -> {
            if (model.isGameOver()) {
                stopUFOSound();
                gameLoop.stop();
            } else {
                int oldLives = model.getLives();
                int oldAliens = model.getAliens().size();
                boolean oldBonusShip = model.isBonusShipActive();
                boolean oldAnimFrame = model.isAnimFrame();

                if (leftPressed) model.movePlayer(-5);
                if (rightPressed) model.movePlayer(5);
                model.tick();

                if (model.getLives() < oldLives) {
                    playExplosionSound();
                }
                if (model.getAliens().size() < oldAliens) {
                    playInvaderKilledSound();
                }
                
                if (model.isBonusShipActive() && !oldBonusShip) {
                    playUFOSound();
                } else if (!model.isBonusShipActive() && oldBonusShip) {
                    stopUFOSound();
                }

                if (model.isAnimFrame() != oldAnimFrame) {
                    playNextInvaderSound();
                }
            }
            view.repaint();
        });
        gameLoop.start();
    }

    private void playShootSound() {
        if (shootClip != null) {
            shootClip.setFramePosition(0);
            shootClip.start();
        }
    }

    private void playExplosionSound() {
        if (explosionClip != null) {
            explosionClip.setFramePosition(0);
            explosionClip.start();
        }
    }

    private void playInvaderKilledSound() {
        if (invaderKilledClip != null) {
            invaderKilledClip.setFramePosition(0);
            invaderKilledClip.start();
        }
    }

    private void playUFOSound() {
        if (ufoClip != null) {
            ufoClip.setFramePosition(0);
            ufoClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopUFOSound() {
        if (ufoClip != null) {
            ufoClip.stop();
        }
    }

    private void playNextInvaderSound() {
        Clip c = invaderClips[currentInvaderSound];
        if (c != null) {
            c.setFramePosition(0);
            c.start();
        }
        currentInvaderSound = (currentInvaderSound + 1) % 4;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}
