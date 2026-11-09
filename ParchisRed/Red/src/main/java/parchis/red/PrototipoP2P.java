package parchis.red;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Prototipo del estilo P2P: dos computadoras se mandan un mensaje.
 *
 * Computadora A:  java -cp Red/target/classes parchis.red.PrototipoP2P Ana 5000
 * Computadora B:  java -cp Red/target/classes parchis.red.PrototipoP2P Luis 5001 IP_DE_A 5000
 * Después escriban texto y Enter para enviarlo a todos los pares; "salir" termina.
 */
public class PrototipoP2P {

    public static void main(String[] args) throws Exception {
        if (args.length != 2 && args.length != 4) {
            System.out.println("Uso: PrototipoP2P <nombre> <puertoLocal> [hostRemoto puertoRemoto]");
            return;
        }
        try (NodoP2P nodo = new NodoP2P(args[0], Integer.parseInt(args[1]))) {
            nodo.alRecibir(m -> System.out.println("[" + m.remitente() + "] " + m.contenido()));
            nodo.iniciar();
            System.out.println("Escuchando en el puerto " + args[1] + " como " + args[0]);
            if (args.length == 4) {
                nodo.conectar(args[2], Integer.parseInt(args[3]));
                System.out.println("Conectado a " + args[2] + ":" + args[3]);
            }
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String linea;
            while ((linea = teclado.readLine()) != null && !linea.equalsIgnoreCase("salir")) {
                nodo.enviarATodos(new Mensaje(NodoP2P.TEXTO, nodo.getNombre(), linea));
            }
        }
    }
}
