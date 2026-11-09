package parchis.presentacion.vistas;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import parchis.dtos.SalaDTO;
import parchis.interfaces.SalaException;
import parchis.presentacion.control.ControlSala;

/** Pantalla inicial: crear una sala (anfitrión) o unirse a la de otro par. */
public class FrmMenuSala extends JFrame {

    private final JTextField txtNombre = new JTextField("Jugador", 14);
    private final JTextField txtPuertoLocal = new JTextField("5000", 14);
    private final JTextField txtHost = new JTextField("127.0.0.1", 14);
    private final JTextField txtPuertoAnfitrion = new JTextField("5000", 14);

    public FrmMenuSala() {
        super("Parchís en red - Sala");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        fila(p, c, 0, "Tu nombre", txtNombre);
        fila(p, c, 1, "Tu puerto", txtPuertoLocal);
        JButton btnCrear = new JButton("Crear sala");
        btnCrear.addActionListener(e -> crear());
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        p.add(btnCrear, c);

        c.gridy = 3;
        p.add(new JLabel("— o únete a la sala de otro jugador —", JLabel.CENTER), c);
        c.gridwidth = 1;
        fila(p, c, 4, "Dirección del anfitrión", txtHost);
        fila(p, c, 5, "Puerto del anfitrión", txtPuertoAnfitrion);
        JButton btnUnirse = new JButton("Unirse a sala");
        btnUnirse.addActionListener(e -> unirse());
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        p.add(btnUnirse, c);

        setContentPane(p);
        pack();
        setLocationRelativeTo(null);
    }

    private void fila(JPanel p, GridBagConstraints c, int y, String etiqueta, JTextField campo) {
        c.gridx = 0; c.gridy = y; c.gridwidth = 1;
        p.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        p.add(campo, c);
    }

    private void crear() {
        try {
            ControlSala control = new ControlSala();
            SalaDTO sala = control.crearSala(txtNombre.getText(), Integer.parseInt(txtPuertoLocal.getText().trim()));
            abrirSala(control, sala);
        } catch (NumberFormatException ex) {
            error("El puerto debe ser un número.");
        } catch (SalaException ex) {
            error(ex.getMessage());
        }
    }

    private void unirse() {
        try {
            ControlSala control = new ControlSala();
            SalaDTO sala = control.unirseASala(txtNombre.getText(),
                    Integer.parseInt(txtPuertoLocal.getText().trim()),
                    txtHost.getText(),
                    Integer.parseInt(txtPuertoAnfitrion.getText().trim()));
            abrirSala(control, sala);
        } catch (NumberFormatException ex) {
            error("Los puertos deben ser números.");
        } catch (SalaException ex) {
            error(ex.getMessage());
        }
    }

    private void abrirSala(ControlSala control, SalaDTO sala) {
        new FrmSala(control, sala).setVisible(true);
        // Siguiente jugador en la misma máquina: sugiere otro puerto local.
        txtPuertoLocal.setText(String.valueOf(Integer.parseInt(txtPuertoLocal.getText().trim()) + 1));
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }
}
