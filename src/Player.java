import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player {
    private int x, y;
    private int speed = 4;
    private String direction = "RIGHT";
    private BufferedImage img;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            img = ImageIO.read(new File("../assets/pacman_" + direction.toLowerCase() + ".png"));
        } catch (Exception e) {
            System.out.println("Failed to load player image: " + e.getMessage());
        }
    }

    public void move(ArrayList<Rectangle> walls) {
        int nextX = x, nextY = y;
        if (direction.equals("UP")) nextY -= speed;
        if (direction.equals("DOWN")) nextY += speed;
        if (direction.equals("LEFT")) nextX -= speed;
        if (direction.equals("RIGHT")) nextX += speed;

        Rectangle nextPos = new Rectangle(nextX, nextY, 28, 28);
        boolean blocked = false;

        for (Rectangle wall : walls)
            if (nextPos.intersects(wall)) blocked = true;

        if (!blocked) {
            x = nextX;
            y = nextY;
        }
    }

    public void setDirection(String dir) {
        this.direction = dir;
        loadImage();
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 28, 28);
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, 28, 28, null);
    }
}
