package objetos;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class main extends WindowAdapter {
	private final JFrame frame;
	private final Surface surface;

	public main() {
		frame = new JFrame();
		surface = new Surface(1920, 1080);

		frame.setUndecorated(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(surface);

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (gd.isFullScreenSupported()) {
			gd.setFullScreenWindow(frame);
		} else {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
		}

		surface.start();
	}

	public void iniciar() {
		frame.setVisible(true);
		surface.createBufferStrategy(3);
		surface.start();
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent e) {
		surface.stop();
		frame.dispose();
		System.exit(0);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new main()::iniciar);
	}

}