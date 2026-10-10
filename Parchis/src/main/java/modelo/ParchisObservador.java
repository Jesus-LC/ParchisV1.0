/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.List;
import negocio.entidades.Jugador;

/**
 *
 * @author jesus
 */
public interface ParchisObservador {
    void alCambiarEstadoJuego(String estadoMensaje, String nombreJugadorActual, int ultimoDado, List<Jugador> jugadores);
}
