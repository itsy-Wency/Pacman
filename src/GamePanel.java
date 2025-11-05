import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Player player;
    private Ghost ghost;
    private ArrayList<Rectangle> walls;
    private ArrayList<Point> dots;
    private BufferedImage wallImg, dotImg;
    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;

    public GamePanel() {
        setPreferredSize(new Dimension(448, 496));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        try {
            wallImg = ImageIO.read(new File("../assets/wall.png"));
            dotImg = ImageIO.read(new File("../assets/dot.png"));
        } catch (Exception e) {
            System.out.println("Error loading wall/dot images: " + e.getMessage());
        }

        resetGame();

        timer = new Timer(100, this);
        timer.start();
    }

    private void resetGame() {
        player = new Player(32, 32);
        ghost = new Ghost(352, 352);

        walls = new ArrayList<>();
        dots = new ArrayList<>();

        // MAP: 14x15 grid (each 32x32 px)
        // Build walls for outer borders
        for (int i = 0; i < 14; i++) {
            walls.add(new Rectangle(i * 32, 0, 32, 32));
            walls.add(new Rectangle(i * 32, 14 * 32, 32, 32));
        }
        for (int i = 0; i < 15; i++) {
            walls.add(new Rectangle(0, i * 32, 32, 32));
            walls.add(new Rectangle(13 * 32, i * 32, 32, 32));
        }

        // INNER MAZE STRUCTURE (medium difficulty)
        // 1 = wall, 0 = dot path
        int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
            {1,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,0,0,1},
            {1,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,1},
            {1,0,1,0,1,1,0,1,0,1,0,1,0,1,1,0,1,0,0,1},
            {1,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,1,0,1,1,1,1,1,0,1,1,0,1,0,0,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
            {1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
            {1,0,1,0,1,1,0,1,1,1,1,1,0,1,1,0,1,0,0,1},
            {1,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,1,0,1,0,1,0,1,0,1,1,0,1,0,0,1},
            {1,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,1},
            {1,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        };


        // Generate walls from map
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 1) {
                    walls.add(new Rectangle(col * 32, row * 32, 32, 32));
                }
            }
        }

        // Add dots in all free spaces
        for (int y = 1; y < 14; y++) {
            for (int x = 1; x < 13; x++) {
                boolean skip = false;
                for (Rectangle w : walls)
                    if (w.contains(x * 32 + 16, y * 32 + 16))
                        skip = true;
                if (!skip)
                    dots.add(new Point(x * 32 + 12, y * 32 + 12));
            }
        }

        gameOver = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            player.move(walls);
            ghost.move(walls);

            // Dot collision
            dots.removeIf(dot -> player.getBounds().intersects(new Rectangle(dot.x, dot.y, 8, 8)) && incrementScore());

            // Ghost collision → Game Over
            if (player.getBounds().intersects(ghost.getBounds())) {
                gameOver = true;
                if (score > highScore)
                    highScore = score;
            }
        }
        repaint();
    }

    private boolean incrementScore() {
        score++;
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Walls
        for (Rectangle wall : walls)
            g.drawImage(wallImg, wall.x, wall.y, 32, 32, null);

        // Dots
        for (Point dot : dots)
            g.drawImage(dotImg, dot.x, dot.y, 8, 8, null);

        // Player & Ghost
        player.draw(g);
        ghost.draw(g);

        // Score text
        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 15);
        g.drawString("High Score: " + highScore, 350, 15);

        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("GAME OVER", 140, 240);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press R to Restart", 150, 270);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!gameOver) {
            if (code == KeyEvent.VK_UP) player.setDirection("UP");
            if (code == KeyEvent.VK_DOWN) player.setDirection("DOWN");
            if (code == KeyEvent.VK_LEFT) player.setDirection("LEFT");
            if (code == KeyEvent.VK_RIGHT) player.setDirection("RIGHT");
        }
        if (code == KeyEvent.VK_R && gameOver) {
            score = 0;
            resetGame();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
