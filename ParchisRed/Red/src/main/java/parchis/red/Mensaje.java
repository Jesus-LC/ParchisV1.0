package parchis.red;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Mensaje entre pares. Viaja como una línea de texto: tipo|remitente|contenido (codificado). */
public record Mensaje(String tipo, String remitente, String contenido) {

    public String serializar() {
        return cod(tipo) + "|" + cod(remitente) + "|" + cod(contenido);
    }

    public static Mensaje deserializar(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length != 3) {
            throw new IllegalArgumentException("Mensaje mal formado: " + linea);
        }
        return new Mensaje(dec(p[0]), dec(p[1]), dec(p[2]));
    }

    private static String cod(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String dec(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
