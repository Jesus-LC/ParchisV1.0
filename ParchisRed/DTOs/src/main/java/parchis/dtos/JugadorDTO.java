package parchis.dtos;

import java.io.Serializable;

/**
 * Un par (jugador) de la sala: su nombre y la dirección en la que escucha.
 * En P2P cada jugador es también un servidor, por eso lleva host y puerto.
 */
public record JugadorDTO(String nombre, String host, int puerto) implements Serializable {

    /** Dirección en formato host:puerto. */
    public String direccion() {
        return host + ":" + puerto;
    }
}
