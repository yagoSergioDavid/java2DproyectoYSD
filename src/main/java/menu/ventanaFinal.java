package menu;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import gestsor.Ranking;
import objetosJuego.Surface;
import sonido.ReproductorMusica;



    public class ventanaFinal extends JFrame {

        
		private static final long serialVersionUID = 1L;
		private JTextField campoNombre;
        private JButton botonGuardar, botonReiniciar, botonMenu;
        private BufferedImage fondoInicial, fondoFinal;
        private float alpha = 1.0f;
        private Timer timerTransicion;


        public ventanaFinal(int puntos, String mejorJugador, int mejorPuntuacion) {
            setTitle("Fin del juego");
            setUndecorated(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //Reproducir musica
            ReproductorMusica.getInstancia().reproducirEfecto("/sonidos/risa.wav");
            ReproductorMusica.getInstancia().reproducir("/sonido/menu.wav");


            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int anchoPantalla = gd.getDisplayMode().getWidth();
            int altoPantalla = gd.getDisplayMode().getHeight();
            setSize(anchoPantalla, altoPantalla);

            try {
                fondoInicial = ImageIO.read(getClass().getResource("/menu/fondoFinal.png"));
                fondoFinal = ImageIO.read(getClass().getResource("/menu/fondoFinPartida.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            JPanel panelConFondo = new JPanel() {
            	@Override
            	protected void paintComponent(Graphics g) {
            	    super.paintComponent(g);
            	    Graphics2D g2d = (Graphics2D) g.create();
            	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            	    // Primero fondo final (el que aparecerá)
            	    g2d.drawImage(fondoFinal, 0, 0, getWidth(), getHeight(), this);

            	    // Luego fondo inicial con opacidad decreciente (el que desaparece)
            	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            	    g2d.drawImage(fondoInicial, 0, 0, getWidth(), getHeight(), this);

            	    g2d.dispose();
            	}

            };

            panelConFondo.setLayout(new BorderLayout());
            panelConFondo.setOpaque(false);

            // Panel central con BoxLayout vertical
            JPanel panelCentral = new JPanel();
            panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
            panelCentral.setOpaque(false);
            panelCentral.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0)); // Margen superior

            JLabel textoPuntos = new JLabel("Has perdido. Tu puntuación: " + puntos);
            JLabel textoRecord = new JLabel("Récord: " + mejorJugador + " con " + mejorPuntuacion);
            textoPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);
            textoRecord.setAlignmentX(Component.CENTER_ALIGNMENT);
            textoPuntos.setFont(new Font("Arial", Font.BOLD, 28));
            textoRecord.setFont(new Font("Arial", Font.BOLD, 24));
            textoPuntos.setForeground(Color.WHITE);
            textoRecord.setForeground(Color.WHITE);

            // Panel nombre con distribución vertical
            JPanel panelNombre = new JPanel();
            panelNombre.setLayout(new BoxLayout(panelNombre, BoxLayout.Y_AXIS));
            panelNombre.setOpaque(false);
            panelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel etiquetaNombre = new JLabel("Tu nombre:");
            etiquetaNombre.setForeground(Color.WHITE);
            etiquetaNombre.setFont(new Font("Arial", Font.BOLD, 22));
            etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

            campoNombre = new JTextField(15);
            campoNombre.setMaximumSize(new Dimension(300, 30));
            campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

            botonGuardar = new RoundedButton("Guardar puntuación");
            botonGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonGuardar.setMaximumSize(new Dimension(300, 50));

            panelNombre.add(etiquetaNombre);
            panelNombre.add(Box.createVerticalStrut(10));
            panelNombre.add(campoNombre);
            panelNombre.add(Box.createVerticalStrut(10));
            panelNombre.add(botonGuardar);

            JLabel mensajeInfo = new JLabel("");
            mensajeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
            mensajeInfo.setFont(new Font("Arial", Font.BOLD, 20));
            mensajeInfo.setForeground(Color.YELLOW);

            // Panel inferior para botones
            JPanel panelBotones = new JPanel();
            panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
            panelBotones.setOpaque(false);
            panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); // margen inferior

            botonReiniciar = new RoundedButton("Reiniciar");
            botonReiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonReiniciar.setMaximumSize(new Dimension(250, 50));

            botonMenu = new RoundedButton("Menú");
            botonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonMenu.setMaximumSize(new Dimension(250, 50));

            panelBotones.add(Box.createVerticalGlue());
            panelBotones.add(botonReiniciar);
            panelBotones.add(Box.createVerticalStrut(20));
            panelBotones.add(botonMenu);
            panelBotones.add(Box.createVerticalStrut(30));

            // Agregar todo al panel central
            panelCentral.add(textoPuntos);
            panelCentral.add(Box.createVerticalStrut(20));
            panelCentral.add(textoRecord);
            panelCentral.add(Box.createVerticalStrut(40));
            panelCentral.add(panelNombre);
            panelCentral.add(Box.createVerticalStrut(15));
            panelCentral.add(mensajeInfo);

            panelConFondo.add(panelCentral, BorderLayout.CENTER);
            panelConFondo.add(panelBotones, BorderLayout.SOUTH);

            setContentPane(panelConFondo);

            // Acción guardar
            botonGuardar.addActionListener(e -> {
                String nombre = campoNombre.getText().trim();
                if (!nombre.isEmpty()) {
                    Ranking.guardarPuntuacion(nombre, puntos);
                    mensajeInfo.setText(" Puntuación guardada correctamente");
                } else {
                    mensajeInfo.setText(" Por favor, escribe un nombre");
                }
            });

            // Acción reiniciar
            botonReiniciar.addActionListener(e -> {
                dispose();
                JFrame nuevaVentana = new JFrame("Cactus Escape");
                nuevaVentana.setUndecorated(true);
                nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Surface nuevoJuego = new Surface(anchoPantalla, altoPantalla, nuevaVentana);
                nuevaVentana.add(nuevoJuego);
                nuevaVentana.pack();

                if (gd.isFullScreenSupported()) {
                    gd.setFullScreenWindow(nuevaVentana);
                } else {
                    nuevaVentana.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    nuevaVentana.setVisible(true);
                }

                nuevoJuego.start();
            });

            // Acción menú
            botonMenu.addActionListener(e -> {
                dispose();
                new menuPrincipal();
            });
            
            
            
            

            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(this);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setVisible(true);
            }
         // Iniciar la transición tras 1 segundo
            new Timer(1000, e -> {
                timerTransicion = new Timer(15, ev -> {
                    alpha -= 0.1f; // Más rápido
                    if (alpha <= 0f) {
                        alpha = 0f;
                        timerTransicion.stop();
                    }
                    repaint();
                });
                timerTransicion.start();
                ((Timer)e.getSource()).stop(); // Detener este timer de espera
            }).start();

        }

        class RoundedButton extends JButton {
            public RoundedButton(String text) {
                super(text);
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setBorder(null);
                setOpaque(false);
                setFont(new Font("Arial", Font.BOLD, 24));
                setForeground(Color.WHITE);
                setBackground(new Color(0, 230, 118)); // Verde menta
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.setColor(getForeground());
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(250, 50);
            }
        }
    }


