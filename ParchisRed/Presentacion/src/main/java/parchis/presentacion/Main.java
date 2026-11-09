package parchis.presentacion;

import javax.swing.SwingUtilities;
import parchis.presentacion.vistas.FrmMenuSala;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMenuSala().setVisible(true));
    }
}
