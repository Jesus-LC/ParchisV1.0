package parchis.sala;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;

/**
 * Mensajes del CU-01 entre pares y su codificación a texto.
 *  UNIRSE        (invitado -> anfitrión)  : JugadorDTO
 *  SALA          (anfitrión -> invitado)  : SalaDTO con el invitado ya incluido
 *  SALA_LLENA    (anfitrión -> invitado)  : alterno A2
 *  JUGADOR_UNIDO (invitado -> todos)      : JugadorDTO
 */
final class ProtocoloSala {

    static final String UNIRSE = "UNIRSE";
    static final String SALA = "SALA";
    static final String SALA_LLENA = "SALA_LLENA";
    static final String JUGADOR_UNIDO = "JUGADOR_UNIDO";

    private ProtocoloSala() { }

    static String codificar(JugadorDTO j) {
        return enc(j.nombre()) + "~" + enc(j.host()) + "~" + j.puerto();
    }

    static JugadorDTO decodificarJugador(String texto) {
        String[] p = texto.split("~", -1);
        if (p.length != 3) {
            throw new IllegalArgumentException("Jugador mal formado: " + texto);
        }
        return new JugadorDTO(dec(p[0]), dec(p[1]), Integer.parseInt(p[2]));
    }

    /** capacidad, anfitrión y luego un jugador por línea. */
    static String codificar(SalaDTO sala) {
        StringBuilder sb = new StringBuilder();
        sb.append(sala.capacidad()).append('\n').append(codificar(sala.anfitrion()));
        for (JugadorDTO j : sala.jugadores()) {
            sb.append('\n').append(codificar(j));
        }
        return sb.toString();
    }

    static SalaDTO decodificarSala(String texto) {
        String[] lineas = texto.split("\n");
        int capacidad = Integer.parseInt(lineas[0]);
        JugadorDTO anfitrion = decodificarJugador(lineas[1]);
        List<JugadorDTO> jugadores = new ArrayList<>();
        for (int i = 2; i < lineas.length; i++) {
            jugadores.add(decodificarJugador(lineas[i]));
        }
        return new SalaDTO(anfitrion, jugadores, capacidad);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String dec(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
