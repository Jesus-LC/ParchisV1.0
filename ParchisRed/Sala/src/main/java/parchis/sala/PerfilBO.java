package parchis.sala;

import parchis.dtos.PerfilDTO;
import parchis.interfaces.IPerfilDAO;
import parchis.interfaces.PersistenciaException;

/** Objeto de negocio del perfil: usa el DAO solo por su interfaz. */
public class PerfilBO {

    private final IPerfilDAO dao;

    public PerfilBO(IPerfilDAO dao) {
        this.dao = dao;
    }

    /** Devuelve el perfil del jugador; si es la primera vez que entra, lo crea. */
    public PerfilDTO obtenerOCrear(String nombre) throws PersistenciaException {
        String limpio = nombre.trim();
        var existente = dao.buscarPorNombre(limpio);
        return existente.isPresent() ? existente.get() : dao.insertar(limpio);
    }
}
