package parchis.interfaces;

/** Falla al leer o guardar datos (base de datos caída, configuración ausente, etc.). */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
