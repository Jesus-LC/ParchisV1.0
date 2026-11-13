package parchis.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import parchis.dtos.PerfilDTO;
import parchis.interfaces.IPerfilDAO;

/** Solo para pruebas sin MySQL (-Dparchis.dao=memoria). No es la opción por defecto. */
public class PerfilDAOMemoria implements IPerfilDAO {

    private final Map<String, PerfilDTO> perfiles = new ConcurrentHashMap<>();
    private final AtomicInteger siguiente = new AtomicInteger(1);

    @Override
    public Optional<PerfilDTO> buscarPorNombre(String nombre) {
        return Optional.ofNullable(perfiles.get(nombre));
    }

    @Override
    public PerfilDTO insertar(String nombre) {
        return perfiles.computeIfAbsent(nombre, n -> new PerfilDTO(siguiente.getAndIncrement(), n));
    }
}
