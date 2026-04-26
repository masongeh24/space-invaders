import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        model.setView(view);
        
        setupWindow();
        setupInput();
        startGameLoop();
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
                        model.firePlayerBullet();
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
                gameLoop.stop();
            } else {
                if (leftPressed) model.movePlayer(-5);
                if (rightPressed) model.movePlayer(5);
                model.tick();
            }
            view.repaint();
        });
        gameLoop.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}
