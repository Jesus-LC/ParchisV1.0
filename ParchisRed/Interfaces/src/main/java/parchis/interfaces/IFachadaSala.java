package parchis.interfaces;

import parchis.dtos.SalaDTO;

/**
 * Fachada del subsistema Sala (CU-01 en P2P).
 * La presentación solo conoce esta interfaz; la implementación (mock o real)
 * la entrega la fábrica.
 */
public interface IFachadaSala {

    /**
     * Paso 1: quien crea la sala abre un puerto y comparte su dirección (anfitrión).
     *
     * @param nombreJugador nombre del anfitrión
     * @param puertoLocal   puerto que abre para escuchar a los demás pares
     * @return la sala con el anfitrión como único jugador
     * @throws SalaException DATOS_INVALIDOS si el nombre o el puerto no sirven
     */
    SalaDTO crearSala(String nombreJugador, int puertoLocal) throws SalaException;

    /**
     * Pasos 2 y 3: se conecta al anfitrión, recibe la lista de pares, se conecta
     * a cada uno y envía JugadorUnido a todos.
     *
     * @param nombreJugador   nombre de quien se une
     * @param puertoLocal     puerto en el que escuchará a los demás pares
     * @param hostAnfitrion   dirección del anfitrión
     * @param puertoAnfitrion puerto del anfitrión
     * @return la copia de la sala ya con el jugador incluido
     * @throws SalaException DATOS_INVALIDOS, SALA_LLENA (A2) o ANFITRION_NO_DISPONIBLE (A3)
     */
    SalaDTO unirseASala(String nombreJugador, int puertoLocal,
                        String hostAnfitrion, int puertoAnfitrion) throws SalaException;

    /** Copia actual de la sala de este par, o null si todavía no está en una. */
    SalaDTO obtenerSala();

    /** Paso 3: avisa a la presentación cuando la copia de la sala cambia (llega un JugadorUnido). */
    void registrarObservador(IObservadorSala observador);
}
