import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class Ghost {
    private int x, y;
    private int speed = 4;
    private BufferedImage img;
    private String direction = "LEFT";
    private Random rand = new Random();

    public Ghost(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            img = ImageIO.read(new File("../assets/ghost_red.png"));
        } catch (Exception e) {
            System.out.println("Failed to load ghost image: " + e.getMessage());
        }
    }

    public void move(ArrayList<Rectangle> walls) {
        int nextX = x, nextY = y;

        switch (direction) {
            case "UP" -> nextY -= speed;
            case "DOWN" -> nextY += speed;
            case "LEFT" -> nextX -= speed;
            case "RIGHT" -> nextX += speed;
        }

        Rectangle nextPos = new Rectangle(nextX, nextY, 28, 28);
        boolean blocked = false;

        for (Rectangle wall : walls)
            if (nextPos.intersects(wall)) blocked = true;

        if (blocked)
            direction = switch (rand.nextInt(4)) {
                case 0 -> "UP";
                case 1 -> "DOWN";
                case 2 -> "LEFT";
                default -> "RIGHT";
            };
        else {
            x = nextX;
            y = nextY;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 28, 28);
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, 28, 28, null);
    }
}
