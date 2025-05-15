package objetos;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Jugador {
	private int x, y;
	private final int size = 50; // Escala deseada
	private Image cactusImage;

	public Jugador() {
		x = 0;
		y = 0;
		try {
			cactusImage = ImageIO.read(getClass().getResource("/objetos/cactus.png")); // ajusta la ruta si es diferente
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPosition(int mouseX, int mouseY) {
		x = mouseX - size / 2;
		y = mouseY - size / 2;
	}

	public void paint(Graphics2D g) {
		if (cactusImage != null) {
			g.drawImage(cactusImage, x, y, size, size, null);
		}
	}

	public boolean colisionaCon(Ball b) {
		double dx = (x + size / 2.0) - (b.getX() + b.getSize() / 2.0);
		double dy = (y + size / 2.0) - (b.getY() + b.getSize() / 2.0);
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance < (size / 2.0 + b.getSize() / 2.0);
	}
}
