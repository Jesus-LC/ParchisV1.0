package parchis.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * Copia de la sala que tiene un par. Es inmutable: cada vez que la sala
 * cambia, el par recibe una copia nueva.
 */
public record SalaDTO(JugadorDTO anfitrion, List<JugadorDTO> jugadores, int capacidad) implements Serializable {

    public SalaDTO {
        jugadores = List.copyOf(jugadores);
    }

    public boolean estaLlena() {
        return jugadores.size() >= capacidad;
    }

    /** Dirección que el anfitrión comparte para que otros se unan. */
    public String direccionAnfitrion() {
        return anfitrion.direccion();
    }
}
