/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio.entidades;

/**
 *
 * @author jesus
 */
import java.util.Random;

public class Dado {
    private int ultimoValor;
    private final Random random = new Random();

    public int lanzar() {
        this.ultimoValor = random.nextInt(6) + 1;
        return ultimoValor;
    }

    public int getUltimoValor() { return ultimoValor; }
}
