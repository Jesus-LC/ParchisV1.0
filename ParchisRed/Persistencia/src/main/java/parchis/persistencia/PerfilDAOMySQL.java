package parchis.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import parchis.dtos.PerfilDTO;
import parchis.interfaces.IPerfilDAO;
import parchis.interfaces.PersistenciaException;

/** Único lugar con SQL del proyecto. */
public class PerfilDAOMySQL implements IPerfilDAO {

    private static final String SELECT_POR_NOMBRE = "SELECT id, nombre FROM perfil WHERE nombre = ?";
    private static final String INSERT = "INSERT INTO perfil (nombre) VALUES (?)";
    private static final int ERROR_DUPLICADO = 1062;

    private ConfiguracionBD config;

    private Connection abrir() throws PersistenciaException {
        try {
            if (config == null) {
                config = ConfiguracionBD.cargar();
            }
            return DriverManager.getConnection(config.url, config.usuario, config.contrasena);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<PerfilDTO> buscarPorNombre(String nombre) throws PersistenciaException {
        try (Connection c = abrir(); PreparedStatement ps = c.prepareStatement(SELECT_POR_NOMBRE)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new PerfilDTO(rs.getInt("id"), rs.getString("nombre"))) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el perfil: " + e.getMessage(), e);
        }
    }

    @Override
    public PerfilDTO insertar(String nombre) throws PersistenciaException {
        try (Connection c = abrir(); PreparedStatement ps = c.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return new PerfilDTO(claves.getInt(1), nombre);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == ERROR_DUPLICADO) {   // otro par lo creó justo antes
                return buscarPorNombre(nombre).orElseThrow(() -> new PersistenciaException("Perfil duplicado"));
            }
            throw new PersistenciaException("Error al guardar el perfil: " + e.getMessage(), e);
        }
    }
}
