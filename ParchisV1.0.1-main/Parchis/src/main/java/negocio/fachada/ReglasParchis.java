/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.fachada;

import java.util.List;
import negocio.entidades.Jugador;

/**
 *
 * @author jesus
 */
public interface ReglasParchis {
    int lanzarDado();
    List<Jugador> obtenerJugadores();
    Jugador obtenerJugadorActual();
    boolean moverFichaJugadorActual(int fichaIndex, int valorDado);
    void pasarSiguienteTurno();
}
