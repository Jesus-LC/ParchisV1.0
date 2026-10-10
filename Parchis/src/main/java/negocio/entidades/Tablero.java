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

public class Tablero {
    private final List<Casilla> casillas = new ArrayList<>();

    public Tablero() {
        for (int i = 1; i <= 68; i++) {
            boolean segura = (i == 5 || i == 12 || i == 17 || i == 22 || i == 29 || i == 34 || i == 39 || i == 46 || i == 51 || i == 56 || i == 63 || i == 68);
            casillas.add(new Casilla(i, segura));
        }
    }

    public List<Casilla> getCasillas() { return casillas; }
}
