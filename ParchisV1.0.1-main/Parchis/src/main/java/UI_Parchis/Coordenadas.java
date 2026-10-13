/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI_Parchis;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jesus
 */
public class Coordenadas {
    private static final Map<String, Point> MAPA_POSICIONES = new HashMap<>();

    static {
        // --- CASAS / CÁRCELES ---
        MAPA_POSICIONES.put("CASA_ROJO_1", new Point(350, 720));
        MAPA_POSICIONES.put("CASA_ROJO_2", new Point(270, 720));
        MAPA_POSICIONES.put("CASA_ROJO_3", new Point(350, 790));
        MAPA_POSICIONES.put("CASA_ROJO_4", new Point(270, 790));

        MAPA_POSICIONES.put("CASA_AMARILLO_1", new Point(1120, 150));
        MAPA_POSICIONES.put("CASA_AMARILLO_2", new Point(1160,150));
        MAPA_POSICIONES.put("CASA_AMARILLO_3", new Point(1120, 220));
        MAPA_POSICIONES.put("CASA_AMARILLO_4", new Point(1160, 220));

        // --- SALIDAS Y SEGUROS PRINCIPALES ---
        MAPA_POSICIONES.put("CASILLA_5", new Point(410, 620));  
        MAPA_POSICIONES.put("CASILLA_22", new Point(570, 620)); 
        MAPA_POSICIONES.put("CASILLA_39", new Point(570, 300)); 
        MAPA_POSICIONES.put("CASILLA_56", new Point(410, 300)); 

        // --- METAS CENTRO ---
        MAPA_POSICIONES.put("META_ROJO", new Point(410, 455));
        MAPA_POSICIONES.put("META_AMARILLO", new Point(490, 410));
    }

    public static Point obtener(String clave) {
        return MAPA_POSICIONES.getOrDefault(clave, new Point(450, 450));
    }
}
