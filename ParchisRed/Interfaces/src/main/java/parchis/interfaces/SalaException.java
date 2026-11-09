package parchis.interfaces;

/** Error de negocio del CU-01. La presentación solo muestra el mensaje. */
public class SalaException extends Exception {

    public enum Codigo {
        /** Nombre vacío, puerto fuera de rango o puerto en uso. */
        DATOS_INVALIDOS,
        /** Alterno A2: la sala ya tiene el máximo de jugadores. */
        SALA_LLENA,
        /** Alterno A3: no hay un anfitrión escuchando en esa dirección. */
        ANFITRION_NO_DISPONIBLE
    }

    private final Codigo codigo;

    public SalaException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
