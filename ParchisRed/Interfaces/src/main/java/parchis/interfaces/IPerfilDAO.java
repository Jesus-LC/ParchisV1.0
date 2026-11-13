package parchis.interfaces;

import java.util.Optional;
import parchis.dtos.PerfilDTO;

/** Acceso a datos del perfil. PerfilBO solo conoce esta interfaz; todo el SQL vive en PerfilDAOMySQL. */
public interface IPerfilDAO {

    Optional<PerfilDTO> buscarPorNombre(String nombre) throws PersistenciaException;

    PerfilDTO insertar(String nombre) throws PersistenciaException;
}
