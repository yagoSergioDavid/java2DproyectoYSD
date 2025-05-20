package menu;

import java.awt.*;
import javax.swing.*;

import gestsor.Ranking;
import objetos.Surface;

public class ventanaFinal extends JFrame {

    private JTextField campoNombre;
    private JButton botonGuardar, botonReiniciar, botonMenu;
    private Image fondo;

    public ventanaFinal(int puntos, String mejorJugador, int mejorPuntuacion) {
        setTitle("Fin del juego");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int anchoPantalla = gd.getDisplayMode().getWidth();
        int altoPantalla = gd.getDisplayMode().getHeight();
        setSize(anchoPantalla, altoPantalla);

        // Cargar fondo
        fondo = new ImageIcon(getClass().getResource("/menu/fondoFinal.png")).getImage();

        // Panel principal con fondo
        JPanel panelConFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelConFondo.setLayout(new BoxLayout(panelConFondo, BoxLayout.Y_AXIS));
        panelConFondo.setOpaque(false);

        // Etiquetas
        JLabel textoPuntos = new JLabel("Has perdido. Tu puntuación: " + puntos);
        JLabel textoRecord = new JLabel("Récord: " + mejorJugador + " con " + mejorPuntuacion);
        textoPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);
        textoRecord.setAlignmentX(Component.CENTER_ALIGNMENT);
        textoPuntos.setFont(new Font("Arial", Font.BOLD, 28));
        textoRecord.setFont(new Font("Arial", Font.BOLD, 24));
        textoPuntos.setForeground(Color.WHITE);
        textoRecord.setForeground(Color.WHITE);

        // Campo nombre y botón guardar
        JPanel panelNombre = new JPanel();
        panelNombre.setOpaque(false);
        campoNombre = new JTextField(15);
        botonGuardar = new JButton("Guardar puntuación");
        panelNombre.add(new JLabel("Tu nombre:"));
        panelNombre.add(campoNombre);
        panelNombre.add(botonGuardar);
        panelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mensaje informativo
        JLabel mensajeInfo = new JLabel("");
        mensajeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mensajeInfo.setFont(new Font("Arial", Font.BOLD, 20));
        mensajeInfo.setForeground(Color.YELLOW);

        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        botonReiniciar = new JButton("Reiniciar");
        botonMenu = new JButton("Menú");
        panelBotones.add(botonReiniciar);
        panelBotones.add(botonMenu);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar todo al panel
        panelConFondo.add(Box.createVerticalStrut(100));
        panelConFondo.add(textoPuntos);
        panelConFondo.add(Box.createVerticalStrut(20));
        panelConFondo.add(textoRecord);
        panelConFondo.add(Box.createVerticalStrut(40));
        panelConFondo.add(panelNombre);
        panelConFondo.add(Box.createVerticalStrut(10));
        panelConFondo.add(mensajeInfo); // Aquí el mensaje
        panelConFondo.add(Box.createVerticalStrut(30));
        panelConFondo.add(panelBotones);

        setContentPane(panelConFondo);

        // Acción guardar
        botonGuardar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                Ranking.guardarPuntuacion(nombre, puntos);
                mensajeInfo.setText("✔ Puntuación guardada correctamente");
            } else {
                mensajeInfo.setText("⚠ Por favor, escribe un nombre");
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
    }
}
