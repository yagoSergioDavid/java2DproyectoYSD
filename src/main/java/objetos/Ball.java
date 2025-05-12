package objetos;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ball {
	private Surface surface;
	private int size;
	private double x;
	private double y;
	private double vx;
	private double vy;
	private Color color;
	private int bounceCount =0;
	private static final double MAX_SPEED = 600; // límite razonable
	private long lastWhiteStart = System.currentTimeMillis();
	private static final long WHITE_INTERVAL = 5000; // cada 3 segundos cambia a blanco
	private static final long WHITE_DURATION = 300; // está blanca durante 300 ms
	private boolean isWhite = false;
	private boolean active = true;

	
	public Ball(Surface surface) {
	    this.surface = surface;

	    // Tamaño fijo
	    size = 50;

	    // Posición centrada
	    x = (surface.getWidth() - size) / 2;
	    y = (surface.getHeight() - size) / 2;

	    // Dirección aleatoria
	    double speed = 200;
	    double direction = Math.random() * 2 * Math.PI; // dirección aleatoria en radianes
	    vx = Math.cos(direction) * speed;
	    vy = Math.sin(direction) * speed;

	    // Color fijo
	    color = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	}


	
	public Ball(Surface surface, double x, double y, int size, double direction, double speed, Color color) {
		this.x = x;
		this.y = y;
		this.size = size;
		vx = Math.cos(direction) * speed;
		vy = Math.sin(direction) * speed;
		this.surface = surface;
		this.color = color;
	}

	public void paint(Graphics2D g) {
		g.setColor(color);
		g.fillOval((int) x, (int) y, (int) size, size);
	}
	
	public void updateColor() {
	    long now = System.currentTimeMillis();

	    if (!isWhite && now - lastWhiteStart >= WHITE_INTERVAL) {
	        isWhite = true;
	        color = Color.WHITE;
	        lastWhiteStart = now; // marca el comienzo del blanco
	    } else if (isWhite && now - lastWhiteStart >= WHITE_DURATION) {
	        isWhite = false;
	        color = new Color(1.0f, 0.0f, 0.0f); // rojo de nuevo
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
	        bounceCount++;
	        if (bounceCount >= 2) {
	            // Calcula velocidad actual
	            double speed = Math.sqrt(vx * vx + vy * vy);

	            if (speed < MAX_SPEED) {
	                // Aumenta un 10% solo si no supera el máximo
	                vx *= 1.1;
	                vy *= 1.1;
	            }

	            bounceCount = 0;
	        }
	    }
	}


}