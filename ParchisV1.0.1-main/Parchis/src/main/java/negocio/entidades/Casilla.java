/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.entidades;

/**
 *
 * @author jesus
 */
public class Casilla {
    private final int idCasilla;
    private final boolean esSegura;

    public Casilla(int idCasilla, boolean esSegura) {
        this.idCasilla = idCasilla;
        this.esSegura = esSegura;
    }

    public int getIdCasilla() { return idCasilla; }
    public boolean isEsSegura() { return esSegura; }
}
