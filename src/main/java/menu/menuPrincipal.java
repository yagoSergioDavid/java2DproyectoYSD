package menu;

import java.awt.*;
import javax.swing.*;

import objetosJuego.Surface;
import sonido.ReproductorMusica;

public class menuPrincipal extends JFrame {

    public menuPrincipal() {
        setUndecorated(true);
        setTitle("Cactus Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //ReproducirMusica
        ReproductorMusica.getInstancia().reproducir("/sonido/menu.wav");

        // Panel principal con imagen de fondo
        JPanel panelConFondo = new JPanel() {
            private static final long serialVersionUID = 1L;
            private final Image fondo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("fondoMenu.jpg"));

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelConFondo.setLayout(new BorderLayout());
        setContentPane(panelConFondo);

        // Panel de botones centrado
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        // Crear botones personalizados
        RoundedButton btnJugar = new RoundedButton("Jugar");
        RoundedButton btnInstrucciones = new RoundedButton("Instrucciones");
        RoundedButton btnSalir = new RoundedButton("Salir");

        Dimension botonSize = new Dimension(300, 80);
        btnJugar.setMaximumSize(botonSize);
        btnInstrucciones.setMaximumSize(botonSize);
        btnSalir.setMaximumSize(botonSize);

        btnJugar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnInstrucciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espaciado
        panelBotones.add(Box.createVerticalGlue());
        panelBotones.add(btnJugar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 30)));
        panelBotones.add(btnInstrucciones);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 30)));
        panelBotones.add(btnSalir);
        panelBotones.add(Box.createVerticalGlue());

        panelConFondo.add(panelBotones, BorderLayout.CENTER);

        // Acciones de los botones
        btnJugar.addActionListener(e -> {
            JFrame ventanaJuego = new JFrame("Cactus Escape");
            ventanaJuego.setUndecorated(true);
            ventanaJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int anchoPantalla = gd.getDisplayMode().getWidth();
            int altoPantalla = gd.getDisplayMode().getHeight();
            
            Surface juego = new Surface(anchoPantalla, altoPantalla, ventanaJuego); // se pasa el JFrame aquí

            ventanaJuego.add(juego);
            ventanaJuego.pack();

            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(ventanaJuego);
            } else {
                ventanaJuego.setExtendedState(JFrame.MAXIMIZED_BOTH);
                ventanaJuego.setVisible(true);
            }

            juego.start();
            
            dispose(); // cerrar menú
        });


        btnInstrucciones.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Mueve el cactus con el ratón.\nPulsa ESPACIO para desactivar bolas.\nEvita que te golpeen.\nGana puntos y consigue intentos extra cada 50 puntos.",
                    "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        setVisible(true);
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



