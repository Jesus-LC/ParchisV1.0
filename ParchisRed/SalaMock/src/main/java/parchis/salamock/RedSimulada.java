package parchis.salamock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * "Red" en memoria del mock: guarda los pares por dirección para que varias
 * ventanas de la misma aplicación se comporten como computadoras distintas.
 */
public class RedSimulada {

    private final Map<String, FachadaSalaMock> pares = new ConcurrentHashMap<>();

    /** Devuelve false si la dirección ya está ocupada (puerto en uso). */
    boolean registrar(String direccion, FachadaSalaMock par) {
        return pares.putIfAbsent(direccion, par) == null;
    }

    FachadaSalaMock buscar(String direccion) {
        return pares.get(direccion);
    }
}
