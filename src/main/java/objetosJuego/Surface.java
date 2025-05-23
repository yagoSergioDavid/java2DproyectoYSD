package objetosJuego;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gestsor.Ranking;
import menu.menuPrincipal;
import menu.ventanaFinal;
import sonido.ReproductorMusica;

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
	private boolean gameOver = false;

	private int puntos = 0;
	private int ultimoMultiploPuntos = 0;

	private Image fondo;
	
	private JFrame frame;

	private Jugador jugador = new Jugador();

	public Surface(int w, int h, JFrame frame) {
		this.frame=frame;
		setPreferredSize(new Dimension(w, h));
		setBackground(Color.BLACK);
		fondo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/objetosJuego/fondo.png"));
		ReproductorMusica.getInstancia().reproducir("/sonido/menu.wav");

		addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
					if (!gameOver && intentosDisponibles > 0 && !balls.isEmpty()) {
						balls.get(0).tryDeactivate();
						intentosDisponibles--;
						System.out.println("Intentos restantes: " + intentosDisponibles);
					} else if (!gameOver) {
						System.out.println("¡Sin intentos disponibles!");
					}
				}

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_R && gameOver) {
					reiniciarJuego();
				}

				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_M && gameOver) {
					volverAlMenu();
				}
			}
		});


		addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jugador.setPosition(e.getX(), e.getY());
			}

			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				jugador.setPosition(e.getX(), e.getY());
			}
		});

		setFocusable(true); // IMPORTANTE para que reciba eventos de teclado
		requestFocusInWindow(); // intenta capturar el foco desde el principio

		// Creamos una imagen transparente pera meterlo como cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); // Imagen vacía
		Cursor invisibleCursor = toolkit.createCustomCursor(cursorImg, new java.awt.Point(0, 0), "InvisibleCursor");
		setCursor(invisibleCursor);

	}
	
	private void volverAlMenu() {
		System.out.println("Volviendo al menú...");
		new menuPrincipal();
		java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
		if (window != null) {
			window.dispose(); // Cierra la ventana actual del juego
		}
		
	}


	private void run() {
	    bufferStrategy = getBufferStrategy();

	    long t0 = System.nanoTime(), t1;
	    long lastBallTime = 0;
	    long firstBallTime = System.currentTimeMillis() + 3000; // Espera 3 segundos antes de la primera bola

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

	        long currentTime = System.currentTimeMillis();

	        if (currentTime - ultimoReinicioIntentos >= TIEMPO_REINICIO) {
	            intentosDisponibles = MAX_INTENTOS;
	            ultimoReinicioIntentos = currentTime;
	            System.out.println("Intentos restaurados.");
	        }

	        if (currentTime >= firstBallTime && !gameOver) {
	            balls.add(new Ball(this));
	            lastBallTime = currentTime;
	            firstBallTime = Long.MAX_VALUE; // primera bola ya creada
	        }

	        if (currentTime - lastBallTime >= 5000 && !gameOver && firstBallTime == Long.MAX_VALUE) {
	            balls.add(new Ball(this));
	            lastBallTime = currentTime;
	        }

	        next((t1 = System.nanoTime()) - t0);
	        drawFrame();
	        t0 = t1;
	    }
	}


	public void incrementarPuntos() {
		puntos++;

		if (puntos >= ultimoMultiploPuntos + 50) { // cada 50 ptos se suma un intento mas
			if (intentosDisponibles < MAX_INTENTOS) {
				intentosDisponibles++;
				System.out.println("¡Intento extra por 50 puntos!");
			}
			ultimoMultiploPuntos += 50;
		}
	}


	public void start() {
		// Asegúrate de que el canvas esté en pantalla antes de crear el buffer
		if (!isDisplayable()) {
			createBufferStrategy(4);
		} else {
			// Espera hasta que el Canvas esté listo
			createBufferStrategySafely1();
		}

		bufferStrategy = getBufferStrategy();

		t = new Thread(this::run);
		t.start();
	}

	private void createBufferStrategySafely1() {
		while (!isDisplayable()) {
			try {
				Thread.sleep(10); // espera un poco
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		createBufferStrategy(4);
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
	    if (gameOver)
	        return;

	    ArrayList<Ball> toRemove = new ArrayList<>();
	    for (Ball ball : balls) {
	        ball.move(lapse);
	        ball.updateColor();
	        if (!ball.isActive()) {
	            toRemove.add(ball);
	            continue;
	        }

	        // Si colisiona con el jugador, termina el juego o penaliza
	        if (jugador.colisionaCon(ball)) {
	            System.out.println("¡Colisión!");
	            gameOver = true;
	            ReproductorMusica.getInstancia().reproducirEfecto("/sonido/risa.wav");

	            // Pausa de 2 segundos antes de pasar a la pantalla final
	            new Thread(() -> {
	                try {
	                    Thread.sleep(2000); // espera 2 segundos
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }

	                SwingUtilities.invokeLater(() -> {
	                    frame.dispose(); // cerrar ventana fullscreen del juego

	                    String mejorJugador = Ranking.obtenerMejorJugador();
	                    int mejorPuntuacion = Ranking.obtenerMejorPuntuacion();

	                    new ventanaFinal(puntos, mejorJugador, mejorPuntuacion);
	                });
	            }).start();

	            return;
	        }

	    }
	    balls.removeAll(toRemove);
	}
	
	public int getPuntos() {
	    return puntos;
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

		if (fondo != null) {
			g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
		} else {
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

//		balls.forEach(ball -> ball.paint(g2d));
		for (Ball b : balls) {
			b.paint(g2d);
		}
		// Espacios restantes por pantalla
		g2d.setColor(Color.WHITE);
		g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
		g2d.drawString("Intentos restantes: " + intentosDisponibles, 10, 20);
		// Puntos por pantalla
		g2d.setColor(Color.YELLOW);
		g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
		g2d.drawString("Puntos: " + puntos, 10, 40);

		jugador.paint(g2d);

		if (gameOver) {
			g2d.setColor(Color.RED);
			g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 36));
			g2d.drawString("¡GAME OVER!", getWidth() / 2 - 120, getHeight() / 2);
			g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
			
		}
	}

	private void reiniciarJuego() {
		System.out.println("Reiniciando juego...");
		balls.clear();
		balls.add(new Ball(this));
		puntos = 0;
		intentosDisponibles = MAX_INTENTOS;
		ultimoReinicioIntentos = System.currentTimeMillis();
		gameOver = false;
		start(); // vuelve a lanzar el hilo

	}

}
