package parchis.red;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/** Dos nodos en la misma máquina para comprobar rápido que el prototipo funciona. */
public class DemoLocal {

    public static void main(String[] args) throws Exception {
        List<String> deAna = new CopyOnWriteArrayList<>();
        List<String> deLuis = new CopyOnWriteArrayList<>();
        try (NodoP2P ana = new NodoP2P("Ana", 5050); NodoP2P luis = new NodoP2P("Luis", 5051)) {
            ana.alRecibir(m -> deLuis.add(m.tipo() + ":" + m.contenido()));   // lo que Ana recibe
            luis.alRecibir(m -> deAna.add(m.tipo() + ":" + m.contenido()));   // lo que Luis recibe
            ana.iniciar();
            luis.iniciar();
            luis.conectar("127.0.0.1", 5050);
            Thread.sleep(300);
            ana.enviarATodos(new Mensaje(NodoP2P.TEXTO, "Ana", "¿Listo para jugar?"));
            luis.enviarATodos(new Mensaje(NodoP2P.TEXTO, "Luis", "¡Listo!"));
            Thread.sleep(300);
            System.out.println("Luis recibió: " + deAna);
            System.out.println("Ana recibió:  " + deLuis);
        }
    }
}
