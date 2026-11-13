package parchis.fabrica;

import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IPerfilDAO;
import parchis.persistencia.PerfilDAOMemoria;
import parchis.persistencia.PerfilDAOMySQL;
import parchis.sala.FachadaSala;
import parchis.sala.PerfilBO;
import parchis.salamock.FachadaSalaMock;
import parchis.salamock.RedSimulada;

/**
 * Único lugar que conoce las clases concretas (fachada, DAO, mock).
 * Hito 2B: entrega la FachadaSala real con PerfilDAOMySQL.
 * Para las pruebas se puede cambiar sin tocar código:
 *   -Dparchis.fachada=mock      usa FachadaSalaMock (Hito 2A)
 *   -Dparchis.dao=memoria       usa un DAO en memoria en vez de MySQL
 */
public final class FabricaFachadaSala {

    private static final RedSimulada RED_SIMULADA = new RedSimulada();
    private static final IPerfilDAO DAO_MEMORIA = new PerfilDAOMemoria();

    private FabricaFachadaSala() { }

    public static IFachadaSala crear() {
        if ("mock".equalsIgnoreCase(System.getProperty("parchis.fachada"))) {
            return new FachadaSalaMock(RED_SIMULADA);
        }
        return new FachadaSala(new PerfilBO(crearPerfilDAO()));
    }

    private static IPerfilDAO crearPerfilDAO() {
        if ("memoria".equalsIgnoreCase(System.getProperty("parchis.dao"))) {
            return DAO_MEMORIA;
        }
        return new PerfilDAOMySQL();
    }
}
