package parchis.presentacion.control;

import parchis.dtos.SalaDTO;
import parchis.fabrica.FabricaFachadaSala;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.SalaException;

/** Controlador del CU-01. Solo conoce la interfaz de la fachada; la fábrica le da la implementación. */
public class ControlSala {

    private final IFachadaSala fachada = FabricaFachadaSala.crear();

    public SalaDTO crearSala(String nombre, int puertoLocal) throws SalaException {
        return fachada.crearSala(nombre, puertoLocal);
    }

    public SalaDTO unirseASala(String nombre, int puertoLocal, String host, int puertoAnfitrion) throws SalaException {
        return fachada.unirseASala(nombre, puertoLocal, host, puertoAnfitrion);
    }

    public SalaDTO obtenerSala() {
        return fachada.obtenerSala();
    }

    public void registrarObservador(IObservadorSala observador) {
        fachada.registrarObservador(observador);
    }
}
