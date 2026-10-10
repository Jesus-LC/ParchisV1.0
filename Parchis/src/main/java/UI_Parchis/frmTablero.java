/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI_Parchis;

import controlador.ControladorTurno;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import modelo.ParchisModelo;
import modelo.ParchisObservador;
import negocio.entidades.Ficha;
import negocio.entidades.Jugador;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/**
 *
 * @author jesus
 */
public class frmTablero extends javax.swing.JFrame implements ParchisObservador {

    private final ParchisModelo modelo;
    private final ControladorTurno controlador;

    // Íconos de Fichas (28x28 px nativas)
    private ImageIcon iconoRojo;
    private ImageIcon iconoAmarillo;

    // Mapeo de Ficha -> JLabel en la UI
    private final Map<String, JLabel> mapFichasUI = new HashMap<>();

    /**
     * Constructor principal invocado desde el Menú.
     */
    public frmTablero(ParchisModelo modelo) {
        this.modelo = modelo;

        // 1. Inicializar componentes del GUI Builder de NetBeans
        initComponents();
        this.setLocationRelativeTo(null); // Centrar en pantalla

        // 2. Cargar recursos gráficos
        cargarImagenesFichas();

        // 3. Vincular Observador/Controlador e instanciar fichas
        if (this.modelo != null) {
            this.modelo.registrarObservador(this);
            this.controlador = new ControladorTurno(this.modelo);
            inicializarFichasGraficas();
        } else {
            this.controlador = null;
        }
    }

    /**
     * Constructor por defecto para el diseñador gráfico de NetBeans.
     */
    public frmTablero() {
        this(null);
    }

    private void cargarImagenesFichas() {
        java.net.URL urlRojo = getClass().getResource("/Recursos/ficha_Roja.png");
        java.net.URL urlAmarillo = getClass().getResource("/Recursos/ficha_Amarilla.png");

        if (urlRojo != null) iconoRojo = new ImageIcon(urlRojo);
        if (urlAmarillo != null) iconoAmarillo = new ImageIcon(urlAmarillo);
    }

    /**
     * Instancia dinámicamente los JLabels de 28x28 y los coloca sobre el JLayeredPane.
     */
    private void inicializarFichasGraficas() {
    if (modelo == null || modelo.obtenerJugadores() == null) return;

    for (Jugador jugador : modelo.obtenerJugadores()) {
        ImageIcon iconoActual = jugador.getColor().equalsIgnoreCase("Rojo") ? iconoRojo : iconoAmarillo;

        int i = 1;
        for (Ficha ficha : jugador.getFichas()) {
            JLabel lblFicha = new JLabel(iconoActual);
            lblFicha.setSize(28, 28); // Tamaño nativo de la ficha

            String idFicha = jugador.getColor() + "_" + i;
            mapFichasUI.put(idFicha, lblFicha);

            // Obtener punto (X, Y) desde Coordenadas
            Point p = Coordenadas.obtener("CASA_" + jugador.getColor().toUpperCase() + "_" + i);
            int xLocal = p.x + 260; 
             int yLocal = p.y;
            lblFicha.setLocation(xLocal, yLocal);

            // Agregar a la capa superior PALETTE_LAYER (capa 2)
            jLayeredPaneTablero.add(lblFicha, javax.swing.JLayeredPane.PALETTE_LAYER);
            i++;
        }
    }
    jLayeredPaneTablero.revalidate();
    jLayeredPaneTablero.repaint();
}

    /**
     * Notificación del Patrón Observador desde ParchisModelo.
     */
    @Override
    public void alCambiarEstadoJuego(String estadoMensaje, String nombreJugadorActual, int ultimoDado, List<Jugador> jugadores) {
        lblTurno.setText("Turno actual: " + nombreJugadorActual);
        lblMensaje.setText("<html>" + estadoMensaje + "</html>");
        actualizarImagenDado(ultimoDado);
        actualizarPosicionesFichas(jugadores);
    }

    private void actualizarPosicionesFichas(List<Jugador> jugadores) {
    if (jugadores == null) return;

    for (Jugador jugador : jugadores) {
        int i = 1;
        for (Ficha ficha : jugador.getFichas()) {
            String idFicha = jugador.getColor() + "_" + i;
            JLabel lblFicha = mapFichasUI.get(idFicha);

            if (lblFicha != null) {
                Point nuevaPos;
                if ("EnCasa".equalsIgnoreCase(ficha.getEstado())) {
                    nuevaPos = Coordenadas.obtener("CASA_" + jugador.getColor().toUpperCase() + "_" + i);
                } else if ("EnMeta".equalsIgnoreCase(ficha.getEstado())) {
                    nuevaPos = Coordenadas.obtener("META_" + jugador.getColor().toUpperCase());
                } else {
                    nuevaPos = Coordenadas.obtener("CASILLA_" + ficha.getPosicion());
                }

                // Actualizar la posición directa
                lblFicha.setLocation(nuevaPos);
            }
            i++;
        }
    }
    jLayeredPaneTablero.revalidate();
    jLayeredPaneTablero.repaint();
}

   private void actualizarImagenDado(int valorDado) {
        if (valorDado >= 1 && valorDado <= 6) {
            java.net.URL imgURL = getClass().getResource("/Recursos/dado" + valorDado + ".png");
           if (imgURL != null) {
                lblDado.setIcon(new javax.swing.ImageIcon(imgURL));
               lblDado.setText("");
           } else {
                lblDado.setIcon(null);
                lblDado.setText("Dado: " + valorDado);
            }
        } else {
            lblDado.setIcon(null);
            lblDado.setText("");
        }
    }
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLayeredPaneTablero = new javax.swing.JLayeredPane();
        jLabel1 = new javax.swing.JLabel();
        btnLanzarDado = new javax.swing.JButton();
        lblDado = new javax.swing.JLabel();
        lblTurno = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Tablero.jpg"))); // NOI18N

        btnLanzarDado.setText("Lanzar Dado");
        btnLanzarDado.addActionListener(this::btnLanzarDadoActionPerformed);

        lblMensaje.setText(".");

        jLayeredPaneTablero.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneTablero.setLayer(btnLanzarDado, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneTablero.setLayer(lblDado, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneTablero.setLayer(lblTurno, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneTablero.setLayer(lblMensaje, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPaneTableroLayout = new javax.swing.GroupLayout(jLayeredPaneTablero);
        jLayeredPaneTablero.setLayout(jLayeredPaneTableroLayout);
        jLayeredPaneTableroLayout.setHorizontalGroup(
            jLayeredPaneTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                .addGroup(jLayeredPaneTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jLayeredPaneTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                                .addComponent(lblDado)
                                .addGap(3, 3, 3)
                                .addComponent(btnLanzarDado))
                            .addComponent(lblMensaje)))
                    .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(85, 85, 85)
                .addComponent(jLabel1))
        );
        jLayeredPaneTableroLayout.setVerticalGroup(
            jLayeredPaneTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(lblTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jLayeredPaneTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(lblMensaje)
                        .addGap(249, 249, 249)
                        .addComponent(lblDado))
                    .addGroup(jLayeredPaneTableroLayout.createSequentialGroup()
                        .addGap(631, 631, 631)
                        .addComponent(btnLanzarDado))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPaneTablero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPaneTablero)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLanzarDadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLanzarDadoActionPerformed
        if (controlador != null) {
            controlador.ejecutarLanzarDado();
        } else {
            System.err.println("Error: El controlador no está inicializado.");
        }
    }//GEN-LAST:event_btnLanzarDadoActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLanzarDado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPaneTablero;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDado;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTurno;
    // End of variables declaration//GEN-END:variables

    
}
