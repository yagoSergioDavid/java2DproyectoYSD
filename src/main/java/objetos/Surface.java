package objetos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Surface extends Canvas {
	private static final long serialVersionUID = 1L;
	private Thread t;
	private boolean paused;
	private ArrayList<Ball> balls = new ArrayList<>();
	private BufferStrategy bufferStrategy;
	
	private int intentosDisponibles = 5;
	private final int MAX_INTENTOS = 5;
	private long ultimoReinicioIntentos = System.currentTimeMillis();
	private final long TIEMPO_REINICIO = 60000; // 60 segundos

	
	public Surface(int w, int h) {
	    setPreferredSize(new Dimension(w, h));
	    setBackground(Color.BLACK);

	    addKeyListener(new java.awt.event.KeyAdapter() {
	        @Override
	        public void keyPressed(java.awt.event.KeyEvent e) {
	            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
	                if (intentosDisponibles > 0 && !balls.isEmpty()) {
	                    balls.get(0).tryDeactivate();
	                    intentosDisponibles--;
	                    System.out.println("Intentos restantes: " + intentosDisponibles);
	                } else {
	                    System.out.println("¡Sin intentos disponibles!");
	                }
	            }
	        }
	    });


	    setFocusable(true); // IMPORTANTE para que reciba eventos de teclado
	    requestFocusInWindow(); // intenta capturar el foco desde el principio
	}


	private void run() {
		balls.add(new Ball(this)); // Añadir la primera bola
		createBufferStrategy(2);
		bufferStrategy = getBufferStrategy();
		
		long t0 = System.nanoTime(), t1;
		long lastBallTime = System.currentTimeMillis(); // tiempo en milisegundos
		
		while (!Thread.currentThread().isInterrupted()) {
			synchronized (this) {
				if (paused) {
					try {
						wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					t0 = System.nanoTime();
				}
			}

			// Comprobar si han pasado 5 segundos
			long currentTime = System.currentTimeMillis();
			if (currentTime - ultimoReinicioIntentos >= TIEMPO_REINICIO) {
			    intentosDisponibles = MAX_INTENTOS;
			    ultimoReinicioIntentos = currentTime;
			    System.out.println("Intentos restaurados.");
			}
			if (currentTime - lastBallTime >= 5000) {
				balls.add(new Ball(this)); // añadir nueva bola
				lastBallTime = currentTime; // reiniciar temporizador
			}

			next((t1 = System.nanoTime()) - t0);
			drawFrame();
			t0 = t1;
		}
		
		
		
		
		
		
	}


	public void start() {
		t = new Thread(this::run);
		t.start();
	}

	public void stop() {
		t.interrupt();
		try {
			t.join();
		} catch (InterruptedException e) {
		}
	}

	public synchronized void pause() {
		paused = true;
	}

	public synchronized void resume() {
		if (paused) {
			paused = false;
			notify();
		}
	}

	public synchronized boolean isPaused() {
		return paused;
	}

	private void next(long lapse) {
	    ArrayList<Ball> toRemove = new ArrayList<>();
	    for (Ball ball : balls) {
	        ball.move(lapse);
	        ball.updateColor();
	        if (!ball.isActive()) {
	            toRemove.add(ball);
	        }
	    }
	    balls.removeAll(toRemove);
	}


	private void drawFrame() {
		Graphics2D g = null;
		try {
			g = (Graphics2D) bufferStrategy.getDrawGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			paint(g);
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();
		} finally {
			if (g != null)
				g.dispose();
		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
//		balls.forEach(ball -> ball.paint(g2d));
		for (Ball b: balls) {
			b.paint(g2d);
		}
	}
}

