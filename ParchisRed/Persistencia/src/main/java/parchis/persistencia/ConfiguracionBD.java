package parchis.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import parchis.interfaces.PersistenciaException;

/**
 * Datos de conexión a MySQL. NUNCA van en el código ni en GitHub: se leen de
 * (en este orden) -Dparchis.db.config=ruta, ./db.local.properties,
 * ~/.parchis/db.local.properties o las variables de entorno
 * PARCHIS_DB_URL, PARCHIS_DB_USER y PARCHIS_DB_PASSWORD.
 */
class ConfiguracionBD {

    final String url;
    final String usuario;
    final String contrasena;

    private ConfiguracionBD(String url, String usuario, String contrasena) {
        this.url = url;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    static ConfiguracionBD cargar() throws PersistenciaException {
        String ruta = System.getProperty("parchis.db.config");
        Path[] candidatos = {
            ruta != null ? Paths.get(ruta) : null,
            Paths.get("db.local.properties"),
            Paths.get(System.getProperty("user.home"), ".parchis", "db.local.properties")
        };
        for (Path p : candidatos) {
            if (p != null && Files.isRegularFile(p)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(p)) {
                    props.load(in);
                } catch (IOException e) {
                    throw new PersistenciaException("No se pudo leer " + p, e);
                }
                return desde(props.getProperty("url"), props.getProperty("usuario"), props.getProperty("contrasena"), p.toString());
            }
        }
        return desde(System.getenv("PARCHIS_DB_URL"), System.getenv("PARCHIS_DB_USER"),
                System.getenv("PARCHIS_DB_PASSWORD"), "las variables de entorno PARCHIS_DB_*");
    }

    private static ConfiguracionBD desde(String url, String usuario, String contrasena, String origen)
            throws PersistenciaException {
        if (url == null || usuario == null) {
            throw new PersistenciaException("Falta la configuración de MySQL. Copia db.example.properties a "
                    + "db.local.properties y llena tus datos (origen revisado: " + origen + ").");
        }
        return new ConfiguracionBD(url, usuario, contrasena == null ? "" : contrasena);
    }
}
