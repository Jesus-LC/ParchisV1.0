/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.entidades;

/**
 *
 * @author jesus
 */
import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private final int id;
    private final String nombre;
    private final String color;
    private final List<Ficha> fichas;

    public Jugador(int id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.fichas = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            fichas.add(new Ficha(i, color));
        }
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getColor() { return color; }
    public List<Ficha> getFichas() { return fichas; }
}
