package parchis.sala;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.PersistenciaException;
import parchis.interfaces.SalaException;
import parchis.interfaces.SalaException.Codigo;
import parchis.red.ConexionPar;
import parchis.red.Mensaje;
import parchis.red.NodoP2P;

/**
 * Subsistema Sala real (CU-01 en P2P). Cada instancia es un par: abre su puerto,
 * guarda su propia copia de la sala y se mantiene al día con los mensajes de los demás.
 */
public class FachadaSala implements IFachadaSala {

    public static final int CAPACIDAD = 4;
    private static final int ESPERA_ANFITRION_MS = 3000;

    private final PerfilBO perfilBO;
    private final List<IObservadorSala> observadores = new CopyOnWriteArrayList<>();
    private final Object cerrojo = new Object();
    private final List<JugadorDTO> pendientes = new ArrayList<>();   // JugadorUnido llegados antes de tener copia
    private volatile CompletableFuture<Mensaje> respuestaAnfitrion;
    private NodoP2P nodo;
    private JugadorDTO yo;
    private SalaDTO copiaSala;
    private boolean anfitrion;

    public FachadaSala(PerfilBO perfilBO) {
        this.perfilBO = perfilBO;
    }

    // ------------------------------------------------------------ paso 1
    @Override
    public SalaDTO crearSala(String nombreJugador, int puertoLocal) throws SalaException {
        validar(nombreJugador, puertoLocal);
        yaEstaEnSala();
        cargarPerfil(nombreJugador);
        abrirNodo(nombreJugador.trim(), puertoLocal);
        JugadorDTO nuevo = new JugadorDTO(nombreJugador.trim(), direccionLocal(), puertoLocal);
        synchronized (cerrojo) {
            yo = nuevo;
            anfitrion = true;
            copiaSala = new SalaDTO(nuevo, List.of(nuevo), CAPACIDAD);
            return copiaSala;
        }
    }

