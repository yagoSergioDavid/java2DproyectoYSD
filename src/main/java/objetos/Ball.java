package objetos;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ball {
    private Surface surface;
    private int size;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private Color color;
    private int bounceCount = 0;
    private static final double MAX_SPEED = 600;
    private long lastWhiteStart = System.currentTimeMillis();
    private static long WHITE_INTERVAL = getRandomTime();
    private static final long WHITE_DURATION = 420;
    private boolean isWhite = false;
    private boolean active = true;

    private BufferedImage meteoroImg;

    public Ball(Surface surface) {
        this.surface = surface;
        size = 50;
        x = (surface.getWidth() - size) / 2;
        y = (surface.getHeight() - size) / 2;

        double speed = 200;
        double direction = Math.random() * 2 * Math.PI;
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;

        color = new Color(1.0f, 0.0f, 0.0f, 1.0f);

        try {
            meteoroImg = ImageIO.read(getClass().getResource("/objetos/meteoro.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen meteoro.png");
            e.printStackTrace();
        }
    }

    private static long getRandomTime() {
        return 3000 + (long) (Math.random() * 3000);
    }

    public Ball(Surface surface, double x, double y, int size, double direction, double speed, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        vx = Math.cos(direction) * speed;
        vy = Math.sin(direction) * speed;
        this.surface = surface;
        this.color = color;

        try {
            meteoroImg = ImageIO.read(getClass().getResource("/meteoro.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen meteoro.png");
            e.printStackTrace();
        }
    }

    public void paint(Graphics2D g) {
        if (isWhite) {
            g.setColor(Color.WHITE);
            g.fillOval((int) x, (int) y, size, size); // si está blanca, mostrar círculo blanco
        } else if (meteoroImg != null) {
            g.drawImage(meteoroImg, (int) x, (int) y, size, size, null);
        } else {
            g.setColor(color);
            g.fillOval((int) x, (int) y, size, size);
        }
    }

    public void updateColor() {
        long now = System.currentTimeMillis();

        if (!isWhite && now - lastWhiteStart >= WHITE_INTERVAL) {
            isWhite = true;
            lastWhiteStart = now;
        } else if (isWhite && now - lastWhiteStart >= WHITE_DURATION) {
            isWhite = false;
            WHITE_INTERVAL = getRandomTime();
        }
    }

    public void tryDeactivate() {
        if (isWhite) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void move(long lapse) {
        x += (lapse * vx) / 1_000_000_000L;
        y += (lapse * vy) / 1_000_000_000L;

        boolean bounced = false;

        if (x + size >= surface.getWidth()) {
            x = 2 * surface.getWidth() - x - 2 * size;
            vx *= -1;
            bounced = true;
        } else if (x < 0) {
            x = -x;
            vx *= -1;
            bounced = true;
        }

        if (y + size >= surface.getHeight()) {
            y = 2 * surface.getHeight() - y - 2 * size;
            vy *= -1;
            bounced = true;
        } else if (y < 0) {
            y = -y;
            vy *= -1;
            bounced = true;
        }

        if (bounced) {
            surface.incrementarPuntos();
            bounceCount++;
            if (bounceCount >= 2) {
                double speed = Math.sqrt(vx * vx + vy * vy);
                if (speed < MAX_SPEED) {
                    vx *= 1.1;
                    vy *= 1.1;
                }
                bounceCount = 0;
            }
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
