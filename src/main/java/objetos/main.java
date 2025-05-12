package objetos;

import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



public class main extends WindowAdapter {
	private final JFrame frame;
	private final Surface surface;

	public main() {
	frame = new JFrame("Bola Rebotando");
	frame.setDefaultCloseOperation(JFrame.
	DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(this);
	frame.add(surface = new Surface(650, 450));
	frame.pack();
		frame.setLocationRelativeTo(null);
	}

	public void iniciar() {
		frame.setVisible(true);
		surface.createBufferStrategy(2);
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