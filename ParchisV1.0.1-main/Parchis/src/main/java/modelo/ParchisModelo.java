/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import java.util.List;
import negocio.entidades.Jugador;
import negocio.fachada.ReglasParchis;

/**
 *
 * @author jesus
 */
public class ParchisModelo {
 
    private final List<ParchisObservador> observadores = new ArrayList<>();
    private final ReglasParchis reglasSubSistema;
    private int ultimoDado;

    public ParchisModelo(ReglasParchis fachada) {
        this.reglasSubSistema = fachada;
        this.ultimoDado = 0;
    }

    public void registrarObservador(ParchisObservador obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(String mensaje) {
        for (ParchisObservador obs : observadores) {
            obs.alCambiarEstadoJuego(
                mensaje,
                reglasSubSistema.obtenerJugadorActual().getNombre(),
                ultimoDado,
                reglasSubSistema.obtenerJugadores()
            );
        }
    }

    public void solicitarLanzamiento() {
        this.ultimoDado = reglasSubSistema.lanzarDado();
        String jugador = reglasSubSistema.obtenerJugadorActual().getNombre();
        notificarObservadores(jugador + " lanzo el dado y obtuvo un " + ultimoDado);
    }

    public void moverFicha(int fichaIndex) {
        if (ultimoDado == 0) {
            notificarObservadores("Primero debes lanzar el dado.");
            return;
        }

        String jugadorNombre = reglasSubSistema.obtenerJugadorActual().getNombre();
        boolean exito = reglasSubSistema.moverFichaJugadorActual(fichaIndex, ultimoDado);

        if (exito) {
            String msj = jugadorNombre + " movio exitosamente la ficha " + (fichaIndex + 1);
            if (ultimoDado != 6) {
                reglasSubSistema.pasarSiguienteTurno();
            } else {
                msj += " ¡Saco un 6, conserva el turno!";
            }
            this.ultimoDado = 0;
            notificarObservadores(msj);
        } else {
            notificarObservadores("Movimiento no permitido para la ficha " + (fichaIndex + 1));
        }
    }
    
    public List<Jugador> obtenerJugadores() {
        return reglasSubSistema.obtenerJugadores();
    }
}
