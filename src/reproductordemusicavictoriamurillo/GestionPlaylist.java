/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductordemusicavictoriamurillo;

/**
 *
 * @author Administrator
 */
import java.util.ArrayList;

public class GestionPlaylist {
    private ArrayList<PistaMusical> playlist;

    public GestionPlaylist() {
        playlist = new ArrayList<>();
    }

    public void agregarPista(PistaMusical pista) {
        playlist.add(pista);
    }

    public PistaMusical obtenerPista(int indice) {
        return playlist.get(indice);
    }

    public ArrayList<PistaMusical> obtenerPlaylist() {
        return playlist;
    }
}