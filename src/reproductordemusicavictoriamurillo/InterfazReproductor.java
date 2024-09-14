package reproductordemusicavictoriamurillo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;



public class InterfazReproductor extends JFrame {

    private ControladorReproductor controlador;
    private GestionPlaylist gestionPlaylist;
    private JList<String> listaPistas;
    private JLabel etiquetaPortada;
    private JLabel etiquetaNombre;
    private JLabel etiquetaArtista;
    private JLabel etiquetaDuracion;
    private JLabel etiquetaGenero;
    private File carpetaMusica;
    private PistaMusical pistaActual = null;
    private JSlider sliderTiempo;

    public InterfazReproductor() {
        controlador = new ControladorReproductor();
        gestionPlaylist = new GestionPlaylist();

        carpetaMusica = new File("Musica");
        if (!carpetaMusica.exists()) {
            carpetaMusica.mkdir();
        }
        setTitle("Reproductor de Música");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Colores que se asemejan a los de Spotify
        Color fondoOscuro = new Color(18, 18, 18);  // Fondo casi negro
        Color verdeSpotify = new Color(30, 215, 96);  // Verde brillante de Spotify
        Color textoBlanco = Color.WHITE;
        Color textoVerdeClaro = new Color(144, 238, 144);  // Verde claro para algunos textos

        // Establecer el color de fondo para toda la ventana
        getContentPane().setBackground(fondoOscuro);

        listaPistas = new JList<>();
        listaPistas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPistas.setBackground(fondoOscuro);
        listaPistas.setForeground(textoBlanco);
        listaPistas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarPortadaPistaSeleccionada();
                mostrarInformacionPistaSeleccionada();  // Mostrar la información de la canción
            }
        });

        etiquetaPortada = new JLabel();
        etiquetaPortada.setHorizontalAlignment(JLabel.CENTER);
        etiquetaPortada.setVerticalAlignment(JLabel.CENTER);

        // Crear las etiquetas para mostrar la información de la canción
        etiquetaNombre = new JLabel();
        etiquetaNombre.setForeground(textoBlanco);  // Cambiar color del texto a blanco
        etiquetaArtista = new JLabel();
        etiquetaArtista.setForeground(textoBlanco);
        etiquetaDuracion = new JLabel();
        etiquetaDuracion.setForeground(textoBlanco);
        etiquetaGenero = new JLabel();
        etiquetaGenero.setForeground(textoBlanco);

        // Configurar el panel donde irá la imagen y la información
        JPanel panelInfoPista = new JPanel(new BorderLayout());
        panelInfoPista.setBackground(fondoOscuro);  // Fondo oscuro para el panel de la información
        panelInfoPista.add(etiquetaPortada, BorderLayout.CENTER);  // Imagen de la canción

        // Panel para la información de la pista
        JPanel panelInformacion = new JPanel(new GridLayout(4, 1));  // Ahora con 4 etiquetas
        panelInformacion.setBackground(fondoOscuro);  // Fondo oscuro
        panelInformacion.add(etiquetaNombre);
        panelInformacion.add(etiquetaArtista);
        panelInformacion.add(etiquetaDuracion);
        panelInformacion.add(etiquetaGenero);

        panelInfoPista.add(panelInformacion, BorderLayout.SOUTH);  // Información de la pista debajo de la imagen

        JButton botonReproducir = new JButton("Reproducir");
        botonReproducir.setBackground(verdeSpotify);  // Botón verde tipo Spotify
        botonReproducir.setForeground(textoBlanco);
        JButton botonPausar = new JButton("Pausar");
        botonPausar.setBackground(fondoOscuro);  // Fondo oscuro para botones secundarios
        botonPausar.setForeground(textoBlanco);
        JButton botonDetener = new JButton("Detener");
        botonDetener.setBackground(fondoOscuro);
        botonDetener.setForeground(textoBlanco);
        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.setBackground(fondoOscuro);
        botonAgregar.setForeground(textoBlanco);

        botonReproducir.addActionListener(e -> {
            try {
                reproducirPistaSeleccionada();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        botonPausar.addActionListener(e -> {
            try {
                controlador.pausar();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        botonDetener.addActionListener(e -> controlador.detener());
        botonAgregar.addActionListener(e -> mostrarDialogoAgregarPista());

        JPanel panelControles = new JPanel();
        panelControles.setBackground(fondoOscuro);  // Fondo oscuro para los controles
        panelControles.add(botonReproducir);
        panelControles.add(botonPausar);
        panelControles.add(botonDetener);
        panelControles.add(botonAgregar);
        

        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2));
        panelPrincipal.add(new JScrollPane(listaPistas));
        panelPrincipal.add(panelInfoPista);  // Panel con la imagen y la información de la pista

        add(panelPrincipal, BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        cargarPistasExistentes();
        setVisible(true);
    }
private void mostrarPortadaPistaSeleccionada() {
    int indiceSeleccionado = listaPistas.getSelectedIndex();
    if (indiceSeleccionado != -1) {
        PistaMusical pistaSeleccionada = gestionPlaylist.obtenerPista(indiceSeleccionado);
        ImageIcon iconoOriginal = pistaSeleccionada.getImagenPortada();

        // Obtener la imagen original
        Image imagenOriginal = iconoOriginal.getImage();
        
        // Dimensiones máximas para mostrar la imagen dentro de la etiqueta
        int anchoEtiqueta = etiquetaPortada.getWidth();
        int altoEtiqueta = etiquetaPortada.getHeight();
        
        // Calcular la proporción manteniendo el aspecto original de la imagen
        double proporcionAncho = (double) anchoEtiqueta / imagenOriginal.getWidth(null);
        double proporcionAlto = (double) altoEtiqueta / imagenOriginal.getHeight(null);
        double proporcion = Math.min(proporcionAncho, proporcionAlto); // Escalar manteniendo el aspecto

        // Escalar la imagen sin smooth scaling
        Image imagenEscalada = imagenOriginal.getScaledInstance(
                (int) (imagenOriginal.getWidth(null) * proporcion),
                (int) (imagenOriginal.getHeight(null) * proporcion),
                Image.SCALE_DEFAULT // Sin smooth scaling
        );
        
        // Mostrar la imagen escalada en la etiqueta
        etiquetaPortada.setIcon(new ImageIcon(imagenEscalada));
        pistaActual = pistaSeleccionada;
    }
}
    private void mostrarInformacionPistaSeleccionada() {
        if (pistaActual != null) {
            etiquetaNombre.setText("Nombre: " + pistaActual.getNombre());
        }
    }

  private void reproducirPistaSeleccionada() throws Exception {
    controlador.detener();

    int indiceSeleccionado = listaPistas.getSelectedIndex();
    if (indiceSeleccionado != -1) {
        PistaMusical pistaSeleccionada = gestionPlaylist.obtenerPista(indiceSeleccionado);
        controlador.reproducir(pistaSeleccionada);
        // No hacemos nada con la imagen aquí, se queda tal como fue seleccionada
    }
}

    private void mostrarDialogoAgregarPista() {
        JFileChooser selectorArchivo = new JFileChooser();
        int resultado = selectorArchivo.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = selectorArchivo.getSelectedFile();
            try {
                PistaMusical nuevaPista = mostrarFormularioPista(archivoSeleccionado);
                if (nuevaPista != null) {
                    File carpetaPista = new File(carpetaMusica, nuevaPista.getNombre());
                    if (!carpetaPista.exists()) {
                        carpetaPista.mkdir();
                    }

                    File destinoMusica = new File(carpetaPista, nuevaPista.getNombre() + ".mp3");
                    Files.copy(archivoSeleccionado.toPath(), destinoMusica.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File destinoImagen = new File(carpetaPista, "portada.jpg");
                    File archivoImagen = new File(nuevaPista.getImagenPortada().getDescription());
                    Files.copy(archivoImagen.toPath(), destinoImagen.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    gestionPlaylist.agregarPista(nuevaPista);
                    actualizarListaPistas();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarListaPistas() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (PistaMusical pista : gestionPlaylist.obtenerPlaylist()) {
            modelo.addElement(pista.getNombre() + " - " + pista.getArtista());
        }
        listaPistas.setModel(modelo);
    }

    private PistaMusical mostrarFormularioPista(File archivoMusica) {
        JDialog dialogo = new JDialog(this, "Agregar Pista", true);
        dialogo.setSize(400, 300);
        dialogo.setLayout(new GridLayout(6, 2));

        JTextField campoNombre = new JTextField();
        JTextField campoArtista = new JTextField();
        JTextField campoGenero = new JTextField();
        JLabel etiquetaImagen = new JLabel("Selecciona una imagen");
        JButton botonSeleccionarImagen = new JButton("Seleccionar Imagen");
        final ImageIcon[] imagenIcono = {null};

        botonSeleccionarImagen.addActionListener(e -> {
            JFileChooser selectorImagen = new JFileChooser();
            int resultado = selectorImagen.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivoImagenSeleccionado = selectorImagen.getSelectedFile();
                imagenIcono[0] = new ImageIcon(archivoImagenSeleccionado.getPath());
                imagenIcono[0].setDescription(archivoImagenSeleccionado.getAbsolutePath());
                etiquetaImagen.setText(archivoImagenSeleccionado.getName());
            }
        });

        JButton botonGuardar = new JButton("Guardar");

        dialogo.add(new JLabel("Nombre de la Pista:"));
        dialogo.add(campoNombre);
        dialogo.add(new JLabel("Artista:"));
        dialogo.add(campoArtista);
        dialogo.add(new JLabel("Género:"));
        dialogo.add(campoGenero);
        dialogo.add(etiquetaImagen);
        dialogo.add(botonSeleccionarImagen);
        dialogo.add(new JLabel(""));
        dialogo.add(botonGuardar);

        final PistaMusical[] nuevaPista = {null};
        botonGuardar.addActionListener(e -> {
            String nombre = campoNombre.getText();
            String artista = campoArtista.getText();
            String genero = campoGenero.getText();

            if (!nombre.isEmpty() && !artista.isEmpty() && imagenIcono[0] != null) {
                PistaMusical pistaTemporal = new PistaMusical(archivoMusica, imagenIcono[0]);
                pistaTemporal.setNombre(nombre);
                pistaTemporal.setArtista(artista);
                pistaTemporal.setGenero(genero);
                nuevaPista[0] = pistaTemporal;
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Completa todos los campos y selecciona una imagen", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        return nuevaPista[0];
    }

    private void cargarPistasExistentes() {
        File[] carpetasPistas = carpetaMusica.listFiles(File::isDirectory);
        if (carpetasPistas != null) {
            for (File carpetaPista : carpetasPistas) {
                try {
                    File archivoMusica = new File(carpetaPista, carpetaPista.getName() + ".mp3");
                    File archivoImagen = new File(carpetaPista, "portada.jpg");

                    ImageIcon imagenPortada;
                    if (archivoImagen.exists()) {
                        imagenPortada = new ImageIcon(archivoImagen.getAbsolutePath());
                        imagenPortada.setDescription(archivoImagen.getAbsolutePath());
                    } else {
                        imagenPortada = new ImageIcon("ruta/a/imagen/por/defecto.jpg");
                    }

                    if (archivoMusica.exists()) {
                        PistaMusical pista = new PistaMusical(archivoMusica, imagenPortada);
                        gestionPlaylist.agregarPista(pista);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            actualizarListaPistas();
        }
    }

    public static void main(String[] args) {
        new InterfazReproductor();
    }
}
