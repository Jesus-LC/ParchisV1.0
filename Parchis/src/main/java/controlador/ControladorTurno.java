/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.ParchisModelo;

/**
 *
 * @author jesus
 */
public class ControladorTurno {
    private final ParchisModelo modelo;

    public ControladorTurno(ParchisModelo modelo) {
        this.modelo = modelo;
    }

    public void ejecutarLanzarDado() {
        modelo.solicitarLanzamiento();
    }

    public void ejecutarMoverFicha(int fichaIndex) {
        modelo.moverFicha(fichaIndex);
    }
}
