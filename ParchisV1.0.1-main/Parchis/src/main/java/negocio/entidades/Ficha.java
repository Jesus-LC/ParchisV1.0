/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.entidades;

/**
 *
 * @author jesus
 */
public class Ficha {
    private final int id;
    private final String color;
    private int posicion;
    private String estado; 

    public Ficha(int id, String color) {
        this.id = id;
        this.color = color;
        this.posicion = 0;
        this.estado = "EnCasa";
    }

    public int getId() { return id; }
    public String getColor() { return color; }
    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public void avanzar(int pasos) { this.posicion += pasos; }
}
