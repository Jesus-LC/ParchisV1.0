package parchis.salamock;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.SalaException;
import parchis.interfaces.SalaException.Codigo;

/**
 * Mock de la fachada: guarda todo en memoria y falla en los alternos A2 y A3.
 * Cada instancia hace de un par; todas comparten la misma RedSimulada.
 */
public class FachadaSalaMock implements IFachadaSala {

    public static final int CAPACIDAD = 4;

    private final RedSimulada red;
    private final List<IObservadorSala> observadores = new CopyOnWriteArrayList<>();
    private JugadorDTO yo;
    private SalaDTO copiaSala;      // la copia de la sala que tiene ESTE par
    private boolean anfitrion;

    public FachadaSalaMock(RedSimulada red) {
        this.red = red;
    }

    @Override
    public synchronized SalaDTO crearSala(String nombreJugador, int puertoLocal) throws SalaException {
        validar(nombreJugador, puertoLocal);
        yaEstaEnSala();
        JugadorDTO nuevo = new JugadorDTO(nombreJugador.trim(), direccionLocal(), puertoLocal);
        // Paso 1: "abre el puerto"; si otro par ya lo tiene, falla como un bind real.
        if (!red.registrar(nuevo.direccion(), this)) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "El puerto " + puertoLocal + " ya está en uso.");
        }
        yo = nuevo;
        anfitrion = true;
        copiaSala = new SalaDTO(nuevo, List.of(nuevo), CAPACIDAD);
        return copiaSala;
    }

    @Override
    public synchronized SalaDTO unirseASala(String nombreJugador, int puertoLocal,
                                            String hostAnfitrion, int puertoAnfitrion) throws SalaException {
        validar(nombreJugador, puertoLocal);
        validar("x", puertoAnfitrion);
        if (hostAnfitrion == null || hostAnfitrion.isBlank()) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "Escribe la dirección del anfitrión.");
        }
        yaEstaEnSala();

        // Paso 2: se conecta al anfitrión.
        FachadaSalaMock par = red.buscar(normalizar(hostAnfitrion) + ":" + puertoAnfitrion);
        if (par == null || !par.anfitrion) {
            // A3
            throw new SalaException(Codigo.ANFITRION_NO_DISPONIBLE,
                    "No hay ninguna sala en " + hostAnfitrion.trim() + ":" + puertoAnfitrion + ".");
        }
        SalaDTO salaAnfitrion = par.obtenerSala();
        if (salaAnfitrion.estaLlena()) {
            // A2
            throw new SalaException(Codigo.SALA_LLENA,
                    "La sala ya tiene " + salaAnfitrion.capacidad() + " jugadores.");
        }

        JugadorDTO nuevo = new JugadorDTO(nombreJugador.trim(), direccionLocal(), puertoLocal);
        if (!red.registrar(nuevo.direccion(), this)) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "El puerto " + puertoLocal + " ya está en uso.");
        }
        yo = nuevo;

        // Paso 2: recibe la lista de pares y se conecta a cada uno.
        List<JugadorDTO> paresActuales = salaAnfitrion.jugadores();
        List<JugadorDTO> todos = new ArrayList<>(paresActuales);
        todos.add(nuevo);
        copiaSala = new SalaDTO(salaAnfitrion.anfitrion(), todos, CAPACIDAD);

        // Paso 3: envía JugadorUnido a todos y cada par actualiza su copia.
        for (JugadorDTO p : paresActuales) {
            FachadaSalaMock destino = red.buscar(p.direccion());
            if (destino != null) {
                destino.recibirJugadorUnido(nuevo);
            }
        }
        return copiaSala;
    }

    @Override
    public synchronized SalaDTO obtenerSala() {
        return copiaSala;
    }

    @Override
    public void registrarObservador(IObservadorSala observador) {
        observadores.add(observador);
    }

    /** Mensaje JugadorUnido recibido de otro par. */
    void recibirJugadorUnido(JugadorDTO jugador) {
        SalaDTO nueva;
        synchronized (this) {
            if (copiaSala == null || copiaSala.jugadores().contains(jugador)) {
                return;
            }
            List<JugadorDTO> todos = new ArrayList<>(copiaSala.jugadores());
            todos.add(jugador);
            copiaSala = new SalaDTO(copiaSala.anfitrion(), todos, CAPACIDAD);
            nueva = copiaSala;
        }
        for (IObservadorSala o : observadores) {
            o.alActualizarSala(nueva);
        }
    }

    // ---------- apoyo ----------

    private void validar(String nombre, int puerto) throws SalaException {
        if (nombre == null || nombre.isBlank()) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "Escribe tu nombre.");
        }
        if (puerto < 1024 || puerto > 65535) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "El puerto debe estar entre 1024 y 65535.");
        }
    }

    private void yaEstaEnSala() throws SalaException {
        if (copiaSala != null) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "Ya perteneces a una sala.");
        }
    }

    private static String direccionLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private static String normalizar(String host) {
        String h = host.trim();
        return (h.equalsIgnoreCase("localhost") || h.equals("127.0.0.1")) ? direccionLocal() : h;
    }
}
