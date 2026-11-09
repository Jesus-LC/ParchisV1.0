package parchis.presentacion.vistas;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;
import parchis.presentacion.control.ControlSala;

/** Muestra la copia de la sala de este par; se refresca cuando llega un JugadorUnido. */
public class FrmSala extends JFrame {

    private final JLabel lblDireccion = new JLabel();
    private final JLabel lblContador = new JLabel();
    private final DefaultListModel<String> modelo = new DefaultListModel<>();

    public FrmSala(ControlSala control, SalaDTO sala) {
        super("Sala de " + sala.jugadores().get(sala.jugadores().size() - 1).nombre());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(lblDireccion, BorderLayout.NORTH);
        arriba.add(lblContador, BorderLayout.SOUTH);
        p.add(arriba, BorderLayout.NORTH);
        p.add(new JScrollPane(new JList<>(modelo)), BorderLayout.CENTER);
        setContentPane(p);
        setSize(360, 280);
        setLocationByPlatform(true);

        mostrar(sala);
        control.registrarObservador(s -> SwingUtilities.invokeLater(() -> mostrar(s)));
    }

    private void mostrar(SalaDTO sala) {
        lblDireccion.setText("Dirección de la sala: " + sala.direccionAnfitrion());
        lblContador.setText("Jugadores: " + sala.jugadores().size() + " / " + sala.capacidad());
        modelo.clear();
        for (JugadorDTO j : sala.jugadores()) {
            String rol = j.equals(sala.anfitrion()) ? "  (anfitrión)" : "";
            modelo.addElement(j.nombre() + " - " + j.direccion() + rol);
        }
    }
}
