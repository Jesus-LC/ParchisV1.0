package parchis.fabrica;

import parchis.interfaces.IFachadaSala;
import parchis.salamock.FachadaSalaMock;
import parchis.salamock.RedSimulada;

/**
 * Único lugar que conoce las clases concretas de la fachada.
 * Hito 2A: entrega el mock. Hito 2B: aquí se cambia a FachadaSala (real).
 */
public final class FabricaFachadaSala {

    private static final RedSimulada RED = new RedSimulada();

    private FabricaFachadaSala() { }

    public static IFachadaSala crear() {
        return new FachadaSalaMock(RED);
    }
}
