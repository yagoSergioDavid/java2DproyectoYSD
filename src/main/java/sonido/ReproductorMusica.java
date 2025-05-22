package sonido;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class ReproductorMusica {

    private Clip clip;
    private static ReproductorMusica instancia;

    // Singleton para mantener una sola instancia global
    public static ReproductorMusica getInstancia() {
        if (instancia == null) {
            instancia = new ReproductorMusica();
        }
        return instancia;
    }

    private ReproductorMusica() {}

    public void reproducir(String ruta) {
        detener(); // Detiene si hay otra canción sonando
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("Archivo de audio no encontrado: " + ruta);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir infinitamente
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public void reproducirEfecto(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("Archivo de efecto no encontrado: " + ruta);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip efecto = AudioSystem.getClip();
            efecto.open(audio);
            efecto.start();

            // Liberar recursos cuando termine
            efecto.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    efecto.close();
                }
            });
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}

