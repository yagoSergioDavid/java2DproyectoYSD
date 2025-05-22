package objetosJuego;

import javax.swing.SwingUtilities;

import menu.menuPrincipal;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new menuPrincipal(); // Abre el menú principal
        });
    }
}
