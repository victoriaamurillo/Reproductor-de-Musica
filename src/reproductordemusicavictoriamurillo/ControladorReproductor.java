package reproductordemusicavictoriamurillo;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;

public class ControladorReproductor {
    private Player reproductor;
    private File archivoPista;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private long totalLength;
    private long pauseLocation;
    private boolean estaPausado = false;

    public void reproducir(PistaMusical pista) throws Exception {
        if (estaPausado && pista.getArchivo().equals(archivoPista)) {
            reanudar();
        } else {
            archivoPista = pista.getArchivo();
            iniciarReproduccion();
        }
    }

    private void iniciarReproduccion() throws Exception {
        fis = new FileInputStream(archivoPista);
        bis = new BufferedInputStream(fis);
        reproductor = new Player(bis);
        totalLength = fis.available();

        new Thread(() -> {
            try {
                reproductor.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        estaPausado = false;
    }

    public void pausar() throws Exception {
        if (reproductor != null && !estaPausado) { // Solo pausar si no está ya pausado
            pauseLocation = fis.available();  // Guardar la posición actual
            reproductor.close();  // Cerrar el reproductor pero no el flujo de archivo
            estaPausado = true;
        }
    }

    private void reanudar() throws Exception {
        if (estaPausado) {
            fis = new FileInputStream(archivoPista);  // Reabrir el archivo
            bis = new BufferedInputStream(fis);
            reproductor = new Player(bis);
            fis.skip(totalLength - pauseLocation);  // Saltar a la posición donde se pausó

            new Thread(() -> {
                try {
                    reproductor.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            estaPausado = false;
        }
    }

    public void detener() {
        if (reproductor != null) {
            reproductor.close();  // Cerrar el reproductor
        }
        try {
            if (fis != null) {
                fis.close();  // Cerrar el flujo de archivo
            }
            if (bis != null) {
                bis.close();  // Cerrar el buffer
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reproductor = null;
        estaPausado = false;
        pauseLocation = 0;
        totalLength = 0;
    }
}
