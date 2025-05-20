package gestsor;

import java.io.*;


public class Ranking {
	
	private static final String ARCHIVO = "rankings.txt";

    // Guarda la puntuación del jugador actual
    public static void guardarPuntuacion(String nombre, int puntos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(nombre + ":" + puntos);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Devuelve la mejor puntuación de todas
    public static int obtenerMejorPuntuacion() {
        int mejor = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length == 2) {
                    int puntos = Integer.parseInt(partes[1]);
                    if (puntos > mejor) {
                        mejor = puntos;
                    }
                }
            }
        } catch (IOException e) {
            // Si no existe el archivo aún, devolvemos 0
            return 0;
        }
        return mejor;
    }

    // Devuelve el nombre del jugador con la mejor puntuación
    public static String obtenerMejorJugador() {
        int mejor = 0;
        String mejorJugador = "Nadie";
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length == 2) {
                    int puntos = Integer.parseInt(partes[1]);
                    if (puntos > mejor) {
                        mejor = puntos;
                        mejorJugador = partes[0];
                    }
                }
            }
        } catch (IOException e) {
            return "Nadie";
        }
        return mejorJugador;
    }

}
