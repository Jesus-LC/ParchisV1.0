package parchis.interfaces;

import parchis.dtos.SalaDTO;

/** Quien quiera enterarse de que la copia de la sala cambió. */
public interface IObservadorSala {
    void alActualizarSala(SalaDTO sala);
}
