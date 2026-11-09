package parchis.red;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Nodo P2P: es servidor (acepta pares) y cliente (se conecta a pares) a la vez.
 * Prototipo del estilo del Hito 2A.
 */
public class NodoP2P implements AutoCloseable {

    public static final String SALUDO = "SALUDO";
    public static final String TEXTO = "TEXTO";

    private final String nombre;
    private final int puerto;
    private final List<ConexionPar> conexiones = new CopyOnWriteArrayList<>();
    private volatile Consumer<Mensaje> alRecibir = m -> { };
    private ServerSocket servidor;

    public NodoP2P(String nombre, int puerto) {
        this.nombre = nombre;
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void alRecibir(Consumer<Mensaje> manejador) {
        this.alRecibir = manejador;
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

    /** Se conecta a otro par. */
    public void conectar(String host, int puertoRemoto) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(host, puertoRemoto), 3000);
        registrar(s);
    }

    private void registrar(Socket s) throws IOException {
        ConexionPar c = new ConexionPar(s, this);
        conexiones.add(c);
        c.start();
        // Al conectarse, cada lado se presenta con su estilo.
        c.enviar(new Mensaje(SALUDO, nombre,
                "Hola, soy " + nombre + ". Mi estilo es P2P: cada nodo es cliente y servidor a la vez."));
    }

    public void enviarATodos(Mensaje m) {
        for (ConexionPar c : conexiones) {
            c.enviar(m);
        }
    }

    public int cantidadPares() {
        return conexiones.size();
    }

    void recibido(Mensaje m) {
        alRecibir.accept(m);
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
