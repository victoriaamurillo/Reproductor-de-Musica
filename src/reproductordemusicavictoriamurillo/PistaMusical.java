package reproductordemusicavictoriamurillo;

import java.io.File;
import javax.swing.ImageIcon;

public class PistaMusical {

    private String nombre;
    private String artista;
    private String duracion;
    private String genero;
    private ImageIcon imagenPortada;
    private File archivo;

    // Constructor que acepta archivo de música y la imagen de portada
    public PistaMusical(File archivo, ImageIcon imagenPortada) {
        this.archivo = archivo;
        this.imagenPortada = imagenPortada;
        this.nombre = archivo.getName();  // Si quieres usar el nombre del archivo por defecto
        this.artista = "Desconocido";  // Puedes modificar esto si tienes más información
        this.duracion = "00:00";  // Puedes establecer la duración si tienes ese dato
        this.genero = "Desconocido";  // Puedes modificar esto si es necesario
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }

    public String getGenero() {
        return genero;
    }

    public ImageIcon getImagenPortada() {
        return imagenPortada;
    }

    public File getArchivo() {
        return archivo;
    }

    public String getDuracion() {
        return duracion;
    }

    // Setters (si necesitas establecerlos más tarde)
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
