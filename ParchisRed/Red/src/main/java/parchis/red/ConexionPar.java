package parchis.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Conexión con un par: un hilo lee mensajes; enviar() escribe en la tubería de salida. */
public class ConexionPar extends Thread {

    private final Socket socket;
    private final NodoP2P nodo;
    private final BufferedReader entrada;
    private final PrintWriter salida;

    ConexionPar(Socket socket, NodoP2P nodo) throws IOException {
        this.socket = socket;
        this.nodo = nodo;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        setDaemon(true);
    }

    public synchronized void enviar(Mensaje m) {
        salida.println(m.serializar());
    }

    /** Dirección del otro extremo, en formato host:puerto. */
    public String direccionRemota() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                nodo.recibido(Mensaje.deserializar(linea), this);
            }
        } catch (IOException | IllegalArgumentException e) {
            // el par se desconectó o mandó basura: se cierra la conexión
        } finally {
            cerrar();
            nodo.cerrada(this);
        }
    }

    void cerrar() {
        try {
            socket.close();
        } catch (IOException ignorada) { }
    }
}
