package parchis.dtos;

import java.io.Serializable;

/** Perfil guardado del jugador (tabla perfil de MySQL). */
public record PerfilDTO(int id, String nombre) implements Serializable {
}