    // ------------------------------------------------------------ pasos 2 y 3
    @Override
    public SalaDTO unirseASala(String nombreJugador, int puertoLocal,
                               String hostAnfitrion, int puertoAnfitrion) throws SalaException {
        validar(nombreJugador, puertoLocal);
        validar("x", puertoAnfitrion);
        if (hostAnfitrion == null || hostAnfitrion.isBlank()) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "Escribe la dirección del anfitrión.");
        }
        yaEstaEnSala();
        cargarPerfil(nombreJugador);
        abrirNodo(nombreJugador.trim(), puertoLocal);   // para que los demás pares puedan conectarse con este

        // Paso 2: se conecta al anfitrión y pide entrar.
        JugadorDTO nuevo = new JugadorDTO(nombreJugador.trim(), direccionLocal(), puertoLocal);
        CompletableFuture<Mensaje> espera = new CompletableFuture<>();
        respuestaAnfitrion = espera;
        Mensaje respuesta;
        try {
            ConexionPar conAnfitrion = nodo.conectar(hostAnfitrion.trim(), puertoAnfitrion);
            conAnfitrion.enviar(new Mensaje(ProtocoloSala.UNIRSE, nuevo.nombre(), ProtocoloSala.codificar(nuevo)));
            respuesta = espera.get(ESPERA_ANFITRION_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            cerrarNodo();
            // A3
            throw new SalaException(Codigo.ANFITRION_NO_DISPONIBLE,
                    "No hay ninguna sala en " + hostAnfitrion.trim() + ":" + puertoAnfitrion + ".");
        }
        if (ProtocoloSala.SALA_LLENA.equals(respuesta.tipo())) {
            cerrarNodo();
            // A2
            throw new SalaException(Codigo.SALA_LLENA, "La sala ya tiene " + CAPACIDAD + " jugadores.");
        }
        if (!ProtocoloSala.SALA.equals(respuesta.tipo())) {
            cerrarNodo();
            throw new SalaException(Codigo.ANFITRION_NO_DISPONIBLE,
                    "No hay ninguna sala en " + hostAnfitrion.trim() + ":" + puertoAnfitrion + ".");
        }

        // Recibe la lista de pares (ya incluye a quien se une, porque el anfitrión le reservó el lugar).
        SalaDTO sala = ProtocoloSala.decodificarSala(respuesta.contenido());
        SalaDTO resultado;
        synchronized (cerrojo) {
            yo = nuevo;
            anfitrion = false;
            List<JugadorDTO> todos = new ArrayList<>(sala.jugadores());
            for (JugadorDTO p : pendientes) {
                if (!todos.contains(p)) {
                    todos.add(p);
                }
            }
            pendientes.clear();
            copiaSala = new SalaDTO(sala.anfitrion(), todos, sala.capacidad());
            resultado = copiaSala;
        }

        // Paso 2: se conecta a cada uno de los demás pares (con el anfitrión ya está conectado).
        for (JugadorDTO p : sala.jugadores()) {
            if (!p.equals(nuevo) && !p.equals(sala.anfitrion())) {
                try {
                    nodo.conectar(p.host(), p.puerto());
                } catch (IOException ignorado) {
                    // ese par ya no está; los demás sí se enteran
                }
            }
        }
        // Paso 3: envía JugadorUnido a todos.
        nodo.enviarATodos(new Mensaje(ProtocoloSala.JUGADOR_UNIDO, nuevo.nombre(), ProtocoloSala.codificar(nuevo)));
        return resultado;
    }

    @Override
    public SalaDTO obtenerSala() {
        synchronized (cerrojo) {
            return copiaSala;
        }
    }

    @Override
    public void registrarObservador(IObservadorSala observador) {
        observadores.add(observador);
    }

    // ------------------------------------------------------------ mensajes recibidos
    private void alRecibir(Mensaje m, ConexionPar origen) {
        try {
            switch (m.tipo()) {
                case ProtocoloSala.UNIRSE -> alRecibirUnirse(m, origen);
                case ProtocoloSala.JUGADOR_UNIDO -> alRecibirJugadorUnido(ProtocoloSala.decodificarJugador(m.contenido()));
                case ProtocoloSala.SALA, ProtocoloSala.SALA_LLENA -> {
                    CompletableFuture<Mensaje> espera = respuestaAnfitrion;
                    if (espera != null) {
                        espera.complete(m);
                    }
                }
                default -> { }
            }
        } catch (IllegalArgumentException mensajeMalFormado) {
            // se ignora un mensaje que no cumple el protocolo
        }
    }

    /** Somos el anfitrión: reservamos el lugar de quien llega o lo rechazamos si la sala está llena. */
    private void alRecibirUnirse(Mensaje m, ConexionPar origen) {
        JugadorDTO jugador = ProtocoloSala.decodificarJugador(m.contenido());
        Mensaje respuesta;
        SalaDTO nueva = null;
        synchronized (cerrojo) {
            if (!anfitrion || copiaSala == null) {
                return;   // este par no es anfitrión: no responde
            }
            if (copiaSala.estaLlena() && !copiaSala.jugadores().contains(jugador)) {
                respuesta = new Mensaje(ProtocoloSala.SALA_LLENA, yo.nombre(), "");
            } else {
                if (!copiaSala.jugadores().contains(jugador)) {
                    List<JugadorDTO> todos = new ArrayList<>(copiaSala.jugadores());
                    todos.add(jugador);
                    copiaSala = new SalaDTO(copiaSala.anfitrion(), todos, copiaSala.capacidad());
                    nueva = copiaSala;
                }
                respuesta = new Mensaje(ProtocoloSala.SALA, yo.nombre(), ProtocoloSala.codificar(copiaSala));
            }
        }
        origen.enviar(respuesta);
        if (nueva != null) {
            avisar(nueva);
        }
    }

    /** Paso 3: cada par actualiza su copia (si ya lo tenía, no hace nada). */
    private void alRecibirJugadorUnido(JugadorDTO jugador) {
        SalaDTO nueva;
        synchronized (cerrojo) {
            if (copiaSala == null) {
                if (!pendientes.contains(jugador)) {
                    pendientes.add(jugador);
                }
                return;
            }
            if (copiaSala.jugadores().contains(jugador)) {
                return;
            }
            List<JugadorDTO> todos = new ArrayList<>(copiaSala.jugadores());
            todos.add(jugador);
            copiaSala = new SalaDTO(copiaSala.anfitrion(), todos, copiaSala.capacidad());
            nueva = copiaSala;
        }
        avisar(nueva);
    }

    private void avisar(SalaDTO sala) {
        for (IObservadorSala o : observadores) {
            o.alActualizarSala(sala);
        }
    }

    // ------------------------------------------------------------ apoyo
    private void cargarPerfil(String nombre) throws SalaException {
        try {
            perfilBO.obtenerOCrear(nombre);
        } catch (PersistenciaException e) {
            throw new SalaException(Codigo.PERFIL_NO_DISPONIBLE, "No se pudo cargar tu perfil: " + e.getMessage());
        }
    }

    private void abrirNodo(String nombre, int puerto) throws SalaException {
        NodoP2P n = new NodoP2P(nombre, puerto);
        n.setSaludoAutomatico(false);
        n.alRecibirDe(this::alRecibir);
        try {
            n.iniciar();
        } catch (IOException e) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "El puerto " + puerto + " ya está en uso.");
        }
        nodo = n;
    }

    private void cerrarNodo() {
        if (nodo != null) {
            nodo.close();
            nodo = null;
        }
    }

    private void validar(String nombre, int puerto) throws SalaException {
        if (nombre == null || nombre.isBlank()) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "Escribe tu nombre.");
        }
        if (puerto < 1024 || puerto > 65535) {
            throw new SalaException(Codigo.DATOS_INVALIDOS, "El puerto debe estar entre 1024 y 65535.");
        }
    }

    private void yaEstaEnSala() throws SalaException {
        synchronized (cerrojo) {
            if (copiaSala != null || nodo != null) {
                throw new SalaException(Codigo.DATOS_INVALIDOS, "Ya perteneces a una sala.");
            }
        }
    }

    /**
     * Dirección que se comparte con los demás pares. Se puede forzar con -Dparchis.host=IP
     * (útil si la computadora tiene VPN o varias tarjetas de red).
     */
    static String direccionLocal() {
        String forzada = System.getProperty("parchis.host");
        if (forzada != null && !forzada.isBlank()) {
            return forzada.trim();
        }
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                        return a.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
