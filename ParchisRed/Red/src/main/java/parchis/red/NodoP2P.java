package parchis.red;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Nodo P2P: es servidor (acepta pares) y cliente (se conecta a pares) a la vez.
 * Lo usa el prototipo del Hito 2A y, en el 2B, el subsistema Sala.
 */
public class NodoP2P implements AutoCloseable {

    public static final String SALUDO = "SALUDO";
    public static final String TEXTO = "TEXTO";
    private static final int TIEMPO_CONEXION_MS = 3000;

    private final String nombre;
    private final int puerto;
    private final List<ConexionPar> conexiones = new CopyOnWriteArrayList<>();
    private volatile BiConsumer<Mensaje, ConexionPar> manejador = (m, c) -> { };
    private volatile boolean saludoAutomatico = true;
    private ServerSocket servidor;

    public NodoP2P(String nombre, int puerto) {
        this.nombre = nombre;
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    /** Si es true (por defecto), al conectarse cada lado se presenta con un SALUDO. */
    public void setSaludoAutomatico(boolean saludoAutomatico) {
        this.saludoAutomatico = saludoAutomatico;
    }

    /** Manejador simple: solo recibe el mensaje. */
    public void alRecibir(Consumer<Mensaje> destino) {
        this.manejador = (m, c) -> destino.accept(m);
    }

    /** Manejador que también recibe la conexión de origen, para poder responderle. */
    public void alRecibirDe(BiConsumer<Mensaje, ConexionPar> destino) {
        this.manejador = destino;
    }

    /** Abre el puerto y empieza a aceptar pares. */
    public void iniciar() throws IOException {
        servidor = new ServerSocket(puerto);
        Thread aceptador = new Thread(() -> {
            while (!servidor.isClosed()) {
                try {
                    registrar(servidor.accept());
                } catch (IOException e) {
                    break;
                }
            }
        }, "aceptador-" + puerto);
        aceptador.setDaemon(true);
        aceptador.start();
    }

    /** Se conecta a otro par y devuelve la conexión. */
    public ConexionPar conectar(String host, int puertoRemoto) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(host, puertoRemoto), TIEMPO_CONEXION_MS);
        return registrar(s);
    }

    private ConexionPar registrar(Socket s) throws IOException {
        ConexionPar c = new ConexionPar(s, this);
        conexiones.add(c);
        c.start();
        if (saludoAutomatico) {
            c.enviar(new Mensaje(SALUDO, nombre,
                    "Hola, soy " + nombre + ". Mi estilo es P2P: cada nodo es cliente y servidor a la vez."));
        }
        return c;
    }

    public void enviarATodos(Mensaje m) {
        for (ConexionPar c : conexiones) {
            c.enviar(m);
        }
    }

    public int cantidadPares() {
        return conexiones.size();
    }

    void recibido(Mensaje m, ConexionPar origen) {
        manejador.accept(m, origen);
    }

    void cerrada(ConexionPar c) {
        conexiones.remove(c);
    }

    @Override
    public void close() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException ignorada) { }
        conexiones.forEach(ConexionPar::cerrar);
    }
}
