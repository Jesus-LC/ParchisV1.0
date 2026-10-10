/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.fachada;

/**
 *
 * @author jesus
 */

import java.util.List;
import negocio.entidades.Dado;
import negocio.entidades.Ficha;
import negocio.entidades.Jugador;
import negocio.entidades.Tablero;

public class ReglasParchisMock implements ReglasParchis {
    private final List<Jugador> jugadores;
    private final Tablero tablero;
    private final Dado dado;
    private int turnoIndex;

    public ReglasParchisMock(List<Jugador> jugadoresRegistrados) {
        this.jugadores = jugadoresRegistrados;
        this.tablero = new Tablero();
        this.dado = new Dado();
        this.turnoIndex = 0;
    }

    @Override
    public int lanzarDado() {
        return dado.lanzar();
    }

    @Override
    public List<Jugador> obtenerJugadores() {
        return jugadores;
    }

    @Override
    public Jugador obtenerJugadorActual() {
        return jugadores.get(turnoIndex);
    }

    @Override
    public boolean moverFichaJugadorActual(int fichaIndex, int valorDado) {
        Jugador jActual = obtenerJugadorActual();
        Ficha ficha = jActual.getFichas().get(fichaIndex);

        if (ficha.getEstado().equals("EnCasa")) {
            if (valorDado == 5) {
                ficha.setEstado("EnJuego");
                ficha.setPosicion(1);
                return true;
            }
            return false;
        }

        if (ficha.getEstado().equals("EnJuego")) {
            ficha.avanzar(valorDado);
            return true;
        }

        return false;
    }

    @Override
    public void pasarSiguienteTurno() {
        turnoIndex = (turnoIndex + 1) % jugadores.size();
    }
}
